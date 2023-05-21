package xyz.connorchickenway.towers.cmd;

import org.bukkit.command.CommandSender;

import xyz.connorchickenway.towers.cmd.abstraction.CommandReason;


public abstract class AbstractCommand 
{
    
    private String subCommand;

    public AbstractCommand( String subCommand )
    {
        this.subCommand = subCommand;
    }

    public abstract CommandReason executeCommand( CommandSender sender, String[] args );

    public abstract int getMaxArgs();

    public abstract String getUsage();

    public abstract String[] missingArguments( String label, String argument );

    public abstract String[] error();

    public String getSubCommand()
    {
        return subCommand;
    }

}
