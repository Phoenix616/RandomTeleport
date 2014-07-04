package de.themoep.bukkit.plugin.RandomTeleport;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.mcore.ps.PS;
import com.sk89q.worldguard.bukkit.WGBukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;


public class RandomTeleport extends JavaPlugin implements CommandExecutor {
	
	public static HashMap<String,Long> cooldown = new HashMap<String,Long> ();
	public static HashSet<UUID> playerlock = new HashSet<UUID> ();
	public static int[] checkstat = new int[100];
	
	//English:
	
	public static String textsearch = ChatColor.GRAY + "RandomTeleport searches for a safe place in world {worldname}. . .";
	public static String textteleport = ChatColor.GRAY + "RandomTeleport teleported you to"; //  + " X: " + xTp + " Y: " + yTp + " Z: " + zTp + "!"
	public static String textlocationerror = ChatColor.DARK_RED + "Error:" + ChatColor.RED + " RandomTeleport could not find a save location!";
	public static String textcooldownerror = ChatColor.RED + "You have to wait {cooldown_text}before using this RandomTeleport again!";
	
	
	//German:
	/*public static String textsearch = ChatColor.GRAY + "Der Zufallsteleporter sucht nach einem sicheren Ort in Welt {worldname} . . .";
	public static String textteleport = ChatColor.GRAY + "Der Zufallsteleporter teleportierte dich nach"; //  + " X: " + xTp + " Y: " + yTp + " Z: " + zTp + "!"
	public static String textlocationerror = ChatColor.DARK_RED + "Fehler:" + ChatColor.RED + " der Zufallsteleporter konnte keinen sicheren Ort finden!";
	public static String textcooldownerror = ChatColor.RED + "Du musst noch {cooldown_text}warten bevor du den Zufallsteleporter wieder nutzen kannst!";
	*/
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.getLogger().log(Level.INFO, "Loading messages from config.");
		RandomTeleport.textsearch = ChatColor.translateAlternateColorCodes("&".charAt(0), this.getConfig().getString("msg.search"));
		RandomTeleport.textteleport = ChatColor.translateAlternateColorCodes("&".charAt(0), this.getConfig().getString("msg.teleport"));
		RandomTeleport.textlocationerror = ChatColor.translateAlternateColorCodes("&".charAt(0), this.getConfig().getString("msg.error.location"));
		RandomTeleport.textcooldownerror = ChatColor.translateAlternateColorCodes("&".charAt(0), this.getConfig().getString("msg.error.cooldown"));
		
