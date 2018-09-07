package com.twanl.tokens.api;

import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.sql.SQLlib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.loadManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Twan on 3/22/2018.
 **/


public class TokensAPI {

    private ConfigManager config = new ConfigManager();
//    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    private SQLlib sql = new SQLlib();
    private Lib lib = new Lib();


    // remove tokens from the player
    public void playerRemoveTokens(UUID uuid, int tokens) {
        if (lib.sqlUse()) {
            sql.removeTokens(uuid, tokens);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            config.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p.getUniqueId()) - tokens);
            config.savePlayers();
        }
    }

    // add tokens to the player
    public void playerAddTokens(UUID uuid, int tokens) {
        if (lib.sqlUse()) {
            sql.addTokens(uuid, tokens);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            config.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p.getUniqueId()) + tokens);
            config.savePlayers();
        }
    }


    public void playerSetTokens(UUID uuid, int tokens) {
        if (lib.sqlUse()) {
            sql.setTokens(uuid, tokens);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            config.getPlayers().set(p.getUniqueId() + ".tokens", tokens);
            config.savePlayers();
        }
    }


    public int playerBalance(UUID uuid) {
        if (lib.sqlUse()) {
            return sql.getTokens(uuid);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return config.getPlayers().getInt(p.getUniqueId() + ".tokens");
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
            if (!config.getPlayers().contains(String.valueOf(p.getUniqueId()))) {
                return false;
            } else {
                return true;
            }
        }
    }


    public String getPrefix() {
//        if (plugin.getConfig().getBoolean("prefix.enable")) {
        if (loadManager.prefix_boolean()) {
//            return Strings.translateColorCodes(plugin.getConfig().getString("prefix.prefix"));
            return Strings.translateColorCodes(loadManager.prefix());
        } else {
            return "Tokens";
        }
    }

    // check if the player is regristated in a bank
    public boolean hasBankAccount(UUID uuid) {
        return lib.playerIsRegisteredInBank(uuid);
    }

    // get the balance of the bank from player
    public int bankBalance(UUID uuid) {
        return lib.bankBalance(uuid);
    }


    // get all the users from a bank in UUID
    public List bankUsersUUID(UUID uuid) {
        config.setup();
        List<String> user = new ArrayList<>();
        for (String key : config.getBank().getStringList(lib.userBankID(uuid) + ".users")) {
            String[] a = key.split(" ");
            user.add(a[0]);
        }
        return user;
    }


    // get all the users from a bank in player name's
    public List bankUsersName(UUID uuid) {
        config.setup();
        List<String> user = new ArrayList<>();
        for (String key : config.getBank().getStringList(lib.userBankID(uuid) + ".users")) {
            String[] a = key.split(" ");
            user.add(a[1]);
        }
        return user;
    }

    public int bankUserRank(UUID uuid){
        config.setup();
        return lib.bankUserRank(uuid);
    }

}
