package de.themoep.randomteleport.hook.plugin;

import de.themoep.randomteleport.hook.WorldborderHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.popcraft.chunkyborder.BorderData;
import org.popcraft.chunkyborder.ChunkyBorder;

public class ChunkyBorderHook implements WorldborderHook {
    private final Plugin plugin;
    private final ChunkyBorder chunkyBorder;

    public ChunkyBorderHook() {
        this.plugin = Bukkit.getPluginManager().getPlugin("ChunkyBorder");
        chunkyBorder = plugin.getServer().getServicesManager().getRegistration(ChunkyBorder.class).getProvider();
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Location getCenter(World world) {
        BorderData borderData = chunkyBorder.getBorders().get(world.getName());
        return borderData != null ? new Location(world, borderData.getCenterX(),0D,borderData.getCenterZ()) : null;
    }

    @Override
    public double getBorderRadius(World world) {
        BorderData borderData = chunkyBorder.getBorders().get(world.getName());
        return borderData != null ? borderData.getRadiusX() : -1;
    }

    @Override
    public boolean isInsideBorder(Location location) {
        BorderData borderData = chunkyBorder.getBorders().get(location.getWorld().getName());
        return borderData == null || borderData.getBorder().isBounding(location.getBlockX(),location.getBlockZ());
    }

    @Override
    public String getPluginName() {
        return plugin.getName();
    }
}