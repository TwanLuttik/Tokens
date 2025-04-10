package com.twanluttik.tokens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Public API for interacting with the Tokens system.
 * This class provides methods for other plugins to interact with the token banks.
 */
public class TokensAPI {
    private static TokensAPI instance;
    private final ConfigManager configManager;

    private TokensAPI() {
        this.configManager = Tokens.getInstance().getConfigManager();
    }

    /**
     * Gets the singleton instance of the TokensAPI.
     * @return The TokensAPI instance
     */
    public static TokensAPI getInstance() {
        if (instance == null) {
            instance = new TokensAPI();
        }
        return instance;
    }

    /**
     * Creates a new token bank.
     * @param name The name of the bank
     * @param ownerUuid The UUID of the bank owner
     * @return The ID of the created bank, or -1 if creation failed
     * @throws SQLException If there's an error accessing the database
     */
    public int createBank(String name, UUID ownerUuid) throws SQLException {
        return BankDatabase.createBank(name, ownerUuid.toString());
    }

    /**
     * Gets the balance of a bank.
     * @param bankId The ID of the bank
     * @return The bank's balance, or -1 if the bank doesn't exist
     * @throws SQLException If there's an error accessing the database
     */
    public int getBankBalance(int bankId) throws SQLException {
        return BankDatabase.getBankBalance(bankId);
    }

    /**
     * Adds tokens to a bank.
     * @param bankId The ID of the bank
     * @param amount The amount of tokens to add
     * @throws SQLException If there's an error accessing the database
     */
    public void addToBank(int bankId, int amount) throws SQLException {
        BankDatabase.addToBank(bankId, amount);
    }

    /**
     * Removes tokens from a bank.
     * @param bankId The ID of the bank
     * @param amount The amount of tokens to remove
     * @throws SQLException If there's an error accessing the database
     */
    public void removeFromBank(int bankId, int amount) throws SQLException {
        BankDatabase.removeFromBank(bankId, amount);
    }

    /**
     * Gets all banks that a player has access to.
     * @param playerUuid The UUID of the player
     * @return A list of bank IDs that the player has access to
     * @throws SQLException If there's an error accessing the database
     */
    public List<Integer> getPlayerBanks(UUID playerUuid) throws SQLException {
        return BankDatabase.getPlayerBanks(playerUuid.toString());
    }

    /**
     * Gets the name of a bank.
     * @param bankId The ID of the bank
     * @return The name of the bank, or null if the bank doesn't exist
     * @throws SQLException If there's an error accessing the database
     */
    public String getBankName(int bankId) throws SQLException {
        return BankDatabase.getBankName(bankId);
    }

    /**
     * Gets the owner of a bank.
     * @param bankId The ID of the bank
     * @return The UUID of the bank owner, or null if the bank doesn't exist
     * @throws SQLException If there's an error accessing the database
     */
    public UUID getBankOwner(int bankId) throws SQLException {
        String ownerUuid = BankDatabase.getBankOwner(bankId);
        return ownerUuid != null ? UUID.fromString(ownerUuid) : null;
    }

    /**
     * Gets all members of a bank.
     * @param bankId The ID of the bank
     * @return A list of UUIDs of bank members
     * @throws SQLException If there's an error accessing the database
     */
    public List<UUID> getBankMembers(int bankId) throws SQLException {
        return BankDatabase.getBankMembers(bankId).stream()
                .map(UUID::fromString)
                .toList();
    }

    /**
     * Adds a member to a bank.
     * @param bankId The ID of the bank
     * @param memberUuid The UUID of the member to add
     * @throws SQLException If there's an error accessing the database
     */
    public void addMember(int bankId, UUID memberUuid) throws SQLException {
        BankDatabase.addMember(bankId, memberUuid.toString());
    }

    /**
     * Removes a member from a bank.
     * @param bankId The ID of the bank
     * @param memberUuid The UUID of the member to remove
     * @throws SQLException If there's an error accessing the database
     */
    public void removeMember(int bankId, UUID memberUuid) throws SQLException {
        BankDatabase.removeMember(bankId, memberUuid.toString());
    }

    /**
     * Deletes a bank.
     * @param bankId The ID of the bank to delete
     * @throws SQLException If there's an error accessing the database
     */
    public void deleteBank(int bankId) throws SQLException {
        BankDatabase.deleteBank(bankId);
    }

    /**
     * Checks if a player exists in the database.
     * @param playerUuid The UUID of the player to check
     * @return true if the player exists, false otherwise
     * @throws SQLException If there's an error accessing the database
     */
    public boolean playerExists(UUID playerUuid) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM players WHERE uuid = ?")) {
            statement.setString(1, playerUuid.toString());
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
    }

    /**
     * Gets the token balance of a player.
     * @param playerUuid The UUID of the player
     * @return The player's token balance, or 0 if the player doesn't exist
     * @throws SQLException If there's an error accessing the database
     */
    public int getPlayerTokens(UUID playerUuid) throws SQLException {
        return Database.getTokens(playerUuid.toString());
    }

    /**
     * Sets the token balance of a player.
     * @param playerUuid The UUID of the player
     * @param amount The new token balance
     * @throws SQLException If there's an error accessing the database
     */
    public void setPlayerTokens(UUID playerUuid, int amount) throws SQLException {
        Database.setTokens(playerUuid.toString(), amount);
    }

    /**
     * Adds tokens to a player's balance.
     * @param playerUuid The UUID of the player
     * @param amount The amount of tokens to add
     * @throws SQLException If there's an error accessing the database
     */
    public void addPlayerTokens(UUID playerUuid, int amount) throws SQLException {
        Database.addTokens(playerUuid.toString(), amount);
    }

    /**
     * Removes tokens from a player's balance.
     * @param playerUuid The UUID of the player
     * @param amount The amount of tokens to remove
     * @return true if the tokens were successfully removed, false if the player doesn't have enough tokens
     * @throws SQLException If there's an error accessing the database
     */
    public boolean removePlayerTokens(UUID playerUuid, int amount) throws SQLException {
        int currentBalance = getPlayerTokens(playerUuid);
        if (currentBalance < amount) {
            return false;
        }
        Database.removeTokens(playerUuid.toString(), amount);
        return true;
    }

    /**
     * Creates a new player in the database with the specified initial token balance.
     * @param playerUuid The UUID of the player
     * @param initialBalance The initial token balance (defaults to 0)
     * @throws SQLException If there's an error accessing the database
     */
    public void createPlayer(UUID playerUuid, int initialBalance) throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO players (uuid, tokens) VALUES (?, ?)")) {
            statement.setString(1, playerUuid.toString());
            statement.setInt(2, initialBalance);
            statement.execute();
        }
    }

    /**
     * Creates a new player in the database with 0 tokens.
     * @param playerUuid The UUID of the player
     * @throws SQLException If there's an error accessing the database
     */
    public void createPlayer(UUID playerUuid) throws SQLException {
        createPlayer(playerUuid, configManager.getInitialTokens());
    }
} 