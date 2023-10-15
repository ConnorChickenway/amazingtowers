package xyz.connorchickenway.towers.game.builder.setup;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.builder.GameBuilder;
import xyz.connorchickenway.towers.game.builder.setup.wand.Wand;
import xyz.connorchickenway.towers.game.world.GameWorld;
import xyz.connorchickenway.towers.game.world.SlimeWorldLoader;
import xyz.connorchickenway.towers.utilities.ItemUtils;
import xyz.connorchickenway.towers.utilities.MetadataUtils;
import xyz.connorchickenway.towers.utilities.StringUtils;

public class SetupListener implements Listener
{

    @EventHandler( ignoreCancelled = true )
    public void onInteract( PlayerInteractEvent event )
    {
        Player player = event.getPlayer();
        if ( ItemUtils.wandItemStack.equals( event.getItem() ) && hasSession( player ) )
        {    
            Action action = event.getAction();
            Wand wand = GameBuilder.getSession( player ).getWand();
            Location blockLocation = fix( event.getClickedBlock().getLocation() );
            event.setCancelled( true );
            boolean set = false;
            if ( action == Action.LEFT_CLICK_BLOCK )
            {
                if ( blockLocation.equals( wand.getPosition1() ) ) return;
                set = wand.setPosition1( blockLocation );
            }
            else if ( action == Action.RIGHT_CLICK_BLOCK )
            {
                if ( blockLocation.equals( wand.getPosition2() ) ) return;
                set = wand.setPosition2( blockLocation ); 
            }
            if ( set )
            {
                String str = ChatColor.GRAY + "%s position set to (x:%.1f y:%d z:%.1f )"; 
                player.sendMessage( String.format( str , ( action == Action.LEFT_CLICK_BLOCK ? "First" : "Second" ), 
                        blockLocation.getX(), blockLocation.getBlockY(), blockLocation.getZ() ) );
            }
        }
    } 
    
    @EventHandler
    public void onQuit( PlayerQuitEvent event )
    {
        Player player = event.getPlayer();
        if ( hasSession( player ) )
        {
            SetupSession session = GameBuilder.getSession( player );
            GameWorld gWorld = session.getBuilder().getGameWorld();
            if ( gWorld != null )
            {
                World defaultWorld = StaticConfiguration.spawn_location != null ? 
                                    StaticConfiguration.spawn_location.getWorld() : Bukkit.getWorld( StringUtils.DEFAULT_WORLD_NAME );
                for ( Player pl : gWorld.getWorld().getPlayers() )
                    pl.teleport( defaultWorld.getSpawnLocation() );
                if ( player.getWorld() != defaultWorld )
                    player.teleport( defaultWorld.getSpawnLocation() );    
                gWorld.unload( false );
                if ( gWorld instanceof SlimeWorldLoader )
                {
                    SlimeLoader loader = SlimeWorldLoader.getLoader();
                    try
                    {
                        loader.deleteWorld( gWorld.getWorldName() );
                    } catch ( Exception e )
                    {
                        
                    } 
                }
            }
            player.setFlySpeed( 0.2f );
            player.getInventory().clear();
            MetadataUtils.remove( player, "setup-session" );
        }
    }

    private Location fix( Location location )
    {
        return location.clone().add( ( ( location.getX() < 0.0 ) ? 1 : 0 ), 0.0, ( ( location.getZ() < 0.0 ) ? 1 : 0 ) );
    }

    private boolean hasSession( Player player )
    {
        return MetadataUtils.has( player , "setup-session" );
    }
    
}
