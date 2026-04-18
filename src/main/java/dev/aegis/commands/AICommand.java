package dev.aegis.commands;

import dev.aegis.AegisAC;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AICommand implements CommandExecutor {

    private final AegisAC plugin;

    public AICommand(AegisAC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("aegis.ai")) {
            player.sendMessage(plugin.getLanguageManager().get("no-permission"));
            return true;
        }
        if (!plugin.getAiManager().isEnabled()) {
            player.sendMessage(plugin.getLanguageManager().get("ai-disabled"));
            return true;
        }
        if (plugin.getAiManager().isOnCooldown(player)) {
            player.sendMessage(plugin.getLanguageManager().get("ai-cooldown")
                    .replace("{seconds}", String.valueOf(plugin.getAiManager().getRemainingCooldown(player))));
            return true;
        }

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("ai")) {
            if (args.length < 1) { player.sendMessage(plugin.getLanguageManager().get("usage-ai")); return true; }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) { player.sendMessage(plugin.getLanguageManager().get("player-not-found")); return true; }
            player.sendMessage(plugin.getLanguageManager().get("ai-analyzing").replace("{player}", target.getName()));
            plugin.getAiManager().analyzePlayer(player, target, result ->
                player.sendMessage(plugin.getLanguageManager().get("prefix") + "§b[AI] §f" + result));

        } else if (cmd.equals("aichat")) {
            if (args.length < 1) { player.sendMessage(plugin.getLanguageManager().get("usage-aichat")); return true; }
            if (args[0].equalsIgnoreCase("analiz") && args.length >= 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { player.sendMessage(plugin.getLanguageManager().get("player-not-found")); return true; }
                player.sendMessage(plugin.getLanguageManager().get("ai-analyzing").replace("{player}", target.getName()));
                plugin.getAiManager().analyzePlayer(player, target, result ->
                    player.sendMessage(plugin.getLanguageManager().get("prefix") + "§b[AI] §f" + result));
            } else {
                String message = String.join(" ", args);
                plugin.getAiManager().chat(player, message, result ->
                    player.sendMessage(plugin.getLanguageManager().get("prefix")
                            + plugin.getLanguageManager().get("aichat-response").replace("{response}", result)));
            }
        }
        return true;
    }
}
