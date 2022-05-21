package de.themoep.randomteleport.hook.plugin;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.ClaimTypes;
import de.themoep.randomteleport.hook.ProtectionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GriefDefenderHook implements ProtectionHook {

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("GriefDefender");
    }

    @Override
    public String getPluginName() {
        return "GriefDefender";
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return GriefDefender.getCore().getClaimManager(location.getWorld().getUID()).getClaimAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getType() == ClaimTypes.WILDERNESS;
    }

    @Override
    public boolean canBuild(Player player, World world, int chunkX, int chunkZ) {
        if(GriefDefender.getCore().isEnabled(world.getUID())) {
            return GriefDefender.getCore().getClaimManager(world.getUID()).getClaimAt(chunkX,0,chunkZ).isTrusted(player.getUniqueId());
        }
        return true;
    }
}