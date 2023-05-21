package xyz.connorchickenway.towers.utilities.location;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class Location 
{

    private final String world;
    private double x, y, z;
    private float yaw, pitch;

    public Location( org.bukkit.Location location )
    {
        this( location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch() );
    }

    public Location( String world, double x, double y, double z )
    {
        this( world, x, y, z, 0.0f, 0.0f );
    }

    public Location( String world, double x, double y, double z, float yaw, float pitch )
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void teleport( Player player )
    {
        org.bukkit.Location loc = this.toBukkitLocation();
        if ( loc == null )
        {
            player.sendMessage( "§cThe world " + world + " has not been loaded - " +
                    "Report to staff this issue" );
            return;
        }
        player.teleport( loc, TeleportCause.PLUGIN );
    }

    public void dropItem( Material material ) 
    {
        org.bukkit.Location location = this.toBukkitLocation();
        if (location == null) return;
        location.getWorld().dropItemNaturally( location, new ItemStack( material ) );
    }


    public org.bukkit.Location toBukkitLocation()
    {
        World world = this.getWorld();
        if ( world == null ) return null;
        return new org.bukkit.Location( world, x, y, z, yaw, pitch );
    }

    public String serializable()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( x ).append( ";" ).append( y ).append( ";" ).append( z ).append( ";" );
        if ( yaw != 0 && pitch != 0 )
        {
            builder.append( yaw ).append( ";" )
                .append( pitch ).append( ";" );
        }
        builder.append( world );
        return builder.toString();
    }

    public String getNameWorld()
    {
        return world;
    }

    public World getWorld()
    {
        return Bukkit.getWorld( world );
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public static Location fromString( String loc )
    {
        if ( loc == null )
        {
            return null;
        }
        String[] split = loc.split( ";" );
        if ( split.length > 3 )
        {
            return new Location( split[ split.length - 1 ] , 
                        Double.valueOf( split[ 0 ] ), 
                        Double.valueOf( split[ 1 ] ), 
                        Double.valueOf( split[ 2 ] ), 
                        Float.valueOf( split[ 3 ] ), 
                        Float.valueOf( split[ 4 ] ) );
        }
        return new Location( split[ split.length - 1 ] , 
                        Double.valueOf( split[ 0 ] ), 
                        Double.valueOf( split[ 1 ] ), 
                        Double.valueOf( split[ 2 ] ) );
    }
    
}
