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
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SimpleOptionParser implements OptionParser {

    private Set<String> aliases;
    private final int argsLength;
    private final BiFunction<RandomSearcher, String[], Boolean> parser;

    public SimpleOptionParser(String option, BiFunction<RandomSearcher, String[], Boolean> parser) {
        this(new String[]{option}, parser);
    }

    public SimpleOptionParser(String[] optionAliases, BiFunction<RandomSearcher, String[], Boolean> parser) {
        this(optionAliases, -1, parser);
    }

    public SimpleOptionParser(String[] optionAliases, int argsLength, BiFunction<RandomSearcher, String[], Boolean> parser) {
        Validate.notEmpty(optionAliases);
        this.aliases = Arrays.stream(optionAliases).map(String::toLowerCase).collect(Collectors.toSet());
        this.argsLength = argsLength;
        this.parser = parser;
    }

    @Override
    public boolean parse(RandomSearcher searcher, String[] args) {
        boolean ret = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                String option = args[i].toLowerCase().substring(1);
                if (option.startsWith("-")) {
                    option = args[i].substring(1);
                }
                if (aliases.contains(option)) {
                    if (!hasAccess(searcher.getInitiator())) {
                        throw new IllegalArgumentException(searcher.getPlugin().getMessage(
                                searcher.getInitiator(), "error.no-permission.option",
                                "option", option,
                                "perm", "randomteleport.manual.option." + aliases.iterator().next()));
                    }
                    if (argsLength < 0 || i + argsLength < args.length) {
                        ret |= parser.apply(searcher, Arrays.copyOfRange(args, i + 1, argsLength < 0 ? args.length : i + 1 + argsLength));
                    }
                }
            }
        }
        return ret;
    }

    private boolean hasAccess(CommandSender initiator) {
        for (String alias : aliases) {
            if (initiator.hasPermission("randomteleport.manual.option." + alias)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getAliases() {
        return aliases;
    }
}
