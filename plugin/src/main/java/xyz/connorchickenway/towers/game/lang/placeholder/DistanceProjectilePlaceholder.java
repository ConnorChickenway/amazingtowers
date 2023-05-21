package xyz.connorchickenway.towers.game.lang.placeholder;

import org.bukkit.entity.Player;

public class DistanceProjectilePlaceholder extends Placeholder<Player>
{

    public DistanceProjectilePlaceholder( Player obj )
    {
        super( "distance", obj );
    }

    @Override
    public String getValue()
    {
        return Double.toString( obj.getLocation().distanceSquared( obj.getKiller().getLocation() ) );
    }
    
    public static DistanceProjectilePlaceholder newInstance( Player obj )
    {
        return new DistanceProjectilePlaceholder( obj );
    }

}
