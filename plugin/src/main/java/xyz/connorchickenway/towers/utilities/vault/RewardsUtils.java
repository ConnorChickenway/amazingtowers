package xyz.connorchickenway.towers.utilities.vault;

import org.bukkit.entity.Player;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.entity.GamePlayer;

import static xyz.connorchickenway.towers.config.StaticConfiguration.*;

public class RewardsUtils
{

    public static int getValue( Player player, Reward reward )
    {
        for( int x = max_vip_multiplication; x > 1; x-- )
            if ( player.hasPermission( "towers.vip.coinx" + x ) )
                return reward.getValue() * x;
        return reward.getValue();
    }

    public static void deposit( GamePlayer gamePlayer, Reward reward )
    {
        if ( is_reward_enabled )
        {
            int value = getValue( gamePlayer.toBukkitPlayer(), reward );
            if ( AmazingTowers.getInstance().getVaultManager().deposit( gamePlayer, value ) )
                if ( is_message_enabled )
                    gamePlayer.sendMessage( reward_lang.replace( "%coins%", "" + value ) );
        }
    }

}
