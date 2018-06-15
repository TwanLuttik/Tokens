package com.twanl.tokens.api;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Functions;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;


/**
 * Created by Twan on 3/22/2018.
 **/


public class TokensAPI {

    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager cfgM = new ConfigManager();
    private Functions F = new Functions();


    public void playerRemoveTokens(Player p, int tokens) {

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p) - tokens);
        cfgM.savePlayers();
    }

    public void playerAddTokens(Player p, int tokens) {

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerBalance(p) + tokens);
        cfgM.savePlayers();
    }

    public void playerSetTokens(Player p, int tokens) {

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", tokens);
        cfgM.savePlayers();
    }

    public int playerBalance(Player p) {

        return cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
    }


    public boolean hasAccount(UUID playerUUID) {
        Player p = (Player) Bukkit.getOfflinePlayer(playerUUID);

        if (!cfgM.getPlayers().contains(String.valueOf(p.getUniqueId()))) {
            return false;
        }

        return true;
    }


}
