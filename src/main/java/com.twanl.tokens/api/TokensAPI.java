package com.twanl.tokens.api;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.sql.SQLlib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;


/**
 * Created by Twan on 3/22/2018.
 **/


public class TokensAPI {

    private ConfigManager cfgM = new ConfigManager();
    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    private SQLlib sql = new SQLlib();
    private Lib lib = new Lib();


    // remove tokens from the player
    public void playerRemoveTokens(UUID uuid, int tokens) {
        if (lib.sqlUse()) {
            sql.removeTokens(uuid, tokens);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p.getUniqueId()) - tokens);
            cfgM.savePlayers();
        }
    }

    // add tokens to the player
    public void playerAddTokens(UUID uuid, int tokens) {
        if (lib.sqlUse()) {
            sql.addTokens(uuid, tokens);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p.getUniqueId()) + tokens);
            cfgM.savePlayers();
        }
    }


    public void playerSetTokens(UUID uuid, int tokens) {
        if (lib.sqlUse()) {
            sql.setTokens(uuid, tokens);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            cfgM.getPlayers().set(p.getUniqueId() + ".tokens", tokens);
            cfgM.savePlayers();
        }
    }


    public int playerBalance(UUID uuid) {
        if (lib.sqlUse()) {
            return sql.getTokens(uuid);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
        }
    }


    public boolean hasAccount(UUID uuid) {
        if (lib.sqlUse()) {
            if (!sql.hasAccount(uuid)) {
                return false;
            }
            return true;
        } else {
            Player p = (Player) Bukkit.getOfflinePlayer(uuid);
            if (!cfgM.getPlayers().contains(String.valueOf(p.getUniqueId()))) {
                return false;
            } else {
                return true;
            }
        }
    }


    public String getPrefix() {
        if (plugin.getConfig().getBoolean("prefix.enable")) {
            return Strings.translateColorCodes(plugin.getConfig().getString("prefix.prefix"));
        } else {
            return "Tokens";
        }
    }


}
