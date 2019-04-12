package de.themoep.randomteleport.searcher.validators;

/*
 * RandomTeleport - randomteleport-plugin - $project.description
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

import de.themoep.randomteleport.searcher.RandomSearcher;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class HeightValidator extends LocationValidator {

    public HeightValidator() {
        super("height");
    }

    @Override
    public boolean validate(RandomSearcher searcher, Location location) {
        Block block = location.getWorld().getHighestBlockAt(location);
        while (block.isEmpty()) {
            block = block.getRelative(BlockFace.DOWN);
            if (block == null || block.getY() == 0) {
                return false;
            }
        }
        location.setY(block.getY());
        return !block.getRelative(BlockFace.UP).getType().isSolid() && !block.getRelative(BlockFace.UP, 2).getType().isSolid();
    }
}
