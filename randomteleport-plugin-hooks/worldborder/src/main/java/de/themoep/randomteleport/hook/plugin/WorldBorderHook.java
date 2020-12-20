package de.themoep.randomteleport.hook.plugin;

/*
 * RandomTeleport - worldborder - $project.description
 * Copyright (c) 2019 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import de.themoep.randomteleport.hook.WorldborderHook;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldBorderHook implements WorldborderHook {
    private final WorldBorder plugin;

    public WorldBorderHook() {
        plugin = WorldBorder.plugin;
    }

    @Override
    public Location getCenter(World world) {
        BorderData data = plugin.getWorldBorder(world.getName());
        return data == null ? null : new Location(world, data.getX(), 0, data.getZ());
    }

    @Override
    public double getBorderRadius(World world) {
        BorderData data = plugin.getWorldBorder(world.getName());
        return data == null ? -1 : Math.min(data.getRadiusX(), data.getRadiusZ());
    }

    @Override
    public boolean isInsideBorder(Location location) {
        BorderData data = plugin.getWorldBorder(location.getWorld().getName());
        return data.insideBorder(location);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getPluginName() {
        return plugin.getName();
    }
}
