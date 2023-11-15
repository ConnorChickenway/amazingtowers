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
import xyz.connorchickenway.towers.game.manager.listener.GameListener;
import xyz.connorchickenway.towers.game.manager.listener.ProtectionListener;
import xyz.connorchickenway.towers.game.sign.GameSign;
import xyz.connorchickenway.towers.game.sign.manager.SignManager;
import xyz.connorchickenway.towers.game.world.BukkitWorldLoader;
import xyz.connorchickenway.towers.game.world.GameWorld;
import xyz.connorchickenway.towers.game.world.SlimeWorldLoader;
import xyz.connorchickenway.towers.game.world.WorldLoader;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.Logger;
import xyz.connorchickenway.towers.utilities.ManagerController;
import xyz.connorchickenway.towers.utilities.StringUtils;

public class GameManager extends ManagerController
{

    private final Map<String, Game> games = Maps.newHashMap();
    private SignManager signManager;
    private final File gameFolder;

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
        plugin.getServer().getPluginManager().registerEvents( new ProtectionListener(), plugin );
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
                    Logger.info( "Cannot read the arena file from " + file.getName() + " directory! (It not exists)" );
                    continue;
                }
                try
                {
                    JsonReader jsonReader = new JsonReader( new FileReader( arenaFile ) );
                    GameBuilder gBuilder = AmazingTowers.GSON.fromJson( jsonReader, GameBuilder.class );
                    jsonReader.close();
                    if ( !gBuilder.hasEverything() )
                    {
                        Logger.info( "Cannot convert " + arenaFile.getName() + " file to arena because it lacks of something." );
                        continue;
                    }
                    GameWorld gameWorld = gBuilder.getWorldLoader() == WorldLoader.BUKKIT ? new BukkitWorldLoader( gBuilder.getName() ) :   
                        AmazingTowers.SLIME_PLUGIN != null ? new SlimeWorldLoader( gBuilder.getName() ) : null;
                    if ( gameWorld != null )
                    {
                        boolean load = gameWorld.load();
                        if ( !load )
                        {
                            Logger.info( "Cannot load " + gBuilder.getName() + " world.!" );
                            continue;
                        }
                        gBuilder.setGameWorld( gameWorld );
                        Logger.info( "World loaded.! " + gBuilder.getName() );
                    }
                    else 
                    {
                        Logger.info( "Cannot load " + gBuilder.getName() + " | game world is null" );
                        continue;
                    }
                    Game game = gBuilder.build();
                    addGame( game ); 
                    Logger.info( "Arena " + gBuilder.getName() + " loaded correctly.!" );
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
                                        GameSign gameSign = new GameSign( game, blockState.getLocation() );
                                        game.setGameSign( gameSign );
                                        gameSign.update();
                                        signManager.add( gameSign );
                                    }else 
                                        Logger.info( "Could not load the block because it's not a sign. Arena: " + gBuilder.getName() );
                                }    
                            }
                        }catch( Exception ex ) 
                        {
                            Logger.info( "Could not load the sign file because it lacks of data. " + gBuilder.getName() + ".sign file.!" );
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
