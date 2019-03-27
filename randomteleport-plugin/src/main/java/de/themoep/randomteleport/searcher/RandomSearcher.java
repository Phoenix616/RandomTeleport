package de.themoep.randomteleport.searcher;

/*
 * RandomTeleport - randomteleport-plugin - $project.description
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
import de.themoep.randomteleport.ValidatorRegistry;
import de.themoep.randomteleport.searcher.options.NotFoundException;
import de.themoep.randomteleport.searcher.validators.LocationValidator;
import io.papermc.lib.PaperLib;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class RandomSearcher {
    private final RandomTeleport plugin;
    private final CommandSender initiator;

    private ValidatorRegistry validators = new ValidatorRegistry();

    private Random random = RandomTeleport.RANDOM;

    private List<Entity> targets = new ArrayList<>();

    private long seed = -1;
    private Location center;
    private int minRadius = 0;
    private int maxRadius = Integer.MAX_VALUE;
    private boolean generatedOnly = false;
    private int maxChecks = 100;
    private int cooldown;
    private int checks = 0;

    private CompletableFuture<Location> future = null;

    public RandomSearcher(RandomTeleport plugin, CommandSender initiator, Location center, int minRadius, int maxRadius) {
        this.plugin = plugin;
        this.initiator = initiator;
        setCenter(center);
        setMinRadius(minRadius);
        setMaxRadius(maxRadius);
        validators.getRaw().putAll(plugin.getLocationValidators().getRaw());
    }

    /**
     * Get all entities targeted by this searcher
     * @return The entitiy to target
     */
    public List<Entity> getTargets() {
        return targets;
    }

    public ValidatorRegistry getValidators() {
        return validators;
    }

    /**
     * Set the seed that should be used when selecting locations. See {@link Random#setSeed(long)}.
     * @param seed The seed.
     */
    public void setSeed(long seed) {
        this.seed = seed;
        if (random == RandomTeleport.RANDOM ) {
            random = new Random(seed);
        } else {
            random.setSeed(seed);
        }
    }

    /**
     * Get the seed of this random searcher. Returns -1 if none was set.
     * @return The seed or -1
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Directly set the Random instance used for selecting coordinates
     * @param random The random instance
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Get the random instance that is used for finding locations
     * @return The random instance; {@link RandomTeleport#RANDOM} by default
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Get the center for this searcher
     * @return The center location
     */
    public Location getCenter() {
        return center;
    }

    /**
     * Set the center of this searcher
     * @param center The center location; never null
     */
    public void setCenter(Location center) {
        Validate.notNull(center, "Center cannot be null!");
        this.center = center;
    }

    /**
     * Get the minimum radius
     * @return The minimum radius, always positive and less than the max radius!
     */
    public int getMinRadius() {
        return minRadius;
    }

    /**
     * Set the minimum search radius
     * @param minRadius The min radius; has to be positive and less than the max radius!
     */
    public void setMinRadius(int minRadius) {
        Validate.isTrue(minRadius >= 0 && minRadius < maxRadius, "Min radius has to be positive and less than the max radius!");
        this.minRadius = minRadius;
    }

    /**
     * Get the maximum radius
     * @return The maximum radius, always greater than the minimum radius
     */
    public int getMaxRadius() {
        return maxRadius;
    }

    /**
     * Set the maximum search radius
     * @param maxRadius The max radius; has to be greater than the min radius!
     */
    public void setMaxRadius(int maxRadius) {
        Validate.isTrue(maxRadius > minRadius, "Max radius has to be greater than the min radius!");
        this.maxRadius = maxRadius;
    }

    /**
     * By default it will search for coordinates in any chunk, even ungenerated ones prompting the world to get
     * generated at the point which might result in some performance impact. This disables that and only searches
     * in already generated chunks.
     * @param generatedOnly Whether or not to search in generated chunks only
     */
    public void searchInGeneratedOnly(boolean generatedOnly) {
        this.generatedOnly = generatedOnly;
    }

    public int getMaxChecks() {
        return maxChecks;
    }

    public void setMaxChecks(int maxChecks) {
        this.maxChecks = maxChecks;
    }

    /**
     * Set the cooldown that a player has to wait before using a searcher with similar settings again
     * @param cooldown The cooldown in seconds
     */
    public void setCooldown(int cooldown) {
        Validate.isTrue(cooldown >= 0, "Cooldown can't be negative!");
        this.cooldown = cooldown;
    }

    /**
     * Get the cooldown that a player has to wait before using a searcher with similar settings again
     * @return  The cooldown in seconds
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Search for a valid location
     * @return A CompletableFuture for when the search task is complete
     */
    public CompletableFuture<Location> search() {
        future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTask(plugin, () -> checkRandom(future));
        return future;
    }

    private void checkRandom(CompletableFuture<Location> future) {
        if (checks >= maxChecks) {
            future.completeExceptionally(new NotFoundException("location"));
            return;
        }
        if (future.isCancelled() || future.isDone() || future.isCompletedExceptionally()) {
            return;
        }
        Location randomLoc = new Location(
                center.getWorld(),
                center.getBlockX() + (random.nextBoolean() ? 1 : -1) * (minRadius + random.nextInt(maxRadius - minRadius)),
                0,
                center.getBlockX() + (random.nextBoolean() ? 1 : -1) * (minRadius + random.nextInt(maxRadius - minRadius))
        );
        PaperLib.getChunkAtAsync(randomLoc, generatedOnly).thenApply(c -> {
            checks++;
            for (LocationValidator validator : getValidators().getAll()) {
                if (!validator.validate(this, randomLoc)) {
                    checkRandom(future);
                    return false;
                }
            }
            future.complete(randomLoc);
            return true;
        }).exceptionally(future::completeExceptionally);
    }

    public RandomTeleport getPlugin() {
        return plugin;
    }

    /**
     * The sender who initiated this search
     * @return The initiator
     */
    public CommandSender getInitiator() {
        return initiator;
    }

    /**
     * Get the currently running search future
     * @return The currently running search future or null if none is running
     */
    public CompletableFuture<Location> getFuture() {
        return future;
    }
}
