package xyz.connorchickenway.towers.game.world;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.Logger;

import java.io.File;

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
        } catch (Exception ex) {
            Logger.error("There was an error loading the world " + worldName, ex);
        }
        return false;
    }

    @Override
    public boolean unload(boolean save) {
        return Bukkit.unloadWorld(worldName, save);
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
        return null;
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
        return SLIME_PLUGIN.getLoader("slime_loader");
    }

}
