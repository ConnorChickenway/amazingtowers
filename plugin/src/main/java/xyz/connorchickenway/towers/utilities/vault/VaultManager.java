package xyz.connorchickenway.towers.utilities.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.utilities.Logger;
import xyz.connorchickenway.towers.utilities.ManagerController;

public class VaultManager extends ManagerController
{

    private Economy economy;
    private Chat chat;

    public VaultManager( AmazingTowers plugin )
    {
        super( plugin );
    }

    @Override
    public void load()
    {
        if ( !plugin.getServer().getPluginManager().isPluginEnabled( "Vault" ) ) return;
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration( Economy.class );
        if ( rsp != null )
            this.economy = rsp.getProvider();
        Logger.info( "Economy: " + (economy != null ? economy.getName() : "NONE") );
        RegisteredServiceProvider<Chat> rspc = plugin.getServer().getServicesManager().getRegistration( Chat.class );
        if ( rspc != null )
            chat = rspc.getProvider();
        Logger.info( "Chat: " + (chat != null ? chat.getName() : "NONE") );
    }

    @Override
    public void disable()
    {

    }

    public boolean hasEconomy()
    {
        return economy != null;
    }

    public Economy getEconomy()
    {
        return economy;
    }

    public boolean hasChat()
    {
        return chat != null;
    }

    public Chat getChat()
    {
        return chat;
    }

}
