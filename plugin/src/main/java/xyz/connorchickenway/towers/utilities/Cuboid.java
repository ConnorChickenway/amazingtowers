package xyz.connorchickenway.towers.utilities;

//You can get more information in here: https://www.spigotmc.org/threads/region-cuboid.329859/

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Cuboid {

    private final int xMin, xMax, yMin, yMax, zMin, zMax;
    private final double xMinCentered, xMaxCentered, yMinCentered, yMaxCentered, zMinCentered, zMaxCentered;
    private final String worldName;

    public Cuboid(Location point1, Location point2) {
        this(point1.getWorld().getName(),
                Math.min(point1.getBlockX(), point2.getBlockX()),
                Math.max(point1.getBlockX(), point2.getBlockX()),
                Math.min(point1.getBlockY(), point2.getBlockY()),
                Math.max(point1.getBlockY(), point2.getBlockY()),
                Math.min(point1.getBlockZ(), point2.getBlockZ()),
                Math.max(point1.getBlockZ(), point2.getBlockZ())
        );
    }

    public Cuboid(String worldName, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax) {
        this.worldName = worldName;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public int getXMin() {
        return xMin;
    }

    public int getYMin() {
        return yMin;
    }

    public int getZMin() {
        return zMin;
    }

    public int getXMax() {
        return xMin;
    }

    public int getYMax() {
        return yMin;
    }

    public int getZMax() {
        return zMin;
    }

    public Location getPoint1() {
        return new Location(this.getWorld(), this.xMin, this.yMin, this.zMin);
    }

    public Location getPoint2() {
        return new Location(this.getWorld(), this.xMax, this.yMax, this.zMax);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public boolean isIn(Location loc) {
        return loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax && loc.getBlockY() >= this.yMin &&
                loc.getBlockY() <= this.yMax && loc.getBlockZ() >= this.zMin && loc.getBlockZ() <= this.zMax;
    }

    public boolean isInWithMarge(Location loc, double marge) {
        return loc.getX() >= this.xMinCentered - marge && loc.getX() <= this.xMaxCentered + marge && loc.getY() >= this.yMinCentered - marge && loc
                .getY() <= this.yMaxCentered + marge && loc.getZ() >= this.zMinCentered - marge && loc.getZ() <= this.zMaxCentered + marge;
    }

    public String serialize() {
        return xMin + ";" + xMax + ";" + yMin + ";" + yMax + ";" + zMin + ";" + zMax + ";" + worldName;
    }

    public static Cuboid deserialize(String string) {
        String[] split = string.split(";");
        if (split.length < 7) return null;
        return new Cuboid(
                split[6],
                Integer.parseInt(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]),
                Integer.parseInt(split[3]),
                Integer.parseInt(split[4]),
                Integer.parseInt(split[5])
        );
    }


}
