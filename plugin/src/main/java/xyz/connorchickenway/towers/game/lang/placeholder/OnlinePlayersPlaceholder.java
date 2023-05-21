package xyz.connorchickenway.towers.game.lang.placeholder;

public class OnlinePlayersPlaceholder extends Placeholder<Integer>
{

    private OnlinePlayersPlaceholder( Integer obj )
    {
        super( "online_players", obj );
    }

    @Override
    public String getValue()
    {
        return Integer.toString( obj );
    }
    
    public static OnlinePlayersPlaceholder newInstance( Integer obj )
    {

        return new OnlinePlayersPlaceholder( obj );
    }

}
