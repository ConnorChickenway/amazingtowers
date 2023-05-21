package xyz.connorchickenway.towers.game.lang.placeholder;

import java.util.HashMap;
import java.util.Map;

public abstract class Placeholder<T>
{

    private String key;
    protected T obj;
    
    public Placeholder( String key, T obj )
    {
        this.key = "%" + key + "%";
        this.obj = obj;
    }

    public String getKey()
    {
        return key;
    }

    public abstract String getValue();

    public static Map<String, Placeholder<?>> builder( Placeholder<?>... args ) 
    {
        Map<String, Placeholder<?>> map = new HashMap<>();
        for ( Placeholder<?> placeholder : args ) 
        {
            map.put( placeholder.getKey(), placeholder );
        }
        return map;
    }

}
