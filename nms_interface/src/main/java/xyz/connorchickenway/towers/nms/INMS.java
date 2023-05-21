package xyz.connorchickenway.towers.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface INMS 
{
    
    void respawnPlayer( Player player ); 

    void sendTitle( Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut );
 
    default void respawn( Player player, Plugin plugin ) 
    {
        Bukkit.getServer().getScheduler().runTaskLater( plugin , () -> 
        {
            respawnPlayer( player );
        }, 2 );
    }

}
