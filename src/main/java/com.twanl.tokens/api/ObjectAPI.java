package com.twanl.tokens.api;

import com.twanl.tokens.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Twan on 3/29/2018.
 **/
public class ObjectAPI {

    private ConfigManager cfgM = new ConfigManager();

    public String transactionPlayer (UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionPlayer = cfgM.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.player");

        if (transactionPlayer == null) {
            return "No player";
        }
        return String.valueOf(transactionPlayer);
    }

    public String transactionDate (UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionDate = cfgM.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.date");

        if (transactionDate == null) {
            return "No data";
        }
        return String.valueOf(transactionDate);
    }

    public String transactionAmount (UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionDate = cfgM.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.amount");

        if (transactionDate == null) {
            return "No value";
        }
        return String.valueOf(transactionDate);
    }


}
