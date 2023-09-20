package xyz.connorchickenway.towers.nms;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.nms.nms.BlockLatest;
import xyz.connorchickenway.towers.nms.nms.VersionSupport_Latest;
import xyz.connorchickenway.towers.utilities.ManagerController;

public class NMSManager extends ManagerController
{

    private INMS nms;
    private IBlockUtilities blockUtilities;

    public NMSManager( AmazingTowers plugin )
    {
        super( plugin );
    }

    @Override
    public void load()
    {
        this.blockUtilities = NMSVersion.isNewerVersion ? new BlockLatest() : new BlockLegacy();
        if ( !NMSVersion.hasSupport() ) return;
        if ( NMSVersion.isNewerVersion )
        {
            nms = new VersionSupport_Latest();
            return;
        }   
        switch( NMSVersion.nmsVersion )
        {
            case V1_8_R3:
                nms = new VersionSupport_1_8_R3();
                break;
            case V1_12_R1:
                nms = new VersionSupport_1_12_R1();
                break;    
            default:
                nms = new EmptyNMS();
                break;
        }
    }

    @Override
    public void disable()
    {
        
    }
    
    public INMS getNMS()
    {
        return nms;
    }

    public IBlockUtilities getBlockUtilities()
    {
        return blockUtilities;
    }

    public static NMSManager get()
    {
        return AmazingTowers.getInstance().getNMSManager();
    }

}
