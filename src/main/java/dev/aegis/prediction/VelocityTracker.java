package dev.aegis.prediction;

import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class VelocityTracker {

    public void onKnockback(Player player, PlayerData data, EntityDamageByEntityEvent event) {
        data.setHasVelocity(true);
        data.setLastVelocityTime(System.currentTimeMillis());
    }

    public boolean isVelocityActive(PlayerData data) {
        return data.isHasVelocity()
                && System.currentTimeMillis() - data.getLastVelocityTime() < 1000;
    }

    public void clearVelocity(PlayerData data) {
        data.setHasVelocity(false);
    }
}
