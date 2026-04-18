package dev.aegis.anticheat.checks.movement;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import dev.aegis.prediction.PredictionEngine;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpeedCheck {

    private final AegisAC plugin;
    private final PredictionEngine predictionEngine;

    public SpeedCheck(AegisAC plugin) {
        this.plugin = plugin;
        this.predictionEngine = new PredictionEngine(plugin);
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.movement.speed.enabled", true)) return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (data.isInVehicle() || data.isHasVelocity()) return;

        double speed = data.getLastSpeed();
        double maxSpeed = predictionEngine.getMaxAllowedSpeed(player, data);

        if (speed > maxSpeed) {
            double excess = speed - maxSpeed;
            int vlAmount = excess > 0.2 ? 3 : 1;
            plugin.getViolationManager().flag(player, data, "Speed[A]", vlAmount,
                    String.format("speed=%.4f max=%.4f", speed, maxSpeed));
        }
    }
}
