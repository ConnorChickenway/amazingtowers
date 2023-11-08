package xyz.connorchickenway.towers.game.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.bukkit.entity.Player;

import com.google.gson.JsonIOException;
import com.google.gson.annotations.SerializedName;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.builder.setup.SetupSession;
import xyz.connorchickenway.towers.game.kit.Kit;
import xyz.connorchickenway.towers.game.world.BukkitWorldLoader;
import xyz.connorchickenway.towers.game.world.GameWorld;
import xyz.connorchickenway.towers.game.world.WorldLoader;
import xyz.connorchickenway.towers.game.kit.DefaultKit;
import xyz.connorchickenway.towers.utilities.Cuboid;
import xyz.connorchickenway.towers.utilities.MetadataUtils;
import xyz.connorchickenway.towers.utilities.location.Location;

public class GameBuilder 
{

    private String name;
    private Location lobby;

    @SerializedName( "iron-generator" ) 
    private Location ironGenerator; 
    @SerializedName( "experience-generator" )
    private Location expGenerator;
    @SerializedName( "blue-spawn" )
    private Location blueSpawn; 
    @SerializedName( "red-spawn" ) 
    private Location redSpawn;
    @SerializedName( "min-players" )
    private int minPlayers;
    @SerializedName( "max-players" ) 
    private int maxPlayers;
    private int count;
    @SerializedName( "max-points" )
    private int maxPoints;
    @SerializedName( "red-pool" )
    private Cuboid redPool;
    @SerializedName( "blue-pool" )
    private Cuboid bluePool;
    private Kit kit;

    private transient GameWorld gameWorld;
    @SerializedName( "world-loader" )
    private WorldLoader worldLoader;

    public GameBuilder()
    {
        this.minPlayers = 2;
        this.maxPlayers = 12;
        this.count = -1;
        this.maxPoints = 10;
    }

    public GameBuilder setName( String name )
    {
        this.name = name;
        return this;
    }
    
    public String getName()
    {
        return name;
    }

    public void setLobby( org.bukkit.Location bukkitLocation )
    {
        this.lobby = new Location( bukkitLocation );
    }
    
    public Location getLobby()
    {
        return lobby;
    }

    public void setIronGenerator( org.bukkit.Location bukkitLocation )
    {
        this.ironGenerator = new Location( bukkitLocation, false );
    }
    
    public Location getIronGenerator()
    {
        return ironGenerator;
    }
    
    public void setExpGenerator( org.bukkit.Location bukkitLocation )
    {
        this.expGenerator = new Location( bukkitLocation, false );
    }
    
    public Location getExpGenerator()
    {
        return expGenerator;
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
    
    public void setBlueSpawn( org.bukkit.Location bukkitLocation )
    {
        this.blueSpawn = new Location( bukkitLocation );
    }
    
    public Location getBlueSpawn()
    {
        return blueSpawn;
    }

    public void setRedSpawn( org.bukkit.Location bukkitLocation )
    {
        this.redSpawn = new Location( bukkitLocation );
    }
    
    public Location getRedSpawn()
    {
        return redSpawn;
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

    public Kit setKit( Kit kit )
    {
        this.kit = kit;
        return this.kit;
    }
    
    public Kit getKit()
    {
        return kit;
    }

    public boolean hasKit()
    {
        return this.kit != null;
    }
    
    public void setBluePool( Cuboid bluePool )
    {
        this.bluePool = bluePool;
    }
    
    public void setRedPool( Cuboid redPool )
    {
        this.redPool = redPool;
    }

    public void setPool( Cuboid cuboid, Team team )
    {
        if ( team == Team.RED )
            setRedPool( cuboid );
        else 
            setBluePool( cuboid );    
    }

    public GameBuilder setGameWorld( GameWorld gameWorld )
    {
        this.gameWorld = gameWorld;
        this.worldLoader = (gameWorld instanceof BukkitWorldLoader ? WorldLoader.BUKKIT : WorldLoader.SLIME); 
        return this;
    }
    
    public GameWorld getGameWorld()
    {
        return gameWorld;
    }
    
    public WorldLoader getWorldLoader()
    {
        return worldLoader;
    }

    public boolean hasEverything()
    {
        return lobby != null && ironGenerator != null &&
            expGenerator != null && blueSpawn != null &&
            redSpawn != null && minPlayers > 0 &&
            maxPlayers > 0 && count > 0 &&
            maxPoints > 0 && redPool != null && 
            bluePool != null;
    }

    public Game build()
    {
        Game game = Game.newInstance( name );
        game.setSpawnLocation( lobby );
        game.setIronGenerator( ironGenerator );
        game.setExperienceGenerator( expGenerator );
        game.setKit( kit != null ? kit : DefaultKit.getInstance() );
        game.setMinPlayers( minPlayers );
        game.setMaxPlayers( maxPlayers );
        game.setCount( count );
        game.setMaxPoints( maxPoints );
        game.getRed().setPool( redPool );
        game.getRed().setSpawnLocation( redSpawn );
        game.getBlue().setPool( bluePool );
        game.getBlue().setSpawnLocation( blueSpawn );
        game.setGameWorld( gameWorld );
        return game;
    }

    public void save()
    {
        File gameFolder = getGameFolder();
        gameFolder.mkdir();
        gameWorld.backup();
        try
        {
            Writer fileWriter = new FileWriter( new File( gameFolder, name + ".json" ) );
            AmazingTowers.GSON.toJson( this, fileWriter );
            fileWriter.flush();
            fileWriter.close();
        } catch ( JsonIOException e )
        {
            e.printStackTrace();
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public File getGameFolder()
    {
        return new File( AmazingTowers.getInstance().getGameManager().getGameFolder(),  name );
    }

    public static GameBuilder builder()
    {
        return new GameBuilder();
    }
    
    public static SetupSession getSession( Player player )
    {
        return ( SetupSession ) MetadataUtils.get( player, "setup-session" );
    }

}
