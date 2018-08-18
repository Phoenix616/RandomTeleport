package de.themoep.bukkit.plugin.RandomTeleport.Listeners;

import de.themoep.bukkit.plugin.RandomTeleport.RandomTeleport;
import org.bukkit.ChatColor;
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

    private final RandomTeleport plugin;

    public SignListener(RandomTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        if(event.getLine(1).equalsIgnoreCase("[rtp]") || event.getLine(1).equalsIgnoreCase("[RandomTP]")){
            if(!event.getPlayer().hasPermission("randomteleport.sign.create")){
                event.getBlock().breakNaturally();
                event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to create RandomTeleport preset signs! " + ChatColor.ITALIC + " (randomteleport.sign.create)");
            } else {
                event.getPlayer().sendMessage(ChatColor.GREEN + "RandomTeleport preset sign created!");
                if(plugin.getConfig().getString("presets." + event.getLine(2).toLowerCase()) == null) {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "Warning: " + ChatColor.RED + "The RandomTeleport preset " + ChatColor.GOLD + event.getLine(2).toLowerCase() + ChatColor.RED + " does not exist!");
                }
            }
        }
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent event){
        if(event.getBlock().getType().name().contains("SIGN")) {
            Sign sign = (Sign) event.getBlock().getState();
            if(sign.getLine(1).equalsIgnoreCase("[rtp]") || sign.getLine(1).equalsIgnoreCase("[RandomTP]")){
                if(!event.getPlayer().hasPermission("randomteleport.sign.create")){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to break RandomTeleport signs! " + ChatColor.ITALIC + " (randomteleport.sign.create)");
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED + "RandomTeleport sign destroyed!");
                }
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.isCancelled() && event.getClickedBlock().getType().name().contains("SIGN")) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if(sign.getLine(1).equalsIgnoreCase("[rtp]") || sign.getLine(1).equalsIgnoreCase("[RandomTP]")) {
                String preset = sign.getLine(2).toLowerCase();
                if(event.getPlayer().hasPermission("randomteleport.sign.preset." + preset)) {
                    if(plugin.getConfig().getString("presets." + preset) == null) {
                        event.getPlayer().sendMessage(ChatColor.RED + "The RandomTeleport preset " + ChatColor.GOLD + preset + ChatColor.RED + " does not exist!");
                    } else {
                        String cmd = "rtp " + preset + " " + event.getPlayer().getName();
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
                    }
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to use the preset " + preset + "! " + ChatColor.ITALIC + " (randomteleport.sign.preset." + preset + ")");
                }
            }
        }
    }

}
