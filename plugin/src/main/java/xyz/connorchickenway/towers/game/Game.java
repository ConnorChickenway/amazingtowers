package xyz.connorchickenway.towers.game;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.entity.inventory.InventorySession;
import xyz.connorchickenway.towers.game.kit.AbstractKit;
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
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.ItemUtils;
import xyz.connorchickenway.towers.utilities.StringUtils;
import xyz.connorchickenway.towers.utilities.location.Location;

import static xyz.connorchickenway.towers.game.lang.placeholder.Placeholder.*;

public class Game
{

    private final String gameName;
    private final Set<GamePlayer> players;
    private final Map<TaskId, GameRunnable> taskMap;
    private final Scoreboard scoreboard;
    private final GameWorld gameWorld;
    private AbstractKit kit;
    private GameState state;
    private Team red, blue;
    private Location lobby, ironGenerator, expGenerator;
    private int minPlayers, maxPlayers, count, maxPoints;
    private GameSign gameSign;
    private GameScoreboard gameScoreboard;

    private Game( String gameName )
    {
        this.gameName = gameName;
        this.players = Sets.newConcurrentHashSet();
        this.taskMap = Maps.newHashMap();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.gameWorld = new GameWorld( gameName );
        this.state = GameState.LOBBY;
        this.red = new Team( this, "red", ChatColor.RED, Color.RED, StaticConfiguration.red_name );
        this.blue = new Team( this, "blue", ChatColor.BLUE, Color.BLUE, StaticConfiguration.blue_name );
        this.gameScoreboard = new GameScoreboard();
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
        message( Lang.JOIN_ARENA,
                builder( pair( PLAYER_NAME, player.getName() ),
                        pair( ONLINE_PLAYERS , players.size() ),
                        pair( MAX_PLAYERS, maxPlayers ) ) );
        InventorySession iSession = gamePlayer.getInventorySession();
        if ( iSession != null )
            iSession.save();
        player.getInventory().clear();    
        boolean hasTeam = this.hasTeam( player.getUniqueId() );
        switch( state )
        {
            
            case LOBBY: 
                updateScoreboard();
                if ( players.size() >= maxPlayers )
                    startArena();   
                break;
            case FINISH:
                player.setGameMode( org.bukkit.GameMode.SPECTATOR );
                break;        
            
            default:
                if ( hasTeam )
                {    
                    player.setGameMode( org.bukkit.GameMode.SURVIVAL );
                    this.getTeam( player.getUniqueId() ).doStuff( player );
                } else
                {
                    player.setGameMode( org.bukkit.GameMode.ADVENTURE );
                    lobby.teleport( player );
                }
                break;

        }
        if ( gameSign != null )
            gameSign.update();
    } 

