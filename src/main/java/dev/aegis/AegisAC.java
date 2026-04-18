package dev.aegis;

import dev.aegis.ai.AIManager;
import dev.aegis.alert.AlertManager;
import dev.aegis.anticheat.CheckManager;
import dev.aegis.anticheat.ViolationManager;
import dev.aegis.commands.AegisCommand;
import dev.aegis.commands.AICommand;
import dev.aegis.core.PlayerDataManager;
import dev.aegis.core.TickHandler;
import dev.aegis.database.DatabaseManager;
import dev.aegis.lang.LanguageManager;
import dev.aegis.packet.PacketManager;
import dev.aegis.replay.ReplayManager;
import dev.aegis.trust.TrustManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AegisAC extends JavaPlugin {

    private static AegisAC instance;

    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private PlayerDataManager playerDataManager;
    private CheckManager checkManager;
    private ViolationManager violationManager;
    private AlertManager alertManager;
    private TrustManager trustManager;
    private ReplayManager replayManager;
    private AIManager aiManager;
    private PacketManager packetManager;
    private TickHandler tickHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.languageManager = new LanguageManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.initialize();
        this.playerDataManager = new PlayerDataManager(this);
        this.violationManager = new ViolationManager(this);
        this.alertManager = new AlertManager(this);
        this.trustManager = new TrustManager(this);
        this.replayManager = new ReplayManager(this);
        this.aiManager = new AIManager(this);
        this.checkManager = new CheckManager(this);

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            this.packetManager = new PacketManager(this);
            this.packetManager.register();
            getLogger().info("ProtocolLib found — packet analysis enabled.");
        } else {
            getLogger().warning("ProtocolLib not found — packet checks disabled.");
        }

        this.tickHandler = new TickHandler(this);
        this.tickHandler.start();

        getCommand("aegis").setExecutor(new AegisCommand(this));
        getCommand("aegis").setTabCompleter(new AegisCommand(this));
        getCommand("ai").setExecutor(new AICommand(this));
        getCommand("aichat").setExecutor(new AICommand(this));

        getLogger().info("AegisAC v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (tickHandler != null) tickHandler.stop();
        if (databaseManager != null) databaseManager.shutdown();
        getLogger().info("AegisAC disabled.");
    }

    public void reload() {
        reloadConfig();
        languageManager.reload();
        checkManager.reload();
    }

    public static AegisAC getInstance() { return instance; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public CheckManager getCheckManager() { return checkManager; }
    public ViolationManager getViolationManager() { return violationManager; }
    public AlertManager getAlertManager() { return alertManager; }
    public TrustManager getTrustManager() { return trustManager; }
    public ReplayManager getReplayManager() { return replayManager; }
    public AIManager getAiManager() { return aiManager; }
    public PacketManager getPacketManager() { return packetManager; }
}
