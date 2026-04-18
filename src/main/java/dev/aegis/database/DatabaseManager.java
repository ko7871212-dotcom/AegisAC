package dev.aegis.database;

import dev.aegis.AegisAC;
import dev.aegis.core.PlayerData;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final AegisAC plugin;
    private Connection connection;

    public DatabaseManager(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            File dbFile = new File(plugin.getDataFolder(),
                    plugin.getConfig().getString("database.file", "aegis_data.db"));
            if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();
            Class.forName("dev.aegis.libs.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            connection.createStatement().execute("PRAGMA journal_mode=WAL;");
            createTables();
            plugin.getLogger().info("Database initialized.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database.", e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT, "
                    + "trust_score INTEGER DEFAULT 50, total_violations INTEGER DEFAULT 0, "
                    + "first_seen LONG, last_seen LONG);");
            stmt.execute("CREATE TABLE IF NOT EXISTS violation_log (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "uuid TEXT, check_name TEXT, vl INTEGER, timestamp LONG);");
        }
    }

    public int loadTrustScore(UUID uuid) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT trust_score FROM players WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("trust_score");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load trust score.", e);
        }
        return 50;
    }

    public void savePlayerData(PlayerData data) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT OR REPLACE INTO players (uuid, name, trust_score, total_violations, "
                  + "first_seen, last_seen) VALUES (?, ?, ?, ?, "
                  + "COALESCE((SELECT first_seen FROM players WHERE uuid=?), ?), ?)");
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setInt(3, data.getTrustScore());
            ps.setInt(4, data.getTotalViolations());
            ps.setString(5, data.getUuid().toString());
            ps.setLong(6, data.getJoinTime());
            ps.setLong(7, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save player data.", e);
        }
    }

    public void logViolation(UUID uuid, String checkName, int vl) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO violation_log (uuid, check_name, vl, timestamp) VALUES (?, ?, ?, ?)");
            ps.setString(1, uuid.toString());
            ps.setString(2, checkName);
            ps.setInt(3, vl);
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to log violation.", e);
        }
    }

    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Error closing database.", e);
        }
    }
}
