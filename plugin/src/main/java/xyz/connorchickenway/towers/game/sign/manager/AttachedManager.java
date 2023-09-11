package xyz.connorchickenway.towers.game.sign.manager;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.google.common.collect.Maps;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.manager.GameManager;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.nms.Color;
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.Logger;
import xyz.connorchickenway.towers.utilities.ManagerController;

public class AttachedManager extends ManagerController
{
    
    private Map<GameState, AttachedBlock> blockMap = Maps.newHashMap();

    public AttachedManager( AmazingTowers plugin )
    {
        super( plugin );
    }

    @Override
    public void load()
    {
        String materialStr = StaticConfiguration.attached_sign_material;
        if ( !NMSVersion.isNewerVersion && 
                    materialStr.equalsIgnoreCase( "Terracotta" ) )
                materialStr = "STAINED_CLAY";
        for ( GameState value : GameState.values() ) 
        {
            Material material = null;
            Color color = null;
            try
            {
                color = Color.valueOf( this.getColor( value ) );
                material = Material.valueOf( NMSVersion.isNewerVersion ? ( color.name() + "_" + materialStr ) : materialStr );
            } catch( Exception ex )
            {
                Logger.error( "There was an error loading an attached block from config! (" + value.name() + ")", ex );
                continue;
            }
            blockMap.put( value , new AttachedBlock( material , color ) );
        }
    }

    @Override
    public void disable()
    {
        
    }

    public AttachedBlock getAttachedBlock( GameState gameState )
    {
        return blockMap.get( gameState );
    }
    
    public class AttachedBlock 
    {

        private Material material;
        private Color color;

        public AttachedBlock( Material material, Color color )
        {
            this.material = material;
            this.color = color;
        }

        public Material getMaterial()
        {
            return material;
        }
        
        public Color getColor()
        {
            return color;
        }

        public void setType( Block block )
        {
            plugin.getNMSManager().getBlockUtilities().setType( block , material, color );
        }

    }
    
    private String getColor( GameState state )
    {
        switch( state )
        {
            case LOBBY:
                return StaticConfiguration.attached_lobby_color;
            case STARTING:
                return StaticConfiguration.attached_starting_color;
            case GAME:
                return StaticConfiguration.attached_game_color;
            case FINISH:
                return StaticConfiguration.attached_finish_color;
            case RELOADING:
                return StaticConfiguration.attached_reloading_color;                
            default:
                break;
        }
        return null;
    }

    public static AttachedManager get()
    {
        return GameManager.get().getSignManager().getAttachedManager();
    }

}
