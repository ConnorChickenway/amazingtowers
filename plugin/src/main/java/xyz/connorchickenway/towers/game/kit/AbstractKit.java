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
        this.load();
    }

    public abstract void load();

    public void sendKit( Player player, Color color ) 
    {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        contents.entrySet().forEach( e -> inventory.setItem( e.getKey(), e.getValue() ) );
        ItemStack[] a = new ItemStack[4];
        for ( int i = 0; i < armor.length; i++ )  
        {
            ItemStack s = armor[i].clone();
            if ( ItemUtils.isArmorLeather( s.getType() ) ) 
            {
                LeatherArmorMeta meta = ( LeatherArmorMeta ) s.getItemMeta();
                meta.setColor( color );
                s.setItemMeta( meta );
            }
            a[i] = s;
        }
        inventory.setArmorContents( a );
    }

}
