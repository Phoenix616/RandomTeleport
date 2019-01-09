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

public interface ProtectionHook extends PluginHook {

    /**
     * Check if a certain location is protected
     * @param location The location to check
     * @return Whether or not it is protected
     */
    boolean isProtected(Location location);

    /**
     * Get the owner of a protection at a certain location, null if no owner
     * @param location The location to check
     * @return The name of the owner of the location, null if no owner
     */
    String getOwner(Location location);

    /**
     * Check if a certain chunk is protected
     * @param chunk The chunk to check
     * @return Whether or not it is protected
     */
    default boolean isProtected(Chunk chunk) {
        return isProtected(chunk.getWorld(), chunk.getX(), chunk.getZ());
    };

    /**
     * Check if a certain chunk is protected
     * @param world The chunk's world
     * @param chunkX The chunk's X coordinate
     * @param chunkZ The chunk's Z coordinate
     * @return Whether or not it is protected
     */
    boolean isProtected(World world, int chunkX, int chunkZ);

    /**
     * Get the owner of a protection at a certain chunk, null if no owner
     * @param chunk The chunk to check
     * @return The name of the owner of the location, null if no owner
     */
    default String getOwner(Chunk chunk) {
        return getOwner(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Get the owner of a protection at a certain chunk, null if no owner
     * @param world The chunk's world
     * @param chunkX The chunk's X coordinate
     * @param chunkZ The chunk's Z coordinate
     * @return The name of the owner of the location, null if no owner
     */
    String getOwner(World world, int chunkX, int chunkZ);

}
