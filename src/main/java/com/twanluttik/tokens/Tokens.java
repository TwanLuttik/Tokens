package com.twanluttik.tokens;

import com.twanluttik.tokens.events.JoinEvent;
import com.twanluttik.tokens.gui.GuiListener;
import com.twanluttik.tokens.gui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.sql.SQLException;


public final class Tokens extends JavaPlugin {
    private static Tokens instance;
    private ConfigManager configManager;

    private BukkitTask hologramTask;
    private GuiManager guiManager;

    private final Commands commands = new Commands();

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
            getLogger().info("Successfully connected to the database");
            
            Objects.requireNonNull(this.getCommand("tokens")).setExecutor(commands);
            Objects.requireNonNull(this.getCommand("tokens")).setTabCompleter(new TokensTabCompleter());

            // Log version with color to console
            String version = getDescription().getVersion();
            getLogger().info("Tokens v" + version + " enabled");
            Bukkit.getConsoleSender().sendMessage(
                ChatColor.GREEN + "✓ " + ChatColor.GOLD + "Tokens " + 
                ChatColor.YELLOW + "v" + version + ChatColor.GREEN + " has been enabled!"
            );
            
            // Check for updates if enabled
            if (configManager.isUpdateCheckerEnabled()) {
                UpdateChecker.checkForUpdates();
            }

            // Initialize library integrations
            LibraryIntegration.initialize();
            if (LibraryIntegration.isDecentHologramsAvailable()) {
                // Initialize HologramManager
                HologramManager.getInstance();
                HologramManager.getInstance().updateHologram();

                // Schedule hologram updates every 5 minutes (20 ticks * 60 * 5 = 6000)
                hologramTask = getServer().getScheduler().runTaskTimer(this, () -> {
                    try {
                        HologramManager.getInstance().updateHologram();
                    } catch (Exception e) {
                        getLogger().warning("Failed to update hologram: " + e.getMessage());
                    }
                }, 6000L, 6000L);
            }

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new PlaceholderExpension().register();
                Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GREEN + "✓ " + ChatColor.GOLD + "Tokens " + ChatColor.GREEN + "PlaceholderAPI integration enabled"
                );
            }

            // Initialize GUI system
            guiManager = new GuiManager(this);
            getServer().getPluginManager().registerEvents(new GuiListener(guiManager), this);

            // Register public API for other plugins
            TokensAPI api = new TokensAPI(this);
            getServer().getServicesManager().register(TokensAPI.class, api, this, ServicePriority.Normal);
            getLogger().info("Tokens API registered for other plugins");

        } catch (SQLException e) {
            getLogger().severe("Failed to connect to the database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        // Cancel scheduled tasks
        if (hologramTask != null) {
            hologramTask.cancel();
        }

        // Close database connection
        Database.closeConnection();
        getLogger().info("Database connection closed");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Tokens has been disabled.");
    }
    
    public static Tokens getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

}
