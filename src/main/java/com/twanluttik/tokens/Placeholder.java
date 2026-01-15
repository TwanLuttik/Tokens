package com.twanluttik.tokens;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class Placeholder extends PlaceholderExpansion {

    private final Plugin plugin;

    public Placeholder(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tokens";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getRequiredPlugin() {
        return "Tokens";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        plugin.getLogger().info("Placeholder request - Player: " + (player != null ? player.getName() : "null") + ", Params: " + params);
        if (params.equalsIgnoreCase("player_tokens_balance")) {
            if (player == null) {
                return "0";
            }
            try {
                int balance = Database.getTokens(player.getUniqueId().toString());
                return String.valueOf(balance);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to get token balance for player: " + e.getMessage());
                return "0";
            }
        }

        if (params.equalsIgnoreCase("placeholder1")) {
            return plugin.getConfig().getString("placeholders.placeholder1", "default1");
        }

        if (params.equalsIgnoreCase("placeholder2")) {
            return plugin.getConfig().getString("placeholders.placeholder2", "default2");
        }

        return null;
    }
}
