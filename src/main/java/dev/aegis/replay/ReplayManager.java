package dev.aegis.replay;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ReplayManager {

    private final AegisAC plugin;
    private final Map<UUID, ReplayData> savedReplays = new HashMap<>();

    public ReplayManager(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void saveReplay(Player target) {
        PlayerData data = plugin.getPlayerDataManager().getData(target);
        if (data == null) return;
        savedReplays.put(target.getUniqueId(),
                new ReplayData(target.getName(), new LinkedList<>(data.getPositionBuffer())));
    }

    public void playReplay(Player admin, UUID targetUUID) {
        ReplayData replay = savedReplays.get(targetUUID);
        if (replay == null) {
            admin.sendMessage("§cNo replay found for this player.");
            return;
        }
        LinkedList<Location> positions = new LinkedList<>(replay.getPositions());
        admin.sendMessage("§bPlaying replay for §f" + replay.getPlayerName()
                + " §b(" + positions.size() + " frames)");
        new BukkitRunnable() {
            int index = 0;
            @Override
            public void run() {
                if (index >= positions.size() || !admin.isOnline()) {
                    cancel();
                    if (admin.isOnline()) admin.sendMessage("§aReplay finished.");
                    return;
                }
                admin.spawnParticle(org.bukkit.Particle.FLAME, positions.get(index++), 1, 0, 0, 0, 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public Map<UUID, ReplayData> getSavedReplays() { return savedReplays; }
}
