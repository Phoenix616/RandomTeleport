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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class BlockValidator extends LocationValidator {

    private final boolean whitelist;
    private final Set<Material> materials = EnumSet.noneOf(Material.class);

    public BlockValidator(Material... materials) {
        this(true, materials);
    }

    public BlockValidator(boolean whitelist, Material... materials) {
        super("block");
        this.whitelist = whitelist;
        Collections.addAll(this.materials, materials);
    }

    @Override
    public boolean validate(RandomSearcher searcher, Location location) {
        Block block = location.getBlock();
        return block != null && materials.contains(block.getType()) == whitelist;
    }
}
