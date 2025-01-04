package org.imradigamer.groupsMinigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

class PlayerUtils {


    public static void applyRedGlowing(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("Eliminado");
        if (team == null) {
            team = board.registerNewTeam("Eliminado");
            team.setColor(ChatColor.RED);
        }
        team.addEntry(player.getName());
        player.setGlowing(true);
    }
    public static void removeGlowing(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("Eliminado");
        if (team != null) {
            team.removeEntry(player.getName());
            if (team.getEntries().isEmpty()) {
                team.unregister();
            }
        }
        player.setGlowing(false);
    }

}