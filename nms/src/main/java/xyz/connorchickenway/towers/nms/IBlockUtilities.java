package xyz.connorchickenway.towers.nms;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IBlockUtilities {

    Block getAttachedBlock(Block b);

    boolean isWallSign(Block b);

    void setType(Block block, Material material, Color color);

    boolean isAttachedBlock(Material material);

}
