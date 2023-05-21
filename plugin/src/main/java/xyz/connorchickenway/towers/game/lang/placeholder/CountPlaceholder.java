package xyz.connorchickenway.towers.game.lang.placeholder;

public class CountPlaceholder extends Placeholder<Integer>
{

    private CountPlaceholder( Integer obj )
    {
        super( "count", obj );
    }

    @Override
    public String getValue()
    {
        return Integer.toString( this.obj );
    }
    
    public static CountPlaceholder newInstance( Integer obj )
    {
        return new CountPlaceholder( obj );
    }

}
