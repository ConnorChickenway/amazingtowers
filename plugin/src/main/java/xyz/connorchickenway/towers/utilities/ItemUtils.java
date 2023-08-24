package xyz.connorchickenway.towers.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.nms.NMSVersion;

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
    
    @SuppressWarnings( "deprecation" )
    public static JsonObject toJson( ItemStack itemStack )
    {
        JsonObject jObject = new JsonObject();
        jObject.addProperty( "type", itemStack.getType().name() );
        jObject.addProperty( "amount", itemStack.getAmount() );
        if ( !NMSVersion.isNewerVersion )
            if ( itemStack.getDurability() > 0 ) 
                jObject.addProperty( "data", itemStack.getDurability() );
        if ( itemStack.hasItemMeta() )
        {
            JsonObject metaObject = new JsonObject();
            ItemMeta itemMeta = itemStack.getItemMeta();
            metaObject.addProperty( "display_name", itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null );
            if ( itemMeta.hasLore() )
            {
                JsonArray loreArray = new JsonArray();
                itemMeta.getLore().forEach( loreArray::add );
                metaObject.add( "lore", loreArray );
            }
            if ( itemMeta.hasEnchants() )
            {
                JsonArray enchantsArray = new JsonArray();
                itemMeta.getEnchants().forEach( (enchantment, integer) -> enchantsArray.add( enchantment + ":" + integer ) );
                metaObject.add( "enchants" , enchantsArray );
            }
            if ( !itemMeta.getItemFlags().isEmpty() ) 
            {
                JsonArray flags = new JsonArray();
                itemMeta.getItemFlags().stream().map( ItemFlag::name ).forEach( flags::add );
                metaObject.add( "flags", flags );
            }
            jObject.add( "item-meta", metaObject );
        }
        return jObject;
    }

    @SuppressWarnings( "deprecation" )
    public static ItemStack fromJson( JsonObject object ) throws Exception
    {
        String type = object.get( "type" ).getAsString();
        int amount = object.get( "amount" ).getAsInt();
        byte durability = -1;
        if ( !NMSVersion.isNewerVersion )
            durability = object.get( "data" ).getAsByte();
        JsonElement element = object.get( "item-meta" );
        ItemStack itemStack = NMSVersion.isNewerVersion ? new ItemStack( Material.getMaterial( type ), amount ) : new ItemStack( Material.getMaterial( type ), amount, durability );
        if ( element != null ) 
        {
            JsonObject metaObj = element.getAsJsonObject();
            ItemMeta itemMeta = itemStack.getItemMeta();
            String displayName = metaObj.get( "display_name" ).getAsString();
            if ( displayName != null )
                itemMeta.setDisplayName( displayName );
            element = object.get( "lore" );
            if ( element != null && element.isJsonArray() )
            {
                JsonArray jsonArray = element.getAsJsonArray();
                List<String> list = AmazingTowers.GSON.fromJson( jsonArray, new TypeToken<ArrayList<String>>(){}.getType() );
                itemMeta.setLore( list );
            }
            element = object.get( "enchants" );
            if ( element != null && element.isJsonArray() )
            {
                JsonArray enchArray = element.getAsJsonArray();
                enchArray.forEach( jElement -> 
                {
                    String[] split = jElement.getAsString().split( ":" );
                    Enchantment enchantment = NMSVersion.isNewerVersion ? 
                        Enchantment.getByKey( NamespacedKey.minecraft( split[ 0 ] ) ) :
                        Enchantment.getByName( split[ 0 ] );
                    int level = Integer.parseInt( split[ 1 ] );    
                    if ( enchantment != null && level > 0)
                        itemMeta.addEnchant( enchantment, level, true );
                } );
            }
            element = object.get( "flags" );
            if ( element != null && element.isJsonArray() )
            {
                JsonArray flagsArray = element.getAsJsonArray();
                flagsArray.forEach( jElement -> 
                {
                    if ( jElement.isJsonPrimitive() )
                    {
                        for ( ItemFlag itemFlag : ItemFlag.values() )
                        {
                            if ( itemFlag.name().equalsIgnoreCase( jElement.getAsString() ) )
                            {
                                itemMeta.addItemFlags( itemFlag );
                                break;
                            }
                        }
                    }
                } );
            }
        }
        return itemStack;
    }
    
}
