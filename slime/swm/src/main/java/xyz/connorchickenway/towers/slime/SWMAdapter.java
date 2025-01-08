package xyz.connorchickenway.towers.slime;

import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class SWMAdapter implements SlimeAdapter {

    private final com.grinderwolf.swm.api.SlimePlugin slimePlugin;
    private final SlimeLoader slimeLoader;
    private final SlimePropertyMap slimeProperties;

    public SWMAdapter(File file) {
        slimePlugin = (com.grinderwolf.swm.api.SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = new SlimeFileLoader(file);
        slimeProperties = new SlimePropertyMap();
        slimeProperties.setString(SlimeProperties.DIFFICULTY, "peaceful");
        slimeProperties.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        slimeProperties.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        slimeProperties.setBoolean(SlimeProperties.PVP, true);
        slimeProperties.setString(SlimeProperties.ENVIRONMENT, "normal");
    }

    @Override
    public void createEmptyWorld(String worldName, boolean readOnly) {
        try {
            SlimeWorld slimeWorld = slimePlugin.createEmptyWorld(slimeLoader, worldName, readOnly, slimeProperties);
            slimePlugin.generateWorld(slimeWorld);
        } catch (WorldAlreadyExistsException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadWorld(String worldName, boolean readOnly) {
        try {
            SlimeWorld slimeWorld = slimePlugin.loadWorld(slimeLoader, worldName, readOnly, slimeProperties);
            slimePlugin.generateWorld(slimeWorld);
        } catch (IOException | WorldInUseException | CorruptedWorldException | NewerFormatException |
                 UnknownWorldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteWorld(String worldName) {
        try {
            slimeLoader.deleteWorld(worldName);
        } catch (UnknownWorldException | IOException  e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean worldExists(String worldName) {
        try {
            return slimeLoader.worldExists(worldName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
