package xyz.connorchickenway.towers.nms;

import org.bukkit.Bukkit;
import xyz.connorchickenway.towers.utilities.StringUtils;

public enum NMSVersion {

    V1_8_R1,
    V1_8_R2,
    V1_8_R3,
    V1_9_R1,
    V1_9_R2,
    V1_10_R1,
    V1_11_R1,
    V1_12_R1,
    V1_13_R1,
    V1_13_R2,
    V1_14_R1,
    V1_15_R1,
    V1_16_R1,
    V1_16_R2,
    V1_16_R3,
    V1_17_R1,
    V1_18_R1,
    V1_18_R2,
    V1_19_R1,
    V1_19_R2,
    V1_19_R3,
    V1_20_R1;


    public boolean isAboveOrEqual(NMSVersion compare) {
        return ordinal() >= compare.ordinal();
    }

    public static boolean isNewerVersion;
    public static NMSVersion nmsVersion;

    static {
        String version = null;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (final ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        nmsVersion = StringUtils.searchEnum(NMSVersion.class, version);
        if (nmsVersion != null)
            isNewerVersion = nmsVersion.isAboveOrEqual(NMSVersion.V1_13_R1);
    }

    public static boolean hasSupport() {
        return nmsVersion != null;
    }

    public static boolean is1_13() {
        return nmsVersion == NMSVersion.V1_13_R1 || nmsVersion == NMSVersion.V1_13_R2;
    }

}
