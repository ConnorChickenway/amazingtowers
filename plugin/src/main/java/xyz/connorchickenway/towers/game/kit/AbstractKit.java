package xyz.connorchickenway.towers.game.kit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import xyz.connorchickenway.towers.utilities.ItemUtils;

public abstract class AbstractKit 
{
    
    protected Map<Integer, ItemStack> contents;
    protected ItemStack[] armor;

    public AbstractKit() 
    {
        this.contents = new HashMap<>();
        this.armor = new ItemStack[4];
    }

    /**
     * @return true if function loads without error.
     */
    public abstract boolean load();

    /**
     * Sends a kit to Player
     * @param player 
     * @param color 
     */
    public void sendKit( Player player, Color color ) 
    {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        contents.entrySet().forEach( e -> inventory.setItem( e.getKey(), e.getValue() ) );
        for ( int i = 0; i < armor.length; i++ )  
        {   
            if ( armor[ i ] == null ) continue;
            ItemStack itemStack = armor[i].clone();
            if ( ItemUtils.isArmorLeather( itemStack.getType() ) ) 
            {
                LeatherArmorMeta meta = ( LeatherArmorMeta ) itemStack.getItemMeta();
                meta.setColor( color );
                itemStack.setItemMeta( meta );
            }
            ItemUtils.setArmor( inventory, itemStack );
        }

    }

}
