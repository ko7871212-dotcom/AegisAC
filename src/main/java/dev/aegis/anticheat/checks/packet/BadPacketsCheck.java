package dev.aegis.anticheat.checks.packet;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BadPacketsCheck {

    private final AegisAC plugin;

    public BadPacketsCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void checkRotation(Player player, PlayerData data, float yaw, float pitch) {
        if (!plugin.getConfig().getBoolean("checks.packet.badpackets.enabled", true)) return;
        if (pitch > 90 || pitch < -90) {
            plugin.getViolationManager().flag(player, data, "BadPackets[A]", 5,
                    "invalidPitch=" + pitch);
        }
        if (Float.isNaN(yaw) || Float.isInfinite(yaw) || Float.isNaN(pitch) || Float.isInfinite(pitch)) {
            plugin.getViolationManager().flag(player, data, "BadPackets[B]", 10, "NaN/Inf rotation");
        }
    }

    public void checkPosition(Player player, PlayerData data, double x, double y, double z) {
        if (!plugin.getConfig().getBoolean("checks.packet.badpackets.enabled", true)) return;
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)
                || Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) {
            plugin.getViolationManager().flag(player, data, "BadPackets[C]", 10, "NaN/Inf position");
        }
        if (data.getLastLocation() != null) {
            double dist = data.getLastLocation().distance(
                    new Location(player.getWorld(), x, y, z));
            if (dist > 100) {
                plugin.getViolationManager().flag(player, data, "BadPackets[D]", 5,
                        "teleport dist=" + String.format("%.2f", dist));
            }
        }
    }
}