    public void leave( GamePlayer gamePlayer, boolean leaveMessage )
    {
        players.remove( gamePlayer );
        gamePlayer.setGame( null );
        gamePlayer.toBukkitPlayer().setScoreboard( null );
        gameScoreboard.remove( gamePlayer );
        if ( leaveMessage )
            message( Lang.LEAVE_ARENA, builder( 
                pair( PLAYER_NAME, gamePlayer.toBukkitPlayer(), null ),
                pair( ONLINE_PLAYERS, players.size() ) ,
                pair( MAX_PLAYERS, maxPlayers ) ) );
        switch( state )
        {
            
            case STARTING: case LOBBY:
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
    }

    public void startArena() 
    {
        this.state = GameState.STARTING;
        GameRunnable gameRunnable = taskMap.get( TaskId.START_COUNTDOWN );
        if ( gameRunnable != null )
        {
            gameRunnable.startTimer();
        } else 
        newRunnable( new GameCountdown( this, TaskId.START_COUNTDOWN, count ) 
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
                //POOLS
                pools();
                //GENERATORS
                generators();
                //SCOREBOARD
                updateScoreboard();
            }
        } );
        if ( gameSign != null )
            gameSign.update();
    }


    private byte generators = 0;
    private void generators()
    {
        //GENERATORS
        if ( generators >= 2 ) 
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
                    this.getTeam( gPlayer.getUniqueId() ).addPoint( gPlayer.toBukkitPlayer() );
            }
        }
    }

    public void finishArena( Team team ) 
    {
        this.state = GameState.FINISH;
        GameRunnable gRunnable = this.taskMap.get( TaskId.GAME_TASK );
        if ( gRunnable != null )
            gRunnable.cancel();
        message( Lang.WIN_FOR_TEAM, 
            builder( 
                pair( COLOR_TEAM , team.getChatColor() ),
                pair( TEAM_NAME, team.getConfigName() ) ) );
        gRunnable = taskMap.get( TaskId.FINISH_TASK );
        if ( gRunnable != null )
            gRunnable.startTimer();
        else 
        {
            newRunnable( new GameTask( this, TaskId.FINISH_TASK, 40 ) 
            {

                public void doCancelStuff() 
                {
                    this.cancel();
                    teleportPlayers();
                    reloadArena();
                };

                AtomicInteger counter = new AtomicInteger( 0 ); 
                @Override
                public boolean cancelTask() 
                {
                    return counter.get() >= 5;
                }
    
                @Override
                public void doStuff() 
                {
                    team.launchFireworks();
                    counter.incrementAndGet();
                }
            } );
        }
        if ( gameSign != null )
            gameSign.update();  
    }

    private void teleportPlayers()
    {
        players.forEach( gPlayer -> 
        {
            if ( GameMode.isMultiArena() )
            {
                InventorySession inventorySession = gPlayer.getInventorySession();
                inventorySession.load();
                inventorySession.clear();
                return;
            }
            
        } );
        
    }

    public void reloadArena() 
    {
        this.state = GameState.RELOADING;
        this.generators = 0;
        this.red.clear();
        this.blue.clear();
        this.players.clear();
        this.taskMap.clear();
        this.gameWorld.unload();
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
                    pair( PLAYER_NAME , player, getChatColor( player ) ),
                    pair( KILLER_NAME ,  killer, getChatColor( killer ) ), 
                    pair( DISTANCE , player ) ) );
            }
            else 
                message( Lang.DEATH_BY_PLAYER ,  builder(
                    pair( PLAYER_NAME , player, getChatColor( player ) ),
                    pair( KILLER_NAME ,  killer, getChatColor( killer ) )
                ) );

            return;
        }
        message( Lang.DEATH_BY_UNKNOWN, builder(  
                pair( PLAYER_NAME , player, getChatColor( player ) )   
            ) );
        if ( StaticConfiguration.drop_armor )
            for ( ItemStack itemStack : event.getDrops() )
                if ( ItemUtils.isArmorLeather( itemStack.getType() ) )
                    itemStack.setType( Material.AIR );
        if ( StaticConfiguration.instant_respawn )
            player.spigot().respawn();      
    }

    public void chat( AsyncPlayerChatEvent event )
    {
        event.setCancelled( true );
        Player player = event.getPlayer();
        final String playerName = player.getName(),
            eventMsg = event.getMessage();
        AtomicReference<String> message = new AtomicReference<String>( "" ); 
        if ( state == GameState.LOBBY || state == GameState.STARTING || !hasTeam( player.getUniqueId() ) )
        {
            message.set( StringUtils.replacePlaceholders( StaticConfiguration.normal_format, 
                builder( 
                    pair( PLAYER_NAME, playerName ),
                    pair( MESSAGE, eventMsg )
                ) ) );
            players.forEach( gPlayer -> gPlayer.sendMessage( message.get() ) );    
            return;    
        }   
        Team team = getTeam( player.getUniqueId() );
        message.set( StringUtils.replacePlaceholders( StaticConfiguration.team_format, 
            builder( 
                pair( PLAYER_NAME, playerName ),
                pair( MESSAGE, eventMsg ),
                pair( COLOR_TEAM, getChatColor( player ) ),
                pair( TEAM_NAME, team.getConfigName() )
            )
        ) );
        team.getOnlinePlayers().forEach( pl -> pl.sendMessage( message.get() ) );
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

    public Team getEnemyTeam( UUID id )
    {
        if ( !this.hasTeam( id  ) ) return null;
        if ( red.isInTeam( id ) ) return blue;
        else return red;
    }

    public boolean hasTeam( UUID id )
    {
        if ( red.isInTeam( id ) || 
                blue.isInTeam( id ) ) return true;
        return false;
    }

    public void message( Lang lang, Map<String, String> placeholderMap )
    {
        lang.sendLang( players, placeholderMap );
    }

    public boolean isState( GameState state )
    {
        return this.state == state;
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
    
    public AbstractKit getKit()
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

    public void setKit( AbstractKit abstractKit )
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
    
    public void setGameSign( GameSign gameSign )
    {
        this.gameSign = gameSign;
    }

    public GameSign getGameSign()
    {
        return gameSign;
    }

    public static Game newInstance( String name )
    {
        return new Game( name );
    }


}
