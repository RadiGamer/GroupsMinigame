package org.imradigamer.groupsMinigame;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupsMinigame extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);

        // Command for starting the game
        getCommand("startgroups").setExecutor((sender, command, label, args) -> {
            if (sender.isOp()) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Uso: /startgroups <Número>");
                    return true;
                }
                try {
                    int groupSize = Integer.parseInt(args[0]);
                    gameManager.startGroups(groupSize); // Start the game with the specified group size
                    sender.sendMessage(ChatColor.GREEN + "Iniciando juego con tamaño de grupo: " + groupSize);

                    Bukkit.dispatchCommand(sender, "multiwarp tp juego3 @a");
                    createSquareWallFromCorners(false);

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "door start true");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "door true G3 @a");

                    // Start a 62-second timer
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        // Execute the command when the timer ends
                        Bukkit.dispatchCommand(sender, "multiwarp tp juego3 @a");
                    }, 62 * 20L); // 62 seconds * 20 ticks per second

                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "El tamaño del grupo debe ser un número válido.");
                }
            }
            return true;
        });


        // Command for the next round
        getCommand("nextround").setExecutor((sender, command, label, args) -> {
            if (sender.isOp()) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Uso: /nextround <Número>");
                    return true;
                }
                try {
                    int groupSize = Integer.parseInt(args[0]);
                    gameManager.resetGlowing(); // Reset glowing effects for the new round
                    gameManager.startGroups(groupSize); // Start a new round
                    sender.sendMessage(ChatColor.GREEN + "Iniciando siguiente ronda con tamaño de grupo: " + groupSize);

                    // Start a 62-second timer
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        // Execute the command when the timer ends
                        Bukkit.dispatchCommand(sender, "multiwarp tp juego3 @a");
                    }, 62 * 20L); // 62 seconds * 20 ticks per second

                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "El tamaño del grupo debe ser un número válido.");
                }
            }
            return true;
        });


        // Command to reset glowing effects
        getCommand("restartglowing").setExecutor((sender, command, label, args) -> {
            if (sender.isOp()) {
                gameManager.resetGlowing();
                sender.sendMessage(ChatColor.GREEN + "Quitando efectos de Glowing.");
            }
            return true;
        });
    }

    public void createSquareWallFromCorners(Boolean remove) {
        Location corner1 = new Location(Bukkit.getWorld("world"), -502, 64, 528);
        Location corner2 = new Location(Bukkit.getWorld("world"), -446, 68, 472);
        World world = corner1.getWorld();
        if (world == null || !world.equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Both corners must be in the same world.");
        }

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        // Iterate over the perimeter to create the wall
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // Place blocks only on the edges
                if (x == minX || x == maxX || z == minZ || z == maxZ) {
                    for (int y = minY; y <= maxY; y++) { // Wall height defined by minY to maxY
                        Location blockLocation = new Location(world, x, y, z);
                        if(!remove) {
                            world.getBlockAt(blockLocation).setType(Material.BARRIER);
                        } else if (remove) {
                            world.getBlockAt(blockLocation).setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

}
