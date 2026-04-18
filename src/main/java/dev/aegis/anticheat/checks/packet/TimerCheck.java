package dev.aegis.anticheat.checks.packet;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerCheck {

    private final AegisAC plugin;
    private final Map<UUID, Long> lastPacketTime = new HashMap<>();
    private final Map<UUID, Integer> packetCount = new HashMap<>();

    public TimerCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void onPacket(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.packet.timer.enabled", true)) return;
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = lastPacketTime.get(uuid);
        if (last != null) {
            int count = packetCount.getOrDefault(uuid, 0) + 1;
            packetCount.put(uuid, count);
            if (count > 25 && now - last < 1000) {
                plugin.getViolationManager().flag(player, data, "Timer[A]", 3,
                        "packets=" + count + " in ~1s");
                packetCount.put(uuid, 0);
            }
        } else {
            packetCount.put(uuid, 1);
        }
        if (last == null || now - last >= 1000) {
            lastPacketTime.put(uuid, now);
            packetCount.put(uuid, 0);
        }
    }
}
