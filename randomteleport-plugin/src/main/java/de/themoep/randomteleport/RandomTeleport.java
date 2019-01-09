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

import de.themoep.randomteleport.hook.HookManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomTeleport extends JavaPlugin {

    private HookManager hookManager;

    public void onEnable() {
        hookManager = new HookManager(this);
        loadConfig();
        getCommand("randomteleport").setExecutor(new RandomTeleportCommand(this));
    }

    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
    }
}
