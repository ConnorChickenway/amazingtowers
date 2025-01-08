package xyz.connorchickenway.towers.game.world;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.Logger;

import java.io.File;

import static xyz.connorchickenway.towers.AmazingTowers.SLIME_ADAPTER;

public class SlimeWorldLoader implements GameWorld {

    private final String worldName;

    public SlimeWorldLoader(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public boolean load() {
        try {
            SLIME_ADAPTER.loadWorld(worldName, true);
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

}
