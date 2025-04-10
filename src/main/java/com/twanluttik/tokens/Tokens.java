package com.twanluttik.tokens;

import com.twanluttik.tokens.events.JoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.sql.SQLException;

public final class Tokens extends JavaPlugin {
    private static Tokens instance;
    private ConfigManager configManager;
    Commands commands = new Commands();

    @Override
    public void onEnable() {
        instance = this;
        try {
            // Initialize configuration
            configManager = ConfigManager.getInstance(this);
            
            // Initialize database
            Database.initialize(configManager);
            getServer().getPluginManager().registerEvents(new JoinEvent(), this);
            
            // Initialize database connection
            Database.getConnection();
            Database.initializeTables();
            BankDatabase.initializeTables();
            System.out.println("Successfully connected to the database");
            
            Objects.requireNonNull(this.getCommand("tokens")).setExecutor(commands);
            System.out.println("Tokens plugin enabled");
            
            // Check for updates if enabled
            if (configManager.isUpdateCheckerEnabled()) {
                UpdateChecker.checkForUpdates();
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        // Close database connection
        Database.closeConnection();
        System.out.println("Database connection closed");
    }
    
    public static Tokens getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
