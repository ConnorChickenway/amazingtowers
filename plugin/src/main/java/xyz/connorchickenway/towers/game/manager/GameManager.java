package xyz.connorchickenway.towers.game.manager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.google.common.collect.Maps;
import com.google.gson.stream.JsonReader;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.builder.GameBuilder;
import xyz.connorchickenway.towers.game.sign.GameSign;
import xyz.connorchickenway.towers.game.sign.manager.SignManager;
import xyz.connorchickenway.towers.game.world.BukkitWorldLoader;
import xyz.connorchickenway.towers.game.world.GameWorld;
import xyz.connorchickenway.towers.game.world.SlimeWorldLoader;
import xyz.connorchickenway.towers.game.world.WorldLoader;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.ManagerController;
import xyz.connorchickenway.towers.utilities.StringUtils;

public class GameManager extends ManagerController
{

    private Map<String, Game> games = Maps.newHashMap();
    private SignManager signManager;
    private File gameFolder;

    public GameManager( AmazingTowers plugin )
    {
        super( plugin );
        this.gameFolder = new File( plugin.getDataFolder(), "arenas" );
        if ( !gameFolder.exists() ) gameFolder.mkdir();
    }

    @Override
    public void load()
    {
        this.loadSignManager();
        this.loadGames();
        plugin.getServer().getPluginManager().registerEvents( new GameListener(), plugin );
    }

    public void loadSignManager()
    {
        if ( GameMode.isMultiArena() )
        {
            this.signManager = new SignManager( plugin );
            this.signManager.load();
        }
    }

    private void loadGames()
    {
        for ( File file : gameFolder.listFiles() )
            if ( file.isDirectory() )
            {
                File arenaFile = new File( file, file.getName() + ".json" );
                if ( !arenaFile.exists() )
                {
                    System.out.println( "Cannot read the arena file from " + file.getName() + " directory! (It not exists)" );
                    continue;
                }
                try
                {
                    JsonReader jsonReader = new JsonReader( new FileReader( arenaFile ) );
                    GameBuilder gBuilder = AmazingTowers.GSON.fromJson( jsonReader, GameBuilder.class );
                    jsonReader.close();
                    if ( !gBuilder.hasEverything() )
                    {
                        System.out.println( "Cannot convert " + arenaFile.getName() + " file to arena because it lacks of something." );
                        continue;
                    }
                    GameWorld gameWorld = gBuilder.getWorldLoader() == WorldLoader.BUKKIT ? new BukkitWorldLoader( gBuilder.getName() ) :   
                        AmazingTowers.SLIME_PLUGIN != null ? new SlimeWorldLoader( gBuilder.getName() ) : null;
                    System.out.println( gBuilder.getName() );    
                    if ( gameWorld != null )
                    {
                        boolean load = gameWorld.load();
                        if ( !load )
                        {
                            System.out.println( "cannot load" );
                            continue;
                        }
                        gBuilder.setGameWorld( gameWorld );
                        System.out.println( "world loaded.! " + gBuilder.getName() );
                    }
                    else 
                    {
                        System.out.println( "cannot load | variable is null" );
                        continue;
                    }
                    Game game = gBuilder.build();
                    addGame( game ); 
                    System.out.println( "Arena " + gBuilder.getName() + " loaded correctly.!" );
                    if ( GameMode.isMultiArena() )
                    {
                        DataInputStream dataInputStream = null;
                        try 
                        {
                            File signFile = new File( file, gBuilder.getName() + ".sign" );
                            if ( signFile.exists() )
                            {
                                dataInputStream = new DataInputStream( new FileInputStream( signFile ) );
                                int x = dataInputStream.readInt(), 
                                    y = dataInputStream.readInt(),
                                    z = dataInputStream.readInt();    
                                World world = StaticConfiguration.spawn_location != null ? 
                                    StaticConfiguration.spawn_location.getWorld() : Bukkit.getWorld( StringUtils.DEFAULT_WORLD_NAME );
                                if ( world != null )
                                {
                                    BlockState blockState = world.getBlockAt( x, y, z ).getState();
                                    if ( blockState instanceof Sign )
                                    {
                                        GameSign gameSign = new GameSign( game, ( Sign ) blockState );
                                        game.setGameSign( gameSign );
                                        gameSign.update();
                                        signManager.add( gameSign );
                                    }else 
                                        System.out.println( "Could not load the block because it's not a sign." );
                                }    
                            }
                        }catch( Exception ex ) 
                        {
                            System.out.println( "Could not load the sign file because it lacks of data.!" );
                        }
                        if ( dataInputStream != null )
                            dataInputStream.close();
                    }
                    else break;
                }catch( Exception ex )
                {
                    ex.printStackTrace();
                }
            }
    }

    @Override
    public void disable()
    {

    }

    public Game getFirstGame()
    {
        for ( Game game : games.values() )
            return game;
        return null;
    }

    public Game getGame( String name )
    {
        return games.get( name.toLowerCase( Locale.ENGLISH ) );
    }

    public void addGame( Game game )
    {
        games.put( game.getGameName().toLowerCase(), game );
    }

    public boolean hasGame( String name )
    {
        return games.containsKey( name.toLowerCase() );
    }

    public SignManager getSignManager()
    {
        return signManager;
    }

    public File getGameFolder()
    {
        return gameFolder;
    }

    public static GameManager get()
    {
        return AmazingTowers.getInstance().getGameManager();
    }

}
