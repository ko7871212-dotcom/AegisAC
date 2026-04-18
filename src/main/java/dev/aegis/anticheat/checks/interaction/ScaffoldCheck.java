package dev.aegis.anticheat.checks.interaction;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class ScaffoldCheck {

    private final AegisAC plugin;
    private final Map<UUID, List<Long>> placeHistory = new HashMap<>();

    public ScaffoldCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void check(Player player, PlayerData data, Block block) {
        if (!plugin.getConfig().getBoolean("checks.interaction.scaffold.enabled", true)) return;
        UUID uuid = player.getUniqueId();
        boolean belowPlayer = block.getY() < player.getLocation().getBlockY();
        boolean playerMoving = data.getLastSpeed() > 0.15;
        List<Long> history = placeHistory.computeIfAbsent(uuid, k -> new ArrayList<>());
        long now = System.currentTimeMillis();
        history.add(now);
        history.removeIf(t -> now - t > 2000);
        if (belowPlayer && playerMoving && history.size() >= 5) {
            float pitch = player.getLocation().getPitch();
            if (pitch < -60) {
                plugin.getViolationManager().flag(player, data, "Scaffold[A]", 3,
                        "pitch=" + String.format("%.1f", pitch) + " blocks=" + history.size());
            }
        }
    }
}
