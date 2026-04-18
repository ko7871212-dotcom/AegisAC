package dev.aegis.anticheat.checks.movement;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class StepCheck {

    private final AegisAC plugin;
    private final double maxStep;

    public StepCheck(AegisAC plugin) {
        this.plugin = plugin;
        this.maxStep = plugin.getConfig().getDouble("checks.movement.step.max-step-height", 1.0);
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.movement.step.enabled", true)) return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (data.isInLiquid()) return;

        double deltaY = data.getDeltaY();
        if (data.isOnGround() && data.wasOnGround() && deltaY > maxStep) {
            plugin.getViolationManager().flag(player, data, "Step[A]", 3,
                    String.format("deltaY=%.4f max=%.2f", deltaY, maxStep));
        }
    }
}
