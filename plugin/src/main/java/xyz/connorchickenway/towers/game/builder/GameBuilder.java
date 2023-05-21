package xyz.connorchickenway.towers.game.builder;

import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.kit.AbstractKit;
import xyz.connorchickenway.towers.game.kit.DefaultKit;
import xyz.connorchickenway.towers.utilities.location.Location;

public class GameBuilder 
{

    private String name;
    private Location lobby, ironGenerator, expGenerator;
    private int minPlayers, maxPlayers, count, maxPoints;
    private AbstractKit abstractKit;

    private GameBuilder(){}

    public GameBuilder setName( String name )
    {
        this.name = name;
        return this;
    }
    
    public String getName()
    {
        return name;
    }

    public void setLobby( Location lobby )
    {
        this.lobby = lobby;
    }
    
    public void setIronGenerator( Location ironGenerator )
    {
        this.ironGenerator = ironGenerator;
    }
    
    public void setExpGenerator( Location expGenerator )
    {
        this.expGenerator = expGenerator;
    }
    
    public void setMinPlayers( int minPlayers )
    {
        this.minPlayers = minPlayers;
    }
    
    public int getMinPlayers()
    {
        return minPlayers;
    }

    public void setMaxPlayers( int maxPlayers )
    {
        this.maxPlayers = maxPlayers;
    }
    
    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public void setCount( int count )
    {
        this.count = count;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public void setMaxPoints( int maxPoints )
    {
        this.maxPoints = maxPoints;
    }
    
    public int getMaxPoints()
    {
        return maxPoints;
    }


    public void setAbstractKit( AbstractKit abstractKit )
    {
        this.abstractKit = abstractKit;
    }

    public boolean hasName()
    {
        return name != null;
    }

    public boolean hasLobby()
    {
        return lobby != null;
    }

    public boolean hasIronGenerator()
    {
        return ironGenerator != null;
    }

    public boolean hasExperienceGenerator()
    {
        return expGenerator != null;
    }

    public boolean hasMinPlayers()
    {
        return minPlayers > 0;
    }

    public boolean hasMaxPlayers()
    {
        return maxPlayers > 0;
    }

    public boolean hasCount()
    {
        return count > 0;
    }

    public boolean hasMaxPoints()
    {
        return maxPoints > 0;
    }

    public boolean hasAbstractKit()
    {
        return abstractKit != null;
    }

    public Game build()
    {
        Game game = Game.newInstance( name );
        game.setSpawnLocation( lobby );
        game.setIronGenerator( ironGenerator );
        game.setExperienceGenerator( expGenerator );
        game.setKit( abstractKit != null ? abstractKit : new DefaultKit() );
        game.setMinPlayers( minPlayers );
        game.setMaxPlayers( maxPlayers );
        game.setCount( count );
        game.setMaxPoints( maxPoints );
        return game;
    }

    public static GameBuilder builder()
    {
        return new GameBuilder();
    }
    
}
