package com.twanluttik.tokens;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import java.util.logging.Level;

/**
 * Utility class for checking plugin integrations and dependencies
 */
public class LibraryIntegration {
    private static boolean hologramEnabled = false;
    private static Plugin hologramPlugin = null;

    /**
     * Initialize and check for available integrations
     */
    public static void initialize() {
        checkHologramIntegration();
        // Add more integration checks here as needed
    }

    /**
     * Check if HolographicDisplays or DecentHolograms is available
     */
    private static void checkHologramIntegration() {
        // Check for HolographicDisplays
        Plugin hd = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
        if (hd != null && hd.isEnabled()) {
            hologramEnabled = true;
            hologramPlugin = hd;
            Bukkit.getLogger().log(Level.INFO, "[Tokens] Successfully integrated with HolographicDisplays!");
            return;
        }

        // Check for DecentHolograms
        Plugin dh = Bukkit.getPluginManager().getPlugin("DecentHolograms");
        if (dh != null && dh.isEnabled()) {
            hologramEnabled = true;
            hologramPlugin = dh;
            Bukkit.getLogger().log(Level.INFO, "[Tokens] Successfully integrated with DecentHolograms!");
        }
    }

    /**
     * Check if hologram integration is available
     * @return true if hologram plugin is available and enabled
     */
    public static boolean isHologramEnabled() {
        return hologramEnabled;
    }

    /**
     * Get the hologram plugin instance if available
     * @return Plugin instance or null if not available
     */
    public static Plugin getHologramPlugin() {
        return hologramPlugin;
    }

    /**
     * Check if a specific plugin is available and enabled
     * @param pluginName Name of the plugin to check
     * @return true if the plugin is available and enabled
     */
    public static boolean isPluginAvailable(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        boolean available = plugin != null && plugin.isEnabled();
        if (available) {
            Bukkit.getLogger().log(Level.INFO, "[Tokens] Successfully integrated with " + pluginName + "!");
        }
        return available;
    }
} 