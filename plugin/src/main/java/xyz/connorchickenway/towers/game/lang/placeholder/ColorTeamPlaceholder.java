package xyz.connorchickenway.towers.game.lang.placeholder;

import org.bukkit.ChatColor;

public class ColorTeamPlaceholder extends Placeholder<ChatColor>
{

    private ColorTeamPlaceholder( ChatColor obj )
    {
        super( "color_team", obj );
    }

    @Override
    public String getValue()
    {
        return obj.toString();
    }

    public static ColorTeamPlaceholder newInstance( ChatColor color )
    {
        return new ColorTeamPlaceholder( color );
    }
    
}
