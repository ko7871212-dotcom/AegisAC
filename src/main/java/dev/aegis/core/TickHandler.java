package dev.aegis.core;

import dev.aegis.AegisAC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TickHandler {

    private final AegisAC plugin;
    private BukkitTask task;
    private int tick;

    public TickHandler(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::onTick, 1L, 1L);
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    private void onTick() {
        tick++;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("aegis.bypass")) continue;
            PlayerData data = plugin.getPlayerDataManager().getData(player);
            if (data == null) continue;

            data.setPing(player.getPing());
            data.setInVehicle(player.isInsideVehicle());
            data.setInLiquid(player.isInWater() || player.isInLava());
            data.setFlying(player.isFlying() || player.getAllowFlight());
            data.setOnIce(isOnIce(player));
            data.setOnSlime(isOnSlime(player));
            data.incrementSessionTicks();

            if (tick % 40 == 0) data.decayAll();
            if (tick % 1200 == 0) plugin.getTrustManager().handleCleanSession(player);

            plugin.getCheckManager().getMovementListener().runChecks(player, data);
        }
    }

    private boolean isOnIce(Player p) {
        org.bukkit.block.Block b = p.getLocation().subtract(0, 0.1, 0).getBlock();
        Material m = b.getType();
        return m == Material.ICE || m == Material.PACKED_ICE
                || m == Material.BLUE_ICE || m == Material.FROSTED_ICE;
    }

    private boolean isOnSlime(Player p) {
        return p.getLocation().subtract(0, 0.1, 0).getBlock().getType() == Material.SLIME_BLOCK;
    }
}
