package de.themoep.randomteleport.hook;

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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class HookManager implements Listener, ProtectionHook, WorldborderHook {
    private final Plugin plugin;

    private Map<String, PluginHook> hookMap = new LinkedHashMap<>();

    public HookManager(Plugin plugin) {
        this.plugin = plugin;

        hookMap.put("Vanilla Minecraft", new MinecraftHook(plugin));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
            if (p.isEnabled()) {
                registerHook(p);
            }
        }
    }

    private void registerHook(Plugin plugin) {
        String path = getClass().getPackage().getName() + ".plugin." + plugin.getName();
        String version = plugin.getDescription().getVersion();
        Class<?> hookClass = null;
        do {
            try {
                hookClass = Class.forName(path + version + "Hook");
                if (!PluginHook.class.isAssignableFrom(hookClass)) {
                    hookClass = null;
                }
            } catch (ClassNotFoundException ignored) {}
            if (version.contains(".")) {
                version = version.substring(0, version.lastIndexOf('.'));
            } else {
                try {
                    hookClass = Class.forName(path + "Hook");
                    if (!PluginHook.class.isAssignableFrom(hookClass)) {
                        hookClass = null;
                    }
                } catch (ClassNotFoundException ignored) {}
                break;
            }
        } while (hookClass == null);

        if (hookClass != null) {
            try {
                PluginHook hook = (PluginHook) hookClass.getConstructor().newInstance();
                if (hook instanceof Listener) {
                    getPlugin().getServer().getPluginManager().registerEvents((Listener) hook, getPlugin());
                }
                hookMap.put(plugin.getName(), hook);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not hook into " + plugin.getName() + " " + plugin.getDescription().getVersion() + "!", e);
            }
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        registerHook(event.getPlugin());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        PluginHook hook = hookMap.remove(event.getPlugin().getName());
        if (hook instanceof Listener) {
            hook.unregisterEvents();
        }
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getPluginName() {
        return plugin.getName();
    }

    /**
     * Get all hooks of a certain type, hooks registered later override hooks registered before them
     * @param hookClass The type of hook to get
     * @param <T> The type of hook to get
     * @return A list of hooks, empty if there are none
     */
    public  <T extends PluginHook> List<T> getHooks(Class<T> hookClass) {
        return hookMap.values().stream().filter(hookClass::isInstance).map(h -> (T) h).sorted(Collections.reverseOrder()).collect(Collectors.toList());
    }

    // Convenience methods to check all registered hooks

    @Override
    public boolean canBuild(Player player, Location location) {
        for (ProtectionHook hook : getHooks(ProtectionHook.class)) {
            if (!hook.canBuild(player, location)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canBuild(Player player, World world, int chunkX, int chunkZ) {
        for (ProtectionHook hook : getHooks(ProtectionHook.class)) {
            if (!hook.canBuild(player, world, chunkX, chunkZ)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Location getCenter(World world) {
        for (WorldborderHook hook : getHooks(WorldborderHook.class)) {
            Location center = hook.getCenter(world);
            if (center != null) {
                return center;
            }
        }
        return null;
    }

    @Override
    public double getBorderRadius(World world) {
        for (WorldborderHook hook : getHooks(WorldborderHook.class)) {
            double radius = hook.getBorderRadius(world);
            if (radius > 0) {
                return radius;
            }
        }
        return -1;
    }

    @Override
    public boolean isInsideBorder(Location location) {
        for (WorldborderHook hook : getHooks(WorldborderHook.class)) {
            if (!hook.isInsideBorder(location)) {
                return false;
            }
        }
        return true;
    }
}
