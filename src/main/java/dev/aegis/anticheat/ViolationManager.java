package dev.aegis.anticheat;

import dev.aegis.AegisAC;
import dev.aegis.alert.AlertLevel;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

public class ViolationManager {

    private final AegisAC plugin;

    public ViolationManager(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void flag(Player player, PlayerData data, String checkName, int amount, String info) {
        int vl = data.addViolation(checkName, amount);
        plugin.getTrustManager().penalize(player);
        AlertLevel level = getAlertLevel(vl);
        plugin.getAlertManager().sendAlert(player, data, checkName, vl, level, info);
        applyPunishment(player, data, vl, level);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
            plugin.getDatabaseManager().logViolation(player.getUniqueId(), checkName, vl));
    }

    private AlertLevel getAlertLevel(int vl) {
        if (vl >= 80) return AlertLevel.VERY_HIGH;
        if (vl >= 40) return AlertLevel.HIGH;
        if (vl >= 15) return AlertLevel.MEDIUM;
        return AlertLevel.LOW;
    }

    private void applyPunishment(Player player, PlayerData data, int vl, AlertLevel level) {
        int kickThreshold = plugin.getConfig().getInt("punishments.violations-for-kick", 50);
        int banThreshold = plugin.getConfig().getInt("punishments.violations-for-ban", 100);

        if (vl >= banThreshold && plugin.getConfig().getBoolean("alerts.very-high.repeat-ban")) {
            plugin.getServer().getScheduler().runTask(plugin, () ->
                player.kickPlayer(plugin.getLanguageManager().get("player-banned")));
        } else if (vl >= kickThreshold && plugin.getConfig().getBoolean("alerts.high.kick")) {
            plugin.getServer().getScheduler().runTask(plugin, () ->
                player.kickPlayer(plugin.getLanguageManager().get("player-kicked")));
        }
    }
}
