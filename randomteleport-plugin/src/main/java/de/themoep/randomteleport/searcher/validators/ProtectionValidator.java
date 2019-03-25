package de.themoep.randomteleport.searcher.validators;

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

import de.themoep.randomteleport.searcher.RandomSearcher;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProtectionValidator extends LocationValidator {

    public ProtectionValidator() {
        super("protection");
    }

    @Override
    public boolean validate(RandomSearcher searcher, Location location) {
        if (searcher.getTargets().isEmpty()) {
            return true;
        }
        for (Entity entity : searcher.getTargets()) {
            if (entity instanceof Player && !searcher.getPlugin().getHookManager().canBuild((Player) entity, location)) {
                return false;
            }
        }
        return true;
    }
}
