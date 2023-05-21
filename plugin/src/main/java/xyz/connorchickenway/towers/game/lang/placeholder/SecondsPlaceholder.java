package xyz.connorchickenway.towers.game.lang.placeholder;

import xyz.connorchickenway.towers.game.lang.Lang;

public class SecondsPlaceholder extends Placeholder<Integer>
{

    private SecondsPlaceholder( Integer obj )
    {
        super( "seconds", obj );
    }

    @Override
    public String getValue()
    {
        String[] split = Lang.SECONDS.get()[0].split( "::" );
        return obj <= 0 ? split[ 0 ] : split[ 1 ];
    }

    public static SecondsPlaceholder newInstance( Integer obj )
    {
        return new SecondsPlaceholder( obj );
    } 

}
