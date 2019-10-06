package de.themoep.randomteleport.hook.plugin;

/*
 * RandomTeleport - Factions Hook
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
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.massivecore.ps.PS;
import de.themoep.randomteleport.hook.ProtectionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Factions2Hook implements ProtectionHook {

    private final Plugin inst;

    public Factions2Hook() {
        inst = Bukkit.getPluginManager().getPlugin("Factions");
    }

    @Override
    public Plugin getPlugin() {
        return inst;
    }

    @Override
    public String getPluginName() {
        return inst.getName();
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return BoardColl.get().getFactionAt(PS.valueOf(location)) == null;
    }

    @Override
    public boolean canBuild(Player player, World world, int chunkX, int chunkZ) {
        return BoardColl.get().getFactionAt(PS.valueOf(world.getName(), chunkX, chunkZ)) == null;
    }
}
