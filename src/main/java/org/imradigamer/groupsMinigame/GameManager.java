package org.imradigamer.groupsMinigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class GameManager {

    private final JavaPlugin plugin;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts the game with a 40-second preparation phase, followed by a 30-second grouping phase.
     */
    public void startGroups(int groupSize) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:juego_3_musica master @s ~ ~ ~ 100");

        // Start the 40-second preparation phase
        new BukkitRunnable() {
            @Override
            public void run() {
                // Announce the required group size after 40 seconds
                Bukkit.broadcastMessage(ChatColor.AQUA + "¡Forma grupos de : " + groupSize + "!");

                switch (groupSize){
                    case 1:
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:uno master @s ~ ~ ~ 100");
                        break;
                        case 2:
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:dos master @s ~ ~ ~ 100");
                            break;

                            case 3:
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:tres master @s ~ ~ ~ 100");
                                break;
                                case 4:
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:cuatro master @s ~ ~ ~ 100");
                                    break;
                                    case 5:
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:cinco master @s ~ ~ ~ 100");
                                        break;
                                        case 6:
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:seis master @s ~ ~ ~ 100");
                                            break;
                                            case 7:
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:siete master @s ~ ~ ~ 100");
                                                break;
                                                case 8:
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:ocho master @s ~ ~ ~ 100");
                                                    break;
                                                    case 9:
                                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:nueve master @s ~ ~ ~ 100");
                                                        break;
                                                        case 10:
                                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:diez master @s ~ ~ ~ 100");
                                                            break;
                                                            case 11:
                                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:once master @s ~ ~ ~ 100");
                                                                break;
                                                                case 12:
                                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:doce master @s ~ ~ ~ 100");
                                                                    break;
                }

                // Update all TextDisplays with the AmountGroups tag
                updateAmountDisplays(groupSize);

                // Start the 30-second grouping phase
                startGroupingPhase(groupSize);
            }
        }.runTaskLater(plugin, 64 * 20L); // 40 seconds delay
    }

    /**
     * Updates all TextDisplays with the AmountGroups tag to display the group size.
     */
    private void updateAmountDisplays(int groupSize) {
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof org.bukkit.entity.TextDisplay textDisplay && entity.getScoreboardTags().contains("AmountGroups")) {
                    textDisplay.setText(String.valueOf(groupSize)); // Display only the number
                }
            }
        });
    }



    /**
     * Starts the 30-second timer for players to form groups.
     */
    private void startGroupingPhase(int groupSize) {
        // Reset TimerGroups displays to 0:30
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof org.bukkit.entity.TextDisplay textDisplay && entity.getScoreboardTags().contains("TimerGroups")) {
                    textDisplay.setText("0:30"); // Reset timer to initial value with white text
                }
            }
        });

        // Reset AmountGroups displays to empty
        resetAmountDisplays();

        // Start the 30-second countdown
        new BukkitRunnable() {
            int timeLeft = 30;

            @Override
            public void run() {
                if (timeLeft >= 0) { // Ensure the timer displays 0:00
                    int minutes = timeLeft / 60;
                    int seconds = timeLeft % 60;
                    String timeDisplay = String.format("%d:%02d", minutes, seconds);

                    // Update all TimerGroups displays
                    Bukkit.getWorlds().forEach(world -> {
                        for (Entity entity : world.getEntities()) {
                            if (entity instanceof org.bukkit.entity.TextDisplay textDisplay && entity.getScoreboardTags().contains("TimerGroups")) {
                                textDisplay.setText(timeDisplay); // Display timer in white
                            }
                        }
                    });

                    timeLeft--;
                } else {
                    // Timer has ended; cancel task and evaluate groups
                    cancel();
                    evaluateGroups(groupSize);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Runs every second for 30 seconds
    }

    private void resetAmountDisplays() {
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof org.bukkit.entity.TextDisplay textDisplay && entity.getScoreboardTags().contains("AmountGroups")) {
                    textDisplay.setText(" "); // Clear the text display
                }
            }
        });
    }





    /**
     * Evaluates players' groups based on the required group size.
     */
    private void evaluateGroups(int groupSize) {
        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.isOp()) // Ignore OP players
                .collect(Collectors.toList());

        for (Player player : players) {
            // Find nearby entities within 5 blocks
            List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);

            // Check if the player is near a room entity with a tag "room_<number>"
            boolean isNearRoom = nearbyEntities.stream()
                    .anyMatch(entity -> entity.getScoreboardTags().stream()
                            .anyMatch(tag -> tag.matches("room_\\d+"))); // Match tags in the format "room_<number>"

            if (!isNearRoom) {
                // Player is not near any room; eliminate them
                PlayerUtils.applyRedGlowing(player);
                player.sendMessage(ChatColor.RED + "¡Has sido eliminado! No estás cerca de una habitación.");
                continue;
            }

            // Count valid nearby players (excluding OPs)
            long count = nearbyEntities.stream()
                    .filter(entity -> entity instanceof Player && !((Player) entity).isOp())
                    .count();

            // Check if the player is in a valid group
            if (count + 1 < groupSize || count + 1 > groupSize) {
                PlayerUtils.applyRedGlowing(player);
                player.sendMessage(ChatColor.RED + "¡Has sido eliminado! Tamaño de grupo incorrecto.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Estás en un grupo válido.");
            }
        }

        Bukkit.broadcastMessage(ChatColor.YELLOW + "¡La fase de formación de grupos ha terminado!");
    }


    /**
     * Resets glowing effects on all players.
     */
    public void resetGlowing() {
        Bukkit.getOnlinePlayers().forEach(PlayerUtils::removeGlowing);
    }
}
