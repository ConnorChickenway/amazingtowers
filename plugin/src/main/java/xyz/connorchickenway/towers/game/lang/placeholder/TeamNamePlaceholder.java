package xyz.connorchickenway.towers.game.lang.placeholder;

public class TeamNamePlaceholder extends Placeholder<String>
{

    public TeamNamePlaceholder( String obj )
    {
        super( "team_name", obj );
    }

    @Override
    public String getValue()
    {
        return obj;
    }
    
    public static TeamNamePlaceholder newInstance( String obj )
    {
        return new TeamNamePlaceholder( obj );
    }

}
