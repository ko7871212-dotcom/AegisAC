package dev.aegis.anticheat.checks.movement;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import dev.aegis.prediction.PredictionEngine;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class AccelerationCheck {

    private final AegisAC plugin;
    private final PredictionEngine engine;

    public AccelerationCheck(AegisAC plugin) {
        this.plugin = plugin;
        this.engine = new PredictionEngine(plugin);
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.movement.acceleration.enabled", true)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (data.isInVehicle() || data.isHasVelocity()) return;
        if (data.getPredictedLocation() == null) return;

        double error = engine.getPredictionError(player, data, data.getCurrentLocation());
        if (error > 0.5) {
            plugin.getViolationManager().flag(player, data, "Acceleration[A]", 2,
                    String.format("predErr=%.4f", error));
        }
    }
}
