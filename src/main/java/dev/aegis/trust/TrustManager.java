package dev.aegis.trust;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

public class TrustManager {

    private final AegisAC plugin;
    private final int increaseAmount;
    private final int decreaseAmount;

    public TrustManager(AegisAC plugin) {
        this.plugin = plugin;
        this.increaseAmount = plugin.getConfig().getInt("trust.increase-per-clean-minute", 1);
        this.decreaseAmount = plugin.getConfig().getInt("trust.decrease-per-flag", 5);
    }

    public void penalize(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;
        data.setTrustScore(Math.max(0, data.getTrustScore() - decreaseAmount));
    }

    public void handleCleanSession(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;
        if (data.getTotalViolations() == 0)
            data.setTrustScore(Math.min(100, data.getTrustScore() + increaseAmount));
    }

    public boolean isHighRisk(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        return data != null && data.getTrustScore() < 30;
    }
}
