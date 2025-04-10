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
    private static final String CURRENT_VERSION = "2.0.0-SHAPSHOT-1";
    private static final Set<UUID> notifiedPlayers = new HashSet<>();
    private static final Gson gson = new Gson();
    
    public static void checkForUpdates() {
        // Check if update checker is enabled in config
        if (!ConfigManager.getInstance(Tokens.getInstance()).isUpdateCheckerEnabled()) {
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
                
                if (!latestVersion.equals(CURRENT_VERSION)) {
                    notifyOps();
                }
            } catch (IOException e) {
                Bukkit.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }
    
    private static void notifyOps() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() && !notifiedPlayers.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "A new version of Tokens is available!");
                player.sendMessage(ChatColor.YELLOW + "Current version: " + CURRENT_VERSION);
                player.sendMessage(ChatColor.YELLOW + "Download it from: https://www.spigotmc.org/resources/tokens-economy-1-8-x-1-13-x-bank-system-sql-api.53944/");
                notifiedPlayers.add(player.getUniqueId());
            }
        }
    }
    
    public static void clearNotifiedPlayers() {
        notifiedPlayers.clear();
    }
} 