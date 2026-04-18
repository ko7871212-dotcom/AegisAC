package dev.aegis.anticheat.checks.interaction;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastPlaceCheck {

    private final AegisAC plugin;
    private final Map<UUID, Long> lastPlace = new HashMap<>();

    public FastPlaceCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void check(Player player, PlayerData data, Block block) {
        if (!plugin.getConfig().getBoolean("checks.interaction.fastplace.enabled", true)) return;
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = lastPlace.get(uuid);
        if (last != null && now - last < 75) {
            plugin.getViolationManager().flag(player, data, "FastPlace[A]", 1,
                    "interval=" + (now - last) + "ms");
        }
        lastPlace.put(uuid, now);
    }
}
