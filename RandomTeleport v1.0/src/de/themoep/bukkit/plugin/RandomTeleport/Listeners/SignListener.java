package de.themoep.bukkit.plugin.RandomTeleport.Listeners;

import de.themoep.bukkit.plugin.RandomTeleport.RandomTeleport;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Phoenix616 on 23.11.2014.
 */
public class SignListener implements Listener {

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        if(event.getLine(1).equalsIgnoreCase("[rtp]") || event.getLine(1).equalsIgnoreCase("[RandomTP]")){
            if(!event.getPlayer().hasPermission("randomteleport.sign.create")){
                event.getBlock().breakNaturally();
                event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to create RandomTeleport preset signs! " + ChatColor.ITALIC + " (randomteleport.sign.create)");
            }
        }
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent event){
        if(event.getBlock().getType() == Material.SIGN) {
            Sign sign = (Sign) event.getBlock();
            if((sign.getLine(1).equalsIgnoreCase("[rtp]") || sign.getLine(1).equalsIgnoreCase("[RandomTP]")) && !event.getPlayer().hasPermission("randomteleport.sign.create")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to break RandomTeleport preset signs! " + ChatColor.ITALIC + " (randomteleport.sign.create)");
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() != Material.SIGN) return;
            Sign sign = (Sign) event.getClickedBlock().getState();
            if(!(sign.getLine(1).equalsIgnoreCase("[rtp]") || sign.getLine(1).equalsIgnoreCase("[RandomTP]"))) return;
            String preset = sign.getLine(2);
            if (event.getPlayer().hasPermission("randomteleport.sign.preset." + preset)) {
                if (RandomTeleport.getPlugin().getConfig().getString("presets." + preset) == null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "The Random Teleport " + preset + " does not exist!");
                } else {
                    String cmd = "rtp " + preset + " " + event.getPlayer().getName();
                    RandomTeleport.getPlugin().getServer().dispatchCommand(RandomTeleport.getPlugin().getServer().getConsoleSender(), cmd);
                }
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to use the preset " + preset + "! " + ChatColor.ITALIC + " (randomteleport.sign.preset." + preset +")");
            }
        }
    }

}
