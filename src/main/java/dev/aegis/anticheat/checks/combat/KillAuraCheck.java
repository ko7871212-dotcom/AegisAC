package dev.aegis.anticheat.checks.combat;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class KillAuraCheck {

    private final AegisAC plugin;

    public KillAuraCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void check(Player player, PlayerData data, EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("checks.combat.killaura.enabled", true)) return;
        Entity target = event.getEntity();
        if (!(target instanceof LivingEntity)) return;

        float yaw = player.getLocation().getYaw();
        float lastYaw = data.getLastYaw();
        float pitch = player.getLocation().getPitch();
        float lastPitch = data.getLastPitch();

        double yawDiff = Math.abs(yaw - lastYaw);
        double pitchDiff = Math.abs(pitch - lastPitch);

        List<Entity> nearby = player.getNearbyEntities(5, 5, 5);
        long livingNearby = nearby.stream()
                .filter(e -> e instanceof LivingEntity && !e.equals(player)).count();

        boolean snapRotation = yawDiff > 30 && pitchDiff > 15;
        boolean facingTarget = isLookingAt(player, (LivingEntity) target);

        if (!facingTarget && data.getCps() > 5) {
            plugin.getViolationManager().flag(player, data, "KillAura[A]", 3,
                    "notFacing cps=" + (int) data.getCps());
        }
        if (snapRotation && livingNearby >= 2) {
            plugin.getViolationManager().flag(player, data, "KillAura[B]", 2,
                    "snapRotation yaw=" + String.format("%.1f", yawDiff));
        }
        if (player.isSprinting() && snapRotation) {
            plugin.getViolationManager().flag(player, data, "KillAura[C]", 2, "sprint+snap");
        }
    }

    private boolean isLookingAt(Player player, LivingEntity entity) {
        var eyeLoc = player.getEyeLocation();
        var direction = eyeLoc.getDirection().normalize();
        var toEntity = entity.getLocation().toVector().subtract(eyeLoc.toVector()).normalize();
        return direction.dot(toEntity) > 0.85;
    }
}
