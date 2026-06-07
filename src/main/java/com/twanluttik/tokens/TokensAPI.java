package com.twanluttik.tokens;

import com.twanluttik.tokens.api.Bank;
import com.twanluttik.tokens.api.TokensException;
import com.twanluttik.tokens.api.event.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Public API for other plugins to integrate with the Tokens economy plugin.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * TokensAPI api = TokensAPI.getAPI();
 * if (api == null) {
 *     // Tokens plugin not loaded
 *     return;
 * }
 *
 * int balance = api.getPlayerTokens(player.getUniqueId());
 * api.addPlayerTokens(player.getUniqueId(), 100);
 * }</pre>
 *
 * <p><b>Important:</b> Most methods perform database operations and are blocking.
 * Avoid calling them on the main server thread for large operations. Use the async variants when available.
 */
public class TokensAPI {

    private static TokensAPI instance;

    private final Tokens plugin;
    private final ConfigManager configManager;

    /**
     * Internal constructor. Use {@link #getAPI()} to obtain the API from other plugins.
     */
    public TokensAPI(Tokens plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        instance = this;
    }

    // ========================== ACCESS =====================================

    /**
     * Gets the TokensAPI instance.
     * <p>
     * This is the recommended way for other plugins to obtain the API.
     *
     * @return The API instance, or null if the Tokens plugin is not enabled or not yet loaded.
     */
    public static TokensAPI getAPI() {
        if (instance != null) {
            return instance;
        }
        // Try Bukkit service manager as fallback (works even if static ref was lost)
        try {
            RegisteredServiceProvider<TokensAPI> registration =
                    Bukkit.getServicesManager().getRegistration(TokensAPI.class);
            if (registration != null) {
                instance = registration.getProvider();
                return instance;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Legacy method kept for backwards compatibility.
     * Prefer {@link #getAPI()}.
     */
    @Deprecated
    public static TokensAPI getInstance() {
        return getAPI();
    }

    /**
     * Returns the underlying Tokens plugin instance (use with care).
     */
    public Tokens getPlugin() {
        return plugin;
    }

    // ========================== PLAYER TOKENS ==============================

    /**
     * Returns the current token balance for a player.
     * Returns 0 if the player does not exist in the database.
     */
    public int getPlayerTokens(UUID playerUuid) {
        if (playerUuid == null) return 0;
        try {
            return Database.getTokens(playerUuid.toString());
        } catch (SQLException e) {
            throw new TokensException("Failed to get tokens for " + playerUuid, e);
        }
    }

    /**
     * Sets a player's token balance to the exact amount.
     */
    public void setPlayerTokens(UUID playerUuid, int amount) {
        if (playerUuid == null) return;
        int oldBalance;
        try {
            oldBalance = Database.getTokens(playerUuid.toString());
        } catch (SQLException e) {
            throw new TokensException("Failed to read current balance", e);
        }

        TokenBalanceChangeEvent event = new TokenBalanceChangeEvent(
                playerUuid,
                Bukkit.getPlayer(playerUuid),
                oldBalance,
                amount,
                TokenBalanceChangeEvent.ChangeReason.API
        );
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        try {
            Database.setTokens(playerUuid.toString(), event.getNewBalance());
        } catch (SQLException e) {
            throw new TokensException("Failed to set tokens", e);
        }
    }

    /**
     * Adds the given amount of tokens to the player's balance.
     */
    public void addPlayerTokens(UUID playerUuid, int amount) {
        if (playerUuid == null || amount == 0) return;
        int oldBalance = getPlayerTokens(playerUuid);
        int newBalance = oldBalance + amount;

        TokenBalanceChangeEvent event = new TokenBalanceChangeEvent(
                playerUuid,
                Bukkit.getPlayer(playerUuid),
                oldBalance,
                newBalance,
                TokenBalanceChangeEvent.ChangeReason.API
        );
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        try {
            Database.addTokens(playerUuid.toString(), amount);
        } catch (SQLException e) {
            throw new TokensException("Failed to add tokens", e);
        }
    }

    /**
     * Attempts to remove tokens from a player's balance.
     *
     * @return true if the player had enough tokens and they were removed
     */
    public boolean removePlayerTokens(UUID playerUuid, int amount) {
        if (playerUuid == null || amount <= 0) return false;
        int oldBalance = getPlayerTokens(playerUuid);
        if (oldBalance < amount) return false;

        int newBalance = oldBalance - amount;

        TokenBalanceChangeEvent event = new TokenBalanceChangeEvent(
                playerUuid,
                Bukkit.getPlayer(playerUuid),
                oldBalance,
                newBalance,
                TokenBalanceChangeEvent.ChangeReason.API
        );
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        try {
            Database.removeTokens(playerUuid.toString(), amount);
            return true;
        } catch (SQLException e) {
            throw new TokensException("Failed to remove tokens", e);
        }
    }

    /**
     * Adds tokens asynchronously. The callback is executed on the main thread.
     */
    public void addPlayerTokensAsync(UUID playerUuid, int amount, Consumer<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean success = true;
            try {
                addPlayerTokens(playerUuid, amount);
            } catch (Exception e) {
                success = false;
                plugin.getLogger().warning("Async addPlayerTokens failed: " + e.getMessage());
            }
            boolean finalSuccess = success;
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (callback != null) callback.accept(finalSuccess);
            });
        });
    }

