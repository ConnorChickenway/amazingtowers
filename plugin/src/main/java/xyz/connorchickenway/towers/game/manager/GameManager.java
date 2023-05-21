package xyz.connorchickenway.towers.game.manager;

import java.util.Locale;
import java.util.Map;

import org.bukkit.event.Listener;

import com.google.common.collect.Maps;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.sign.manager.SignManager;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.ManagerController;

public class GameManager extends ManagerController implements Listener
{

    private Map<String, Game> games = Maps.newHashMap();
    private SignManager signManager;

    public GameManager( AmazingTowers plugin )
    {
        super( plugin );
    }

    @Override
    public void load()
    {
        this.loadSignManager();
        this.loadGames();
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

    public Game getGame( String name )
    {
        return games.get( name.toLowerCase( Locale.ENGLISH ) );
    }
    
    public SignManager getSignManager()
    {
        return signManager;
    }

    public static GameManager get()
    {
        return AmazingTowers.getInstance().getGameManager();
    }
    

}
