package xyz.connorchickenway.towers.cmd.abstraction;

import org.bukkit.command.CommandSender;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.cmd.AbstractCommand;
import xyz.connorchickenway.towers.config.ConfigurationManager.ConfigName;
import xyz.connorchickenway.towers.utilities.StringUtils;

public class ConfigSubCommand extends AbstractCommand
{

    public ConfigSubCommand()
    {
        super( "config" );
    }

    @Override
    public CommandReason executeCommand( CommandSender sender, String[] args )
    {
        if ( args[0].equalsIgnoreCase( "reload" ) )
        {
            final ConfigName config = ConfigName.fromString( args[ 1 ] );
            if ( config != null )
            {
                config.getConfiguration().loadConfiguration();
                if ( config == ConfigName.SCOREBOARD )
                    AmazingTowers.getInstance().getScoreboardManager().load();
                sender.sendMessage( StringUtils.color( "&aConfig " + config.getName() + " has been reloaded!." ) );
                return CommandReason.OK;
            }
            return CommandReason.ERROR;
        }
        return CommandReason.WRONG_ARGS;
    }

    @Override
    public int getMaxArgs()
    {
        return 3;
    }

    @Override
    public String getUsage()
    {
        return this.getSubCommand() + " <reload> <" + ConfigName.names() + ">";
    }

    @Override
    public String[] missingArguments( String label, String argument )
    {
        return new String[]{ StringUtils.color( "&c/" + label + " " + this.getUsage()  ) };
    }

    @Override
    public String[] error()
    {
        return new String[]{StringUtils.color( "&cConfig name is incorrect.!" ),
                            StringUtils.color( "&7Config names: &a" + ConfigName.names() ) };
    }
    
}