		this.getLogger().log(Level.INFO, "Attempting to load cooldown.map...");
		cooldown = (HashMap<String, Long>) readMap("cooldown.map");
	}
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws NumberFormatException {
    	if(cmd.getName().equalsIgnoreCase("randomteleport") || cmd.getName().equalsIgnoreCase("randomtp") || cmd.getName().equalsIgnoreCase("rtp")) {
    		boolean force = false;
    		//boolean tppoints = false;
    		boolean xoption = false;
    		boolean zoption = false;
    		boolean coption = false;
    		String playername = sender.getName();
    		Player player = Bukkit.getServer().getPlayer(playername);
    		int xCenter = 0;
    		int zCenter = 0;
    		int minRange;
    		int maxRange;
    		int cooldowntime = 0;
    		World world = null;
    		    		
    		if(args.length == 0 && sender.hasPermission("randomteleport.presets.default")) {
    			if(this.getConfig().getString("presets.default") != null) {
    				String defaultcmd = this.getConfig().getString("presets.default").replace("/", "");
    				defaultcmd = defaultcmd + " -p " + sender.getName();
    				this.getServer().dispatchCommand(this.getServer().getConsoleSender(),defaultcmd);
    				return true;
    			}
    		}
    		
    		if(args.length == 1) {
    			if(sender.hasPermission("randomteleport.presets." + args[0].toLowerCase())) {
	    			if(this.getConfig().getString("presets." + args[0].toLowerCase()) == null) {
	    				sender.sendMessage(ChatColor.RED + "The Random Teleport " + args[0].toLowerCase() + " does not exist!");
	    				return true;
	    			}
	    			String defaultcmd = this.getConfig().getString("presets." + args[0].toLowerCase()).replace("/", "");
					defaultcmd = defaultcmd + " -p " + sender.getName();
					this.getServer().dispatchCommand(this.getServer().getConsoleSender(),defaultcmd);
					return true;
    			}
    			
    		}
			
    		// analyze the args & get parameter
    		if(args.length == 1 && args[0].equalsIgnoreCase("stat") && sender.hasPermission("randomteleport.stat")) {    			
				sender.sendMessage("--RandomTeleport statistics--");
				sender.sendMessage("Checks - Times occured");
    			for(int i = 0; i < 100; i++) {
    				if(checkstat[i] != 0) {
    					if(i == 99) sender.sendMessage(ChatColor.RED + "Canceled - " + checkstat[i] + "x");
        				else sender.sendMessage(i + 1 + " - " + checkstat[i] + "x");
    				} 
    			}
    			return true; 
    		}

    		if(!sender.hasPermission("randomteleport.use") && sender instanceof Player) {
    			sender.sendMessage("You don't have the permission randomteleport.use");
    			return true;
    		}
    		
    		if(args.length < 2) {
    			sender.sendMessage(ChatColor.DARK_RED + "Syntax error:" + ChatColor.RED + " Not enough arguments!");
    			return false;
    		}
    		
    		
    		try {
	    		//set ranges
	    		minRange = Integer.parseInt(args[0]);
	    		maxRange = Integer.parseInt(args[1]);
	
	    		if(minRange >= maxRange) {
	    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " maxRange must be bigger then minRange!");
	    			return true;
	    		}
    		} catch(NumberFormatException e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Your input contains an invalid number!");
    			return true;
    		}
    		
    		//getLogger().info("Success: Parsing bounds");
    		
    		
			if(args.length > 2) {
    			for(int i = 2; i < args.length; i++) {  
    				if(args[i].startsWith("-")) {  	
    					if(args[i].equalsIgnoreCase("-p") || args[i].equalsIgnoreCase("-player") && sender.hasPermission("randomteleport.tpothers")) {
    						if(i+1 >= args.length || args[i+1].startsWith("-")) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " The " + args[i] + " option needs an argument (" + args[i] + " value)!");
    			    			return true;
    						}
    						playername = args[i+1];   
    			    		player = Bukkit.getServer().getPlayer(playername);	
    			    		i++;
    			    		if(player == null) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Player '" + playername + "' is not found online!");
    			    			return true;
    			    		}
    					} else if(args[i].equalsIgnoreCase("-w") || args[i].equalsIgnoreCase("-world")) {
    						if(i+1 >= args.length || args[i+1].startsWith("-")) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " The " + args[i] + " option needs an argument (" + args[i] + " value)!");
    			    			return true;
    						}
						
				    		world = Bukkit.getServer().getWorld(args[i+1]);
				    		if(world == null) {
				    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " The world \"" + args[i+1] + "\" given in the " + args[i] + " option does not exist!");
    			    			return true;    						
				    		}
				    		i++;
    			    	
				       	// if -x/-z option is selected set x/z it to its values
    					} else if(args[i].equalsIgnoreCase("-x") || args[i].equalsIgnoreCase("-xPos")) {
    						if(i+1 >= args.length || (args[i+1].startsWith("-") && !isNumeric(args[i+1].substring(1)))) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " The " + args[i] + " option needs an argument (" + args[i] + " value)!");
    			    			return true;
    						}
    						try {    
    				    		xCenter = Integer.parseInt(args[i+1]);
    				    		xoption = true;
    				    		i++;
    			    		} catch(NumberFormatException e) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Your input contains a invalid number in the " + args[i] + " option!");
    			    			return true;
    			    		}
    					} else if(args[i].equalsIgnoreCase("-z") || args[i].equalsIgnoreCase("-zPos")) {
    						if(i+1 >= args.length || (args[i+1].startsWith("-") && !isNumeric(args[i+1].substring(1)))) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " The " + args[i] + " option needs an argument (" + args[i] + " value)!");
    			    			return true;
    						}
    						try {    
    				    		zCenter = Integer.parseInt(args[i+1]);
    				    		zoption = true;
    				    		i++;
    			    		} catch(NumberFormatException e) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Your input contains a invalid number in the " + args[i] + " option!");
    			    			return true;
    			    		}
    					} else if(args[i].equalsIgnoreCase("-c") || args[i].equalsIgnoreCase("-cooldown")) {
    						if(i+1 >= args.length || args[i+1].startsWith("-")) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " The " + args[i] + " option needs an argument (" + args[i] + " value)!");
    			    			return true;
    						}
    						try {    
    				    		cooldowntime = Integer.parseInt(args[i+1]);
    				    		coption = true;
    				    		i++;
    			    		} catch(NumberFormatException e) {
    			    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Your input contains a invalid number in the " + args[i] + " option!");
    			    			return true;
    			    		}
    					} else if(args[i].equalsIgnoreCase("-f") || args[i].equalsIgnoreCase("-force")) {
    						force = true;
    					} else {
    						sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Your input contains a invalid option (" + args[i] + ")!");
			    			return false;
    					}
    				}
    			}
    		}

			//getLogger().info("Success: Parsed options");
    		
    		if(playername == "CONSOLE") {
    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Silly filly! The console can not teleport! Did you forgot to add the -player <playername> option?");
    			return true;
    		}
    		
    		if(world == null) {
        		if(sender instanceof Player) world = ((Player) sender).getWorld();
        		else if(sender instanceof BlockCommandSender) world = ((BlockCommandSender) sender).getBlock().getWorld();
        		else if(sender instanceof ConsoleCommandSender) world = player.getWorld();
        		else world = player.getWorld();
    		}
    		
    		if(world.getEnvironment() == Environment.NETHER && !force) {
    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " RandomTeleport currently does not work in the nether!");
    			return true;
    		}
    		
    		if(playerlock.contains(player.getUniqueId())) {
    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " RandomTeleport already tries to teleport this player!");
    			return true;
    		}
    		playerlock.add(player.getUniqueId());
    		

    		//getLogger().info("Starting to read cooldown hashmap");
    		
    		String cooldownid = player.getUniqueId().toString() + minRange + maxRange + xCenter + zCenter + cooldowntime + force;
    		if(cooldown.containsKey(cooldownid) && cooldown.get(cooldownid) + cooldowntime * 1000 >  System.currentTimeMillis()) {
    			
    			// convert seconds in dhms format
    			long cooldown_seconds = (cooldown.get(cooldownid)/1000 + cooldowntime - System.currentTimeMillis()/1000) + 1;
    			String cooldown_text = "";    			
    			int cooldown_days = (int) (cooldown_seconds / 86400);    			
    			if(cooldown_days > 0) cooldown_seconds = cooldown_seconds - 86400 * cooldown_days;    			
    			int cooldown_hours = (int) (cooldown_seconds / 3600);    			
    			if(cooldown_hours > 0) cooldown_seconds = cooldown_seconds - 3600 * cooldown_hours;    			
    			int cooldown_minutes = (int) (cooldown_seconds / 60);    			
    			if(cooldown_minutes > 0) cooldown_seconds = cooldown_seconds - 60 * cooldown_minutes;    			
    			if(cooldown_days > 0) cooldown_text = cooldown_days + "d ";
    			if(cooldown_hours > 0) cooldown_text = cooldown_text + cooldown_hours + "h ";
    			if(cooldown_minutes > 0) cooldown_text = cooldown_text + cooldown_minutes + "m ";
    			if(cooldown_seconds > 0) cooldown_text = cooldown_text + cooldown_seconds + "s ";
    			
    			// display cooldown
    			if(playername != "CONSOLE") sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " This teleport is on cooldown for player " + playername + "!");
    			player.sendMessage(textcooldownerror.replaceAll("\\{cooldown_text\\}", cooldown_text));
    			playerlock.remove(player.getUniqueId());
    			return true;
    		}
    		

    		//getLogger().info("Success: Read Cooldown");
    		
    		player.sendMessage(textsearch.replaceAll("\\{worldname\\}", world.getName()));	    		

    		// set center coordinates to player location
    		if(!xoption) {
	    		xCenter = (int) player.getLocation().getX();
    		}
    		if(!zoption) {
	    		zCenter = (int) player.getLocation().getZ();
    		}

    		getLogger().fine("RandomTeleport for player '" + playername + "' with minRange " + minRange + " maxRange " + maxRange + " xCenter " + xCenter + " zCenter " + zCenter + " force=" + force);

    		int z;
			int x;
    		int zold = 0;
			int xold = 0;
			int chunksum = 0;
			int chunksumold = 0;
			for(int chunkcount = 0; chunkcount < 10 && chunksum < 81; chunkcount ++) {
				int count = 0; 
				do {
					count++;
		    		Random r = new Random();
		    		
		    		//get random range in min and max range
		    		int xRange = minRange + r.nextInt(maxRange - minRange);
		    		int zRange = minRange + r.nextInt(maxRange - minRange);
		    		
		    		//make range negative with a 50% chance
		    		if (r.nextBoolean()) xRange = 0 - xRange;
		    		if (r.nextBoolean()) zRange = 0 - zRange;
		    		
		    		x = xCenter + xRange;
		    		z = zCenter + zRange;
		    		if(count == 100) {
		    			sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " RandomTeleport could not find a save location!");
		    			if(!sender.getName().equalsIgnoreCase(player.getName())) player.sendMessage(textlocationerror);
		    			getLogger().info("Error: RandomTeleport could not find a save location after " + count + " tries for the player '" + playername + "' (minRange " + minRange + " maxRange " + maxRange + " xCenter " + xCenter + " zCenter " + zCenter + " force=" + force + ")");
		    			checkstat[count-1] = checkstat[count-1]++;
		    			playerlock.remove(player.getUniqueId());
		    			return true;
		    		};
	    		} while(!teleportCheck(player,world,x,z,force)); 
				
				checkstat[count-1] = checkstat[count-1] + 1;
    			
				// if in force mode don't check chunks around location					
				
				if(chunkcount == 0) {
					xold = x;
					zold = z;
				}
				
				if(force) break;
				
				//(re)set sum of valid chunks to zero
				chunksum = 0;
				
				// checks a square of 15x15 around the random position for protected WorldGuard and Factions regions
				if(Bukkit.getPluginManager().getPlugin("Factions") != null) {
					for(int i = -4; i <= 4; i++) {
						for(int j = -4; j <= 4; j++) {
							int xcheck = x + i * 16;
							int zcheck = z + j * 16;
							Location location = new Location(world, xcheck, world.getHighestBlockYAt(xcheck, zcheck), zcheck);
							if(checkforRegion(player,location)) chunksum++;
							
						}
					}
					getLogger().fine("RandomTeleport (" + chunkcount + ". try) found " + chunksum + " unprotected chunks around the location " + x + "/" + z );
					
					
					// if more not protected chunks were found then at the last random location: 
					// --> save the position to xold and zold and the chunksum to chunksumold
					// --> xold/zold hold the coords of the location with the least protected chunks around it
					if(chunksum > chunksumold) {
						xold = x;
						zold = z;
						chunksumold = chunksum;
					}
				} else break;
				
			// break the loop and use the x/z values with the highest sum of non proteted chunks 
			// if there is no location after 10 tries found which has no protected regions in a 15x15 square around the location 
			// or
			// aborts if all 225 around the location are not protected
			};
			x = xold;
			z = zold;
			
			// attempts to teleport player, sends message if it fails
			if(!teleportPlayer(playername,x,z,world)) 
				sender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " Player '" + playername + "' is not online anymore!");
			else 
				getLogger().fine("Used teleport location X: " + x + " Z: " + z + " for player '" + playername + "' RandomTeleportID: " + cooldownid);
				if(coption && !player.hasPermission("randomteleport.cooldownexempt")){
					cooldown.put(cooldownid, System.currentTimeMillis());
					writeMap(cooldown, "cooldown.map");
					//getLogger().info("Saved cooldown");
				}
    		playerlock.remove(player.getUniqueId());
			return true;    		
    	} 
    	return false; 
    }

    // teleports player with the name playername at the highest block at x/z
	private boolean teleportPlayer(String playername, int x ,int z, World world) {
		final Player player = Bukkit.getServer().getPlayer(playername);
		if(player == null||world == null) {    			
			return false;
		}
		final int xTp = x;
		final int yTp = world.getHighestBlockYAt(x, z);
		final int zTp = z;
		
		player.teleport(new Location(world, xTp + 0.5, yTp + 0.5, zTp + 0.5));
	    player.sendMessage(textteleport + " X: " + xTp + " Y: " + yTp + " Z: " + zTp + "!");	    		
		return true;
	}

	// function for checking if block is valid to teleport to (no lava, fire, water, ...), also if it is protected by WG or Factions
	private boolean teleportCheck(Player player, World world, int x, int z, boolean force) {
		int y = world.getHighestBlockYAt(x, z);
		Block highest = world.getBlockAt(x, y - 1, z);
		getLogger().finer("Checked teleport location for player '" + player.getName() + "' X: " + x + " Y: " + (y - 1) + "  Z: " + z + " is " + highest.getType() + " + " + world.getBlockAt(x, y + 1, z).getType());
		
		if(!force) {			
			switch (world.getEnvironment()) {
	    		case NETHER:	
	    			return false;
	    		case THE_END:
	    			if(highest.getType() == Material.AIR || highest.getType() == Material.WATER || highest.getType() == Material.STATIONARY_WATER || highest.getType() == Material.STATIONARY_LAVA || highest.getType() == Material.WEB || highest.getType() == Material.LAVA || highest.getType() == Material.CACTUS || highest.getType() == Material.ENDER_PORTAL || highest.getType() == Material.PORTAL) 
	    				return false;
	    		case NORMAL:
	    		default:
	    			if(highest.getType() != Material.SAND && highest.getType() != Material.GRAVEL && highest.getType() != Material.DIRT && highest.getType() != Material.GRASS && true)
	    				return false;
			}		
			if(!checkforRegion(player, highest.getLocation())) return false;
			
			
		} else {
			if(highest.getType() == Material.AIR || highest.getType() == Material.WATER || highest.getType() == Material.STATIONARY_WATER || highest.getType() == Material.STATIONARY_LAVA || highest.getType() == Material.WEB || highest.getType() == Material.LAVA || highest.getType() == Material.CACTUS || highest.getType() == Material.ENDER_PORTAL || highest.getType() == Material.PORTAL) 
				return false;
		}
		
		
		return true;
		
	}

	/**
	 * Checks if the player can build at the highest block of the location
	 * @param player The Player to check with
	 * @param location the black at the location to check
	 * @return true or false
	 */
	// 
	private boolean checkforRegion(Player player, Location location) {
		Block highest = location.getWorld().getBlockAt(location);
		if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null && !WGBukkit.getPlugin().canBuild(player, highest)) {
			return false;
		}
		if(Bukkit.getPluginManager().getPlugin("Factions") != null){
			Faction wilderness = FactionColls.get().getForWorld(location.getWorld().getName()).getNone();
			Faction faction = BoardColls.get().getFactionAt(PS.valueOf(highest));
			if(faction != wilderness) return false;
		}
		return true;
	}
	
	/**
	 * Writes a Hashmap to a file
	 * @param object The Hashmap to write
	 * @param outputFile The file to write to
	 */
	public void writeMap(Object object, String outputFile) {
		try
	      {
			File file = new File(getDataFolder().getPath() + "/" + outputFile);
			if (!file.isFile()) {
				if(!file.createNewFile()){			
					throw new IOException("Error creating new file: " + file.getPath());
				}
			}
			FileOutputStream fileOut = new FileOutputStream(file.getPath());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(object);
			out.close();
			fileOut.close();
			getLogger().fine("Serialized data is saved in " + file.getPath());
	      }catch(IOException i)
	      {
	    	  i.printStackTrace();
	      }
	}
	
	/**
	 * Reads a Hashmap from a file
	 * @param inputFile The file to read from
	 * @return An Object which is a HashMap<Object,Object>
	 */
	@SuppressWarnings("unchecked")
	public Object readMap(String inputFile) {
		HashMap<Object, Object> map = new HashMap<Object,Object>();
		File file = new File(getDataFolder().getPath() + "/" + inputFile);
		if (!file.isFile()) {
			getLogger().log(Level.INFO, "No file found in " + file.getPath());
			try {
				if(!file.createNewFile())
				{
					throw new IOException("Error while creating new file: " + file.getPath());
				} else {
					writeMap(map, inputFile);
					getLogger().log(Level.INFO, "New file created in " + file.getPath());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try
	      {
	         FileInputStream fileIn = new FileInputStream(file.getPath());
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         map = (HashMap<Object, Object>) in.readObject();
	         in.close();
	         fileIn.close();
	         getLogger().log(Level.INFO, "Sucessfully loaded cooldown.map.");
	      }catch(IOException i)
	      {
		     getLogger().log(Level.WARNING, "No saved Map found in " + inputFile);
	      } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return map;
	}
	
	/**
	 * Checks if a string is mumeric
	 * @param str to test
	 * @return True if input string is numeric
	 */
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
    
}
