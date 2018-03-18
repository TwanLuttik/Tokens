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



    public void addTokens (UUID uuid, Player p, int commandOutput) {
        int tokens = cfgM.getPlayers().getInt(uuid + ".tokens");

        cfgM.getPlayers().set(uuid + ".tokens", tokens + commandOutput);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + commandOutput + " Tokens are added to " + F.getName(String.valueOf(uuid)));
    }

    public void removeTokens (UUID uuid, Player p, int commandOutput) {

        int tokens = cfgM.getPlayers().getInt(uuid + ".tokens");

        cfgM.getPlayers().set(uuid + ".tokens", tokens - commandOutput);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + commandOutput + " Tokens are removed from " + F.getName(String.valueOf(uuid)));
    }

    public void setTokens (UUID uuid, Player p, int commandOutput) {

        cfgM.getPlayers().set(uuid + ".tokens", commandOutput);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + commandOutput + " Tokens are set to " + F.getName(String.valueOf(uuid)));
    }






}
