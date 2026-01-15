package com.twanluttik.tokens;

import com.twanluttik.tokens.events.JoinEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
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
            // Initialize bStats
//            int pluginId = 28805;
//            Metrics metrics = new Metrics(this, pluginId); // Replace 20830 with your actual plugin ID from bStats
            
            // Initialize configuration
            configManager = ConfigManager.initialize(this);
            
            // Initialize database
            Database.initialize(configManager);
            getServer().getPluginManager().registerEvents(new JoinEvent(), this);
            
            // Initialize database connection
            Database.getConnection();
            Database.initializeTables();
            BankDatabase.initializeTables();
            CheckManager.initializeTables();
            System.out.println("Successfully connected to the database");
            
            Objects.requireNonNull(this.getCommand("tokens")).setExecutor(commands);
            System.out.println("Tokens plugin enabled");
            
            // Check for updates if enabled
            if (configManager.isUpdateCheckerEnabled()) {
                UpdateChecker.checkForUpdates();
            }

            // Initialize library integrations
            LibraryIntegration.initialize();
            if (LibraryIntegration.isPluginAvailable("DecentHolograms")) {
                // Initialize HologramManager
                HologramManager.getInstance();
                HologramManager.getInstance().updateHologram();

                // Schedule hologram updates every 5 minutes
                getServer().getScheduler().runTaskTimer(this, () -> {
                    try {
                        HologramManager.getInstance().updateHologram();
                    } catch (Exception e) {
                        getLogger().warning("Failed to update hologram: " + e.getMessage());
                    }
                }, 6000L, 6000L);
            }

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new Placeholder(this).register();
                getLogger().info("PlaceholderAPI integration enabled");
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
