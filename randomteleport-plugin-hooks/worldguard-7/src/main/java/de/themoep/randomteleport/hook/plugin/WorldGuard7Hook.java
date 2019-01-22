package de.themoep.randomteleport.hook.plugin;

/*
 * RandomTeleport - worldguard-7 - $project.description
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

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import de.themoep.randomteleport.hook.ProtectionHook;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuard7Hook implements ProtectionHook {

    private final WorldGuardPlugin inst;

    public WorldGuard7Hook() {
        inst = WorldGuardPlugin.inst();
    }

    @Override
    public Plugin getPlugin() {
        return inst;
    }

    @Override
    public String getPluginName() {
        return inst.getName();
    }

    @Override
    public boolean canBuild(Player player, org.bukkit.Location location) {
        return canBuild(WorldGuardPlugin.inst().wrapPlayer(player), BukkitAdapter.adapt(location));
    }

    @Override
    public boolean canBuild(Player player, World world, int chunkX, int chunkZ) {
        return canBuild(WorldGuardPlugin.inst().wrapPlayer(player), new Location(
                BukkitAdapter.adapt(world), (double) chunkX * 16 + 8, world.getSeaLevel(), (double) chunkZ * 16 + 8)
        );
    }

    private boolean canBuild(LocalPlayer player, Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testBuild(location, player, Flags.BUILD);
    }
}
