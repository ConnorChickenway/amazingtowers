package xyz.connorchickenway.towers.game.kit;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import xyz.connorchickenway.towers.utilities.ItemUtils;

public class JsonKit extends AbstractKit
{

    private File jsonFile;

    public JsonKit( File jsonFile )
    {
        this.jsonFile = jsonFile;
    }

    @Override
    public boolean load()
    {
        try
        {
            JsonObject jObject = new JsonParser().parse( new FileReader( jsonFile ) ).getAsJsonObject();
            JsonElement element = jObject.get( "armor" );
            if ( element != null && element.isJsonArray() )
            {
                JsonArray jsonArray = element.getAsJsonArray();
                for( int x = 0; x < armor.length; x++ )
                    armor[ x ] = ItemUtils.fromJson( jsonArray.get( x ).getAsJsonObject() );
            }
            element = jObject.get( "contents" );
            if ( element != null && element.isJsonArray() )
            {
                for ( JsonElement jElement : element.getAsJsonArray() )
                {
                    if ( jElement.isJsonObject() )
                    {
                        JsonObject itemObject = jElement.getAsJsonObject();
                        int index = itemObject.getAsJsonPrimitive( "index" ).getAsInt();
                        ItemStack itemStack = ItemUtils.fromJson( itemObject.getAsJsonObject( "item-stack" ) );
                        contents.put( index, itemStack );
                    }
                }
            }
            return true;
        } catch ( Exception ex )
        {
            System.out.println( "There was an error loading a kit.!" );
            ex.printStackTrace();
        }
        return false; 
    }

    public JsonObject save()
    {
        JsonObject jObject = new JsonObject();
        JsonArray jArray = new JsonArray();
        for (ItemStack armor : armor) 
            jArray.add( ItemUtils.toJson( armor ) );
        jObject.add( "armor", jArray );
        jArray = new JsonArray();
        for ( Map.Entry<Integer, ItemStack> entry : contents.entrySet() )
        {
            JsonObject obj = new JsonObject(); 
            obj.addProperty( "index", entry.getKey() );
            obj.add( "item-stack", ItemUtils.toJson( entry.getValue() ) );
            jArray.add( obj );
        }
        jObject.add( "contents", jArray );
        return jObject;
    }
    
    public File getJsonFile()
    {
        return jsonFile;
    }
    
}
