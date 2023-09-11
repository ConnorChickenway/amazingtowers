package xyz.connorchickenway.towers.cmd;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.utilities.ManagerController;
import xyz.connorchickenway.towers.utilities.Pair;
import xyz.connorchickenway.towers.utilities.StringUtils;

public class CommandManager extends ManagerController implements CommandExecutor
{

    private Set<Pair<SubCommand, Method>> subCommandList;
    private SubCommandListener sCommandListener;

    public CommandManager( AmazingTowers plugin )
    {
        super( plugin );
        this.subCommandList = new HashSet<>();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
    {
        if ( !sender.hasPermission( "towers.admin" ) )
        {
            sender.sendMessage( StringUtils.color( "AmazingTowers v" + plugin.getDescription().getVersion() + " | Author: ConnorChickenway" ) );
            return false;
        }
        if ( args.length <= 0 )
        {
            help( sender, label, false );
            return false;
        }
        boolean isSetup = args[ 0 ].equalsIgnoreCase( "setup" );
        if ( isSetup && args.length <= 1 )
        {
            help( sender, label, true );
            return false;
        }
        Pair<SubCommand, Method> pair = null;
        for ( Pair<SubCommand, Method> f : subCommandList )
        {
            SubCommand key = f.getKey();
            if ( isSetup )
            {
                if ( key.setup_cmd()
                        && args[ 1 ].equalsIgnoreCase( key.subcmd() ) )
                {
                    pair = f;
                    break;
                }
            }
            else 
            {
                if ( !key.setup_cmd() 
                        && args[ 0 ].equalsIgnoreCase( key.subcmd() ) )
                {
                    pair = f;
                    break;
                }
            }
        }
        if ( pair != null )
        {
            SubCommand subCommand = pair.getKey();
            if ( !subCommand.can_console() && !( sender instanceof Player ) )
            {
                sender.sendMessage( StringUtils.color( "&cThe console cannot execute that command!." ) );
                return false;
            }
            final int max_args = subCommand.max_args() + ( subCommand.setup_cmd() ? 1 : 0 );
            if ( args.length != max_args )
            {
                sender.sendMessage( StringUtils.color( "&cUsage: " + this.getUsage( label, subCommand ) ) );
                return false;
            }
            if ( subCommand.builder_cmd() || subCommand.wand_usage() )
            {
                Player player = ( Player ) sender;
                if ( !player.hasMetadata( "setup-session" ) )
                {
                    sender.sendMessage( ChatColor.RED + "You must create an arena to execute that command.!" );
                    return false;
                }
                
            }
            CommandReason cr = null;
            try
            {
                cr = ( CommandReason ) pair.getValue().invoke(  sCommandListener, sender, getArgs( args, isSetup ) );
            } catch ( Exception e )
            {
                e.printStackTrace();
            } 
            if ( cr != null )
            {
                switch ( cr )
                {
                case WRONG_ARGS:
                case USAGE:
                    sender.sendMessage( ChatColor.RED + "Usage: " + ChatColor.GRAY + this.getUsage( label, subCommand ) );
                    break;
                case ERROR:
                    sender.sendMessage( subCommand.error() );
                    break;
                default:
                    break;
                }
                return cr.getReturn();
            }
        }
        return false;
    }

    private void help( CommandSender sender, String label, boolean setup ) 
    {
        List<String> messageList = new ArrayList<>();
        messageList.add( ChatColor.BLUE + "" + ChatColor.BOLD + "AmazingTowers " + ChatColor.DARK_GRAY + "\u00BB " + ChatColor.GRAY + (setup ? "Setup " : "") + "Command list:" );
        for ( Pair<SubCommand, Method> pair : subCommandList )
        {   
            SubCommand subCommand = pair.getKey();
            if ( setup )
            {
                if ( !subCommand.setup_cmd() ) continue;
            }
            else 
            {
                if ( subCommand.setup_cmd() ) continue;
            }
            messageList.add( StringUtils.color( "  &8-&7" + getUsage( label, subCommand ) ) );   
        }
        if ( !setup )
            messageList.add( StringUtils.color( "  &8-&7/" + label + " setup" ) );
        sender.sendMessage( messageList.toArray( new String[ messageList.size() ] ) );
    }

    @Override
    public void load()
    {
        plugin.getCommand( "towers" ).setExecutor( this );
        this.sCommandListener = new SubCommandListener();
        for( Method method : sCommandListener.getClass().getMethods() )
        {
            if ( !method.isAnnotationPresent( SubCommand.class ) ) continue;
            subCommandList.add( new Pair<SubCommand,Method>( 
                    method.getAnnotation( SubCommand.class ), method ) );
        }
    }

    @Override
    public void disable()
    {
        subCommandList = null;
    }

    private String getUsage( String label, SubCommand subCmd )
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "/" + label )
                    .append( " " )
                    .append( subCmd.setup_cmd() ? "setup " : "" )
                    .append( subCmd.subcmd() )
                    .append( " " );
        for ( int x = 0; x < subCmd.usage().length; x++ ) 
        {
            String str = subCmd.usage()[ x ];
            if ( str.isEmpty() ) continue;
            stringBuilder.append( "<" + str + ">" )
                    .append( " " );
        }
        return stringBuilder.toString();
    }
    
    private String[] getArgs( String args[], boolean isSetup )
    {
        String[] argsx = new String[ args.length - ( isSetup ? 2 : 1 ) ]; 
        for ( int x = 0; x < argsx.length; x++ ) 
            argsx[ x ] = args[ (isSetup ? 2 : 1) + x ];
        return argsx;
    }

}
