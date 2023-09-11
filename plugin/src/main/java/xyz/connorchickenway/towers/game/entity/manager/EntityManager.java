package xyz.connorchickenway.towers.game.entity.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.utilities.ManagerController;

public class EntityManager extends ManagerController implements Listener
{

    private Map<UUID, GamePlayer> players;

    public EntityManager( AmazingTowers plugin )
    {
        super( plugin );
    }

    @Override
    public void load()
    {
        players = new ConcurrentHashMap<>();
        plugin.getServer().getPluginManager().registerEvents( this , plugin );
    }

    @Override
    public void disable()
    {
        players.clear();
    }
    
    @EventHandler( priority =  EventPriority.LOWEST )
    public void onJoin( PlayerJoinEvent event )
    {
        Player player = event.getPlayer();
        players.put( player.getUniqueId() , GamePlayer.create( player ) );
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onQuit( PlayerQuitEvent event ) 
    {
        Player player = event.getPlayer();
        players.remove( player.getUniqueId() );
    }

    public GamePlayer get( Player player )
    {
        return players.get( player.getUniqueId() );
    }

    public GamePlayer get( UUID id )
    {
        return players.get( id );
    }

    public static GamePlayer getPlayer( UUID id )
    {
        return AmazingTowers.getInstance().getEntityManager().get( id );
    }

}
