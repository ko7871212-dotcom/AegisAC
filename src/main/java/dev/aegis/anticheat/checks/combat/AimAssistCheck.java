package dev.aegis.anticheat.checks.combat;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

import java.util.List;

public class AimAssistCheck {

    private final AegisAC plugin;

    public AimAssistCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.combat.aimassist.enabled", true)) return;
        List<float[]> rotations = data.getRotationHistory();
        if (rotations.size() < 10) return;

        double yawVariance = computeVariance(rotations, true);
        double pitchVariance = computeVariance(rotations, false);

        if (yawVariance < 0.001 && pitchVariance < 0.001 && data.getCps() > 3) {
            plugin.getViolationManager().flag(player, data, "AimAssist[A]", 2,
                    String.format("yawVar=%.5f pitchVar=%.5f", yawVariance, pitchVariance));
        }

        float[] last = rotations.get(rotations.size() - 1);
        float[] secondLast = rotations.get(rotations.size() - 2);
        double lastDelta = Math.abs(last[0] - secondLast[0]);
        if (lastDelta > 20 && yawVariance < 0.01) {
            plugin.getViolationManager().flag(player, data, "AimAssist[B]", 2,
                    "snapThenSmooth=" + String.format("%.2f", lastDelta));
        }
    }

    private double computeVariance(List<float[]> rotations, boolean yaw) {
        int idx = yaw ? 0 : 1;
        double mean = rotations.stream().mapToDouble(r -> r[idx]).average().orElse(0);
        return rotations.stream().mapToDouble(r -> Math.pow(r[idx] - mean, 2)).average().orElse(0);
    }
}
