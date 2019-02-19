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

public abstract class LocationValidator {
    private String type;

    public LocationValidator(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Validate a location
     * @param searcher  The searcher attempting to use this validator
     * @param location  The location to validate
     * @return          True if it's valid; false if not
     */
    public abstract boolean validate(RandomSearcher searcher, Location location);

}
