package xyz.connorchickenway.towers.utilities;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import xyz.connorchickenway.towers.AmazingTowers;

public class BungeecordUtils {

    public static void sendPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(AmazingTowers.getInstance(), "BungeeCord", out.toByteArray());
    }

}
