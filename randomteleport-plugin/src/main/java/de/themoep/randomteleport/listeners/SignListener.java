package de.themoep.randomteleport.listeners;

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

import de.themoep.randomteleport.RandomTeleport;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.logging.Level;

public class SignListener implements Listener {

    private final RandomTeleport plugin;

    public SignListener(RandomTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignCreate(SignChangeEvent event) {
        if (plugin.matchesSignVariable(event.getLine(1))) {
            if (!event.getPlayer().hasPermission("randomteleport.sign.create")) {
                event.getBlock().breakNaturally();
                plugin.sendMessage(event.getPlayer(), "sign.no-permission.create", "perm", "randomteleport.sign.create");
            } else {
                String preset = event.getLine(2);
                plugin.sendMessage(event.getPlayer(), "sign.created", "preset", preset);
                if (plugin.getConfig().getString("presets." + preset.toLowerCase()) == null) {
                    plugin.sendMessage(event.getPlayer(), "error.preset-doesnt-exist", "preset", preset);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignDestroy(BlockBreakEvent event) {
        if (event.getBlock().getType().name().contains("SIGN")) {
            Sign sign = (Sign) event.getBlock().getState();
            if (plugin.matchesSignVariable(sign.getLine(1))) {
                if (!event.getPlayer().hasPermission("randomteleport.sign.destroy")) {
                    event.setCancelled(true);
                    plugin.sendMessage(event.getPlayer(), "sign.no-permission.destroy", "perm", "randomteleport.sign.destroy");
                } else {
                    plugin.sendMessage(event.getPlayer(), "sign.destroyed", "preset", sign.getLine(2));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().name().contains("SIGN")) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if (plugin.matchesSignVariable(sign.getLine(1))) {
                String preset = sign.getLine(2).toLowerCase();
                if (event.getPlayer().hasPermission("randomteleport.sign.preset." + preset)) {
                    if (plugin.getConfig().getString("presets." + preset) == null) {
                        plugin.sendMessage(event.getPlayer(), "error.preset-doesnt-exist", "preset", preset);
                    } else {
                        try {
                            plugin.runPreset(plugin.getServer().getConsoleSender(), preset, event.getPlayer(), event.getClickedBlock().getLocation());
                        } catch (IllegalArgumentException e) {
                            plugin.sendMessage(event.getPlayer(), "error.preset-invalid", "preset", preset);
                            plugin.getLogger().log(Level.SEVERE, "Error while parsing preset " + preset, e);
                        }
                    }
                } else {
                    plugin.sendMessage(event.getPlayer(), "sign.no-permission.use",
                            "preset", preset,
                            "perm", "randomteleport.sign.use"
                    );
                }
            }
        }
    }
}
