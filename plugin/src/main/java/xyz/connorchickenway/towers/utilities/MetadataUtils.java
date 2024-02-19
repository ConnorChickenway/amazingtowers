package xyz.connorchickenway.towers.utilities;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import xyz.connorchickenway.towers.AmazingTowers;

public class MetadataUtils {

    public static void set(Metadatable metadatable, String metadataKey, Object obj) {
        metadatable.setMetadata(metadataKey, new FixedMetadataValue(AmazingTowers.getInstance(), obj));
    }

    public static boolean has(Metadatable metadatable, String metadataKey) {
        return metadatable.hasMetadata(metadataKey);
    }

    public static void remove(Metadatable metadatable, String metadataKey) {
        metadatable.removeMetadata(metadataKey, AmazingTowers.getInstance());
    }

    public static Object get(Metadatable metadatable, String metadataKey) {
        MetadataValue mValue = metadatable.getMetadata(metadataKey).get(0);
        return mValue != null ? mValue.value() : null;
    }

}
