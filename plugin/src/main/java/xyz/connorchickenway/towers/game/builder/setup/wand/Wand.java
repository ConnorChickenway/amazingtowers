package xyz.connorchickenway.towers.game.builder.setup.wand;

import org.bukkit.Location;
import xyz.connorchickenway.towers.utilities.Cuboid;

public class Wand {

    private Location[] region = new Location[2];

    public Location getPosition1() {
        return region[0];
    }

    public Location getPosition2() {
        return region[1];
    }

    public boolean setPosition1(Location location) {
        if (!location.equals(getPosition1())) {
            region[0] = location;
            return true;
        }
        return false;
    }

    public boolean setPosition2(Location location) {
        if (!location.equals(getPosition2())) {
            region[1] = location;
            return true;
        }
        return false;
    }

    public boolean hasLocations() {
        return region[0] != null && region[1] != null;
    }

    public Cuboid createCuboid() {
        if (hasLocations())
            return new Cuboid(getPosition1(), getPosition2());
        return null;
    }

}
