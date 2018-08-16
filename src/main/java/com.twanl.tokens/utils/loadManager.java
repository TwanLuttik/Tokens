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


    public static HashSet<Double> config_CONFIG_VERSION = new HashSet<>();
    public static HashSet<String> config_DATABASE = new HashSet<>();
    public static HashMap<Integer, Boolean> config_UPDATE_MESSAGE = new HashMap<>();
    public static HashSet<String> config_BONUS = new HashSet<>();
    public static HashSet<String> config_PREFIX = new HashSet<>();
    public static HashMap<Integer, Boolean> config_PREFIX_BOOLEAN = new HashMap<>();
    public static HashSet<Integer> config_TOKENS_BUY = new HashSet<>();
    public static HashSet<Integer> config_TOKENS_SELL = new HashSet<>();
    public static ArrayList<String> config_SIGN = new ArrayList<>();

    public static HashSet<String> config_SQL_HOST = new HashSet<>();
    public static HashSet<Integer> config_SQL_PORT = new HashSet<>();
    public static HashSet<String> config_SQL_USERNAME = new HashSet<>();
    public static HashSet<String> config_SQL_PASSWORD = new HashSet<>();
    public static HashSet<String> config_SQL_DATABASE = new HashSet<>();
    public static HashSet<String> config_SQL_TABLE = new HashSet<>();


    public static void loadHashSet() {
        Tokens plugin = Tokens.getPlugin(Tokens.class);

        config_CONFIG_VERSION.add(plugin.getConfig().getDouble("ConfigVersion"));
        config_DATABASE.add(plugin.getConfig().getString("database"));
        config_UPDATE_MESSAGE.put(1, plugin.getConfig().getBoolean("update_message"));
        config_BONUS.add(plugin.getConfig().getString("bonus_message"));
        config_PREFIX.add(plugin.getConfig().getString("prefix.prefix"));
        config_PREFIX_BOOLEAN.put(1, plugin.getConfig().getBoolean("prefix.enable"));
        config_TOKENS_BUY.add(plugin.getConfig().getInt("tokens.buy_price"));
        config_TOKENS_SELL.add(plugin.getConfig().getInt("tokens.sell_price"));

        for (String key : plugin.getConfig().getConfigurationSection("sign").getKeys(false)) {
            config_SIGN.add(plugin.getConfig().getString("sign."+key));
        }

        config_SQL_HOST.add(plugin.getConfig().getString("mySQL.host"));
        config_SQL_PORT.add(plugin.getConfig().getInt("mySQL.port"));
        config_SQL_USERNAME.add(plugin.getConfig().getString("mySQL.username"));
        config_SQL_PASSWORD.add(plugin.getConfig().getString("mySQL.password"));
        config_SQL_DATABASE.add(plugin.getConfig().getString("mySQL.database"));
        config_SQL_TABLE.add(plugin.getConfig().getString("mySQL.table"));

        Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.green + "loaded the config to the hashSet");
    }


    public static double config_version() {
        return Double.parseDouble(config_CONFIG_VERSION.toString().replace("[", "").replace("]", ""));
    }

    public static String database() {
        return config_DATABASE.toString().replace("[", "").replace("]", "");
    }

    public static boolean update_message() {
        return config_UPDATE_MESSAGE.get(1);
    }

    public static String bonus_message() {
        return config_BONUS.toString().replace("[", "").replace("]", "");
    }

    public static String prefix() {
        return config_PREFIX.toString().replace("[", "").replace("]", "");
    }

    public static Boolean prefix_boolean() {
        return config_PREFIX_BOOLEAN.get(1);
    }

    public static int tokens_buy() {
        return Integer.parseInt(config_TOKENS_BUY.toString().replace("[", "").replace("]", ""));
    }

    public static int token_sell() {
        return Integer.parseInt(config_TOKENS_SELL.toString().replace("[", "").replace("]", ""));
    }

    public static ArrayList<String> sign() {
        return config_SIGN;
    }

    public static String host() {
        return config_SQL_HOST.toString().replace("[", "").replace("]", "");
    }

    public static int port() {
        return Integer.parseInt(config_SQL_PORT.toString().replace("[", "").replace("]", ""));
    }

    public static String username() {
        return config_SQL_USERNAME.toString().replace("[", "").replace("]", "");
    }

    public static String passwod() {
        return config_SQL_PASSWORD.toString().replace("[", "").replace("]", "");
    }

    public static String table() {
        return config_SQL_TABLE.toString().replace("[", "").replace("]", "");
    }

    public static String db() {
        return config_SQL_DATABASE.toString().replace("[", "").replace("]", "");
    }

}
