package org.imradigamer.groupsMinigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class GameManager {

    private final GroupsMinigame plugin;

    private static final int ROOM_RADIUS = 3;
    private static final String ROOM_TAG_PREFIX = "room_";
    private static final String VISUAL_ROOM_TAG_PREFIX = "visual_room_";

    public GameManager(GroupsMinigame plugin) {
        this.plugin = plugin;
    }

    public void startGroups(int groupSize) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as @a at @s run playsound minecraft:juego_3_musica master @s ~ ~ ~ 100");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lights true");

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.AQUA + "¡Forma grupos de : " + groupSize + "!");
                playGroupSizeSound(groupSize);

                updateTextDisplays("0/" + groupSize);
                resetTextDisplays();
                startGroupingPhase(groupSize);
                plugin.createSquareWallFromCorners(true);
            }
        }.runTaskLater(plugin, 64 * 20L);
    }

    private void playGroupSizeSound(int groupSize) {
        String sound = switch (groupSize) {
            case 1 -> "uno";
            case 2 -> "dos";
            case 3 -> "tres";
            case 4 -> "cuatro";
            case 5 -> "cinco";
            case 6 -> "seis";
            case 7 -> "siete";
            case 8 -> "ocho";
            case 9 -> "nueve";
            case 10 -> "diez";
            case 11 -> "once";
            case 12 -> "doce";
            default -> null;
        };

        if (sound != null) {
            // Play sound to all players
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "execute as @a at @s run playsound minecraft:" + sound + " master @s ~ ~ ~ 100");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "execute as @a at @s run playsound minecraft:agrupados master @s ~ ~ ~ 100");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "lights false");


            // Display a title to all players
            String title = ChatColor.YELLOW + "Grupo de tamaño: "+ ChatColor.GOLD + groupSize;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(title, " ", 10, 70, 20);
            }
        }
    }


    private void updateTextDisplays(String displayText) {
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof org.bukkit.entity.TextDisplay textDisplay && entity.getScoreboardTags().contains("AmountGroups")) {
                    textDisplay.setText(displayText);
                }
            }
        });
    }

    private void startGroupingPhase(int groupSize) {
        new BukkitRunnable() {
            int timeLeft = 30;

            @Override
            public void run() {
                if (timeLeft >= 0) {
                    updateTimerDisplays(timeLeft);
                    updatePlayerCounts(groupSize);
                    timeLeft--;
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "stopsound @a");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "lights true");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "door stop false");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateTimerDisplays(int timeLeft) {
        String timeDisplay = String.format("0:%02d", timeLeft);
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof org.bukkit.entity.TextDisplay textDisplay && entity.getScoreboardTags().contains("TimerGroups")) {
                    textDisplay.setText(timeDisplay);
                }
            }
        });
    }

    private void updatePlayerCounts(int groupSize) {
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains(VISUAL_ROOM_TAG_PREFIX)) {
                    List<Player> nearbyPlayers = entity.getNearbyEntities(ROOM_RADIUS, ROOM_RADIUS, ROOM_RADIUS).stream()
                            .filter(e -> e instanceof Player)
                            .map(e -> (Player) e)
                            .collect(Collectors.toList());

                    String displayText = nearbyPlayers.size() + "/" + groupSize;
                    if (entity instanceof org.bukkit.entity.TextDisplay textDisplay) {
                        textDisplay.setText(displayText);
                    }
                }
            }
        });
    }

    public void resetGlowing() {
        Bukkit.getOnlinePlayers().forEach(PlayerUtils::removeGlowing);
    }

    public void resetTextDisplays() {
        updateTextDisplays("0/0");
    }
}
