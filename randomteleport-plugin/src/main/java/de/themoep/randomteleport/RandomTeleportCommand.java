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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class RandomTeleportCommand implements CommandExecutor {
    private final RandomTeleport plugin;

    public RandomTeleportCommand(RandomTeleport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String preset = "default";
                if (plugin.getConfig().getBoolean("use-player-world-as-preset", false)) {
                    String worldName = player.getWorld().getName().toLowerCase();
                    if (presetExistsInConfig(worldName))
                        preset = worldName;
                }
                runPreset(preset, sender, player, player.getLocation());
                return true;
            }
        } else if (args.length == 1) {
            if ("--reload".equalsIgnoreCase(args[0]) && sender.hasPermission("randomteleport.reload")) {
                plugin.loadConfig();
                plugin.sendMessage(sender, "reloaded");
                return true;
            } else if ("--stat".equalsIgnoreCase(args[0]) && sender.hasPermission("randomteleport.stat")) {
                //TODO: teleporter and searcher statistics
            } else if (sender instanceof Player) {
                runPreset(args[0].toLowerCase(), sender, (Player) sender, ((Player) sender).getLocation());
                return true;
            }
        } else {
            try {
                if (sender.hasPermission("randomteleport.manual")) {
                    plugin.parseAndRun(sender, getLocation(sender), args);
                    return true;
                } else {
                    plugin.sendMessage(sender, "error.no-permission.general", "perm", "randomteleport.manual");
                    return true;
                }
            } catch (IllegalArgumentException e) {
                if (args.length == 2) {
                    Player target = plugin.getServer().getPlayer(args[1]);
                    if (target == null) {
                        plugin.sendMessage(sender, "error.player-not-found", "what", args[1]);
                        return true;
                    }
                    String[] presets = args[0].split(",");
                    runPreset(presets[RandomTeleport.RANDOM.nextInt(presets.length)].toLowerCase(), sender, target, target.getLocation());
                    return true;
                }
                sender.sendMessage(e.getMessage());
            }
        }
        return false;
    }

    private void runPreset(String preset, CommandSender sender, Player target, Location center) {
        if (!sender.hasPermission("randomteleport.presets." + preset)) {
            plugin.sendMessage(sender, "error.no-permission.preset",
                    "preset", preset, "perm",
                    "randomteleport.presets." + preset
            );
        } else if (sender != target && !sender.hasPermission("randomteleport.tpothers")) {
            plugin.sendMessage(sender, "error.no-permission.tp-others", "perm", "randomteleport.tpothers");
        } else if (!presetExistsInConfig(preset)) {
            plugin.sendMessage(sender, "error.preset-doesnt-exist", "preset", preset);
        } else {
            if (sender == target) {
                for (RandomSearcher searcher : plugin.getRunningSearchers().values()) {
                    if (searcher.getTargets().contains(target)) {
                        plugin.sendMessage(sender, "error.already-searching", "preset", preset);
                        return;
                    }
                }
            }

            try {
                plugin.runPreset(plugin.getServer().getConsoleSender(), preset, target, center);
            } catch (IllegalArgumentException e) {
                plugin.sendMessage(sender, "error.preset-invalid", "preset", preset);
                plugin.getLogger().log(Level.SEVERE, "Error while parsing preset " + preset, e);
            }
        }
    }

    private boolean presetExistsInConfig(String preset) {
        return plugin.getConfig().getString("presets." + preset) != null;
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
