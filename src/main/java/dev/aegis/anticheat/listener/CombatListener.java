package dev.aegis.anticheat.listener;

import dev.aegis.AegisAC;
import dev.aegis.anticheat.checks.combat.*;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CombatListener implements Listener {

    private final AegisAC plugin;
    private final KillAuraCheck killAuraCheck;
    private final ReachCheck reachCheck;
    private final AutoClickerCheck autoClickerCheck;
    private final AimAssistCheck aimAssistCheck;

    public CombatListener(AegisAC plugin, KillAuraCheck killAuraCheck,
                          ReachCheck reachCheck, AutoClickerCheck autoClickerCheck,
                          AimAssistCheck aimAssistCheck) {
        this.plugin = plugin;
        this.killAuraCheck = killAuraCheck;
        this.reachCheck = reachCheck;
        this.autoClickerCheck = autoClickerCheck;
        this.aimAssistCheck = aimAssistCheck;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (player.hasPermission("aegis.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;
        data.recordClick();
        data.setLastAttackTime(System.currentTimeMillis());
        killAuraCheck.check(player, data, event);
        reachCheck.check(player, data, event);
        autoClickerCheck.check(player, data);
        aimAssistCheck.check(player, data);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("aegis.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;
        if (event.getAction().name().contains("LEFT")) data.recordClick();
    }
}
