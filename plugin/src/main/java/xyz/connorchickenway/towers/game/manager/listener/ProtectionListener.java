package xyz.connorchickenway.towers.game.manager.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.entity.manager.EntityManager;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.nms.NMSVersion;

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

    @EventHandler
    public void onTeamSpawnBlockBreak( BlockBreakEvent event )
    {
        if ( event.isCancelled() ) return;
        GamePlayer gamePlayer = EntityManager.getPlayer( event.getPlayer().getUniqueId() );
        if ( gamePlayer.isInGame() )
        {
            Game game = gamePlayer.getGame();
            if ( !game.isState( GameState.GAME ) ) return;
            Block block = event.getBlock();
            if ( game.getBlueSpawnCuboid().isIn( block.getLocation() ) || game.getRedSpawnCuboid().isIn( block.getLocation() ) )
                event.setCancelled( true );
        }
    }

    @EventHandler
    public void onTeamSpawnBlockPlace( BlockPlaceEvent event )
    {
        if ( event.isCancelled() ) return;
        GamePlayer gamePlayer = EntityManager.getPlayer( event.getPlayer().getUniqueId() );
        if ( gamePlayer.isInGame() )
        {
            Game game = gamePlayer.getGame();
            if ( !game.isState( GameState.GAME ) ) return;
            Block block = event.getBlock();
            if ( game.getBlueSpawnCuboid().isIn( block.getLocation() ) || game.getRedSpawnCuboid().isIn( block.getLocation() ) )
                event.setCancelled( true );
        }
    }

    @EventHandler
    public void onTeamSpawnEntityExplode( EntityExplodeEvent event )
    {
        Game game = plugin.getGameManager().getGame( event.getLocation().getWorld().getName() );
        if ( game != null )
        {
            List<Block> blockList = new ArrayList<>( event.blockList() );
            boolean isRemoved = false;
            for ( Block block : event.blockList() )
                if ( game.getRedSpawnCuboid().isIn( block.getLocation() ) ||
                        game.getBlueSpawnCuboid().isIn( block.getLocation() ) )
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

    @EventHandler
    public void onFallingBlock( EntityChangeBlockEvent event )
    {
        if ( event.isCancelled() ) return;
        if ( event.getEntityType() != EntityType.FALLING_BLOCK ) return;
        FallingBlock fallingBlock = ( FallingBlock ) event.getEntity();
        Game game = plugin.getGameManager().getGame( fallingBlock.getWorld().getName() );
        if ( game != null )
        {
            if ( game.getRedSpawnCuboid().isIn( fallingBlock.getLocation() ) ||
                    game.getBlueSpawnCuboid().isIn( fallingBlock.getLocation() ) ||
                    game.getRed().getPool().isIn( fallingBlock.getLocation() ) ||
                    game.getBlue().getPool().isIn( fallingBlock.getLocation() ))
            {
                Block block = event.getBlock();
                block.getWorld().dropItemNaturally( block.getLocation(),
                        new ItemStack( NMSVersion.isNewerVersion ?
                                fallingBlock.getBlockData().getMaterial() : fallingBlock.getMaterial() ) );
                fallingBlock.remove();
                event.setCancelled( true );
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend( BlockPistonExtendEvent event )
    {
        if ( event.isCancelled() ) return;
        Block block = event.getBlock();
        Game game = plugin.getGameManager().getGame( block.getWorld().getName() );
        if ( game != null )
        {
            if ( game.getRedSpawnCuboid().isInWithMarge( block.getLocation(), 1 ) ||
                    game.getBlueSpawnCuboid().isInWithMarge( block.getLocation(), 1 ) )
                event.setCancelled( true );
            for ( Block bl : event.getBlocks() )
            {
                if ( bl.equals( block ) ) continue;
                if ( game.getRedSpawnCuboid().isInWithMarge( bl.getLocation(), 1 ) ||
                        game.getBlueSpawnCuboid().isInWithMarge( bl.getLocation(), 1 ) )
                {
                    event.setCancelled( true );
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract( BlockPistonRetractEvent event )
    {
        if ( event.isCancelled() ) return;
        if ( event.isSticky() )
        {
            Block block = event.getBlock();
            Game game = plugin.getGameManager().getGame( block.getWorld().getName() );
            if ( game != null )
                for ( Block bl : event.getBlocks() )
                {
                    if ( game.getRedSpawnCuboid().isInWithMarge( bl.getLocation(), 1 ) ||
                            game.getBlueSpawnCuboid().isInWithMarge( bl.getLocation(), 1 ) )
                    {
                        event.setCancelled( true );
                        break;
                    }
                }
        }
    }

}
