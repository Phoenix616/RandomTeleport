RandomTeleport v2
==============

Second generation random teleport Bukkit Plugin.

Lets you teleport players to a random location of a map within your provided parameters while respecting stuff like world borders and region protections.

SpigotMC resource page: http://www.spigotmc.org/resources/fubs-random-teleport.1094/

## Command

Aliases: `randomteleport`, `randomtp`, `rtp`

Permission: `randomteleport.use`

Usage                                       | Description
--------------------------------------------|-------------------------------
`/rtp`                                      | uses the default preset  
`/rtp <preset1,...> [<playername>]`         | uses a specific or random preset  
`/rtp <minRange> <maxRange> [<options>]`    | `minRange` - minimum distance to the center point (square shaped) <br> `maxRange` - maximum distance to the center point (square shaped)  
`/rtp --stat`                               | shows a statistic of the teleports since the last restart  
`/rtp --reload`                             | reloads the config  

Option                          | Description
--------------------------------|-------------------------------------------
`-p,-player <playername>`       | teleports other players 
`-w,-world <world1,world2,...>` | teleports the player in a specific or random world   
`-b,-biome <biomename...>`      | only teleport to this biome (multiple allowed, Bukkit biome names!)  
`-x,-xPos <x value>`            | x axis of the center point, if not set the player's x axis is used  
`-z,-zPos <z value>`            | z axis of the center point, if not set the player's z axis is used  
`-minY <y value>`               | minimum y value that the random location should have (default: 0)  
`-maxY <y value>`               | maximum y value that the random location should have (default: world height, half in nether)  
`-l,-loaded`                    | only search loaded chunks for possible locations (might fail more often)  
`-g,-generated`                 | only search generated chunks for possible locations  
`-c, -cooldown <seconds>`       | cooldown in seconds after which the player can use this teleporter again  
`-id <id>`                      | the ID to use for the cooldown, uses automatically generated one if not provided  
`-f,-force`                     | teleport even if there is no dirt/grass/sand/gravel, only checks for lava/water/cactus, ignores WorldGuard/Faction regions  
`-f,-force [<blocks/regions>]`  | only ignore blocks or regions  
`-t,-tries <amount>`            | the amount of times the plugin should try to find a random location before giving up  
`-sp,spawnpoint [force]`        | set the respawn point of the player to the location he teleported to (force overrides existing spawnpoint)  
`-checkdelay <ticks>`           | the amount of ticks to wait between each chunk check
`-debug`                        | print some more debugging information in the log

## Permissions

Permission                          | Default | Description
------------------------------------|---------|---------------------------
randomteleport.use                  | op      | Gives permission to the command
randomteleport.manual               | op      | Gives permission to manually specify parameters in the command
randomteleport.manual.option.*      | op      | Gives permission to use certain options in the command
randomteleport.tpothers             | op      | Gives permission to teleport other players
randomteleport.cooldownexempt       | op      | Teleportcooldown does not effect these players
randomteleport.stat                 | op      | Permission for showing the teleport statistic
randomteleport.reload               | op      | Permission to use the reload command 
randomteleport.presets.default      | op      | Gives permission to use the default random teleport preset
randomteleport.presets.*            | op      | Gives permission to use all random teleport presets
randomteleport.sign.preset.default  | op      | Gives permission to use the default preset with a rightclick on a preset sign
randomteleport.sign.preset.*        | op      | Gives permission to use all presets with a rightclick on a preset sign
randomteleport.sign.create          | op      | Allows creating preset signs ([rtp] or [RandomTP] on the 2nd line and the preset name on the 3rd)
randomteleport.sign.destroy         | op      | Allows destroying preset signs ([rtp] or [RandomTP] on the 2nd line and the preset name on the 3rd)

## Downloads

Releases: [SpigotMC resource page](http://www.spigotmc.org/resources/fubs-random-teleport.1094/)

Development Builds: [Minebench.de Jenkins server](https://ci.minebench.de/job/Randomteleport/)

## License

```
RandomTeleport
Copyright (c) 2019 Max Lee aka Phoenix616 (mail@moep.tv)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```