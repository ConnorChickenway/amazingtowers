package xyz.connorchickenway.towers.game.lang.placeholder;

public class MessagePlaceholder extends Placeholder<String>
{

    private MessagePlaceholder( String obj )
    {
        super( "msg", obj );
    }

    @Override
    public String getValue()
    {
        return obj;
    }

    public static MessagePlaceholder newInstance( String obj )
    {
        return new MessagePlaceholder( obj );
    }
    
}
