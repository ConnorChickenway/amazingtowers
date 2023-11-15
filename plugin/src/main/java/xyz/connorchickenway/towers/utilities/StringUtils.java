package xyz.connorchickenway.towers.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import xyz.connorchickenway.towers.game.lang.utils.MessageUtils;

public class StringUtils
{

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile( "[%]([^%]+)[%]" ), 
            CENTER_PATTERN = Pattern.compile( "[{](center)[}]" );

    public static final String DEFAULT_WORLD_NAME;
    static 
    {
        Properties props = new Properties();
        try
        {
            props.load( new FileInputStream( "server.properties" ) );
        } catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        DEFAULT_WORLD_NAME = props.getProperty( "level-name" );
    }

    public static String color( String text )
    {
        return ChatColor.translateAlternateColorCodes( '&', text );
    }
    
    public static String replacePlaceholders( String text, Map<String, String> placeholders )
    {
        StringBuilder builder = new StringBuilder();
        String t = text;
        if ( !t.isEmpty() )
        {
            t = color( text );
            if ( placeholders != null && !placeholders.isEmpty() )
            {
                Matcher m = PLACEHOLDER_PATTERN.matcher( t );
                while( m.find() )
                {
                    String value = placeholders.get( m.group() );
                    if ( value == null ) continue;
                    t = t.replaceAll( Pattern.quote( m.group() ),
                            Matcher.quoteReplacement( value ) );
                }
            }
            Matcher center = StringUtils.CENTER_PATTERN.matcher( t );
            if ( center.find() ) 
                t = MessageUtils.convert( t );
        }
        builder.append( t );
        return builder.toString();
    }

    /**public static String capitalize( String input )
    {
        return input.substring( 0, 1 ).toUpperCase() + input.substring( 1 ).toLowerCase();
    }**/

    //https://stackoverflow.com/a/28333189
    public static <T extends Enum<?>> T searchEnum( Class<T> enumeration,
        String search ) 
        {
        for ( T each : enumeration.getEnumConstants() ) 
            if ( each.name().compareToIgnoreCase( search ) == 0 )
                return each;
    
        return null;
    }

    public static boolean isBlank( String str )
    {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }

}
