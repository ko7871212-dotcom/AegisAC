package dev.aegis.prediction;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PredictionEngine {

    private final AegisAC plugin;
    private static final double GRAVITY = 0.08;
    private static final double DRAG = 0.98;
    private static final double BASE_SPEED = 0.221;
    private static final double SPRINT_MODIFIER = 1.3;

    public PredictionEngine(AegisAC plugin) {
        this.plugin = plugin;
    }

    public Location predict(Player player, PlayerData data) {
        Location current = data.getCurrentLocation();
        if (current == null) return null;
        double predictedX = current.getX() + data.getDeltaX() * DRAG;
        double predictedZ = current.getZ() + data.getDeltaZ() * DRAG;
        double predictedY = data.isOnGround() ? current.getY()
                : current.getY() + (data.getLastDeltaY() - GRAVITY) * DRAG;
        return new Location(current.getWorld(), predictedX, predictedY, predictedZ,
                current.getYaw(), current.getPitch());
    }

    public double getPredictionError(Player player, PlayerData data, Location actual) {
        Location predicted = predict(player, data);
        if (predicted == null) return 0;
        double pingTolerance = plugin.getConfig().getBoolean("lag-compensation.enabled")
                ? Math.min(data.getPing(),
                    plugin.getConfig().getInt("lag-compensation.max-ping-tolerance-ms", 300)) * 0.001 * 0.5
                : 0;
        return Math.max(0, predicted.distance(actual) - pingTolerance);
    }

    public double getMaxAllowedSpeed(Player player, PlayerData data) {
        double base = BASE_SPEED;
        PotionEffect speed = player.getPotionEffect(PotionEffectType.SPEED);
        if (speed != null) base += (speed.getAmplifier() + 1) * 0.04;
        if (player.isSprinting()) base *= SPRINT_MODIFIER;
        if (data.isOnIce()) base *= 1.5;
        if (data.isOnSlime()) base *= 1.2;
        return base * plugin.getConfig().getDouble("checks.movement.speed.max-speed-multiplier", 1.35);
    }
}
