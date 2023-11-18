package xyz.connorchickenway.towers.game.world;

import java.io.File;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.FileUtils;
import xyz.connorchickenway.towers.utilities.Logger;

public class BukkitWorldLoader implements GameWorld
{

    private final String worldName;

    public BukkitWorldLoader( String worldName )
    {
        this.worldName = worldName;
    }

    public BukkitWorldLoader( World world )
    {
        this( world.getName() );
    }

    @Override
    public boolean load()
    {
        boolean load = copyWorldDir( this.getBackupFolder(), new File( worldName ) );
        createWorld( worldName );
        return load;
    }

    @Override
    public boolean unload( boolean save )
    {
        World world = getWorld();
        boolean unload = false;
        if ( world != null )
        {
            unload = Bukkit.unloadWorld( world, save );
            if ( unload )
                FileUtils.delete( new File( worldName ) );
        }
        return unload;
    }
    
    @Override
    public String getWorldName()
    {
        return worldName;
    }

    @Override
    public void backup()
    {
        File worldFile = new File( worldName );
        copyWorldDir( worldFile, this.getBackupFolder() );
    }
    
    @Override
    public File getBackupFolder()
    {
        return new File( AmazingTowers.getInstance().getGameManager().getGameFolder(), getWorldName() + File.separator + "backup" );
    }

    @SuppressWarnings( "deprecation" )
    public static World createWorld( String name )
    {
        WorldCreator wc = new WorldCreator( name );
        wc.type( WorldType.FLAT );
        wc.generator( new ChunkGenerator() 
        {
        
            @Override
            public ChunkData generateChunkData( World world, Random random, int x, int z, BiomeGrid biome )
            {
                return createChunkData( world );
            }
        });
        wc.generateStructures( false );
        World world = wc.createWorld();
        world.setAutoSave( false );
        if ( NMSVersion.isNewerVersion )
        {
            world.setGameRule( GameRule.DO_MOB_SPAWNING, false );
            world.setGameRule( GameRule.MOB_GRIEFING, false );
            world.setGameRule( GameRule.DO_DAYLIGHT_CYCLE, false );
            world.setGameRule( GameRule.ANNOUNCE_ADVANCEMENTS, false );
        }else 
        {
            world.setGameRuleValue( "doMobSpawning", "false" );
            world.setGameRuleValue( "mobGriefing", "false" );
            world.setGameRuleValue( "doDaylightCycle", "false" );
            world.setGameRuleValue( "announceAdvancements", "false" );
        }
        
        return world;
    }

    private static boolean copyWorldDir( File source, File destination )
    {
        if ( source.isDirectory() )
        {
            if ( !destination.exists() )
                destination.mkdirs();
            for ( File file : source.listFiles() )
                if ( file.getName().equals( "region" ) )
                {
                    if ( !file.isDirectory() ) break;
                    File regionFolder = new File( destination, file.getName() );
                    regionFolder.mkdir();
                    for ( File region : file.listFiles() )
                        FileUtils.copyFile( region, new File( regionFolder, region.getName() ) );
                    return true;
                }
        }
        return false;
    }

}
