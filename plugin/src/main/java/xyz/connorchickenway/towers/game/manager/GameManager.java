package xyz.connorchickenway.towers.game.manager;

import java.util.Locale;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.common.collect.Maps;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
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
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
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

    @EventHandler
    public void onDeath( PlayerDeathEvent event )
    {
        GamePlayer gPlayer = plugin.getEntityManager().get( event.getEntity().getUniqueId() );
        if ( !gPlayer.isInGame() ) return;
        gPlayer.getGame().death( event );
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onChat( AsyncPlayerChatEvent event )
    {
        if ( event.isCancelled() || !StaticConfiguration.chat_enabled ) return;
        GamePlayer gPlayer = plugin.getEntityManager().get( event.getPlayer().getUniqueId() );
        if ( !gPlayer.isInGame() ) return;
        gPlayer.getGame().chat( event );
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
