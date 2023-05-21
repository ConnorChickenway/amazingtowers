package xyz.connorchickenway.towers.nms;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

public class BlockLegacy implements IBlockUtilities 
{

    @Override
    public Block getAttachedBlock( Block b )
    {
        if ( this.isWallSign( b ) )
            return b.getRelative( ( ( Attachable ) b.getState().getData() ).getAttachedFace() );
        return null;
    }

    @Override
    public boolean isWallSign( Block b )
    {
        MaterialData mData = b.getState().getData();
        if ( mData instanceof org.bukkit.material.Sign )
        {    org.bukkit.material.Sign sign = ( org.bukkit.material.Sign ) mData;
            if ( sign.isWallSign() )
                return true;
        }
        return false;
    }

    @Override
    public boolean isAttachedBlock( Material material )
    {
        return material == Material.WOOL || material == Material.STAINED_CLAY || material == Material.STAINED_GLASS;
    }

    @Deprecated
    @Override
    public void setType( Block block, Material material, Color color )
    {
        if ( isAttachedBlock( material ) )
        {
            block.setType( material );
            block.setData( color.getData() );
        }
    }
    
}
