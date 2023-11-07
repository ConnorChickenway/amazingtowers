package xyz.connorchickenway.towers.game;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.entity.inventory.InventorySession;
import xyz.connorchickenway.towers.game.kit.Kit;
import xyz.connorchickenway.towers.game.lang.Lang;
import xyz.connorchickenway.towers.game.lang.placeholder.Placeholder;
import xyz.connorchickenway.towers.game.runnable.GameCountdown;
import xyz.connorchickenway.towers.game.runnable.GameRunnable;
import xyz.connorchickenway.towers.game.runnable.GameTask;
import xyz.connorchickenway.towers.game.runnable.util.TaskId;
import xyz.connorchickenway.towers.game.scoreboard.GameScoreboard;
import xyz.connorchickenway.towers.game.sign.GameSign;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.game.team.Team;
import xyz.connorchickenway.towers.game.world.GameWorld;
import xyz.connorchickenway.towers.nms.NMSManager;
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.*;
import xyz.connorchickenway.towers.utilities.location.Location;

import static xyz.connorchickenway.towers.game.lang.placeholder.Placeholder.*;

public class Game
{

    private final String gameName;
    private final Set<GamePlayer> players;
    private final Map<TaskId, GameRunnable> taskMap;
    private final Scoreboard scoreboard;
    private final File folder;
    private GameWorld gameWorld;
    private Kit kit;
    private GameState state;
    private Team red, blue;
    private Location lobby, ironGenerator, expGenerator;
    private int minPlayers, maxPlayers, count, maxPoints;
    private GameSign gameSign;
    private final GameScoreboard gameScoreboard;

    private Game( String gameName )
    {
        this.gameName = gameName;
        this.players = Sets.newConcurrentHashSet();
        this.taskMap = Maps.newHashMap();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.state = GameState.LOBBY;
        this.red = new Team( this, "red", ChatColor.RED, Color.RED, StaticConfiguration.red_name );
        this.blue = new Team( this, "blue", ChatColor.BLUE, Color.BLUE, StaticConfiguration.blue_name );
        this.gameScoreboard = new GameScoreboard();
        this.folder = new File( AmazingTowers.getInstance().getGameManager().getGameFolder(),  gameName );
    }

    public void join( GamePlayer gamePlayer )
    {
        Player player = gamePlayer.toBukkitPlayer();
        if ( players.size() >= maxPlayers && !player.hasPermission( "towers.joinfull" ) )
        {
            Lang.ARENA_FULL.sendLang( player, null );
            return;
        }
        gamePlayer.setGame( this );
        players.add( gamePlayer );
        player.setScoreboard( scoreboard );
        gameScoreboard.add( gamePlayer );
        InventorySession iSession = gamePlayer.getInventorySession();
        if ( iSession != null )
            iSession.save();
        player.getInventory().clear();    
        boolean hasTeam = this.hasTeam( player.getUniqueId() );
        switch( state )
        {
            
            case LOBBY: 
                updateScoreboard();
                if ( players.size() >= minPlayers )
                    startArena();   
                break;
            case GAME:
                if ( hasTeam )
                {
                    player.setGameMode( org.bukkit.GameMode.SURVIVAL );
                    this.getTeam( player.getUniqueId() ).doStuff( player, false );
                }
                break;
            case FINISH:
                player.setGameMode( org.bukkit.GameMode.SPECTATOR );
                break;        
            
            default:
                break;

        }
        if ( !hasTeam )
        {
            if ( player.getGameMode() != org.bukkit.GameMode.SPECTATOR )
                player.setGameMode( org.bukkit.GameMode.ADVENTURE );
            lobby.teleport( player );
            setItems( player );
        }
        if ( gameSign != null )
            gameSign.update();
        message( Lang.JOIN_ARENA,
                builder( pair( PLAYER_NAME, player ),
                        pair( ONLINE_PLAYERS , players.size() ),
                        pair( MAX_PLAYERS, maxPlayers ) ) );
        for ( PotionEffect effect : player.getActivePotionEffects() )
            player.removePotionEffect( effect.getType() );
    } 

