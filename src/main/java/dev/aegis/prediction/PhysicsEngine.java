package dev.aegis.prediction;

import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PhysicsEngine {

    private static final double GRAVITY = 0.08;
    private static final double DRAG = 0.98;
    private static final double JUMP_VELOCITY = 0.42;

    public double computeExpectedDeltaY(PlayerData data) {
        if (data.isOnGround()) return 0;
        return (data.getLastDeltaY() - GRAVITY) * DRAG;
    }

    public double getJumpVelocity(Player player) {
        double base = JUMP_VELOCITY;
        PotionEffect jump = player.getPotionEffect(PotionEffectType.JUMP_BOOST);
        if (jump != null) base += (jump.getAmplifier() + 1) * 0.1;
        return base;
    }

    public boolean isPhysicsViolation(PlayerData data, double actualDeltaY, double tolerance) {
        if (data.isOnGround()) return false;
        if (data.getAirTicks() <= 1) return false;
        double expected = computeExpectedDeltaY(data);
        return Math.abs(actualDeltaY - expected) > tolerance;
    }
}
