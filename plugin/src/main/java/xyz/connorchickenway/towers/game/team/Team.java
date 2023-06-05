package xyz.connorchickenway.towers.game.team;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.lang.Lang;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.utilities.Cuboid;
import xyz.connorchickenway.towers.utilities.StringUtils;
import xyz.connorchickenway.towers.utilities.location.Location;

import static xyz.connorchickenway.towers.game.lang.placeholder.Placeholder.*;

public class Team
{

    private final Set<UUID> players = Sets.newHashSet();
    private final org.bukkit.scoreboard.Team team;
    private final Game game;
    private final String teamName;
    private final ChatColor chatColor;
    private final Color color;
    private int points;
    private Location spawn;
    private Cuboid pool;
    private String configName;

    public Team( Game game, String teamName, ChatColor chatColor, Color color, String configName )
    {
        this.team = game.getScoreboard().registerNewTeam( teamName );
        this.team.setPrefix( chatColor + "[" + configName + "]" );
        this.team.setAllowFriendlyFire( false );
        this.team.setCanSeeFriendlyInvisibles( true );
        this.team.setColor( chatColor );
        this.game = game;
        this.teamName = teamName;
        this.chatColor = chatColor;
        this.color = color;
        this.points = 0;
        this.configName = configName;
    }

    public void addPoint( Player player )
    {
        if ( !players.contains( player.getUniqueId() ) || player.getHealth() == 0 ) return;
        ++points;
        spawn.teleport( player );
        game.message( Lang.getLang( "point_scored" ),
                builder( 
                    pair( PLAYER_NAME , player, chatColor ),
                    pair( COLOR_TEAM, chatColor ),
                    pair( TEAM_NAME , teamName ) ) );
        if ( points >= 10 ) game.finishArena( this );
    }

    public void addPlayer( Player player, int enemyTeam )
    {
        if ( players.contains( player.getUniqueId() ) )
        {
            Lang.ALREADY_TEAM.sendLang( player, null );
            return;
        }
        if ( players.size() > enemyTeam )
        {
            Lang.UNBALANCED_TEAM.sendLang( player, null );
            return;
        }
        players.add( player.getUniqueId() );
        Lang.JOIN_TEAM.sendLang( player, 
            builder( 
                pair( TEAM_NAME, configName ),
                pair( COLOR_TEAM, chatColor )
             ) );
        if ( game.isState( GameState.GAME ) ) doStuff( player );
    }

    public void launchFireworks()
    {
        this.getOnlinePlayers().forEach( this::firework );
    }

    private void firework( Player player )
    {
        final Firework f = player.getLocation().getWorld().spawn( player.getLocation(), ( Firework.class ) );
        f.detonate();
        final FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect( FireworkEffect.builder().flicker( false ).trail( true ).with( FireworkEffect.Type.BALL ).withColor( color ).withFade( color ).build() );
        fm.setPower( 1 );
        f.setFireworkMeta( fm );
    }

    public void message( Player player, String message )
    {
        String format = StringUtils.replacePlaceholders( StaticConfiguration.team_format, 
            builder( 
                pair( COLOR_TEAM , chatColor ), 
                pair( TEAM_NAME , configName ), 
                pair( MESSAGE , message ) ) );
        this.getOnlinePlayers().forEach( online -> online.sendMessage( format ) );
    }

    public void startGame()
    {
        this.getOnlinePlayers().forEach( ( player ) -> 
        {
            player.setGameMode( GameMode.SURVIVAL );
            doStuff( player );
            this.team.addEntry( player.getName() );
        } );
    }

    public void doStuff( Player player )
    {
        game.getKit().sendKit( player, color );
        this.spawn.teleport( player );
    }

    public void clear()
    {
        this.points = 0;
        this.players.clear();
        this.team.getEntries().forEach( this.team::removeEntry );
    }

    public void remove( UUID id )
    {
        players.remove( id );
    }

    public boolean isInTeam( UUID id )
    {
        return players.contains( id );
    }

    public Set<Player> getOnlinePlayers()
    {
        ImmutableSet.Builder<Player> builder = ImmutableSet.builder();
        Iterator<UUID> iterator = players.iterator();
        while( iterator.hasNext() )
        {
            UUID id = iterator.next();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( id );
            if ( offlinePlayer.isOnline() ) builder.add( (Player) offlinePlayer );
        }
        return builder.build();
    }

    public String getTeamName()
    {
        return teamName;
    }

    public int getSize()
    {
        return players.size();
    }

    public int getSizeOnline()
    {
        int i = 0;
        Iterator<UUID> iterator = players.iterator();
        while( iterator.hasNext() )
        {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( iterator.next() );
            if ( offlinePlayer.isOnline() ) ++i;
        }
        return i;
    }

    public int getPoints()
    {
        return points;
    }

    public Location getSpawn()
    {
        return spawn;
    }

    public ChatColor getChatColor()
    {
        return chatColor;
    }

    public Cuboid getPool()
    {
        return pool;
    }

    public void setPool( Cuboid cuboid )
    {
        this.pool = cuboid;
    }

    public void setConfigName( String configName )
    {
        this.configName = configName;
    }
    
    public String getConfigName()
    {
        return configName;
    }

    public void setSpawnLocation( Location spawn )
    {
        this.spawn = spawn;
    }
    
    public Color getColor()
    {
        return color;
    }

}
