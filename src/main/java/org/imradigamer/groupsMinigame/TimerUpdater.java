package org.imradigamer.groupsMinigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class TimerUpdater {

    private final JavaPlugin plugin;

    public TimerUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Resets all TextDisplays with the TimerGroups tag to the initial time (0:30).
     */
    public void resetTextDisplays() {
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof TextDisplay textDisplay && entity.getScoreboardTags().contains("TimerGroups")) {
                    textDisplay.setText("0:30");
                }
            }
        });
    }

    /**
     * Starts the countdown timer for the specified duration.
     */
    public void startTimer(int seconds) {
        // Reset the text to the initial time
        resetTextDisplays();

        new BukkitRunnable() {
            int timeLeft = seconds;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    // Stop the timer when it reaches zero
                    cancel();
                    return;
                }

                // Convert time to minutes:seconds format
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                String timeDisplay = String.format("%d:%02d", minutes, seconds);

                // Update all TextDisplays with the TimerGroups tag
                Bukkit.getWorlds().forEach(world -> {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof TextDisplay textDisplay && entity.getScoreboardTags().contains("TimerGroups")) {
                            textDisplay.setText(timeDisplay); // Update the text
                        }
                    }
                });

                // Decrement the time
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // Runs every 20 ticks (1 second)
    }
}
