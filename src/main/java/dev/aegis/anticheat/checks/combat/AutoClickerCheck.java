package dev.aegis.anticheat.checks.combat;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

public class AutoClickerCheck {

    private final AegisAC plugin;
    private final int maxCPS;

    public AutoClickerCheck(AegisAC plugin) {
        this.plugin = plugin;
        this.maxCPS = plugin.getConfig().getInt("checks.combat.autoclicker.max-cps", 20);
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.combat.autoclicker.enabled", true)) return;
        double cps = data.getCps();
        if (cps > maxCPS) {
            plugin.getViolationManager().flag(player, data, "AutoClicker[A]", 3,
                    "cps=" + (int) cps + " max=" + maxCPS);
        }
    }
}
