package xyz.connorchickenway.towers.game.builder.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import xyz.connorchickenway.towers.nms.NMSVersion;

public class ItemStackAdapter extends TypeAdapter<ItemStack>
{

    @SuppressWarnings( "deprecation" )
    @Override
    public void write( JsonWriter writer, ItemStack itemStack ) throws IOException
    {
        writer.beginObject();
        writer.name( "type" ).value( itemStack.getType().name() );
        writer.name( "amount" ).value( itemStack.getAmount() );
        if ( !NMSVersion.isNewerVersion && itemStack.getDurability() >= 1 ) 
            writer.name( "data" ).value( itemStack.getDurability() );
        if ( itemStack.hasItemMeta() )
        {
            writer.name( "item-meta" );
            writer.beginObject();
            ItemMeta itemMeta = itemStack.getItemMeta();
            writer.name( "display-name" ).value( itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null );
            if ( itemMeta.hasLore() )
            {
                writer.name( "lore" );
                writer.beginArray();
                for ( String lore : itemMeta.getLore() )
                    writer.value( lore );
                writer.endArray();
            }
            if ( itemMeta.hasEnchants() )
            {
                writer.name( "enchants" );
                writer.beginArray();
                for ( Map.Entry<Enchantment, Integer> entry : itemMeta.getEnchants().entrySet() )
                    writer.value( entry.getKey() + ":" + entry.getValue() );
                writer.endArray();
            }
            if ( itemMeta.getItemFlags().size() >= 1 )
            {
                writer.name( "flags" );
                writer.beginArray();
                for ( ItemFlag flag : itemMeta.getItemFlags() )
                    writer.value( flag.name() );
                writer.endArray();
            }
            writer.endObject();
        }        
        writer.endObject();
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public ItemStack read( JsonReader reader ) throws IOException
    {
        final Builder builder = new Builder();
        reader.beginObject();
        while( reader.hasNext() )
        {
            switch( reader.nextName() )
            {
                case "type":
                    builder.type = reader.nextString();
                    break;
                case "amount":
                    builder.amount = reader.nextInt();
                    break;
                case "data":
                    builder.data = (byte) reader.nextInt();
                    break;
                case "item-meta":
                    reader.beginObject();
                    while( reader.hasNext() )
                    {
                        switch( reader.nextName() )
                        {
                            case "display-name":
                                builder.displayName = reader.nextString();
                                break;
                            case "lore":
                                reader.beginArray();
                                while( reader.hasNext() )
                                    builder.lore.add( reader.nextString() );
                                reader.endArray();
                                break;
                            case "enchants":
                                reader.beginArray();
                                while( reader.hasNext() )
                                {
                                    String key = reader.nextString();
                                    String[] split = key.split( ":" );
                                    if ( split.length >= 2 )
                                    {
                                        Enchantment enchantment = NMSVersion.isNewerVersion
                                                ? Enchantment.getByKey( NamespacedKey.minecraft( split[0] ) )
                                                : Enchantment.getByName( split[0] );
                                        int level = Integer.parseInt( split[1] );
                                        if ( enchantment != null && level > 0 )
                                            builder.enchantMap.put( enchantment, level );
                                    }
                                }
                                reader.endArray();
                                break;
                            case "flags":
                                reader.beginArray();
                                while( reader.hasNext() )
                                {
                                    String itemFlagKey = reader.nextString();
                                    for ( ItemFlag itemFlag : ItemFlag.values() )
                                        if ( itemFlag.name().equalsIgnoreCase( itemFlagKey ) )
                                        {
                                            builder.itemFlags.add( itemFlag );
                                            break;
                                        }
                                }
                                reader.endArray();
                                break;            
                        }
                    }
                    reader.endObject();
                    break;
                default: break;           
            }
        }
        reader.endObject();
        return builder.build();
    }  
    
    private class Builder 
    {

        String type;
        int amount;
        byte data;
        String displayName;
        List<String> lore = new ArrayList<>();
        Map<Enchantment, Integer> enchantMap = new HashMap<>();
        List<ItemFlag> itemFlags = new ArrayList<>();

        @SuppressWarnings( "deprecation" )
        public ItemStack build()
        {
            final Material material = Material.valueOf( type );
            ItemStack itemStack = NMSVersion.isNewerVersion ? new ItemStack( material, amount ) : 
                new ItemStack( material, amount, data );
            ItemMeta itemMeta = itemStack.getItemMeta();
            if ( displayName != null )
                itemMeta.setDisplayName( displayName );
            if ( !lore.isEmpty() )
                itemMeta.setLore( lore );
            if ( !enchantMap.isEmpty() )    
                enchantMap.forEach( (enchatment, level) -> itemMeta.addEnchant( enchatment, level, true ) );
            if ( !itemFlags.isEmpty() )
                itemFlags.forEach( itemMeta::addItemFlags );
            return itemStack;
        }

    }
    
}
