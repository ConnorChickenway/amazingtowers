package xyz.connorchickenway.towers.cmd;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.cmd.abstraction.CommandReason;
import xyz.connorchickenway.towers.cmd.abstraction.ConfigSubCommand;
import xyz.connorchickenway.towers.utilities.ManagerController;
import xyz.connorchickenway.towers.utilities.StringUtils;

public class CommandManager extends ManagerController implements CommandExecutor
{

    private List<AbstractCommand> subCommandList = Lists.newArrayList();

    public CommandManager( AmazingTowers plugin )
    {
        super( plugin );
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
    {
        if ( !sender.hasPermission( "towers.admin" ) )
        {
            sender.sendMessage( StringUtils.color( "AmazingTowers v" + plugin.getDescription().getVersion() + " | Author: ConnorChickenway" ) );
            return false;
        }
        if ( !( sender instanceof Player ) && 
                !args[ 0 ].equalsIgnoreCase( "config" ) )
        {
            sender.sendMessage( StringUtils.color( "&cThe console cannot execute that command!." ) );
            return false;
        }
        if ( args.length <= 0 )
        {
            help( sender, label );
            return false;
        }
        for ( AbstractCommand acmd : subCommandList )
        {
            if ( !args[ 0 ].equalsIgnoreCase( acmd.getSubCommand() ) ) continue;
            if ( args.length != acmd.getMaxArgs() )
            {
                sender.sendMessage( StringUtils.color( "&cUsage: " + acmd.getUsage() ) );
                return false;
            }
            CommandReason cr = acmd.executeCommand( sender, args );
            switch( cr )
            {
                case WRONG_ARGS:
                    sender.sendMessage( acmd.missingArguments( label, args[ 0 ] ) );
                    break;
                case USAGE:
                    sender.sendMessage( StringUtils.color( "&cUsage: /" + label + " " + acmd.getUsage() ) );
                    break;
                case ERROR:
                    sender.sendMessage( acmd.error() );
                    break;
                    default:
                    break; 
            }
            return cr.getReturn();
        }
        return false;
    }

    private void help( CommandSender sender, String label ) 
    {
        String[] message = new String[ subCommandList.size() + 1 ];
        message[ 0 ] = ChatColor.RED + "Usage:";
        for ( int x = 0; x < subCommandList.size(); x++ )
        {   
            AbstractCommand subCommand = subCommandList.get( x );
            message[ x + 1 ] = ChatColor.RED + "  /" + label + " " + subCommand.getUsage();
        }
        sender.sendMessage( message );
    }

    @Override
    public void load()
    {
        plugin.getCommand( "towers" ).setExecutor( this );
        subCommandList.add( new ConfigSubCommand() );
    }

    @Override
    public void disable()
    {
        subCommandList.clear();
    }
    
}
