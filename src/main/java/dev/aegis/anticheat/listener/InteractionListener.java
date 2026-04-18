package dev.aegis.anticheat.listener;

import dev.aegis.AegisAC;
import dev.aegis.anticheat.checks.interaction.*;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class InteractionListener implements Listener {

    private final AegisAC plugin;
    private final FastBreakCheck fastBreakCheck;
    private final FastPlaceCheck fastPlaceCheck;
    private final ScaffoldCheck scaffoldCheck;

    public InteractionListener(AegisAC plugin, FastBreakCheck fastBreakCheck,
                               FastPlaceCheck fastPlaceCheck, ScaffoldCheck scaffoldCheck) {
        this.plugin = plugin;
        this.fastBreakCheck = fastBreakCheck;
        this.fastPlaceCheck = fastPlaceCheck;
        this.scaffoldCheck = scaffoldCheck;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("aegis.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;
        fastBreakCheck.check(player, data, event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("aegis.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;
        fastPlaceCheck.check(player, data, event.getBlock());
        scaffoldCheck.check(player, data, event.getBlock());
    }
}
