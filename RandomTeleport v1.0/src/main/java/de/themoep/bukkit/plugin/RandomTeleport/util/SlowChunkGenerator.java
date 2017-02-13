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

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class SlowChunkGenerator {
    private static final Queue<ChunkGroup> queue = new LinkedList<ChunkGroup>();

    public static void tick() {
        if (queue.isEmpty())
            return;

        ChunkGroup group = queue.peek();
        if (group == null)
            return;

        if (group.chunks.isEmpty()) {
            queue.poll();
            group.next.run();
            return;
        }

        Chunk chunk = group.chunks.peek();
        if (chunk.isLoaded()) {
            group.chunks.poll();
            return;
        }

        chunk.load(true);
    }

    public static void loadChunkSlowly(final World world, final int i, final int j, final Runnable next) {
        Validate.notNull(next);

        final int range = Bukkit.getServer().getViewDistance();

        ChunkGroup group = new ChunkGroup();
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                group.chunks.add(world.getChunkAt(x, z));
            }
        }
        group.next = next;
        queue.add(group);
    }

    private static class ChunkGroup {
        Queue<Chunk> chunks = new LinkedList<Chunk>();
        Runnable next;
    }
}
