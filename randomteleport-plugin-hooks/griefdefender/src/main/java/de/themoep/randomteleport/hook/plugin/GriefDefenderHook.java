package de.themoep.randomteleport.hook.plugin;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.ClaimManager;
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
        return canBuild(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean canBuild(Player player, World world, int chunkX, int chunkZ) {
        return canBuild(world, chunkX * 16, world.getSeaLevel(), chunkZ * 16);
    }

    private boolean canBuild(World world, int x, int y, int z) {
        if (GriefDefender.getCore().isEnabled(world.getUID())) {
            ClaimManager claimManager = GriefDefender.getCore().getClaimManager(world.getUID());
            if (claimManager == null) {
                return true;
            } else {
                return claimManager.getClaimAt(x, y, z).getType() == ClaimTypes.WILDERNESS;
            }
        }
        return true;
    }
}