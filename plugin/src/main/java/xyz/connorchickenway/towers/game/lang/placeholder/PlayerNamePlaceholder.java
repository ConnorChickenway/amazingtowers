package xyz.connorchickenway.towers.game.lang.placeholder;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerNamePlaceholder extends Placeholder<Player>
{

    private final ChatColor color;

    private PlayerNamePlaceholder( Player obj, ChatColor color )
    {
        super( "player_name", obj );
        this.color = color;
    }

    @Override
    public String getValue()
    {
        return ( color != null ? color : "" ) + obj.getName();
    }
    
    public static PlayerNamePlaceholder newInstance( Player obj, ChatColor color )
    {
        return new PlayerNamePlaceholder( obj, color );
    }

}
