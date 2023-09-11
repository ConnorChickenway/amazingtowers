package xyz.connorchickenway.towers.game.manager;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.sign.manager.SignManager;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.ManagerController;

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
