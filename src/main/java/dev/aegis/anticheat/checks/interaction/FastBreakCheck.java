package dev.aegis.anticheat.checks.interaction;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastBreakCheck {

    private final AegisAC plugin;
    private final Map<UUID, Long> lastBreak = new HashMap<>();
    private final Map<UUID, Integer> breakCount = new HashMap<>();

    public FastBreakCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void check(Player player, PlayerData data, Block block) {
        if (!plugin.getConfig().getBoolean("checks.interaction.fastbreak.enabled", true)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = lastBreak.get(uuid);

        if (last != null && now - last < 50) {
            int count = breakCount.getOrDefault(uuid, 0) + 1;
            breakCount.put(uuid, count);
            if (count >= 3) {
                plugin.getViolationManager().flag(player, data, "FastBreak[A]", 2,
                        "interval=" + (now - last) + "ms");
                breakCount.put(uuid, 0);
            }
        } else {
            breakCount.put(uuid, 0);
        }
        lastBreak.put(uuid, now);
    }
}
