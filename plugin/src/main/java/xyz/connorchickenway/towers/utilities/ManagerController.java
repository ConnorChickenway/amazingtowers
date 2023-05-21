package xyz.connorchickenway.towers.utilities;

import xyz.connorchickenway.towers.AmazingTowers;

public abstract class ManagerController 
{
 
    protected AmazingTowers plugin;

    public ManagerController( AmazingTowers plugin )
    {
        this.plugin = plugin;
    }

    public abstract void load();

    public abstract void disable();
    
}
