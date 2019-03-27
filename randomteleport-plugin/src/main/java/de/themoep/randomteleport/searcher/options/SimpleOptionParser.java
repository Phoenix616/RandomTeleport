package de.themoep.randomteleport.searcher.options;

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

import java.util.Arrays;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SimpleOptionParser implements OptionParser {

    private Set<String> aliases;
    private final BiFunction<RandomSearcher, String[], Boolean> parser;

    public SimpleOptionParser(String option, BiFunction<RandomSearcher, String[], Boolean> parser) {
        this(new String[]{option}, parser);
    }

    public SimpleOptionParser(String[] optionAliases, BiFunction<RandomSearcher, String[], Boolean> parser) {
        this.aliases = Arrays.stream(optionAliases).map(String::toLowerCase).collect(Collectors.toSet());
        this.parser = parser;
    }

    @Override
    public boolean parse(RandomSearcher searcher, String[] args) {
        boolean ret = false;
        String[] optionParts = String.join(" ", args).split(" -");
        for (String optionPart : optionParts) {
            String[] parts = optionPart.split(" ");
            while (parts[0].startsWith("-")) {
                parts[0] = parts[0].substring(1);
            }
            if (aliases.contains(parts[0].toLowerCase())) {
                ret |= parser.apply(searcher, Arrays.copyOfRange(parts, 1, parts.length));
            }
        }
        return ret;
    }
}
