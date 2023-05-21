package xyz.connorchickenway.towers.game.lang.placeholder;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KillerNamePlaceholder extends Placeholder<Player>
{

    private final ChatColor color;

    private KillerNamePlaceholder( Player obj, ChatColor color )
    {
        super( "killer_name", obj );
        this.color = color;
    }

    @Override
    public String getValue()
    {
        Player killer = obj.getKiller();
        return color + killer.getName();
    }

    public static KillerNamePlaceholder newInstance( Player player, ChatColor color )
    {
        return new KillerNamePlaceholder( player, color );
    }
    
}
