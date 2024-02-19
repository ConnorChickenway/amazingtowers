package xyz.connorchickenway.towers.game.builder.setup;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.connorchickenway.towers.game.builder.GameBuilder;
import xyz.connorchickenway.towers.game.builder.setup.wand.Wand;
import xyz.connorchickenway.towers.utilities.ItemUtils;

public class SetupSession {

    private final Player player;
    private final GameBuilder gameBuilder;
    private final Wand wand;

    public SetupSession(Player player) {
        this.player = player;
        this.gameBuilder = GameBuilder.builder();
        this.wand = new Wand();
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.addItem(ItemUtils.wandItemStack);
        player.setGameMode(org.bukkit.GameMode.CREATIVE);
        player.setFlying(true);
    }

    public Player getPlayer() {
        return player;
    }

    public GameBuilder getBuilder() {
        return gameBuilder;
    }

    public Wand getWand() {
        return wand;
    }

}
