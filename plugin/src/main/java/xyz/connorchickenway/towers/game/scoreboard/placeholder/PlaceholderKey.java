package xyz.connorchickenway.towers.game.scoreboard.placeholder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.utilities.StringUtils;

public enum PlaceholderKey 
{
    
    DATE( gPlayer  -> AmazingTowers.getInstance().getScoreboardManager().getDate() ), 
    ONLINE_PLAYERS( gPlayer -> gPlayer.getGame().getOnlinePlayers() ), 
    MAX_PLAYERS( gPlayer -> gPlayer.getGame().getMaxPlayers() ), 
    MAP( gPlayer -> gPlayer.getGame().getGameName() ), 
    SECONDS( gPlayer -> gPlayer.getGame().getRealSeconds() ),
    POINTS_BLUE( gPlayer -> gPlayer.getGame().getBlue().getPoints() ),
    POINTS_RED( gPlayer -> gPlayer.getGame().getRed().getPoints() ),
    MAX_POINTS( gPlayer -> gPlayer.getGame().getMaxPoints() ),
    PLAYER_KILLS( GamePlayer::getKills );

    private Function<GamePlayer, Object> function;

    private PlaceholderKey( Function<GamePlayer, Object> function )
    {
        this.function = function;
    }

    public String toPlaceholder()
    {
        return "%" + name().toLowerCase() + "%";
    }

    public static List<PlaceholderKey> search( String text )
    {
        if ( text.isEmpty() )
            return null;
        List<PlaceholderKey> l = new ArrayList<>();
        Matcher m = StringUtils.PLACEHOLDER_PATTERN.matcher( text );
        while( m.find() )
        {
            String key = m.group().replaceAll( "%" , "" );
            PlaceholderKey pKey = StringUtils.searchEnum( PlaceholderKey.class , key );
            if ( pKey != null )
                l.add( pKey );
        }
        return l.size() > 0 ? l : null;
    }

    public String perform( GamePlayer obj )
    {
        return String.valueOf( function.apply( obj ) );
    }

}