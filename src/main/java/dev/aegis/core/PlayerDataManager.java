package dev.aegis.core;

import dev.aegis.AegisAC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager implements Listener {

    private final AegisAC plugin;
    private final Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();

    public PlayerDataManager(AegisAC plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = new PlayerData(player);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            int trust = plugin.getDatabaseManager().loadTrustScore(player.getUniqueId());
            data.setTrustScore(trust);
        });
        dataMap.put(player.getUniqueId(), data);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData data = dataMap.remove(player.getUniqueId());
        if (data != null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseManager().savePlayerData(data));
        }
    }

    public PlayerData getData(Player player) { return dataMap.get(player.getUniqueId()); }
    public PlayerData getData(UUID uuid) { return dataMap.get(uuid); }
    public Collection<PlayerData> getAllData() { return dataMap.values(); }
}
