package com.twanluttik.tokens.core;

import com.twanluttik.tokens.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Actions {

    public boolean playerExists(UUID playerUUID) throws SQLException {
        Connection connection = Database.getConnection();
        String query = "SELECT uuid FROM players WHERE uuid = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void createPlayer(UUID playerUUID) throws SQLException {
        Connection connection = Database.getConnection();
        String query = "INSERT INTO players (uuid, tokens) VALUES (?, 0)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID.toString());
            statement.executeUpdate();
        }
    }
}
