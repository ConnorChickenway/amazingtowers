package xyz.connorchickenway.towers.nms;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class VersionSupport_1_12_R1 implements INMS {

    @Override
    public void respawnPlayer(Player player) {
        PacketPlayInClientCommand packet = new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
        ((CraftPlayer) player).getHandle().playerConnection.a(packet);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent component;
        if (title != null) {
            component = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            sendPacket(((CraftPlayer) player), new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, component));
        }

        if (subtitle != null) {
            component = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            sendPacket(((CraftPlayer) player), new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, component));
        }
        sendPacket(((CraftPlayer) player), new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut));
    }

    private void sendPacket(CraftPlayer player, PacketPlayOutTitle packet) {
        player.getHandle().playerConnection.sendPacket(packet);
    }


}
