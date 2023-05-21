package xyz.connorchickenway.towers.game.scoreboard.placeholder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import xyz.connorchickenway.towers.utilities.StringUtils;

public enum PlaceholderKey 
{
    
    DATE, 
    ONLINE_PLAYERS, 
    MAX_PLAYERS, 
    MAP, 
    SECONDS,
    POINTS_BLUE,
    POINTS_RED,
    MAX_POINTS;

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

}
