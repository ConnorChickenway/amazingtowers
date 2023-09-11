package xyz.connorchickenway.towers.game.kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import xyz.connorchickenway.towers.nms.NMSVersion;

public class DefaultKit extends Kit
{
    
    private DefaultKit()
    {
        armor[0] = new ItemStack( Material.LEATHER_HELMET );
        armor[1] = new ItemStack( Material.LEATHER_CHESTPLATE );
        armor[2] = new ItemStack( Material.LEATHER_LEGGINGS );
        armor[3] = new ItemStack( Material.LEATHER_BOOTS );
        contents.put(0, new ItemStack( NMSVersion.isNewerVersion ? Material.WOODEN_SWORD : Material.valueOf( "WOOD_SWORD" ) ) );
        contents.put(1, new ItemStack( Material.STONE_PICKAXE) );
        contents.put(8, new ItemStack( Material.BAKED_POTATO, 64 ) );
    }

    private static DefaultKit instance = new DefaultKit();

    public static DefaultKit getInstance()
    {
        return instance;
    }

}
