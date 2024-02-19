package xyz.connorchickenway.towers.game.builder;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Team {

    RED(ChatColor.RED, DyeColor.RED), BLUE(ChatColor.BLUE, DyeColor.BLUE);

    private final ChatColor chatColor;
    private final DyeColor dyeColor;

    Team(ChatColor chatColor, DyeColor dyeColor) {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @Override
    public String toString() {
        return chatColor + name();
    }

    public static Team get(String str) {
        for (Team teamName : values()) {
            if (teamName.name().equalsIgnoreCase(str))
                return teamName;
        }
        return null;
    }

}
