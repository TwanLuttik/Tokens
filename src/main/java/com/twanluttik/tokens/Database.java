package com.twanluttik.tokens;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class Database {
    private static Connection connection;
    private static ConfigManager configManager;
    
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL JDBC driver", e);
        }
    }

    public static void initialize(ConfigManager config) {
        configManager = config;
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            props.setProperty("user", configManager.getDatabaseUsername());
            props.setProperty("password", configManager.getDatabasePassword());
            props.setProperty("ssl", String.valueOf(configManager.isDatabaseSSL()));

            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                    configManager.getDatabaseHost(),
                    configManager.getDatabasePort(),
                    configManager.getDatabaseName());

            connection = DriverManager.getConnection(jdbcUrl, props);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                if (Tokens.getInstance() != null) {
                    Tokens.getInstance().getLogger().warning("Failed to close database connection: " + e.getMessage());
                } else {
                    Bukkit.getLogger().warning("[Tokens] Failed to close database connection: " + e.getMessage());
                }
            }
        }
    }

    public static void initializeTables() throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS players (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "tokens INT DEFAULT 0" +
                        ")")) {
            statement.execute();
        }
    }

    public static int getTokens(String uuid) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT tokens FROM players WHERE uuid = ?")) {
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("tokens");
            }
            return 0;
        }
    }

    public static void setTokens(String uuid, int amount) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO players (uuid, tokens) VALUES (?, ?) " +
                "ON CONFLICT (uuid) DO UPDATE SET tokens = ?")) {
            statement.setString(1, uuid);
            statement.setInt(2, amount);
            statement.setInt(3, amount);
            statement.execute();
        }
    }

    public static void addTokens(String uuid, int amount) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO players (uuid, tokens) VALUES (?, ?) " +
                "ON CONFLICT (uuid) DO UPDATE SET tokens = players.tokens + ?")) {
            statement.setString(1, uuid);
            statement.setInt(2, amount);
            statement.setInt(3, amount);
            statement.execute();
        }
    }

    public static void removeTokens(String uuid, int amount) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE players SET tokens = tokens - ? WHERE uuid = ? AND tokens >= ?")) {
            statement.setInt(1, amount);
            statement.setString(2, uuid);
            statement.setInt(3, amount);
            statement.execute();
        }
    }

    public static List<UUID> getTopPlayers(int limit) throws SQLException {
        Connection connection = getConnection();
        List<UUID> topPlayers = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT uuid FROM players ORDER BY tokens DESC LIMIT ?")) {
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                topPlayers.add(UUID.fromString(resultSet.getString("uuid")));
            }
        }
        
        return topPlayers;
    }

    public static int getPlayerTokens(UUID uuid) throws SQLException {
        return getTokens(uuid.toString());
    }

    /**
     * Returns the player's rank in the token leaderboard (1 = richest).
     * Players with the same balance will have the same rank.
     */
    public static int getPlayerRank(String uuid) throws SQLException {
        Connection connection = getConnection();
        // Number of players with strictly more tokens + 1
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 + COUNT(*) FROM players p " +
                "WHERE p.tokens > (SELECT tokens FROM players WHERE uuid = ?)")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1; // fallback if player has the highest or only balance
    }

    /**
     * Returns the player's rank (convenience overload).
     */
    public static int getPlayerRank(UUID uuid) throws SQLException {
        return getPlayerRank(uuid.toString());
    }
}
