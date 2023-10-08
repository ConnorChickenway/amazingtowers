package xyz.connorchickenway.towers.game.manager;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.game.team.Team;
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.ItemUtils;
import xyz.connorchickenway.towers.utilities.MetadataUtils;
import xyz.connorchickenway.towers.utilities.location.Location;

public class GameListener implements Listener
{

    private final AmazingTowers plugin = AmazingTowers.getInstance();
    private final ItemStack lapislazuli;

    public GameListener()
    {
        if ( NMSVersion.isNewerVersion )
            lapislazuli = new ItemStack( Material.LAPIS_LAZULI, 64 );
        else 
        {
            Dye dye = new Dye( DyeColor.BLUE );
            lapislazuli = dye.toItemStack( 64 );
        }    
    }

    @EventHandler
    public void onDeath( PlayerDeathEvent event )
    {
        GamePlayer gPlayer = plugin.getEntityManager().get( event.getEntity().getUniqueId() );
        if ( !gPlayer.isInGame() ) return;
        gPlayer.getGame().death( event );
    }

    @EventHandler
    public void onRespawn( PlayerRespawnEvent event )
    {
        GamePlayer gPlayer = plugin.getEntityManager().get( event.getPlayer().getUniqueId() );
        if ( !gPlayer.isInGame() ) return;
        gPlayer.getGame().respawn( event );
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onChat( AsyncPlayerChatEvent event )
    {
        if ( event.isCancelled() || !StaticConfiguration.chat_enabled ) return;
        GamePlayer gPlayer = plugin.getEntityManager().get( event.getPlayer().getUniqueId() );
        if ( !gPlayer.isInGame() ) return;
        gPlayer.getGame().chat( event );
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onDamage( EntityDamageEvent event )
    {
        if ( event.isCancelled() ) return;
        Entity entity = event.getEntity();
        if ( !( entity instanceof Player ) ) return;
        GamePlayer gPlayer = plugin.getEntityManager().get( entity.getUniqueId() );
        if ( !gPlayer.isInGame() ) return;
        Game game = gPlayer.getGame();
        if ( event.getCause() == DamageCause.VOID )
        {
            ( ( HumanEntity ) entity ).setHealth( 0.0D );
            return;
        }    
        if ( game.isLobby() || !game.hasTeam( entity.getUniqueId() ) )
            event.setCancelled( true );
    }

    @EventHandler
    public void onInteract( PlayerInteractEvent event )
    {
        if ( event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR ) return;
        ItemStack itemStack = event.getItem();
        if ( itemStack == null ) return;
        Player player = event.getPlayer();
        if ( !MetadataUtils.has( player, "items-game" ) ) return;
        GamePlayer gPlayer = plugin.getEntityManager().get( player );
        if ( gPlayer.isInGame() )
        {
            Game game = gPlayer.getGame();
            if ( game.isState( GameState.FINISH ) ) return;
            boolean isQuit = false;
            if ( itemStack.equals( ItemUtils.quitItem ) )
            {
                if ( GameMode.isMultiArena() )
                {
                    game.leave( gPlayer, true );
                    Location lobby = StaticConfiguration.spawn_location;
                    if ( lobby != null ) lobby.teleport( player );
                } else
                {

                }
                isQuit = true;
            }
            if ( !isQuit )
            {
                ItemStack[] items = ( ItemStack[] ) MetadataUtils.get( player, "items-game" );
                Team previousTeam = game.getTeam(  player.getUniqueId() );
                boolean joined = false;
                if ( itemStack.equals( items[ 0 ] ) )
                {    
                    joined = game.getRed().addPlayer( player, game.getBlue().getSizeOnline() );
                    if ( game.isLobby() )
                    {
                        if ( joined )
                        {
                            if ( items[1].getEnchantments().size() >= 1 )
                            {
                                ItemUtils.removeGlow( items[1] );
                                player.getInventory().setItem( StaticConfiguration.blue_position, items[1] );
                            }
                            ItemUtils.setGlow( items[0] );
                            player.getInventory().setItem( StaticConfiguration.red_position, items[0] );
                            player.updateInventory();
                        }
                    }
                }
                else if ( itemStack.equals( items[1] ) )
                {   
                    joined = game.getBlue().addPlayer( player, game.getRed().getSizeOnline() );
                    if ( game.isLobby() )
                    {
                        if ( joined )
                        {
                            if ( items[0].getEnchantments().size() >= 1 )
                            {
                                ItemUtils.removeGlow( items[0] );
                                player.getInventory().setItem( StaticConfiguration.red_position, items[0] );
                            }
                            ItemUtils.setGlow( items[1] );
                            player.getInventory().setItem( StaticConfiguration.blue_position, items[1] );
                            player.updateInventory();
                        }
                    }
                }                    
                if ( joined )
                    if ( previousTeam != null )
                        previousTeam.remove( player.getUniqueId() );        
            }
            
            event.setCancelled( true );
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onClick( InventoryClickEvent event )
    {
        ItemStack currentItem = event.getCurrentItem();
        if ( event.isCancelled() || event.getClickedInventory() == null || 
                    currentItem.getType() == Material.AIR ) return;
        HumanEntity player = event.getWhoClicked();
        if ( !MetadataUtils.has( player, "items-game" ) ) return;
        if ( currentItem.equals( ItemUtils.quitItem ) )
            event.setCancelled( true );
        else 
        {
            ItemStack[] items = (ItemStack[]) MetadataUtils.get( player, "items-game" );
            if ( currentItem.equals( items[0] ) || currentItem.equals( items[1] ) )
                event.setCancelled( true );
        }
    }

    //LAPIS LAZULI
    @EventHandler( priority =  EventPriority.MONITOR )
    public void onEnchantInventory( InventoryOpenEvent event )
    {
        if ( event.isCancelled() ) return;
        Inventory inventory = event.getInventory();
        if ( !( inventory instanceof EnchantingInventory ) ) return;
        inventory.setItem( 1, lapislazuli );
    }

    @EventHandler
    public void onDropItem( PlayerDropItemEvent event )
    {
        if ( !StaticConfiguration.drop_armor ) return;
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if ( ItemUtils.isArmorLeather( itemStack.getType() ) )
            itemStack.setType( Material.AIR );
    }

    @EventHandler
    public void onCloseInventory( InventoryCloseEvent event )
    {
        Inventory inventory = event.getInventory();
        if ( !(inventory instanceof EnchantingInventory ) ) return;
        inventory.setItem( 1, null ); 
    }

    @EventHandler
    public void onInteractInventory( InventoryClickEvent event )
    {
        Inventory inventory = event.getClickedInventory();
        if ( inventory instanceof EnchantingInventory )
        {
            if ( event.getSlot() == 1 )
                event.setCancelled( true );
        }
    }

    /**@EventHandler( priority =  EventPriority.MONITOR ) 
    public void onBreak( BlockBreakEvent event )
    {
        if ( event.isCancelled() ) return;
        Player player = event.getPlayer();
        GamePlayer gPlayer = plugin.getEntityManager().get( player.getUniqueId() );
        if ( gPlayer.isInGame() )
        {
            Game game = gPlayer.getGame();
            if ( game.isState( GameState.LOBBY ) || game.isState( GameState.STARTING ) || !game.hasTeam( player.getUniqueId() ) )
            event.setCancelled( true );
        }
    }**/

    @EventHandler
    public void onJoin( PlayerJoinEvent event )
    {
        Player player = event.getPlayer();
        if ( GameMode.isMultiArena() )
        {
            Location location = StaticConfiguration.spawn_location;
            if ( location != null )
                player.teleport( location.toBukkitLocation() );
        }
        else 
        {
            event.setJoinMessage( null );
            Game game = GameManager.get().getFirstGame();
            if ( game != null )
            {
                GamePlayer gamePlayer = plugin.getEntityManager().get( player );
                if( gamePlayer != null )
                    game.join( gamePlayer );
            }
        }
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event )
    {
        GamePlayer gamePlayer = plugin.getEntityManager().get( event.getPlayer() );
        if ( gamePlayer != null && gamePlayer.isInGame() )
        {
            Game game = gamePlayer.getGame();
            game.leave( gamePlayer, true );
            event.setQuitMessage( null );
        }
    }
    
}