    public void leave( GamePlayer gamePlayer, boolean leaveMessage )
    {
        Player player = gamePlayer.toBukkitPlayer();
        players.remove( gamePlayer );
        gamePlayer.setGame( null );
        gameScoreboard.remove( gamePlayer );
        MetadataUtils.remove( player, "items-game" );
        if ( leaveMessage )
            message( Lang.LEAVE_ARENA, builder( 
                pair( PLAYER_NAME, player ),
                pair( ONLINE_PLAYERS, players.size() ) ,
                pair( MAX_PLAYERS, maxPlayers ) ) );
        switch( state )
        {
            
            case STARTING: 
            case LOBBY:
                if ( state == GameState.LOBBY )
                    updateScoreboard();
                if ( isStarting() )
                {
                    if ( players.size() < minPlayers )
                    {
                        GameRunnable countdownGame = taskMap.get( TaskId.START_COUNTDOWN );
                        if ( countdownGame != null )
                        {
                            message( Lang.NECESSARY_PLAYERS, null );
                            countdownGame.cancel();
                            state = GameState.LOBBY;
                            updateScoreboard();
                        }
                    }
                }
                Team team = this.getTeam( gamePlayer.getUniqueId() );
                if ( team != null ) team.remove( gamePlayer.getUniqueId() );
                break;
            
            case GAME:
                if ( !this.hasTeam( gamePlayer.getUniqueId() ) ) return;
                int red = this.red.getSizeOnline(), blue = this.blue.getSizeOnline();
                if ( red == 0 && blue >= 1 ) this.finishArena( this.blue );
                else if(blue == 0 && red >= 1) this.finishArena(this.red);
                break;    
            
            default:
                break;

        }
        InventorySession iSession = gamePlayer.getInventorySession();
        if ( iSession != null )
        {
            iSession.load();
            iSession.clear();
        }
        if ( gameSign != null )
            gameSign.update();
        for ( PotionEffect effect : player.getActivePotionEffects() )
            player.removePotionEffect( effect.getType() );
    }

    public void startArena() 
    {
        this.state = GameState.STARTING;
        GameRunnable gameRunnable = taskMap.get( TaskId.START_COUNTDOWN );
        if ( gameRunnable != null )
        {
            gameRunnable.startTimer( false );
        } else newRunnable( new GameCountdown( this, TaskId.START_COUNTDOWN, count )
        {

            @Override
            public void doStuff()
            {
                //STATE
                state = GameState.GAME;
                //TEAMS
                red.startGame();
                blue.startGame();
                //GAME_TASK
                startTaskGame();
            }

            @Override
            public void broadcast()
            {
                message( Lang.GAME_START,
                    Placeholder.builder(
                        pair( COUNT ,  this.getSeconds() ),
                        pair( SECONDS , this.getSeconds() ) ) );
            }

            @Override
            public void doPerSecond()
            {
                updateScoreboard();
            }

        } );
        if ( gameSign != null )
            gameSign.update();
    }

    private void startTaskGame() {
        newRunnable( new GameTask( this, TaskId.GAME_TASK, 20 ) 
        {

            public void doCancelStuff() {};

            @Override
            public boolean cancelTask() 
            {
                return isState( GameState.FINISH );
            }

            @Override
            public void doStuff() 
            {
                //GENERATORS
                generators();
                //SCOREBOARD
                updateScoreboard();
            }
        } );
        newRunnable( new GameTask( this, TaskId.POOLS_TASK, 10 ) 
        {

            @Override
            public boolean cancelTask()
            {
                return isState( GameState.FINISH );
            }

            @Override
            public void doCancelStuff()
            {}

            @Override
            public void doStuff()
            {
                pools();
            }
            
        } );
        if ( gameSign != null )
            gameSign.update();
    }


