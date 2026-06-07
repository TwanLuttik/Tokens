package com.twanluttik.tokens;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private static ConfigManager instance;
    private final Plugin plugin;
    private final FileParser fileParser;

    public FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.fileParser = new FileParser(plugin);
    }

    public void loadConfig() {
        config = fileParser.loadFile("config.yml");
    }

    public static ConfigManager initialize(Plugin plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
        }
        instance.loadConfig();
        return instance;
    }

    // Version is provided by the plugin description, not config
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    // Database settings
    public String getDatabaseHost() {
        return config.getString("database.host", "0.0.0.0");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 5432);
    }

    public String getDatabaseName() {
        return config.getString("database.database", "mydatabase");
    }

    public String getDatabaseUsername() {
        return config.getString("database.username", "myuser");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "mypassword");
    }

    public boolean isDatabaseSSL() {
        return config.getBoolean("database.ssl", false);
    }

    // General settings
    public int getInitialTokens() {
        return config.getInt("settings.initial-tokens", 0);
    }

    public boolean isAutoCreateBank() {
        return config.getBoolean("settings.auto-create-bank", false);
    }

    public int getMaxBanksPerPlayer() {
        return config.getInt("settings.max-banks-per-player", 3);
    }

    public int getMaxMembersPerBank() {
        return config.getInt("settings.max-members-per-bank", 10);
    }

    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean("settings.check-for-updates", true);
    }

    // Messages
    public String getPrefix() {
        return config.getString("messages.prefix", "&6[Tokens] &r");
    }

    public String getNoPermissionMessage() {
        return config.getString("messages.no-permission", "&cYou don't have permission to use this command!");
    }

    public String getPlayerNotFoundMessage() {
        return config.getString("messages.player-not-found", "&cPlayer not found!");
    }

    public String getInvalidAmountMessage() {
        return config.getString("messages.invalid-amount", "&cInvalid amount!");
    }

    public String getBankCreatedMessage() {
        return config.getString("messages.bank-created", "&aCreated bank '%bank_name%' successfully!");
    }

    public String getBankDeletedMessage() {
        return config.getString("messages.bank-deleted", "&aDeleted bank '%bank_name%'");
    }

    public String getDepositSuccessMessage() {
        return config.getString("messages.deposit-success", "&aDeposited %amount% tokens to bank '%bank_name%'");
    }

    public String getWithdrawSuccessMessage() {
        return config.getString("messages.withdraw-success", "&aWithdrew %amount% tokens from bank '%bank_name%'");
    }

    public String getInviteSuccessMessage() {
        return config.getString("messages.invite-success", "&aInvited %player% to bank '%bank_name%'");
    }

    public String getRemoveSuccessMessage() {
        return config.getString("messages.remove-success", "&aRemoved %player% from bank '%bank_name%'");
    }

    public String getBalanceMessage() {
        return config.getString("messages.balance", "&aYour balance: %balance% tokens");
    }

    public String getBankBalanceMessage() {
        return config.getString("messages.bank-balance", "&aBank '%bank_name%' balance: %balance% tokens");
    }
} 