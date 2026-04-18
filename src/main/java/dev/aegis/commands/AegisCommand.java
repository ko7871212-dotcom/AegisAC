package dev.aegis.commands;

import dev.aegis.AegisAC;
import dev.aegis.admin.AdminGUI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AegisCommand implements CommandExecutor, TabCompleter {

    private final AegisAC plugin;
    private final AdminGUI adminGUI;

    public AegisCommand(AegisAC plugin) {
        this.plugin = plugin;
        this.adminGUI = new AdminGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("aegis.admin")) {
            sender.sendMessage(plugin.getLanguageManager().get("no-permission"));
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("panel")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console cannot open GUI.");
                return true;
            }
            adminGUI.openMain(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reload();
                sender.sendMessage(plugin.getLanguageManager().get("reload-success"));
            }
            case "info" -> {
                sender.sendMessage("§bAegisAC §fv" + plugin.getDescription().getVersion());
                sender.sendMessage("§7Online: §f" + plugin.getServer().getOnlinePlayers().size());
            }
            default -> sender.sendMessage(plugin.getLanguageManager().get("usage-aegis"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("panel", "reload", "info");
        return List.of();
    }
}