    private byte generators = 0;
    private void generators()
    {
        //GENERATORS
        if ( generators >= 3 ) 
        {
            ironGenerator.dropItem( Material.IRON_INGOT );
            expGenerator.dropItem( NMSVersion.isNewerVersion ? Material.EXPERIENCE_BOTTLE : Material.valueOf( "EXP_BOTTLE" ) );
            generators = 0;
        }
        ++generators;
    }

    private void pools()
    {
        Iterator<GamePlayer> players = this.players.iterator();
        while( players.hasNext() )
        {
            GamePlayer gPlayer = players.next();
            Team enemyTeam = this.getEnemyTeam( gPlayer.getUniqueId() );
            if ( enemyTeam != null )  
            {
                org.bukkit.Location bLocation = gPlayer.getLocation(); 
                if ( enemyTeam.getPool().isIn( bLocation ) )
                {   
                    Team team = this.getTeam( gPlayer.getUniqueId() );
                    team.addPoint( gPlayer.toBukkitPlayer() );
                }
            }
        }
    }

    public void finishArena( Team winnerTeam ) 
    {
        this.state = GameState.FINISH;
        GameRunnable gRunnable = this.taskMap.get( TaskId.GAME_TASK );
        if ( gRunnable != null )
            gRunnable.cancel();
        message( Lang.WIN_FOR_TEAM, 
            builder( 
                pair( COLOR_TEAM , winnerTeam.getChatColor() ),
                pair( TEAM_NAME, winnerTeam.getConfigName() ) ) );
        gRunnable = taskMap.get( TaskId.FINISH_TASK );
        if ( gRunnable != null )
            gRunnable.startTimer( false );
        else 
        {
            newRunnable( new GameTask( this, TaskId.FINISH_TASK, 40 ) 
            {

                public void doCancelStuff() 
                {
                    teleportPlayers( winnerTeam );
                    reloadArena();
                };

                final AtomicInteger counter = new AtomicInteger( 0 );
                @Override
                public boolean cancelTask() 
                {
                    return counter.get() >= 5;
                }
    
                @Override
                public void doStuff() 
                {
                    winnerTeam.launchFireworks();
                    counter.incrementAndGet();
                }
            } );
        }
        if ( gameSign != null )
            gameSign.update();  
    }

    private void teleportPlayers( Team winnerTeam )
    {
        players.forEach( gamePlayer ->
        {
            leave( gamePlayer, false );
            Player player = gamePlayer.toBukkitPlayer();
            if ( GameMode.isMultiArena() )
            {
                Location location = StaticConfiguration.spawn_location;
                if ( location != null )
                {
                    location.teleport( player );
                }
            } else player.kickPlayer( getWinnerMessage( winnerTeam ) );
        });
    }

    public void reloadArena() 
    {
        this.state = GameState.RELOADING;
        this.generators = 0;
        this.red.clear();
        this.blue.clear();
        this.players.clear();
        this.taskMap.clear();
        this.gameWorld.unload( false );
        this.gameWorld.load();
        GameCountdown gameCountdown = ( GameCountdown ) taskMap.get( TaskId.START_COUNTDOWN );
        if ( gameCountdown != null )
            gameCountdown.setSeconds( this.count );
        this.state = GameState.LOBBY;
        if ( gameSign != null )
            gameSign.update();
    }
    
