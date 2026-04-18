package dev.aegis.anticheat.checks.movement;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class JesusCheck {

    private final AegisAC plugin;

    public JesusCheck(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void check(Player player, PlayerData data) {
        if (!plugin.getConfig().getBoolean("checks.movement.jesus.enabled", true)) return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (data.isInLiquid()) return;
        if (player.getPotionEffect(PotionEffectType.SLOW_FALLING) != null) return;
        if (player.getPotionEffect(PotionEffectType.LEVITATION) != null) return;

        org.bukkit.block.Block below = player.getLocation().subtract(0, 0.1, 0).getBlock();
        boolean onWater = below.getType() == Material.WATER || below.getType() == Material.LAVA;

        if (onWater && data.isOnGround() && !player.isSwimming()) {
            plugin.getViolationManager().flag(player, data, "Jesus[A]", 2, "walking on liquid");
        }
    }
}
