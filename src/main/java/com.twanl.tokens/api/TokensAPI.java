package com.twanl.tokens.api;

import com.twanl.tokens.Functions;
import com.twanl.tokens.Tokens;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sun.security.krb5.Config;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class TokensAPI {

    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager cfgM = new ConfigManager();
    private Functions F = new Functions();



    public void addTokens (UUID uuid, Player p, int tokens) {
        int playerTokens = cfgM.getPlayers().getInt(uuid + ".tokens");

        cfgM.getPlayers().set(uuid + ".tokens", playerTokens + tokens);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + tokens + " Tokens are added to " + F.getName(String.valueOf(uuid)));
    }

    public void removeTokens (UUID uuid, Player p, int tokens) {

        int playerTokens = cfgM.getPlayers().getInt(uuid + ".tokens");

        cfgM.getPlayers().set(uuid + ".tokens", playerTokens - tokens);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + tokens + " Tokens are removed from " + F.getName(String.valueOf(uuid)));
    }

    public void setTokens (UUID uuid, Player p, int tokens) {

        cfgM.getPlayers().set(uuid + ".tokens", tokens);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + tokens + " Tokens are set to " + F.getName(String.valueOf(uuid)));
    }

    public void giveallTokens (Player p, int tokens) {

        String tokens1 = String.valueOf(tokens);
        String defaultMessage = plugin.getConfig().getString("bonus_message");
        String replacedMessage = defaultMessage.replace("{tokens}", tokens1);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int playerTokens = cfgM.getPlayers().getInt(onlinePlayer.getUniqueId() + ".tokens");
            cfgM.getPlayers().set(onlinePlayer.getUniqueId() + ".tokens", playerTokens + tokens);

            Strings.translateColorCodes(onlinePlayer, replacedMessage);
        }
        cfgM.savePlayers();
    }



    // Check if player exist in file
    public boolean playerCheck (UUID uuid) {
        if (!cfgM.getPlayers().contains(String.valueOf(uuid))) {
            cfgM.getPlayers().set(uuid + ".tokens", 0);
            cfgM.savePlayers();
            return false;
        }
        return false;
    }





}
