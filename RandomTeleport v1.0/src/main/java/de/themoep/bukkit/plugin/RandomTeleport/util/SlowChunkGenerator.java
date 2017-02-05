/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.themoep.bukkit.plugin.RandomTeleport.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import de.themoep.bukkit.plugin.RandomTeleport.RandomTeleport;

public class SlowChunkGenerator {
	private static final Set<SimpleChunkLocation> loading = new HashSet<SimpleChunkLocation>();
	public static void loadChunkSlowly(final World world, final int i, final int j, final Runnable next){
		final int range = Bukkit.getServer().getViewDistance();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int x = -range; x <= range; x++){
					for(int z = -range; z <= range; z++){
						final int currX = i + x;
						final int currZ = j + z;
						
						//skip if already loaded
						if(world.isChunkLoaded(currX, currZ))
							continue;
						
						SimpleChunkLocation scLoc = new SimpleChunkLocation(world.getName(), currX, currZ);
						//skip if another thread is working on it
						if(loading.contains(scLoc))
							continue;
						
						//set this chunk as loading
						loading.add(scLoc);
						
						BukkitTask task = Bukkit.getScheduler().runTask(RandomTeleport.instance, new Runnable(){
							@Override
							public void run() {
								world.loadChunk(currX, currZ, true);
							}
						});
						
						do {
							try {
								Thread.yield();
								Thread.sleep(100L);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} while (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()));
						
						//remove this chunk from loading
						loading.remove(scLoc);
					}
				}
				
				Bukkit.getScheduler().runTask(RandomTeleport.instance, next);
			}
		}){{setPriority(Thread.MIN_PRIORITY);}}.start();
	}
	
	private static class SimpleChunkLocation{
		String world;
		int i;
		int j;
		
		public SimpleChunkLocation(String world, int i, int j) {
			this.world = world;
			this.i = i;
			this.j = j;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			result = prime * result + j;
			result = prime * result + ((world == null) ? 0 : world.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SimpleChunkLocation other = (SimpleChunkLocation) obj;
			if (i != other.i)
				return false;
			if (j != other.j)
				return false;
			if (world == null) {
				if (other.world != null)
					return false;
			} else if (!world.equals(other.world))
				return false;
			return true;
		}
		
	}
}
