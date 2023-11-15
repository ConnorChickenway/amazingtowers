package xyz.connorchickenway.towers.game.manager.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.entity.manager.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class ProtectionListener implements Listener
{

    private final AmazingTowers plugin = AmazingTowers.getInstance();

    @EventHandler( priority = EventPriority.MONITOR )
    public void onBorderPlace( BlockPlaceEvent event )
    {
        if ( event.isCancelled() ) return;
        GamePlayer gamePlayer = EntityManager.getPlayer( event.getPlayer().getUniqueId() );
        if ( gamePlayer.isInGame() )
        {
            Block block = event.getBlock();
            Game game = gamePlayer.getGame();
            if ( game.getBorder().isIn( block.getLocation() ) ) return;
            event.setCancelled( true );
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onPoolBreak( BlockBreakEvent event )
    {
        if ( event.isCancelled() ) return;
        GamePlayer gamePlayer = EntityManager.getPlayer( event.getPlayer().getUniqueId() );
        if ( gamePlayer.isInGame() )
        {
            Block block = event.getBlock();
            Game game = gamePlayer.getGame();
            if ( game.getRed().getPool().isInWithMarge( block.getLocation(), 1 ) ||
            game.getBlue().getPool().isInWithMarge( block.getLocation(), 1 ) )
                event.setCancelled( true );
        }
    }

    @EventHandler
    public void onExplodePool( EntityExplodeEvent event )
    {
        Game game = plugin.getGameManager().getGame( event.getLocation().getWorld().getName() );
        if ( game != null )
        {
            List<Block> blockList = new ArrayList<>( event.blockList() );
            boolean isRemoved = false;
            for ( Block block : event.blockList() )
                if ( game.getRed().getPool().isIn( block.getLocation() ) ||
                        game.getBlue().getPool().isIn( block.getLocation() ) )
                {
                    blockList.remove( block );
                    isRemoved = true;
                }
            if ( isRemoved )
            {
                event.blockList().clear();
                event.blockList().addAll( blockList );
            }
        }
    }


}