    public void death( PlayerDeathEvent event )
    {
        event.setDeathMessage( null );
        Player player = event.getEntity();
        if ( !player.isDead() ) return;
        Player killer = player.getKiller();
        if ( killer != null )
        {
            if ( player.getLastDamageCause().getCause() == DamageCause.PROJECTILE )
            {
                message( Lang.DEATH_BY_PROJECTILE ,  builder(
                    pair( PLAYER_NAME, player, getChatColor( player ) ),
                    pair( KILLER_NAME,  killer, getChatColor( killer ) ), 
                    pair( DISTANCE, player ) ) );
            }
            else 
                message( Lang.DEATH_BY_PLAYER,  builder(
                    pair( PLAYER_NAME, player, getChatColor( player ) ),
                    pair( KILLER_NAME,  killer, getChatColor( killer ) )
                ) );
        }
        else
        {
            message( Lang.DEATH_BY_UNKNOWN, builder(
                    pair( PLAYER_NAME , player, getChatColor( player ) )
            ) );
        }
        if ( StaticConfiguration.drop_armor )
            for ( ItemStack itemStack : event.getDrops() )
                if ( ItemUtils.isArmorLeather( itemStack.getType() ) )
                    itemStack.setType( Material.AIR );
        if ( StaticConfiguration.instant_respawn )
            NMSManager.get().getNMS().respawn ( player, AmazingTowers.getInstance() );   
    }

    public void respawn( PlayerRespawnEvent event )
    {
        Player player = event.getPlayer();
        Team team = getTeam( player.getUniqueId() );
        if ( team != null )
        {
            this.getKit().sendKit( player, team.getColor() );
            event.setRespawnLocation( team.getSpawn().toBukkitLocation() );
        }
        else 
            event.setRespawnLocation( lobby.toBukkitLocation() );   
    }

    public void chat( AsyncPlayerChatEvent event )
    {
        event.setCancelled( true );
        Player player = event.getPlayer();
        String eventMsg = event.getMessage();
        AtomicReference<String> message = new AtomicReference<String>( "" ); 
        if ( state == GameState.LOBBY || state == GameState.STARTING || !hasTeam( player.getUniqueId() ) )
        {
            message.set( StringUtils.replacePlaceholders( StaticConfiguration.normal_format, 
                builder(
                    pair( PREFIX, player ),
                    pair( PLAYER_NAME, player ),
                    pair( MESSAGE, eventMsg )
                ) ) );
            players.forEach( gPlayer -> gPlayer.sendMessage( message.get() ) );    
            return;    
        }   
        Team team = getTeam( player.getUniqueId() );
        message.set( StringUtils.replacePlaceholders( StaticConfiguration.team_format, 
            builder( pair( PREFIX, player ),
                pair( PLAYER_NAME, player ),
                pair( MESSAGE, eventMsg ),
                pair( COLOR_TEAM, getChatColor( player ) ),
                pair( TEAM_NAME, team.getConfigName() )
            )
        ) );
        team.getOnlinePlayers().forEach( pl -> pl.sendMessage( message.get() ) );
    }

    private String getWinnerMessage( Team winnerTeam )
    {
        StringBuilder builder = new StringBuilder();
        String[] text = Lang.WIN_FOR_TEAM.get();
        boolean previousEmpty = false;
        for ( String t : text )
        {
            if ( t.isEmpty() )
            {
                if ( !previousEmpty ) builder.append( "\n" );
                previousEmpty = true;
            } else
            {
                builder.append( StringUtils.replacePlaceholders( t,
                        builder( pair( COLOR_TEAM, winnerTeam.getChatColor() ),
                                pair( TEAM_NAME, winnerTeam.getConfigName() ) ) ) );
                builder.append( "\n" );
                previousEmpty = false;
            }
        }
        return builder.toString();
    }

    private void setItems( Player player )
    {
        Inventory inventory = player.getInventory();
        ItemStack redItem = ItemUtils.redItem.clone(),
            blueItem = ItemUtils.blueItem.clone();
        inventory.setItem( StaticConfiguration.red_position, redItem );
        inventory.setItem( StaticConfiguration.blue_position, blueItem );
        inventory.setItem( StaticConfiguration.quit_position, ItemUtils.quitItem );
        ItemStack[] items = {redItem, blueItem};
        MetadataUtils.set( player, "items-game", items );
    }

    public void updateScoreboard()
    {
        gameScoreboard.update( this.state );
    }

    private boolean isStarting()
    {
        return state == GameState.STARTING;
    }

