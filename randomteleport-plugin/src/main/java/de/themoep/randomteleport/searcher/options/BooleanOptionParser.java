package de.themoep.randomteleport.searcher.options;

/*
 * RandomTeleport - randomteleport-plugin - $project.description
 * Copyright (c) 2022 Max Lee aka Phoenix616 (mail@moep.tv)
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

import java.util.function.Consumer;

public class BooleanOptionParser extends SimpleOptionParser {

    public BooleanOptionParser(String optionAlias, Consumer<RandomSearcher> consumer) {
        this(new String[]{optionAlias}, consumer);
    }

    public BooleanOptionParser(String[] optionAliases, Consumer<RandomSearcher> consumer) {
        super(optionAliases, 0, ((searcher, args) -> {
            consumer.accept(searcher);
            return true;
        }));
    }
    
}
