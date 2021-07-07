package de.themoep.randomteleport.hook.plugin;

import de.themoep.randomteleport.hook.WorldborderHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.popcraft.chunkyborder.BorderData;
import org.popcraft.chunkyborder.ChunkyBorder;

public class ChunkyBorderHook implements WorldborderHook {
    private final ChunkyBorder plugin;

    public ChunkyBorderHook() {
        this.plugin = (ChunkyBorder) Bukkit.getPluginManager().getPlugin("ChunkyBorder");
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Location getCenter(World world) {
        BorderData borderData = plugin.getBorders().get(world.getName());
        return new Location(world, borderData.getCenterX(),0D,borderData.getCenterZ());
    }

    @Override
    public double getBorderRadius(World world) {
        BorderData borderData = plugin.getBorders().get(world.getName());
        return borderData.getRadiusX();
    }

    @Override
    public boolean isInsideBorder(Location location) {
        BorderData borderData = plugin.getBorders().get(location.getWorld().getName());
        return borderData.getBorder().isBounding(location.getBlockX(),location.getBlockZ());
    }

    @Override
    public String getPluginName() {
        return plugin.getName();
    }
}