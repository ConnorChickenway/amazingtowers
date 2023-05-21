package xyz.connorchickenway.towers.game.entity.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventorySession 
{
    
    private final UUID id;

    private ItemStack[] armorContents, contents;
    private double health;
    private int food;

    public InventorySession( UUID uuid )
    {
        this.id = uuid;
    }

    public void save()
    {
        Player player = Bukkit.getPlayer( id );
        PlayerInventory playerInventory = player.getInventory();
        this.armorContents = playerInventory.getArmorContents();
        this.contents = playerInventory.getContents();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
    }

    public void load()
    {
        Player player = Bukkit.getPlayer( id );
        PlayerInventory pInventory = player.getInventory();
        pInventory.clear();
        pInventory.setArmorContents( armorContents );
        pInventory.setContents( contents );
        player.setHealth( health );
        player.setFoodLevel( food );
    }

    public void clear()
    {
        this.armorContents = null;
        this.contents = null;
        this.health = -1;
        this.food = -1;
    }

}
