package de.themoep.randomteleport;

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
import de.themoep.randomteleport.searcher.options.OptionParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Arrays;

public class RandomTeleportCommand implements CommandExecutor {
    private final RandomTeleport plugin;

    public RandomTeleportCommand(RandomTeleport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if ("reload".equalsIgnoreCase(args[0])) {
                plugin.loadConfig();
                plugin.sendMessage(sender, "reloaded");
                return true;
            }
        } else if (args.length > 1) {
            try {
                RandomSearcher searcher = new RandomSearcher(plugin, sender, getLocation(sender), Integer.parseInt(args[0]), Integer.parseInt(args[1]));

                String[] optionArgs = Arrays.copyOfRange(args, 2, args.length);
                for (OptionParser parser : plugin.getOptionParsers()) {
                    parser.parse(searcher, optionArgs);
                }

                searcher.getTargets().forEach(p -> plugin.sendMessage(p, "search", "world", searcher.getCenter().getWorld().getName()));
                searcher.search().thenApply(location -> {
                    searcher.getTargets().forEach(p -> {
                        p.teleport(location);
                        plugin.sendMessage(p, "teleport",
                                "world", location.getWorld().getName(),
                                "x", String.valueOf(location.getBlockX()),
                                "y", String.valueOf(location.getBlockY()),
                                "z", String.valueOf(location.getBlockZ())
                        );
                    });
                    return true;
                }).exceptionally(ex -> {
                    plugin.sendMessage(sender, "error.location");
                    sender.sendMessage(ex.getMessage());
                    return true;
                });
                return true;
            } catch (IllegalArgumentException e) {
                sender.sendMessage(e.getMessage());
            }
        }
        return false;
    }

    private static Location getLocation(CommandSender sender) {
        if (sender instanceof Entity) {
            return ((Entity) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getLocation();
        }
        return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }
}
