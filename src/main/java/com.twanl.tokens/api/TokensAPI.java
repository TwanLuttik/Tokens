package com.twanl.tokens.api;

import com.twanl.tokens.Tokens;
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


    // remove tokens from the player
    public void playerRemoveTokens(Player p, int tokens) {

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p.getUniqueId()) - tokens);
        cfgM.savePlayers();
    }

    // add tokens to the player
    public void playerAddTokens(Player p, int tokens) {

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p.getUniqueId()) + tokens);
        cfgM.savePlayers();
    }


    public void playerSetTokens(Player p, int tokens) {

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", tokens);
        cfgM.savePlayers();
    }

//    public int playerBalance(Player p) {
//
//        return cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
//    }

    public int playerBalance(UUID uuid) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);

        return cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
    }


    public boolean hasAccount(UUID playerUUID) {
        Player p = (Player) Bukkit.getOfflinePlayer(playerUUID);

        if (!cfgM.getPlayers().contains(String.valueOf(p.getUniqueId()))) {
            return false;
        }
        return true;
    }


    public String getPrefix() {
        if (plugin.getConfig().getBoolean("prefix.enable")) {
            return Strings.translateColorCodes(plugin.getConfig().getString("prefix.prefix"));
        } else {
            return "Tokens";
        }
    }


}
