package com.twanluttik.tokens;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

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
                e.printStackTrace();
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
            var resultSet = statement.executeQuery();
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
}
