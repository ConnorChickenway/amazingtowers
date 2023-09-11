package xyz.connorchickenway.towers.game.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;

public interface GameWorld 
{

    boolean load();
    boolean unload( boolean save );
    default World getWorld()
    {
        return Bukkit.getWorld( getWorldName() );
    };
    String getWorldName();
    void backup();
    File getBackupFolder();

}
