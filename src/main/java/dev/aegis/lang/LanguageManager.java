package dev.aegis.lang;

import dev.aegis.AegisAC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {

    private final AegisAC plugin;
    private FileConfiguration langConfig;

    public LanguageManager(AegisAC plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        String lang = plugin.getConfig().getString("language", "en_US");
        File langFile = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + lang + ".yml", false);
            if (!langFile.exists()) {
                plugin.saveResource("lang/en_US.yml", false);
                langFile = new File(plugin.getDataFolder(), "lang/en_US.yml");
            }
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        InputStream defStream = plugin.getResource("lang/" + lang + ".yml");
        if (defStream == null) defStream = plugin.getResource("lang/en_US.yml");
        if (defStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defStream, StandardCharsets.UTF_8));
            langConfig.setDefaults(defaults);
        }
    }

    public void reload() { load(); }

    public String get(String key) {
        return langConfig.getString(key, "&c[Missing: " + key + "]").replace("&", "§");
    }

    public String get(String key, String... replacements) {
        String val = get(key);
        for (int i = 0; i < replacements.length - 1; i += 2)
            val = val.replace(replacements[i], replacements[i + 1]);
        return val;
    }
}
