package xyz.connorchickenway.towers.game.builder.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import xyz.connorchickenway.towers.utilities.location.Location;

import java.io.IOException;

public class LocationAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter writer, Location value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.value(value.serialize());
    }

    @Override
    public Location read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return Location.deserialize(reader.nextString());
    }

}
