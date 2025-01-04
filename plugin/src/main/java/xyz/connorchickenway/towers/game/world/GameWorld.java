package xyz.connorchickenway.towers.game.world;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

public interface GameWorld {

    boolean load();

    boolean unload(boolean save);

    default World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    String getWorldName();

    void backup();

    File getBackupFolder();

}
