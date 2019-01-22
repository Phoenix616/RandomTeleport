package de.themoep.randomteleport.hook;

/*
 * RandomTeleport
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

import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Lockable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MinecraftHook implements ProtectionHook, WorldborderHook {
    private final Plugin plugin;

    public MinecraftHook(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getPluginName() {
        return "Vanilla Minecraft";
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        BlockStateSnapshotResult state = PaperLib.getBlockState(location.getBlock(), false);
        if (state.getState() instanceof Lockable) {
            return ((Lockable) state.getState()).getLock() == null;
        }
        return true;
    }

    @Override
    public boolean canBuild(Player player, World world, int chunkX, int chunkZ) {
        return true;
    }

    @Override
    public Location getCenter(World world) {
        if (world.getWorldBorder() != null) {
            return world.getWorldBorder().getCenter();
        }
        return null;
    }

    @Override
    public double getBorderRadius(World world) {
        if (world.getWorldBorder() != null) {
            return world.getWorldBorder().getSize() / 2;
        }
        return 0;
    }

    @Override
    public boolean isInsideBorder(Location location) {
        if (location.getWorld().getWorldBorder() != null) {
            return location.getWorld().getWorldBorder().isInside(location);
        }
        return true;
    }
}
