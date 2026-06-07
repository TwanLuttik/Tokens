package com.twanluttik.tokens;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UpdateChecker {
    private static final String SPIGOT_API_URL = "https://api.spigotmc.org/legacy/update.php?resource=53944";
    private static final Set<UUID> notifiedPlayers = new HashSet<>();
    private static final Gson gson = new Gson();

    public static void checkForUpdates() {
        // Check if update checker is enabled in config
        ConfigManager configManager = Tokens.getInstance().getConfigManager();
        if (configManager == null || !configManager.isUpdateCheckerEnabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Tokens.getInstance(), () -> {
            try {
                URL url = new URL(SPIGOT_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                
                String latestVersion = gson.fromJson(reader, String.class);
                if (latestVersion == null) return;

                String currentVersion = Tokens.getInstance().getDescription().getVersion();
                
                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    notifyOps(latestVersion, currentVersion);
                }
            } catch (IOException e) {
                Bukkit.getLogger().warning("[Tokens] Failed to check for updates: " + e.getMessage());
            }
        });
    }
    
    private static void notifyOps(String latestVersion, String currentVersion) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() && !notifiedPlayers.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "A new version of Tokens is available!");
                player.sendMessage(ChatColor.YELLOW + "Current version: " + currentVersion);
                player.sendMessage(ChatColor.YELLOW + "Latest version: " + latestVersion);
                player.sendMessage(ChatColor.YELLOW + "Download it from: https://www.spigotmc.org/resources/tokens-economy-1-8-x-1-13-x-bank-system-sql-api.53944/");
                notifiedPlayers.add(player.getUniqueId());
            }
        }
    }
    
    public static void clearNotifiedPlayers() {
        notifiedPlayers.clear();
    }
} 