    /**
     * Removes tokens asynchronously. Callback receives whether removal succeeded.
     */
    public void removePlayerTokensAsync(UUID playerUuid, int amount, Consumer<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean result = removePlayerTokens(playerUuid, amount);
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (callback != null) callback.accept(result);
            });
        });
    }

    /**
     * Returns the player's current rank on the token leaderboard (1 = richest).
     */
    public int getPlayerRank(UUID playerUuid) {
        if (playerUuid == null) return 1;
        try {
            return Database.getPlayerRank(playerUuid);
        } catch (SQLException e) {
            throw new TokensException("Failed to get player rank", e);
        }
    }

    /**
     * Returns a list of the top N richest players (by UUID).
     */
    public List<UUID> getTopPlayers(int limit) {
        if (limit <= 0) return List.of();
        try {
            return Database.getTopPlayers(limit);
        } catch (SQLException e) {
            throw new TokensException("Failed to load top players", e);
        }
    }

    /**
     * Checks if a player record exists in the database.
     */
    public boolean playerExists(UUID playerUuid) {
        if (playerUuid == null) return false;
        try {
            Connection conn = Database.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM players WHERE uuid = ?")) {
                ps.setString(1, playerUuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new TokensException("Failed to check player existence", e);
        }
    }

    /**
     * Ensures a player record exists (creates with initial tokens if missing).
     */
    public void createPlayerIfMissing(UUID playerUuid) {
        if (playerUuid == null) return;
        if (playerExists(playerUuid)) return;
        try {
            createPlayer(playerUuid);
        } catch (Exception ignored) {
            // createPlayer already handles it
        }
    }

    /**
     * Creates a player record with the configured initial balance.
     */
    public void createPlayer(UUID playerUuid) {
        if (playerUuid == null) return;
        try {
            createPlayer(playerUuid, configManager.getInitialTokens());
        } catch (Exception e) {
            throw new TokensException("Failed to create player", e);
        }
    }

    /**
     * Creates a player record with a specific starting balance.
     */
    public void createPlayer(UUID playerUuid, int initialBalance) {
        if (playerUuid == null) return;
        try {
            Connection conn = Database.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO players (uuid, tokens) VALUES (?, ?) ON CONFLICT (uuid) DO NOTHING")) {
                ps.setString(1, playerUuid.toString());
                ps.setInt(2, initialBalance);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new TokensException("Failed to create player", e);
        }
    }

    // ========================== BANKS ======================================

    /**
     * Creates a new bank. The owner is automatically added as a member.
     *
     * @return the new bank ID, or -1 on failure
     */
    public int createBank(String name, UUID ownerUuid) {
        if (name == null || ownerUuid == null) return -1;

        // Respect max banks per player
        try {
            List<Integer> existing = BankDatabase.getPlayerBanks(ownerUuid.toString());
            if (existing.size() >= configManager.getMaxBanksPerPlayer()) {
                return -1;
            }
        } catch (SQLException ignored) {
        }

        try {
            int bankId = BankDatabase.createBank(name, ownerUuid.toString());
            if (bankId != -1) {
                Player creator = Bukkit.getPlayer(ownerUuid);
                Bukkit.getPluginManager().callEvent(
                        new BankCreatedEvent(bankId, name, ownerUuid, creator)
                );
            }
            return bankId;
        } catch (SQLException e) {
            throw new TokensException("Failed to create bank", e);
        }
    }

    /**
     * Returns a full snapshot of a bank, or null if it does not exist.
     */
    public Bank getBank(int bankId) {
        try {
            String name = BankDatabase.getBankName(bankId);
            if (name == null) return null;

            String ownerStr = BankDatabase.getBankOwner(bankId);
            UUID owner = ownerStr != null ? UUID.fromString(ownerStr) : null;
            int balance = BankDatabase.getBankBalance(bankId);
            List<UUID> members = BankDatabase.getBankMembers(bankId).stream()
                    .map(UUID::fromString)
                    .toList();

            return new Bank(bankId, name, owner, balance, members);
        } catch (SQLException e) {
            throw new TokensException("Failed to load bank " + bankId, e);
        }
    }

    /**
     * Returns all banks a player has access to (as full Bank objects).
     */
    public List<Bank> getPlayerBanks(UUID playerUuid) {
        if (playerUuid == null) return List.of();
        try {
            List<Integer> ids = BankDatabase.getPlayerBanks(playerUuid.toString());
            List<Bank> banks = new ArrayList<>(ids.size());
            for (int id : ids) {
                Bank b = getBank(id);
                if (b != null) banks.add(b);
            }
            return banks;
        } catch (SQLException e) {
            throw new TokensException("Failed to load player banks", e);
        }
    }

    /**
     * Returns only the bank IDs a player has access to (lighter than full objects).
     */
    public List<Integer> getPlayerBankIds(UUID playerUuid) {
        if (playerUuid == null) return List.of();
        try {
            return BankDatabase.getPlayerBanks(playerUuid.toString());
        } catch (SQLException e) {
            throw new TokensException("Failed to load player bank ids", e);
        }
    }

    public int getBankBalance(int bankId) {
        try {
            return BankDatabase.getBankBalance(bankId);
        } catch (SQLException e) {
            throw new TokensException("Failed to get bank balance", e);
        }
    }

    public String getBankName(int bankId) {
        try {
            return BankDatabase.getBankName(bankId);
        } catch (SQLException e) {
            throw new TokensException("Failed to get bank name", e);
        }
    }

    public UUID getBankOwner(int bankId) {
        try {
            String s = BankDatabase.getBankOwner(bankId);
            return s != null ? UUID.fromString(s) : null;
        } catch (SQLException e) {
            throw new TokensException("Failed to get bank owner", e);
        }
    }

    public List<UUID> getBankMembers(int bankId) {
        try {
            return BankDatabase.getBankMembers(bankId).stream().map(UUID::fromString).toList();
        } catch (SQLException e) {
            throw new TokensException("Failed to get bank members", e);
        }
    }

    /**
     * Deposits tokens from a player's balance into a bank.
     * Fires BankBalanceChangeEvent and TokenBalanceChangeEvent.
     *
     * @return true on success
     */
    public boolean depositToBank(UUID playerUuid, int bankId, int amount) {
        if (playerUuid == null || amount <= 0) return false;

        Player player = Bukkit.getPlayer(playerUuid);

        if (!hasBankAccess(playerUuid, bankId)) return false;

        int playerBalance = getPlayerTokens(playerUuid);
        if (playerBalance < amount) return false;

        int bankOld = getBankBalance(bankId);

        // Events
        TokenBalanceChangeEvent tokenEvent = new TokenBalanceChangeEvent(
                playerUuid, player, playerBalance, playerBalance - amount,
                TokenBalanceChangeEvent.ChangeReason.BANK_DEPOSIT);
        Bukkit.getPluginManager().callEvent(tokenEvent);
        if (tokenEvent.isCancelled()) return false;

        BankBalanceChangeEvent bankEvent = new BankBalanceChangeEvent(
                bankId, getBankName(bankId), playerUuid, player,
                bankOld, bankOld + amount, true);
        Bukkit.getPluginManager().callEvent(bankEvent);
        if (bankEvent.isCancelled()) return false;

        try {
            Database.removeTokens(playerUuid.toString(), amount);
            BankDatabase.addToBank(bankId, amount);
            return true;
        } catch (SQLException e) {
            throw new TokensException("Failed during bank deposit", e);
        }
    }

    /**
     * Withdraws tokens from a bank to a player's balance.
     */
    public boolean withdrawFromBank(UUID playerUuid, int bankId, int amount) {
        if (playerUuid == null || amount <= 0) return false;

        Player player = Bukkit.getPlayer(playerUuid);

        if (!hasBankAccess(playerUuid, bankId)) return false;

        int bankBalance = getBankBalance(bankId);
        if (bankBalance < amount) return false;

        int playerOld = getPlayerTokens(playerUuid);

        TokenBalanceChangeEvent tokenEvent = new TokenBalanceChangeEvent(
                playerUuid, player, playerOld, playerOld + amount,
                TokenBalanceChangeEvent.ChangeReason.BANK_WITHDRAW);
        Bukkit.getPluginManager().callEvent(tokenEvent);
        if (tokenEvent.isCancelled()) return false;

        BankBalanceChangeEvent bankEvent = new BankBalanceChangeEvent(
                bankId, getBankName(bankId), playerUuid, player,
                bankBalance, bankBalance - amount, false);
        Bukkit.getPluginManager().callEvent(bankEvent);
        if (bankEvent.isCancelled()) return false;

        try {
            BankDatabase.removeFromBank(bankId, amount);
            Database.addTokens(playerUuid.toString(), amount);
            return true;
        } catch (SQLException e) {
            throw new TokensException("Failed during bank withdraw", e);
        }
    }

    /**
     * Adds a member to a bank. Only the owner should do this (enforced by commands, not strictly here).
     */
    public void addBankMember(int bankId, UUID memberUuid) {
        if (memberUuid == null) return;
        try {
            BankDatabase.addMember(bankId, memberUuid.toString());
            String bankName = getBankName(bankId);
            UUID owner = getBankOwner(bankId);
            Player inviter = Bukkit.getPlayer(owner);
            Bukkit.getPluginManager().callEvent(
                    new BankMemberAddedEvent(bankId, bankName, owner, memberUuid, inviter)
            );
        } catch (SQLException e) {
            throw new TokensException("Failed to add bank member", e);
        }
    }

    public void removeBankMember(int bankId, UUID memberUuid) {
        if (memberUuid == null) return;
        try {
            BankDatabase.removeMember(bankId, memberUuid.toString());
            String bankName = getBankName(bankId);
            UUID owner = getBankOwner(bankId);
            Player remover = Bukkit.getPlayer(owner);
            Bukkit.getPluginManager().callEvent(
                    new BankMemberRemovedEvent(bankId, bankName, owner, memberUuid, remover)
            );
        } catch (SQLException e) {
            throw new TokensException("Failed to remove bank member", e);
        }
    }

    /**
     * Deletes a bank permanently.
     */
    public void deleteBank(int bankId) {
        try {
            String name = getBankName(bankId);
            UUID owner = getBankOwner(bankId);
            Player deleter = Bukkit.getPlayer(owner);

            BankDatabase.deleteBank(bankId);

            Bukkit.getPluginManager().callEvent(
                    new BankDeletedEvent(bankId, name, owner, deleter)
            );
        } catch (SQLException e) {
            throw new TokensException("Failed to delete bank", e);
        }
    }

    // ---- Bank permission helpers ----

    public boolean hasBankAccess(UUID playerUuid, int bankId) {
        if (playerUuid == null) return false;
        try {
            return BankDatabase.hasAccess(playerUuid.toString(), bankId);
        } catch (SQLException e) {
            throw new TokensException("Failed to check bank access", e);
        }
    }

    public boolean isBankOwner(UUID playerUuid, int bankId) {
        if (playerUuid == null) return false;
        try {
            return BankDatabase.isOwner(playerUuid.toString(), bankId);
        } catch (SQLException e) {
            throw new TokensException("Failed to check bank ownership", e);
        }
    }

    public boolean isBankMember(UUID playerUuid, int bankId) {
        if (playerUuid == null) return false;
        try {
            return BankDatabase.isMember(playerUuid.toString(), bankId);
        } catch (SQLException e) {
            throw new TokensException("Failed to check bank membership", e);
        }
    }

    // ========================== CHECKS =====================================

    /**
     * Creates a token check. Deducts tokens from the creator immediately.
     * Fires CheckCreatedEvent on success.
     *
     * @return the check ItemStack or null if insufficient funds / invalid input
     */
    public ItemStack createCheck(Player creator, int amount) {
        if (creator == null || amount <= 0) return null;

        int balance = getPlayerTokens(creator.getUniqueId());
        if (balance < amount) return null;

        // Fire balance change event for the deduction
        TokenBalanceChangeEvent tokenEvent = new TokenBalanceChangeEvent(
                creator.getUniqueId(), creator, balance, balance - amount,
                TokenBalanceChangeEvent.ChangeReason.CHECK_CREATE);
        Bukkit.getPluginManager().callEvent(tokenEvent);
        if (tokenEvent.isCancelled()) return null;

        try {
            Database.removeTokens(creator.getUniqueId().toString(), amount);
            ItemStack check = CheckManager.createCheck(creator, amount);

            // Try to extract check ID from lore for the event
            String checkId = null;
            if (check != null && check.hasItemMeta() && check.getItemMeta().hasLore()) {
                for (String line : check.getItemMeta().getLore()) {
                    if (line.contains("ID: ")) {
                        checkId = line.substring(line.indexOf("ID: ") + 4).trim();
                        break;
                    }
                }
            }
            Bukkit.getPluginManager().callEvent(new CheckCreatedEvent(creator, amount, check, checkId));
            return check;
        } catch (SQLException e) {
            throw new TokensException("Failed to create check", e);
        }
    }

    public boolean isValidCheck(ItemStack item) {
        return CheckManager.isValidCheck(item);
    }

    /**
     * Redeems a check for the player.
     * Fires CheckRedeemedEvent on success.
     */
    public boolean redeemCheck(Player redeemer, ItemStack check) {
        if (redeemer == null || check == null) return false;

        // We need to peek the amount/creator before redemption for the event
        int amount = 0;
        UUID originalCreator = null;
        String checkId = null;

        if (check.hasItemMeta() && check.getItemMeta().hasLore()) {
            for (String line : check.getItemMeta().getLore()) {
                if (line.contains("Amount: ")) {
                    try {
                        String val = line.substring(line.indexOf("Amount: ") + 8).trim();
                        amount = Integer.parseInt(val);
                    } catch (Exception ignored) {}
                }
                if (line.contains("ID: ")) {
                    checkId = line.substring(line.indexOf("ID: ") + 4).trim();
                }
                if (line.contains("Created by: ")) {
                    // We don't have the UUID here easily; the CheckManager will validate
                }
            }
        }

        try {
            boolean success = CheckManager.redeemCheck(redeemer, check);
            if (success) {
                // The check manager already added the tokens. We still fire an event.
                Bukkit.getPluginManager().callEvent(
                        new CheckRedeemedEvent(redeemer, amount, check, originalCreator, checkId)
                );
            }
            return success;
        } catch (SQLException e) {
            throw new TokensException("Failed to redeem check", e);
        }
    }

    // ========================== CONFIG / INFO ==============================

    public int getInitialTokens() {
        return configManager.getInitialTokens();
    }

    public int getMaxBanksPerPlayer() {
        return configManager.getMaxBanksPerPlayer();
    }

    public int getMaxMembersPerBank() {
        return configManager.getMaxMembersPerBank();
    }

    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * Returns whether the given plugin name is currently enabled on the server.
     */
    public boolean isIntegrationAvailable(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null &&
               Bukkit.getPluginManager().getPlugin(pluginName).isEnabled();
    }
}
 