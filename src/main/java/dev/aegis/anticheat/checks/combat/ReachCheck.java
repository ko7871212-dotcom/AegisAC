package dev.aegis.anticheat.checks.combat;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ReachCheck {

    private final AegisAC plugin;
    private final double maxReach;

    public ReachCheck(AegisAC plugin) {
        this.plugin = plugin;
        this.maxReach = plugin.getConfig().getDouble("checks.combat.reach.max-reach", 3.2);
    }

    public void check(Player player, PlayerData data, EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("checks.combat.reach.enabled", true)) return;
        Entity target = event.getEntity();
        double dist = player.getLocation().distance(target.getLocation());
        double tolerance = 0.1 + (data.getPing() / 1000.0) * 0.5;
        double allowed = maxReach + tolerance;
        if (dist > allowed) {
            plugin.getViolationManager().flag(player, data, "Reach[A]", 3,
                    String.format("dist=%.2f max=%.2f", dist, allowed));
        }
    }
}
