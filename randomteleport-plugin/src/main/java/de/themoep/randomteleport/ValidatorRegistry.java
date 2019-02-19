package de.themoep.randomteleport;

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

import de.themoep.randomteleport.searcher.validators.LocationValidator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidatorRegistry {

    private Map<String, LocationValidator> validators = new LinkedHashMap<>();

    /**
     * Get the map of currently set location validators
     * @return The map of validators
     */
    public Map<String, LocationValidator> getRaw() {
        return validators;
    }

    public Collection<LocationValidator> getAll() {
        return validators.values();
    }

    /**
     * Add a location validator that is provided in this plugin
     * @param validator The validator to add
     * @return The previously registered validator of the same type and name or null if none was registered
     */
    public LocationValidator add(LocationValidator validator) {
        return validators.put(validator.getType().toLowerCase(), validator);
    }

    /**
     * Remove a location validator that is provided in this plugin
     * @param validator The validator to remove
     * @return The removed registered validator with the same type or null if it wasn't registered
     */
    public LocationValidator remove(LocationValidator validator) {
        return remove(validator.getType());
    }

    /**
     * Remove a location validator that is provided in this plugin
     * @param type The type of the validator to remove
     * @return The removed registered validator with the same type or null if it wasn't registered
     */
    public LocationValidator remove(String type) {
        return validators.remove(type.toLowerCase());
    }

    /**
     * Get a location validator that is provided in this plugin
     * @param type The type of the validator to get
     * @return The registered validator with the provided type or null if none was registered
     */
    public LocationValidator get(String type) {
        return validators.get(type.toLowerCase());
    }
}
