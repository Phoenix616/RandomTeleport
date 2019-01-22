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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface ProtectionHook extends PluginHook {

    /**
     * Check if a player can build at a location
     *
     * @param player The player to check
     * @param location The location to check
     * @return Whether or not the player can build
     */
    boolean canBuild(Player player, Location location);

    /**
     * Check if a player can build in a chunk
     * @param player The player to check
     * @param chunk The chunk to check
     * @return Whether or not the player can build
     */
    default boolean canBuild(Player player, Chunk chunk) {
        return canBuild(player, chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Check if a player can build in a chunk
     * @param player The player to check
     * @param world The chunk's world
     * @param chunkX The chunk's X coordinate
     * @param chunkZ The chunk's Z coordinate
     * @return Whether or not the player can build
     */
    boolean canBuild(Player player, World world, int chunkX, int chunkZ);

}
