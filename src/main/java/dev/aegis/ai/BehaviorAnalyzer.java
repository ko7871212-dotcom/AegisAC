package dev.aegis.ai;

import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

public class BehaviorAnalyzer {

    private static final double ALPHA = 0.1;

    public void update(Player player, PlayerData data) {
        double avgCPS = data.getAvgCPS() == 0 ? data.getCps()
                : (1 - ALPHA) * data.getAvgCPS() + ALPHA * data.getCps();
        double avgSpeed = data.getAvgSpeed() == 0 ? data.getLastSpeed()
                : (1 - ALPHA) * data.getAvgSpeed() + ALPHA * data.getLastSpeed();
        data.setAvgCPS(avgCPS);
        data.setAvgSpeed(avgSpeed);
    }

    public double getAnomalyScore(PlayerData data) {
        if (data.getSessionTicks() < 200) return 0;
        double cpsDev = data.getAvgCPS() > 0
                ? Math.abs(data.getCps() - data.getAvgCPS()) / data.getAvgCPS() : 0;
        double speedDev = data.getAvgSpeed() > 0
                ? Math.abs(data.getLastSpeed() - data.getAvgSpeed()) / data.getAvgSpeed() : 0;
        return Math.min(1.0, (cpsDev + speedDev) / 2.0);
    }
}
