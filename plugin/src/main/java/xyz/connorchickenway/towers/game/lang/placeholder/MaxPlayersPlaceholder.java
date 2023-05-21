package xyz.connorchickenway.towers.game.lang.placeholder;

public class MaxPlayersPlaceholder extends Placeholder<Integer>
{

    public MaxPlayersPlaceholder( Integer obj )
    {
        super( "max_players", obj );
    }

    @Override
    public String getValue()
    {
        return Integer.toString( obj );
    }

    public static MaxPlayersPlaceholder newInstance( Integer obj )
    {
        return new MaxPlayersPlaceholder( obj );
    }
    
}
