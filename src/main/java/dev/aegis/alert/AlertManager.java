package dev.aegis.alert;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AlertManager {

    private final AegisAC plugin;

    public AlertManager(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void sendAlert(Player player, PlayerData data, String check,
                          int vl, AlertLevel level, String info) {
        String msgKey = switch (level) {
            case LOW -> "alert-low";
            case MEDIUM -> "alert-medium";
            case HIGH -> "alert-high";
            case VERY_HIGH -> "alert-very-high";
        };

        String msg = plugin.getLanguageManager().get(msgKey)
                .replace("{player}", player.getName())
                .replace("{check}", check)
                .replace("{vl}", String.valueOf(vl))
                .replace("{info}", info != null ? info : "");

        String coloredMsg = plugin.getLanguageManager().get("prefix") + msg;

        switch (level) {
            case LOW -> {
                if (plugin.getConfig().getBoolean("alerts.low.log")) log(coloredMsg);
            }
            case MEDIUM -> {
                if (plugin.getConfig().getBoolean("alerts.medium.log")) log(coloredMsg);
                if (plugin.getConfig().getBoolean("alerts.medium.notify-admins")) notifyAdmins(coloredMsg);
                if (plugin.getConfig().getBoolean("alerts.medium.actionbar")) sendActionBar(player, coloredMsg);
            }
            case HIGH -> {
                if (plugin.getConfig().getBoolean("alerts.high.log")) log(coloredMsg);
                if (plugin.getConfig().getBoolean("alerts.high.notify-admins")) notifyAdmins(coloredMsg);
            }
            case VERY_HIGH -> {
                log(coloredMsg);
                notifyAdmins(coloredMsg);
                if (plugin.getConfig().getBoolean("alerts.very-high.title")) sendTitle(player);
                if (plugin.getConfig().getBoolean("alerts.very-high.sound")) playSound(player);
            }
        }
    }

    private void notifyAdmins(String msg) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("aegis.notify")) p.sendMessage(msg);
            }
        });
    }

    private void sendActionBar(Player player, String msg) {
        Bukkit.getScheduler().runTask(plugin, () ->
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg)));
    }

    private void sendTitle(Player player) {
        Bukkit.getScheduler().runTask(plugin, () ->
            player.sendTitle("§4⚠ CRITICAL", "§cVery high risk detected", 5, 40, 10));
    }

    private void playSound(Player player) {
        Bukkit.getScheduler().runTask(plugin, () ->
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.0f));
    }

    private void log(String msg) {
        plugin.getLogger().info(msg.replaceAll("§[0-9a-fk-or]", ""));
    }
}
