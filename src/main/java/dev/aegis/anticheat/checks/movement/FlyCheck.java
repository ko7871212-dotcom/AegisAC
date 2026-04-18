package dev.aegis.anticheat.checks.movement;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FlyCheck {

    private final AegisAC plugin;
    private final int maxAirTicks;

    public FlyCheck(AegisAC plugin) {
        this.plugin = plugin;
        this.maxAirTicks = plugin.getConfig().getInt("checks.movement.fly.max-airtime-ticks", 10);
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.movement.fly.enabled", true)) return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (player.isFlying() || player.getAllowFlight()) return;
        if (data.isInLiquid() || data.isInVehicle() || data.isHasVelocity()) return;
        if (player.getPotionEffect(PotionEffectType.LEVITATION) != null) return;
        if (player.getPotionEffect(PotionEffectType.SLOW_FALLING) != null) return;

        int airTicks = data.getAirTicks();
        double deltaY = data.getDeltaY();

        if (airTicks > maxAirTicks) {
            if (deltaY > 0.01 && airTicks > maxAirTicks + 2) {
                plugin.getViolationManager().flag(player, data, "Fly[A]", 3,
                        "airTicks=" + airTicks + " deltaY=" + String.format("%.4f", deltaY));
            }
            if (Math.abs(deltaY) < 0.005 && airTicks > maxAirTicks + 5) {
                plugin.getViolationManager().flag(player, data, "Fly[B]", 2,
                        "airTicks=" + airTicks + " hover");
            }
        }
    }
}
