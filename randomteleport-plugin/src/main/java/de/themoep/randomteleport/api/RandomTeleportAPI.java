package de.themoep.randomteleport.api;

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

import de.themoep.randomteleport.searcher.RandomSearcher;
import de.themoep.randomteleport.searcher.validators.LocationValidator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface RandomTeleportAPI {


  /**
   *
   * Returns a random Location
   *
   * @param player the Player initiating the search
   * @param center the location where the search should begin
   * @param minRange the minimum distance a found location has to the center location
   * @param maxRange the maximum distance a found location has to the center location
   * @param validators additional LocationValidators to customize validity check of a location
   * @throws ExecutionException see {@link CompletableFuture#get()}
   * @throws InterruptedException see {@link CompletableFuture#get()}
   *
   * @return a random Location
   */
  Location getRandomLocation(Player player, Location center, int minRange, int maxRange, LocationValidator... validators)
      throws ExecutionException, InterruptedException;

  /**
   *
   * Teleports the passed Player to a random Location
   *
   * @param player the Player initiating the search
   * @param center the location where the search should begin
   * @param minRange the minimum distance a found location has to the center location
   * @param maxRange the maximum distance a found location has to the center location
   * @param validators additional LocationValidators to customize validity check of a location
   * @throws ExecutionException see {@link CompletableFuture#get()}
   * @throws InterruptedException see {@link CompletableFuture#get()}
   */
  void teleportToRandomLocation(Player player, Location center, int minRange, int maxRange, LocationValidator... validators)
      throws ExecutionException, InterruptedException;

  /**
   * Creates a RandomSearcher instance with the passed parameters
   *
   * @param player the Player initiating the search
   * @param center the location where the search should begin
   * @param minRange the minimum distance a found location has to the center location
   * @param maxRange the maximum distance a found location has to the center location
   * @param validators additional LocationValidators to customize validity check of a location
   * @throws ExecutionException see {@link CompletableFuture#get()}
   * @throws InterruptedException see {@link CompletableFuture#get()}
   *
   * @return a randomSearcher instance
   */
  RandomSearcher getRandomSearcher(Player player, Location center, int minRange, int maxRange, LocationValidator... validators)
      throws ExecutionException, InterruptedException;

}
