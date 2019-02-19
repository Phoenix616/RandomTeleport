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
import de.themoep.randomteleport.searcher.options.NotFoundException;
import de.themoep.randomteleport.searcher.options.OptionParser;
import de.themoep.randomteleport.searcher.options.PlayerNotFoundException;
import de.themoep.randomteleport.searcher.options.SimpleOptionParser;
import de.themoep.randomteleport.searcher.options.WorldNotFoundException;
import de.themoep.randomteleport.searcher.validators.BiomeValidator;
import de.themoep.randomteleport.searcher.validators.BlockValidator;
import de.themoep.randomteleport.searcher.validators.ProtectionValidator;
import de.themoep.randomteleport.searcher.validators.WorldborderValidator;
import de.themoep.utils.lang.bukkit.LanguageManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

public class RandomTeleport extends JavaPlugin {

    public static final Random RANDOM = new Random();
    private HookManager hookManager;
    private LanguageManager lang;

    private ValidatorRegistry locationValidators = new ValidatorRegistry();
    private List<OptionParser> optionParsers = new ArrayList<>();

    private Material[] saveBlocks;
    private Material[] unsaveBlocks;

    public void onEnable() {
        hookManager = new HookManager(this);
        loadConfig();
        initOptionParsers();
        initValidators();
        getCommand("randomteleport").setExecutor(new RandomTeleportCommand(this));
    }

    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        saveBlocks = getConfig().getStringList("save-blocks").stream()
                .map(s -> {
                    Material mat = Material.matchMaterial(s);
                    if (mat == null) {
                        getLogger().log(Level.WARNING, "Error in save-blocks config! No material found with name " + s);
                    }
                    return mat;
                })
                .filter(Objects::nonNull)
                .toArray(Material[]::new);
        unsaveBlocks = getConfig().getStringList("unsave-blocks").stream()
                .map(s -> {
                    Material mat = Material.matchMaterial(s);
                    if (mat == null) {
                        getLogger().log(Level.WARNING, "Error in unsave-blocks config! No material found with name " + s);
                    }
                    return mat;
                })
                .filter(Objects::nonNull)
                .toArray(Material[]::new);
        lang = new LanguageManager(this, getConfig().getString("lang"));
        lang.setPlaceholderPrefix("{");
        lang.setPlaceholderSuffix("}");
    }

    private void initOptionParsers() {
        addOptionParser(new SimpleOptionParser(array("p", "player"), (searcher, args) -> {
            if (args.length > 0) {
                List<Player> players = new ArrayList<>();
                for (String arg : args) {
                    Player player = getServer().getPlayer(arg);
                    if (player == null) {
                        throw new PlayerNotFoundException(arg);
                    }
                    players.add(player);
                }
                searcher.getTargets().addAll(players);
                return true;
            }
            return false;
        }));
        addOptionParser(new SimpleOptionParser(array("x", "xpos"), (searcher, args) -> {
            if (args.length > 0) {
                searcher.getCenter().setX(Integer.parseInt(args[0]));
                return true;
            }
            return false;
        }));
        addOptionParser(new SimpleOptionParser(array("y", "ypos"), (searcher, args) -> {
            if (args.length > 0) {
                searcher.getCenter().setX(Integer.parseInt(args[0]));
                return true;
            }
            return false;
        }));
        addOptionParser(new SimpleOptionParser(array("w", "world"), (searcher, args) -> {
            if (args.length > 0) {
                World world = getServer().getWorld(args[0]);
                if (world == null) {
                    throw new WorldNotFoundException(args[0]);
                }
                searcher.getCenter().setWorld(world);
                return true;
            }
            return false;
        }));
        addOptionParser(new SimpleOptionParser(array("c", "cooldown"), (searcher, args) -> {
            if (args.length > 0) {
                searcher.setCooldown(Integer.parseInt(args[0]));
                return true;
            }
            return false;
        }));
        addOptionParser(new SimpleOptionParser(array("f", "force"), (searcher, args) -> {
            if (args.length > 0) {
                if ("regions".equalsIgnoreCase(args[1])) {
                    searcher.getValidators().remove("protection");
                } else if ("blocks".equalsIgnoreCase(args[1])) {
                    searcher.getValidators().add(new BlockValidator(false, unsaveBlocks));
                } else {
                    throw new NotFoundException(args[1]);
                }
            } else {
                searcher.getValidators().remove("protection");
                searcher.getValidators().add(new BlockValidator(false, unsaveBlocks));
            }
            return true;
        }));
        addOptionParser(new SimpleOptionParser(array("b", "biome"), (searcher, args) -> {
            if (args.length > 0) {
                List<Biome> biomes = new ArrayList<>();
                for (String arg : args) {
                    biomes.add(Biome.valueOf(arg.toUpperCase()));
                }
                if (!biomes.isEmpty()) {
                    searcher.getValidators().add(new BiomeValidator(biomes.toArray(new Biome[0])));
                    return true;
                }
                return false;
            }
            return false;
        }));
        addOptionParser(new SimpleOptionParser(array("g", "generated", "l", "loaded"), (searcher, args) -> {
            // loaded is removed as we load chunks async which should no longer lead to performance issues
            // now it just works like the new "generated" option where it only checks generated chunks
            searcher.searchInGeneratedOnly(true);
            return true;
        }));
    }

    private void initValidators() {
        locationValidators.add(new WorldborderValidator());
        locationValidators.add(new ProtectionValidator());
        locationValidators.add(new BlockValidator(saveBlocks));
    }

    /**
     * Utility method to create arrays with a nicer syntax. Seriously, why does Java not just accept {"string"}?!?
     * @param array The array values
     * @return The same array
     */
    private static <T> T[] array(T... array) {
        return array;
    }

    public boolean sendMessage(CommandSender sender, String key, String... replacements) {
        String message = lang.getConfig(sender).get(key, replacements);
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(message);
            return true;
        }
        return false;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public ValidatorRegistry getLocationValidators() {
        return locationValidators;
    }

    /**
     *
     * @return
     */
    public List<OptionParser> getOptionParsers() {
        return optionParsers;
    }

    /**
     * Add an option parser to this plugin
     * @param parser The parser to add
     */
    public void addOptionParser(OptionParser parser) {
        optionParsers.add(parser);
    }

}
