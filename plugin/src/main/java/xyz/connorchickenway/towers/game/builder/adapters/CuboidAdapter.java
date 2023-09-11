package xyz.connorchickenway.towers.game.builder.adapters;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import xyz.connorchickenway.towers.utilities.Cuboid;

public class CuboidAdapter extends TypeAdapter<Cuboid>
{

    @Override
    public void write( JsonWriter writer, Cuboid value ) throws IOException
    {
        if ( value == null )
        {
            writer.nullValue();
            return;
        }
        writer.value( value.serialize() ); 
    }

    @Override
    public Cuboid read( JsonReader reader ) throws IOException
    {
        if ( reader.peek() == JsonToken.NULL )
        {
            reader.nextNull();
            return null;
        }
        return Cuboid.deserialize( reader.nextString() );
    }
    
    

}
