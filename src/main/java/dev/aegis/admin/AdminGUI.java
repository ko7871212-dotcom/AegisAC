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

public class AdminGUI implements Listener {

    private final AegisAC plugin;

    public AdminGUI(AegisAC plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openMain(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 27,
                plugin.getLanguageManager().get("gui-main-title"));
        setItem(inv, 11, Material.PLAYER_HEAD,
                plugin.getLanguageManager().get("gui-players-title"),
                List.of("§7View all online players"));
        setItem(inv, 13, Material.REDSTONE,
                plugin.getLanguageManager().get("gui-suspicious-title"),
                List.of("§7Players with high violations"));
        setItem(inv, 15, Material.BOOK, "§ePlugin Info",
                List.of("§7Version: §f" + plugin.getDescription().getVersion()));
        admin.openInventory(inv);
    }

    public void openPlayerList(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 54,
                plugin.getLanguageManager().get("gui-players-title"));
        int slot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (slot >= 45) break;
            PlayerData data = plugin.getPlayerDataManager().getData(p);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName("§b" + p.getName());
            List<String> lore = new ArrayList<>();
            if (data != null) {
                lore.add("§eVL: §f" + data.getTotalViolations());
                lore.add("§eTrust: §f" + data.getTrustScore());
                lore.add("§eCPS: §f" + String.format("%.1f", data.getCps()));
                lore.add("§ePing: §f" + data.getPing() + "ms");
            }
            lore.add(""); lore.add("§7Click to open panel");
            meta.setLore(lore);
            skull.setItemMeta(meta);
            inv.setItem(slot++, skull);
        }
        admin.openInventory(inv);
    }

    public void openSuspicious(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 54,
                plugin.getLanguageManager().get("gui-suspicious-title"));
        int slot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().getData(p);
            if (data == null || data.getTotalViolations() < 10) continue;
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName("§c" + p.getName());
            meta.setLore(Arrays.asList("§eVL: §f" + data.getTotalViolations(),
                    "§eTrust: §f" + data.getTrustScore(), "", "§7Click to open panel"));
            skull.setItemMeta(meta);
            inv.setItem(slot++, skull);
            if (slot >= 45) break;
        }
        admin.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;
        String title = event.getView().getTitle();
        String mainTitle = plugin.getLanguageManager().get("gui-main-title");
        String playersTitle = plugin.getLanguageManager().get("gui-players-title");
        String suspiciousTitle = plugin.getLanguageManager().get("gui-suspicious-title");
        if (!title.equals(mainTitle) && !title.equals(playersTitle) && !title.equals(suspiciousTitle)) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (title.equals(mainTitle)) {
            if (event.getSlot() == 11) openPlayerList(admin);
            else if (event.getSlot() == 13) openSuspicious(admin);
        } else {
            if (clicked.getType() == Material.PLAYER_HEAD) {
                String name = clicked.getItemMeta().getDisplayName()
                        .replace("§b", "").replace("§c", "");
                Player target = Bukkit.getPlayer(name);
                if (target != null) new PlayerPanelGUI(plugin).open(admin, target);
            }
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
