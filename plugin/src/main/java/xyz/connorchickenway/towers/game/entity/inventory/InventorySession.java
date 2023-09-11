package xyz.connorchickenway.towers.game.entity.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;

import xyz.connorchickenway.towers.game.entity.GamePlayer;

public class InventorySession 
{
    
    private final GamePlayer gamePlayer;

    private ItemStack[] armorContents, contents;
    private double health;
    private int food;
    private Scoreboard previousScoreboard;

    public InventorySession( GamePlayer gamePlayer )
    {
        this.gamePlayer = gamePlayer;
    }

    public void save()
    {
        Player player = gamePlayer.toBukkitPlayer();
        PlayerInventory playerInventory = player.getInventory();
        this.armorContents = playerInventory.getArmorContents();
        this.contents = playerInventory.getContents();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.previousScoreboard = player.getScoreboard();
    }

    public void load()
    {
        Player player = gamePlayer.toBukkitPlayer();
        PlayerInventory pInventory = player.getInventory();
        pInventory.clear();
        pInventory.setArmorContents( armorContents );
        pInventory.setContents( contents );
        player.setHealth( health );
        player.setFoodLevel( food );
        player.setScoreboard( previousScoreboard );
    }

    public void clear()
    {
        this.armorContents = null;
        this.contents = null;
        this.health = -1;
        this.food = -1;
        this.previousScoreboard = null;
    }

}
