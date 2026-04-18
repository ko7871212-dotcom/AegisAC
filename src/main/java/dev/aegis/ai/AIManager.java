package dev.aegis.ai;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AIManager {

    private final AegisAC plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public AIManager(AegisAC plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("ai.enabled", true);
    }

    public boolean isOnCooldown(Player player) {
        long cooldownMs = plugin.getConfig().getLong("ai.cooldown-seconds", 10) * 1000L;
        Long last = cooldowns.get(player.getUniqueId());
        return last != null && System.currentTimeMillis() - last < cooldownMs;
    }

    public long getRemainingCooldown(Player player) {
        long cooldownMs = plugin.getConfig().getLong("ai.cooldown-seconds", 10) * 1000L;
        Long last = cooldowns.get(player.getUniqueId());
        if (last == null) return 0;
        return Math.max(0, (cooldownMs - (System.currentTimeMillis() - last)) / 1000);
    }

    public void analyzePlayer(Player requester, Player target, Callback callback) {
        cooldowns.put(requester.getUniqueId(), System.currentTimeMillis());
        PlayerData data = plugin.getPlayerDataManager().getData(target);
        if (data == null) { callback.onResult("ERROR: Player data not found."); return; }
        String prompt = buildAnalysisPrompt(target, data);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String result = callAPI(prompt);
                plugin.getServer().getScheduler().runTask(plugin, () -> callback.onResult(result));
            } catch (Exception e) {
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        callback.onResult("ERROR: " + e.getMessage()));
            }
        });
    }

    public void chat(Player requester, String message, Callback callback) {
        cooldowns.put(requester.getUniqueId(), System.currentTimeMillis());
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String result = callAPI(message);
                plugin.getServer().getScheduler().runTask(plugin, () -> callback.onResult(result));
            } catch (Exception e) {
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        callback.onResult("ERROR: " + e.getMessage()));
            }
        });
    }

    private String buildAnalysisPrompt(Player target, PlayerData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an AntiCheat AI for a Minecraft server. ")
          .append("Analyze this player and determine if they are cheating.\n\n")
          .append("Player: ").append(target.getName()).append("\n")
          .append("Ping: ").append(data.getPing()).append("ms\n")
          .append("CPS: ").append(String.format("%.1f", data.getCps())).append("\n")
          .append("Trust Score: ").append(data.getTrustScore()).append("/100\n")
          .append("Total Violations: ").append(data.getTotalViolations()).append("\n")
          .append("Check Violations:\n");
        data.getViolations().forEach((check, vl) -> {
            if (vl > 0) sb.append("  - ").append(check).append(": ").append(vl).append("\n");
        });
        sb.append("\nRespond with: RISK LEVEL (LOW/MEDIUM/HIGH) and a brief suggestion (max 2 sentences). ");
        sb.append("Format: 'RISK: <level> | <suggestion>'");
        return sb.toString();
    }

    private String callAPI(String prompt) throws Exception {
        String apiUrl = plugin.getConfig().getString("ai.api-url",
                "https://api.openai.com/v1/chat/completions");
        String apiKey = plugin.getConfig().getString("ai.api-key", "");
        String model = plugin.getConfig().getString("ai.model", "gpt-4o-mini");
        int maxTokens = plugin.getConfig().getInt("ai.max-tokens", 300);

        if (apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY_HERE"))
            return "AI API key not configured.";

        String requestBody = "{\"model\":\"" + model + "\",\"max_tokens\":" + maxTokens
                + ",\"messages\":[{\"role\":\"user\",\"content\":" + escapeJson(prompt) + "}]}";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(15000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) response.append(line);
        }
        return parseResponse(response.toString());
    }

    private String parseResponse(String json) {
        try {
            int contentIdx = json.indexOf("\"content\":");
            if (contentIdx == -1) return "AI returned no content.";
            int start = json.indexOf("\"", contentIdx + 10) + 1;
            int end = json.indexOf("\"", start);
            StringBuilder result = new StringBuilder();
            for (int i = start; i < end; i++) {
                if (json.charAt(i) == '\\' && i + 1 < end) {
                    char next = json.charAt(i + 1);
                    if (next == 'n') { result.append('\n'); i++; }
                    else if (next == '"') { result.append('"'); i++; }
                    else result.append(json.charAt(i));
                } else result.append(json.charAt(i));
            }
            return result.toString();
        } catch (Exception e) {
            return "Failed to parse AI response.";
        }
    }

    private String escapeJson(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }

    public interface Callback {
        void onResult(String result);
    }
}
