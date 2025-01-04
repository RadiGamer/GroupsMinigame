package org.imradigamer.groupsMinigame;

import org.bukkit.ChatColor;
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
                    gameManager.startGroups(groupSize);
                    sender.sendMessage(ChatColor.GREEN + "Iniciando juego con tamaño de grupo: " + groupSize);
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
}
