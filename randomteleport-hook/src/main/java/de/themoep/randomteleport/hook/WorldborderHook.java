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

import org.bukkit.Location;
import org.bukkit.World;

public interface WorldborderHook extends PluginHook {

    /**
     * Get the center of the world border
     * @param world The world to get the border center for
     * @return The center or null if there is no border
     */
    Location getCenter(World world);

    /**
     * Get the radius of the world border
     * @param world The world to get the border radius for
     * @return The radius or -1 if there is no border
     */
    double getBorderRadius(World world);

    /**
     * Convenience method to check if a location is inside the border
     * @param location The location to check
     * @return True if it is inside (or there is no border), false if not
     */
    boolean isInsideBorder(Location location);
}
