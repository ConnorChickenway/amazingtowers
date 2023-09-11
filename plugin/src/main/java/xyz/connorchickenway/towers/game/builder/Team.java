package xyz.connorchickenway.towers.game.builder;

import org.bukkit.ChatColor;

public enum Team 
{
    
    RED( ChatColor.RED ), BLUE( ChatColor.BLUE );
    
    private ChatColor chatColor;

    Team( ChatColor chatColor )
    {
        this.chatColor = chatColor;
    }

    @Override
    public String toString()
    {
        return chatColor + name();
    }

    public static Team get( String str )
    {
        for( Team teamName : values() )
        {
            if ( teamName.name().equalsIgnoreCase( str ) )
                return teamName;
        }
        return null;
    }

}
