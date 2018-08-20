package com.twanl.tokens.utils;

import com.twanl.tokens.Tokens;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Twan on 8/16/2018.
 **/
@SuppressWarnings("ALL")
public class loadManager {


    public static HashSet<Double> configVersion = new HashSet<>();
    public static HashSet<String> databaseOption = new HashSet<>();
    public static HashMap<Integer, Boolean> updateMessage = new HashMap<>();
    public static HashSet<String> bonus = new HashSet<>();
    public static HashSet<String> prefix = new HashSet<>();
    public static HashMap<Integer, Boolean> prefixBoolean = new HashMap<>();
    public static HashSet<Integer> tokensBuy = new HashSet<>();
    public static HashSet<Integer> tokensSell = new HashSet<>();
    public static ArrayList<String> sign = new ArrayList<>();

    public static HashSet<String> sqlHost = new HashSet<>();
    public static HashSet<Integer> sqlPort = new HashSet<>();
    public static HashSet<String> sqlUsername = new HashSet<>();
    public static HashSet<String> sqlPassword = new HashSet<>();
    public static HashSet<String> sqlDatabase = new HashSet<>();
    public static HashSet<String> sqlTable = new HashSet<>();
    public static HashMap<Integer, Boolean> sqlUseSsl = new HashMap<>();


    public static void loadHashSet() {
        Tokens plugin = Tokens.getPlugin(Tokens.class);

        configVersion.add(plugin.getConfig().getDouble("ConfigVersion"));
        databaseOption.add(plugin.getConfig().getString("database"));
        updateMessage.put(1, plugin.getConfig().getBoolean("update_message"));
        bonus.add(plugin.getConfig().getString("bonus_message"));
        prefix.add(plugin.getConfig().getString("prefix.prefix"));
        prefixBoolean.put(1, plugin.getConfig().getBoolean("prefix.enable"));
        tokensBuy.add(plugin.getConfig().getInt("tokens.buy_price"));
        tokensSell.add(plugin.getConfig().getInt("tokens.sell_price"));

        for (String key : plugin.getConfig().getConfigurationSection("sign").getKeys(false)) {
            sign.add(plugin.getConfig().getString("sign."+key));
        }

        sqlHost.add(plugin.getConfig().getString("mySQL.host"));
        sqlPort.add(plugin.getConfig().getInt("mySQL.port"));
        sqlUsername.add(plugin.getConfig().getString("mySQL.username"));
        sqlPassword.add(plugin.getConfig().getString("mySQL.password"));
        sqlDatabase.add(plugin.getConfig().getString("mySQL.database"));
        sqlTable.add(plugin.getConfig().getString("mySQL.table"));
        sqlUseSsl.put(1, plugin.getConfig().getBoolean("mySQL.useSSL"));

        Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.green + "Loaded the config successfully!");
    }


    public static double config_version() {
        return Double.parseDouble(configVersion.toString().replace("[", "").replace("]", ""));
    }

    public static String database() {
        return databaseOption.toString().replace("[", "").replace("]", "");
    }

    public static boolean update_message() {
        return updateMessage.get(1);
    }

    public static String bonus_message() {
        return bonus.toString().replace("[", "").replace("]", "");
    }

    public static String prefix() {
        return prefix.toString().replace("[", "").replace("]", "");
    }

    public static Boolean prefix_boolean() {
        return prefixBoolean.get(1);
    }

    public static int tokens_buy() {
        return Integer.parseInt(tokensBuy.toString().replace("[", "").replace("]", ""));
    }

    public static int token_sell() {
        return Integer.parseInt(tokensSell.toString().replace("[", "").replace("]", ""));
    }

    public static ArrayList<String> sign() {
        return sign;
    }

    public static String host() {
        return sqlHost.toString().replace("[", "").replace("]", "");
    }

    public static int port() {
        return Integer.parseInt(sqlPort.toString().replace("[", "").replace("]", ""));
    }

    public static String username() {
        return sqlUsername.toString().replace("[", "").replace("]", "");
    }

    public static String passwod() {
        return sqlPassword.toString().replace("[", "").replace("]", "");
    }

    public static String table() {
        return sqlTable.toString().replace("[", "").replace("]", "");
    }

    public static String db() {
        return sqlDatabase.toString().replace("[", "").replace("]", "");
    }

    public static Boolean useSSL() {
        return sqlUseSsl.get(1);
    }

}
