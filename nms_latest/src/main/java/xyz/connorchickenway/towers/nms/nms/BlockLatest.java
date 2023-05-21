package xyz.connorchickenway.towers.nms.nms;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;

import xyz.connorchickenway.towers.nms.Color;
import xyz.connorchickenway.towers.nms.IBlockUtilities;

public class BlockLatest implements IBlockUtilities
{

    @Override
    public Block getAttachedBlock( Block b )
    {
        BlockData bData = b.getBlockData();
        if ( bData instanceof WallSign )
        {
            WallSign signData = (WallSign) bData;
            return b.getRelative( signData.getFacing().getOppositeFace() );
        }
        return null;
    }

    @Override
    public boolean isWallSign( Block b )
    {
        return b.getBlockData() instanceof WallSign;
    }

    @Override
    public boolean isAttachedBlock( Material material )
    {
        String name = material.name();
        return name.contains( "WOOL" ) || name.contains( "STAINED_GLASS" ) || name.equals( "TERRACOTTA" );
    }

    @Override
    public void setType( Block block, Material material, Color color )
    {
        if ( isAttachedBlock( material ) )
            block.setType( material );  
    }

}
