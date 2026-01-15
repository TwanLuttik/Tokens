package com.twanluttik.tokens;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private static ConfigManager instance;
    private Plugin plugin;
    private FileParser fileParser;

    private FileConfiguration pluginConfig;
    public FileConfiguration configConfig;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.fileParser = new FileParser(plugin);
    }

    public void loadConfig() {
        configConfig = fileParser.loadFile("config.yml");
    }

    public static ConfigManager initialize(Plugin plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
        }
        instance.loadConfig();
        return instance;
    }



//    public void saveConfig() {
//        try {
//            config.save(configFile);
//        } catch (IOException e) {
//            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
//        }
//    }

//    public void reloadConfig() {
//        config = YamlConfiguration.loadConfiguration(configFile);
//    }

    public String getVersion() {
        return pluginConfig.getString("version");
    }

    // Database settings
    public String getDatabaseHost() {
        return configConfig.getString("database.host", "0.0.0.0");
    }

    public int getDatabasePort() {
        return configConfig.getInt("database.port", 5432);
    }

    public String getDatabaseName() {
        return configConfig.getString("database.database", "mydatabase");
    }

    public String getDatabaseUsername() {
        return configConfig.getString("database.username", "myuser");
    }

    public String getDatabasePassword() {
        return configConfig.getString("database.password", "mypassword");
    }

    public boolean isDatabaseSSL() {
        return configConfig.getBoolean("database.ssl", false);
    }

    // General settings
    public int getInitialTokens() {
        return configConfig.getInt("settings.initial-tokens", 0);
    }

    public boolean isAutoCreateBank() {
        return configConfig.getBoolean("settings.auto-create-bank", false);
    }

    public int getMaxBanksPerPlayer() {
        return configConfig.getInt("settings.max-banks-per-player", 3);
    }

    public int getMaxMembersPerBank() {
        return configConfig.getInt("settings.max-members-per-bank", 10);
    }

    public boolean isUpdateCheckerEnabled() {
        return configConfig.getBoolean("settings.check-for-updates", true);
    }

    // Messages
    public String getPrefix() {
        return configConfig.getString("messages.prefix", "&6[Tokens] &r");
    }

    public String getNoPermissionMessage() {
        return configConfig.getString("messages.no-permission", "&cYou don't have permission to use this command!");
    }

    public String getPlayerNotFoundMessage() {
        return configConfig.getString("messages.player-not-found", "&cPlayer not found!");
    }

    public String getInvalidAmountMessage() {
        return configConfig.getString("messages.invalid-amount", "&cInvalid amount!");
    }

    public String getBankCreatedMessage() {
        return configConfig.getString("messages.bank-created", "&aCreated bank '%bank_name%' successfully!");
    }

    public String getBankDeletedMessage() {
        return configConfig.getString("messages.bank-deleted", "&aDeleted bank '%bank_name%'");
    }

    public String getDepositSuccessMessage() {
        return configConfig.getString("messages.deposit-success", "&aDeposited %amount% tokens to bank '%bank_name%'");
    }

    public String getWithdrawSuccessMessage() {
        return configConfig.getString("messages.withdraw-success", "&aWithdrew %amount% tokens from bank '%bank_name%'");
    }

    public String getInviteSuccessMessage() {
        return configConfig.getString("messages.invite-success", "&aInvited %player% to bank '%bank_name%'");
    }

    public String getRemoveSuccessMessage() {
        return configConfig.getString("messages.remove-success", "&aRemoved %player% from bank '%bank_name%'");
    }

    public String getBalanceMessage() {
        return configConfig.getString("messages.balance", "&aYour balance: %balance% tokens");
    }

    public String getBankBalanceMessage() {
        return configConfig.getString("messages.bank-balance", "&aBank '%bank_name%' balance: %balance% tokens");
    }
} 