package xyz.connorchickenway.towers.game.lang;

import com.google.common.collect.Lists;

import xyz.connorchickenway.towers.config.ConfigurationManager.ConfigName;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.utilities.StringUtils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public enum Lang
{

    JOIN_ARENA( "join_arena", "&a%player_name% &7joined the game (&a%online_players%&7/&a%max_players%&7)" ),
    LEAVE_ARENA( "leave_arena", "&c%player_name% &7leave the game (&c%online_players%&7/&c%max_players%&7)" ),
    ARENA_FULL( "arena_full", "&cThe arena is full!" ), ARENA_FINISH( "arena_finish", "&cThe arena is finished!" ),
    NECESSARY_PLAYERS( "necessary_players",
            "&aThe countdown has been cancelled, because the minimum number of players is lower!" ),
    GAME_START( "game_start", "&eThe game starts in &c%count% &e%seconds%!" ), SECONDS( "seconds", "second::seconds" ),
    POINT_FOR_TEAM( "point_scored", "", "{center}%color_team%&l♦  %color_team%%player_name% &7scored point for %color_team%&l%team_name%!  %color_team%&l♦", "" ),
    WIN_FOR_TEAM( "win", "", "{center}%color_team%&l%team_name% Team has won the game!", "" ),
    DEATH_BY_PLAYER( "death.by_player", "%player_name% &7was slain by %killer_name%" ),
    DEATH_BY_PROJECTILE( "death.by_projectile",
            "%player_name% &7was shot by %killer_name% &7from &e%distance% blocks" ),
    DEATH_BY_UNKNOWN( "death.by_unknown", "%player_name% &7died." ),
    UNBALANCED_TEAM( "unbalanced_team", "&cYou cannot join this team because it's unbalanced" ),
    ALREADY_TEAM( "already_team", "&cYou're already on that team" ),
    JOIN_TEAM( "join_team", "&7You joined %color_team%&l%team_name% TEAM!" );

    private String path;
    private String[] def;

    Lang( String path, String... def )
    {
        this.path = path;
        this.def = def;
    }

    public void sendLang( Player player )
    {
        this.sendLang( player, null );
    }

    public void sendLang( Player player, Map<String, String> placeholders )
    {
        String[] text = this.getMessage( placeholders );
        player.sendMessage( text );
    }

    public void sendLang( Collection<GamePlayer> collection )
    {
        this.sendLang( collection, null );
    }

    public void sendLang( Collection<GamePlayer> collection, Map<String, String> placeholders )
    {
        String[] text = this.getMessage( placeholders );
        collection.forEach( player -> player.sendMessage( text ) );
    }

    private String[] getMessage( Map<String, String> placeholders )
    {
        String[] text = this.get();
        String[] newText = new String[text.length];
        for ( int x = 0; x < text.length; x++ )
        {
            newText[ x ] = StringUtils.replacePlaceholders( text[ x ], placeholders );
        }
        return newText;
    }

    public String getPath()
    {
        return path;
    }

    public String[] getDef()
    {
        return def;
    }

    public String[] get()
    {
        return LANG.containsKey( toString() ) ? LANG.get( toString() ) : getDef();
    }

    private static final Map<String, String[]> LANG = new HashMap<>();

    public static void loadMessages()
    {
        FileConfiguration configuration = ConfigName.LANG.getConfiguration().getFileConfiguration();
        for ( Lang value : Lang.values() )
        {
            String path = value.getPath();
            List<String> list = configuration.isList( path ) ? configuration.getStringList( path )
                    : Lists.newArrayList();       
            if ( list.isEmpty() )
            {
                String k = configuration.getString( path );
                if ( k == null ) continue;
                list.add( k );
            }
            LANG.put( value.toString(), list.toArray( new String[ list.size() ] ) );
        }
    }

    public static Lang getLang( String path )
    {
        return Arrays.asList( Lang.values() ).stream().filter( lang -> lang.getPath().equals( path ) ).findFirst()
                .orElse( null );
    }

}
