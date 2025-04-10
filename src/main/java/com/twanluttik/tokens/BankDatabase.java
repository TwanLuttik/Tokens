package com.twanluttik.tokens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankDatabase {
    public static void initializeTables() throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS banks (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(32) NOT NULL, " +
                "owner_uuid VARCHAR(36) NOT NULL, " +
                "balance INT DEFAULT 0" +
                ")")) {
            statement.execute();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS bank_members (" +
                "bank_id INT NOT NULL, " +
                "member_uuid VARCHAR(36) NOT NULL, " +
                "PRIMARY KEY (bank_id, member_uuid), " +
                "FOREIGN KEY (bank_id) REFERENCES banks(id) ON DELETE CASCADE" +
                ")")) {
            statement.execute();
        }
    }

    public static int createBank(String name, String ownerUuid) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO banks (name, owner_uuid) VALUES (?, ?) RETURNING id")) {
            statement.setString(1, name);
            statement.setString(2, ownerUuid);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
    }

    public static void addMember(int bankId, String memberUuid) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO bank_members (bank_id, member_uuid) VALUES (?, ?)")) {
            statement.setInt(1, bankId);
            statement.setString(2, memberUuid);
            statement.execute();
        }
    }

    public static void removeMember(int bankId, String memberUuid) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM bank_members WHERE bank_id = ? AND member_uuid = ?")) {
            statement.setInt(1, bankId);
            statement.setString(2, memberUuid);
            statement.execute();
        }
    }

    public static List<Integer> getPlayerBanks(String uuid) throws SQLException {
        Connection connection = Database.getConnection();
        List<Integer> banks = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM banks WHERE owner_uuid = ? OR id IN " +
                "(SELECT bank_id FROM bank_members WHERE member_uuid = ?)")) {
            statement.setString(1, uuid);
            statement.setString(2, uuid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                banks.add(rs.getInt("id"));
            }
        }
        return banks;
    }

    public static int getBankBalance(int bankId) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT balance FROM banks WHERE id = ?")) {
            statement.setInt(1, bankId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("balance");
            }
            return -1;
        }
    }

    public static void addToBank(int bankId, int amount) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE banks SET balance = balance + ? WHERE id = ?")) {
            statement.setInt(1, amount);
            statement.setInt(2, bankId);
            statement.execute();
        }
    }

    public static void removeFromBank(int bankId, int amount) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE banks SET balance = balance - ? WHERE id = ? AND balance >= ?")) {
            statement.setInt(1, amount);
            statement.setInt(2, bankId);
            statement.setInt(3, amount);
            statement.execute();
        }
    }

    public static String getBankName(int bankId) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT name FROM banks WHERE id = ?")) {
            statement.setInt(1, bankId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
            return null;
        }
    }

    public static String getBankOwner(int bankId) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT owner_uuid FROM banks WHERE id = ?")) {
            statement.setInt(1, bankId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("owner_uuid");
            }
            return null;
        }
    }

    public static List<String> getBankMembers(int bankId) throws SQLException {
        Connection connection = Database.getConnection();
        List<String> members = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT member_uuid FROM bank_members WHERE bank_id = ?")) {
            statement.setInt(1, bankId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                members.add(rs.getString("member_uuid"));
            }
        }
        return members;
    }

    public static void deleteBank(int bankId) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM banks WHERE id = ?")) {
            statement.setInt(1, bankId);
            statement.execute();
        }
    }

    public static boolean isMember(String uuid, int bankId) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM bank_members WHERE bank_id = ? AND member_uuid = ?")) {
            statement.setInt(1, bankId);
            statement.setString(2, uuid);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
    }

    public static boolean isOwner(String uuid, int bankId) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM banks WHERE id = ? AND owner_uuid = ?")) {
            statement.setInt(1, bankId);
            statement.setString(2, uuid);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
    }
} 