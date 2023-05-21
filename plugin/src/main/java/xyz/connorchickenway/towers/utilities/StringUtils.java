package xyz.connorchickenway.towers.utilities;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import xyz.connorchickenway.towers.game.lang.placeholder.Placeholder;

public class StringUtils
{

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile( "[%]([^%]+)[%]" ), 
            CENTER_PATTERN = Pattern.compile( "[{](center)[}]" );

    public static String color( String text )
    {
        return ChatColor.translateAlternateColorCodes( '&', text );
    }

    public static String replacePlaceholders( String text, Map<String, Placeholder<?>> placeholders )
    {
        StringBuilder builder = new StringBuilder();
        String t = text;
        if ( !t.isEmpty() )
        {
            t = ChatColor.translateAlternateColorCodes( '&', t );
            if ( placeholders != null && !placeholders.isEmpty() )
            {
                Matcher m = PLACEHOLDER_PATTERN.matcher( t );
                while( m.find() )
                {
                    Placeholder<?> placeholder = placeholders.get( m.group() );
                    if ( placeholder == null ) continue;
                    t = t.replace( Pattern.quote( placeholder.getKey() ),
                            Matcher.quoteReplacement( placeholder.getValue() ) );
                }
            }
        }
        builder.append( t );
        return builder.toString();
    }

    //https://stackoverflow.com/a/28333189
    public static <T extends Enum<?>> T searchEnum( Class<T> enumeration,
        String search ) 
        {
        for ( T each : enumeration.getEnumConstants() ) 
            if ( each.name().compareToIgnoreCase(search) == 0 )
                return each;
    
        return null;
    }

}
