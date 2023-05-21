package xyz.connorchickenway.towers.game.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.entity.inventory.InventorySession;
import xyz.connorchickenway.towers.utilities.GameMode;

public class GamePlayer 
{

    private final UUID id;
    private InventorySession inventorySession;
    private Game game;

    private GamePlayer( UUID id )
    {
        this.id = id;
        if ( GameMode.isMultiArena() )
            this.inventorySession = new InventorySession( id );
    }
    
    public Location getLocation()
    {
        return toBukkitPlayer().getLocation();
    }

    public void sendMessage( String text )
    {
        toBukkitPlayer().sendMessage( text );
    }

    public void sendMessage( String... text )
    {
        toBukkitPlayer().sendMessage( text );
    }

    public UUID getUniqueId()
    {
        return id;
    }    

    public Player toBukkitPlayer()
    {
        return Bukkit.getPlayer( this.id );
    }

    public void setGame( Game game )
    {
        this.game = game;
    }

    public boolean isInGame()
    {
        return this.game != null;
    }

    public Game getGame()
    {
        return game;
    }
    
    public InventorySession getInventorySession()
    {
        return inventorySession;
    }

    public static GamePlayer create( UUID uuid )
    {
        return new GamePlayer( uuid );
    }

}
