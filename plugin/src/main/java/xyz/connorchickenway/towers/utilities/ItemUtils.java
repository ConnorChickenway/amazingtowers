package xyz.connorchickenway.towers.utilities;

import org.bukkit.Material;

public class ItemUtils 
{

    public static boolean isArmorLeather( Material material ) 
    {
        switch ( material )  
        {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return true;
            default:
                return false;
        }
    }
    
}
