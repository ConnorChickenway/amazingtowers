package xyz.connorchickenway.towers.game.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.utilities.FileUtils;

public class GameWorld
{

    private final String worldName;
    private final File backupFolder;

    public GameWorld( String worldName )
    {
        this.worldName = worldName;
        this.backupFolder = new File( AmazingTowers.getInstance().getDataFolder() + File.separator + "games"
                + File.separator + worldName + File.separator + "backup" );
    }

    public void load()
    {
        FileUtils.copyDirectory( backupFolder , new File( worldName ) );
        World world = Bukkit.createWorld( new WorldCreator( worldName ) );
        world.setKeepSpawnInMemory( false );
    }

    public void unload()
    {
        boolean unload = Bukkit.unloadWorld( worldName , false );
        if ( unload )
            FileUtils.delete( new File( worldName ) );
    }

    public World getWorld() 
    {
        return Bukkit.getWorld( worldName );
    }

    public File getBackupFolder()
    {
        return backupFolder;
    }

}
