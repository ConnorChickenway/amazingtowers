package xyz.connorchickenway.towers.game.world;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

import static xyz.connorchickenway.towers.AmazingTowers.SLIME_PLUGIN;

public class SlimeWorldLoader implements GameWorld
{

    private final String worldName;
    
    public SlimeWorldLoader( String worldName )
    {
        this.worldName = worldName;
    }

    public SlimeWorldLoader( SlimeWorld slimeWorld )
    {
        this( slimeWorld.getName() );
    }

    @Override
    public boolean load()
    {
        SlimeLoader loader = getLoader();
        try
        {
            SlimeWorld slimeWorld = SLIME_PLUGIN.loadWorld( loader, worldName, true, getProperties() );
            SLIME_PLUGIN.generateWorld( slimeWorld );
            return true;
        } catch( Exception ignore ){}
        return false;
    }

    @Override
    public boolean unload( boolean save )
    {
        SlimeLoader loader = getLoader();
        try
        {
            Bukkit.unloadWorld( worldName, save );
            loader.unlockWorld( worldName );
            return true;
        } catch ( UnknownWorldException | IOException e )
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void backup(){}

    @Override
    public String getWorldName()
    {
        return worldName;
    }

    @Override
    public File getBackupFolder()
    {
        throw new UnsupportedOperationException("Unimplemented method 'getWorldName'");
    }

    public static SlimePropertyMap getProperties()
    {
        SlimePropertyMap properties = new SlimePropertyMap();
        properties.setValue( SlimeProperties.DIFFICULTY, "peaceful" );
        properties.setValue( SlimeProperties.ALLOW_ANIMALS, false );
        properties.setValue( SlimeProperties.ALLOW_MONSTERS, false );
        properties.setValue( SlimeProperties.PVP, true );
        properties.setValue( SlimeProperties.ENVIRONMENT, "normal" );
        return properties; 
    }

    public static SlimeLoader getLoader()
    {
        return SLIME_PLUGIN.getLoader( "file" );
    }

}
