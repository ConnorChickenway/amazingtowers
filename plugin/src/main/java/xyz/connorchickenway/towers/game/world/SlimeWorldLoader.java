package xyz.connorchickenway.towers.game.world;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import xyz.connorchickenway.towers.nms.NMSVersion;

import java.io.File;
import java.io.IOException;

import static xyz.connorchickenway.towers.AmazingTowers.SLIME_PLUGIN;

public class SlimeWorldLoader implements GameWorld {

    private final String worldName;

    public SlimeWorldLoader(String worldName) {
        this.worldName = worldName;
    }

    public SlimeWorldLoader(SlimeWorld slimeWorld) {
        this(slimeWorld.getName());
    }

    @Override
    public boolean load() {
        SlimeLoader loader = getLoader();
        try {
            SlimeWorld slimeWorld = SLIME_PLUGIN.loadWorld(loader, worldName, true, getProperties());
            SLIME_PLUGIN.generateWorld(slimeWorld);
            World world = this.getWorld();
            if (NMSVersion.isNewerVersion)
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            else
                world.setGameRuleValue("announceAdvancements", "false");
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public boolean unload(boolean save) {
        SlimeLoader loader = getLoader();
        try {
            Bukkit.unloadWorld(worldName, save);
            loader.unlockWorld(worldName);
            return true;
        } catch (UnknownWorldException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void backup() {
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public File getBackupFolder() {
        throw new UnsupportedOperationException("Unimplemented method 'getWorldName'");
    }

    public static SlimePropertyMap getProperties() {
        SlimePropertyMap properties = new SlimePropertyMap();
        properties.setString(SlimeProperties.DIFFICULTY, "peaceful");
        properties.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        properties.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        properties.setBoolean(SlimeProperties.PVP, true);
        properties.setString(SlimeProperties.ENVIRONMENT, "normal");
        return properties;
    }

    public static SlimeLoader getLoader() {
        return SLIME_PLUGIN.getLoader("file");
    }

}
