package dev.aegis.admin;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class PlayerPanelGUI implements Listener {

    private final AegisAC plugin;

    public PlayerPanelGUI(AegisAC plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player admin, Player target) {
        PlayerData data = plugin.getPlayerDataManager().getData(target);
        String title = plugin.getLanguageManager().get("gui-player-panel-title")
                .replace("{player}", target.getName());
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName("§b" + target.getName());
        List<String> lore = new ArrayList<>();
        if (data != null) {
            lore.add("§eVL: §f" + data.getTotalViolations());
            lore.add("§eCPS: §f" + String.format("%.1f", data.getCps()));
            lore.add("§eTrust: §f" + data.getTrustScore());
            lore.add("§ePing: §f" + data.getPing() + "ms");
        }
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);
        inv.setItem(4, skull);

        setItem(inv, 10, Material.BARRIER, plugin.getLanguageManager().get("gui-ban"),
                List.of("§7Click to ban"));
        setItem(inv, 12, Material.BLAZE_POWDER, plugin.getLanguageManager().get("gui-kick"),
                List.of("§7Click to kick"));
        setItem(inv, 14, Material.FEATHER, plugin.getLanguageManager().get("gui-fly-toggle"),
                List.of("§7Toggle fly"));
        setItem(inv, 16, Material.ENCHANTED_BOOK, plugin.getLanguageManager().get("gui-ai-analyze"),
                List.of("§7Run AI analysis"));
        setItem(inv, 22, Material.CLOCK, "§aPlay Replay", List.of("§7View movement replay"));
        setItem(inv, 18, Material.ARROW, plugin.getLanguageManager().get("gui-back"),
                List.of("§7Back to list"));

        admin.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;
        if (!admin.hasPermission("aegis.admin")) return;
        String title = event.getView().getTitle();
        if (!title.startsWith("§8» §b") || !title.endsWith(" §8«")) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        String targetName = title.replace("§8» §b", "").replace(" §8«", "");
        Player target = Bukkit.getPlayer(targetName);
        switch (event.getSlot()) {
            case 10 -> {
                if (target != null) { admin.closeInventory(); target.kickPlayer("§cBanned by admin."); }
            }
            case 12 -> {
                if (target != null) { admin.closeInventory(); target.kickPlayer("§eKicked by admin."); }
            }
            case 14 -> {
                if (target != null) {
                    boolean fly = !target.getAllowFlight();
                    target.setAllowFlight(fly);
                    admin.sendMessage("§aFly for §f" + targetName + " §aset to: §f" + fly);
                }
            }
            case 16 -> {
                if (target != null && plugin.getAiManager().isEnabled()) {
                    admin.closeInventory();
                    admin.sendMessage(plugin.getLanguageManager().get("ai-analyzing")
                            .replace("{player}", targetName));
                    plugin.getAiManager().analyzePlayer(admin, target, result ->
                        admin.sendMessage(plugin.getLanguageManager().get("prefix") + "§b[AI] §f" + result));
                }
            }
            case 22 -> {
                if (target != null) { admin.closeInventory(); plugin.getReplayManager().playReplay(admin, target.getUniqueId()); }
            }
            case 18 -> new AdminGUI(plugin).openPlayerList(admin);
        }
    }

    private void setItem(Inventory inv, int slot, Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
}
