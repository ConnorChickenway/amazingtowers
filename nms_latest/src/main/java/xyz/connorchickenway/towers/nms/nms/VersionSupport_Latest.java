package xyz.connorchickenway.towers.nms.nms;

import org.bukkit.entity.Player;
import xyz.connorchickenway.towers.nms.INMS;

public class VersionSupport_Latest implements INMS {

    @Override
    public void respawnPlayer(Player player) {
        player.spigot().respawn();
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

}
