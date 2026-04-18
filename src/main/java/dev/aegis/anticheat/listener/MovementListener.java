package dev.aegis.anticheat.listener;

import dev.aegis.AegisAC;
import dev.aegis.anticheat.checks.movement.*;
import dev.aegis.anticheat.checks.packet.TimerCheck;
import dev.aegis.core.PlayerData;
import dev.aegis.prediction.PredictionEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {

    private final AegisAC plugin;
    private final FlyCheck flyCheck;
    private final SpeedCheck speedCheck;
    private final NoFallCheck noFallCheck;
    private final JesusCheck jesusCheck;
    private final StepCheck stepCheck;
    private final AccelerationCheck accelerationCheck;
    private final TimerCheck timerCheck;
    private final PredictionEngine predictionEngine;

    public MovementListener(AegisAC plugin, FlyCheck flyCheck, SpeedCheck speedCheck,
                            NoFallCheck noFallCheck, JesusCheck jesusCheck,
                            StepCheck stepCheck, AccelerationCheck accelerationCheck,
                            TimerCheck timerCheck) {
        this.plugin = plugin;
        this.flyCheck = flyCheck;
        this.speedCheck = speedCheck;
        this.noFallCheck = noFallCheck;
        this.jesusCheck = jesusCheck;
        this.stepCheck = stepCheck;
        this.accelerationCheck = accelerationCheck;
        this.timerCheck = timerCheck;
        this.predictionEngine = new PredictionEngine(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("aegis.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;
        data.setPredictedLocation(predictionEngine.predict(player, data));
        data.updateLocation(event.getTo(), player.isOnGround());
        runChecks(player, data);
    }

    public void runChecks(Player player, PlayerData data) {
        if (player.hasPermission("aegis.bypass")) return;
        if (data.isFlying()) return;
        if (data.isInVehicle()) return;
        flyCheck.check(player, data);
        speedCheck.check(player, data);
        noFallCheck.check(player, data);
        jesusCheck.check(player, data);
        stepCheck.check(player, data);
        accelerationCheck.check(player, data);
    }
}
