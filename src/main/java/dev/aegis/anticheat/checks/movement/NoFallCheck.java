package dev.aegis.anticheat.checks.movement;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import dev.aegis.prediction.PhysicsEngine;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class NoFallCheck {

    private final AegisAC plugin;
    private final PhysicsEngine physicsEngine;

    public NoFallCheck(AegisAC plugin) {
        this.plugin = plugin;
        this.physicsEngine = new PhysicsEngine();
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.movement.nofall.enabled", true)) return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (data.isInLiquid() || data.isInVehicle()) return;
        if (data.wasOnGround() && !data.isOnGround()) return;

        double deltaY = data.getDeltaY();
        int airTicks = data.getAirTicks();

        if (airTicks > 5 && deltaY < -0.5) {
            if (physicsEngine.isPhysicsViolation(data, deltaY, 0.05)) {
                plugin.getViolationManager().flag(player, data, "NoFall[A]", 2,
                        String.format("deltaY=%.4f airTicks=%d", deltaY, airTicks));
            }
        }
    }
}
