package xyz.connorchickenway.towers.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.team.Team;

public class ItemUtils 
{

    public static ItemStack wandItemStack = ItemBuilder
        .of( Material.IRON_AXE )
        .setDisplayName( ChatColor.GREEN + "Region Selector" )
        .toItemStack();

    public static ItemStack blueItem, redItem, quitItem;

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

    public static void setArmor( PlayerInventory pInventory, ItemStack itemStack )
    {
        if ( itemStack == null ) return;
        String armorName = itemStack.getType().name();
        if ( armorName.endsWith( "_HELMET" ) )
            pInventory.setHelmet( itemStack );
        else if ( armorName.endsWith( "_CHESTPLATE" ) )
            pInventory.setChestplate( itemStack );
        else if ( armorName.endsWith( "_LEGGINGS" ) )
            pInventory.setLeggings( itemStack );    
        else 
            pInventory.setBoots( itemStack );    
    }

    public static void setGlow( ItemStack itemStack )
    {
        itemStack.addUnsafeEnchantment( Enchantment.LUCK, 1 );
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        itemStack.setItemMeta( meta );
    }

    public static void removeGlow( ItemStack itemStack )
    {
        itemStack.removeEnchantment( Enchantment.LUCK );
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeItemFlags( ItemFlag.HIDE_ENCHANTS );
        itemStack.setItemMeta( meta );
    }

    public static boolean isBlueItem( ItemStack itemStack )
    {
        if ( itemStack != null )
            return itemStack.getItemMeta().getDisplayName().equals( blueItem.getItemMeta().getDisplayName() ) &&
                itemStack.getType() == blueItem.getType();
        return false;
    }

    public static boolean isRedItem( ItemStack itemStack )
    {
        if ( itemStack != null )
            return itemStack.getItemMeta().getDisplayName().equals( redItem.getItemMeta().getDisplayName() ) &&
                itemStack.getType() == redItem.getType();
        return false;
    }

    public static Pair<ItemStack, Integer> getItem( Player player, Team team )
    {
        ItemStack[] items = ( ItemStack[] ) MetadataUtils.get( player, "items-game" );
        if ( items == null ) return null;
        if ( team.getTeamName().equals( "red" ) )
            return new Pair<>( items[ 0 ], StaticConfiguration.red_position );
        return new Pair<>( items[ 1 ], StaticConfiguration.blue_position );
    }

}
