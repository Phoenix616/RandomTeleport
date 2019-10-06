package de.themoep.randomteleport.hook.plugin;

/*
 * RandomTeleport - GriefPrevention Hook
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

import de.themoep.randomteleport.hook.ProtectionHook;
import me.ryanhamshire.GriefPrevention.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GriefPreventionHook implements ProtectionHook {

    private final GriefPrevention inst;

    public GriefPreventionHook() {
        inst = GriefPrevention.instance;
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
    public boolean canBuild(Player player, Location location) {
        return inst.dataStore.getClaimAt(location, false, null) == null;
    }

    @Override
    public boolean canBuild(Player player, World world, int chunkX, int chunkZ) {
        if (inst.claimsEnabledForWorld(world)) {
            for (Claim claim : inst.dataStore.getClaims(chunkX, chunkZ)) {
                if (claim.getLesserBoundaryCorner().getWorld().equals(world) && !player.getUniqueId().equals(claim.ownerID)) {
                    return false;
                }
            }
        }
        return true;
    }
}
