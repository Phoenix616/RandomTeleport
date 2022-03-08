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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.EnumSet;

public class HeightValidator extends LocationValidator {

    private final EnumSet<Material> unsafeBlocks;

    public HeightValidator(Material[] unsafeBlocks) {
        super("height");
        this.unsafeBlocks = EnumSet.noneOf(Material.class);
        Collections.addAll(this.unsafeBlocks, unsafeBlocks);
    }

    @Override
    public boolean validate(RandomSearcher searcher, Location location) {
        Block block = location.getWorld().getHighestBlockAt(location);
        if (block.getY() > searcher.getMaxY()) {
            block = location.getWorld().getBlockAt(
                    block.getX(),
                    searcher.getMinY() + searcher.getRandom().nextInt(searcher.getMaxY() - searcher.getMinY()),
                    block.getZ()
            );
        }
        while (block.isEmpty()) {
            block = block.getRelative(BlockFace.DOWN);
            if (block == null || block.getY() < searcher.getMinY()) {
                return false;
            }
        }
        location.setY(block.getY());
        return isSafe(block.getRelative(BlockFace.UP)) && isSafe(block.getRelative(BlockFace.UP, 2));
    }

    private boolean isSafe(Block block) {
        return block.isPassable() && !block.isLiquid() && !unsafeBlocks.contains(block.getType());
    }
}