    private void newRunnable( GameRunnable gameRunnable )
    {
        this.taskMap.put( gameRunnable.getTaskID() , gameRunnable );
    }

    public int getRealSeconds()
    {
        GameCountdown gameRunnable = ( GameCountdown ) this.taskMap.get( TaskId.START_COUNTDOWN );
        return gameRunnable.getSeconds();
    }

    private ChatColor getChatColor( Player player )
    {
        return this.getTeam( player.getUniqueId() ).getChatColor();
    }

    public Team getTeam( UUID id ) 
    {
        if ( red.isInTeam( id ) ) return red;
        else if ( blue.isInTeam( id ) ) return blue;
        return null;
    }

    public Team getTeam( ItemStack itemStack )
    {
        if ( ItemUtils.isBlueItem( itemStack ) )
            return blue;
        else if ( ItemUtils.isRedItem( itemStack ) )
            return red;
        return null;
    }

    public Team getEnemyTeam( UUID id )
    {
        if ( !this.hasTeam( id  ) ) return null;
        if ( red.isInTeam( id ) ) return blue;
        return red;
    }

    public Team getEnemyTeam( Team team )
    {
        if ( team == null ) return null;
        if ( red.equals( team ) ) return blue;
        return red;
    }

    public boolean hasTeam( UUID id )
    {
        return red.isInTeam( id ) || blue.isInTeam( id );
    }

    public void message( Lang lang, Map<String, String> placeholderMap )
    {
        lang.sendLang( players, placeholderMap );
    }

    public boolean isState( GameState state )
    {
        return this.state == state;
    }

    public boolean isLobby()
    {
        return this.state == GameState.LOBBY || this.state == GameState.STARTING;
    }

    public String getGameName()
    {
        return gameName;
    }

    public Scoreboard getScoreboard()
    {
        return this.scoreboard;
    }

    public Team getRed()
    {
        return this.red;
    }

    public Team getBlue()
    {
        return this.blue;
    }
    
    public Kit getKit()
    {
        return kit;
    }

    public Set<GamePlayer> getPlayers()
    {
        return players;
    }

    public Location getSpawn()
    {
        return lobby;
    }

    public void setSpawnLocation( Location lobby )
    {
        this.lobby = lobby;
    }

    public Location getIronGenerator()
    {
        return ironGenerator;
    }

    public void setIronGenerator( Location ironGenerator )
    {
        this.ironGenerator = ironGenerator;
    }

    public Location getExperienceGenerator()
    {
        return expGenerator;
    }

    public void setExperienceGenerator( Location expGenerator )
    {
        this.expGenerator = expGenerator;
    }

    public void setKit( Kit abstractKit )
    {
        this.kit = abstractKit;
    }

    public void setMinPlayers( int minPlayers )
    {
        this.minPlayers = minPlayers;
    }
    
    public void setMaxPlayers( int maxPlayers )
    {
        this.maxPlayers = maxPlayers;
    }
    
    public void setCount( int count )
    {
        this.count = count;
    }
    
    public void setMaxPoints( int maxPoints )
    {
        this.maxPoints = maxPoints;
    }
    
    public int getMaxPoints()
    {
        return maxPoints;
    }

    public int getCount()
    {
        return count;
    }

    public GameWorld getGameWorld()
    {
        return gameWorld;
    }

    public GameState getState()
    {
        return state;
    }

    public int getOnlinePlayers()
    {
        return players.size();
    }
    
    public int getMaxPlayers()
    {
        return maxPlayers;
    }
    
    public void setGameWorld( GameWorld gameWorld )
    {
        this.gameWorld = gameWorld;
    }

    public void setGameSign( GameSign gameSign )
    {
        this.gameSign = gameSign;
    }

    public GameSign getGameSign()
    {
        return gameSign;
    }
    
    public File getFolder()
    {
        return folder;
    }

    public static Game newInstance( String name )
    {
        return new Game( name );
    }


}
