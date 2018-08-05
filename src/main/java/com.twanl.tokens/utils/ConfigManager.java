package com.twanl.tokens.utils;

import com.twanl.tokens.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;

/**
 * Created by Twan on 3/22/2018.
 **/

public class ConfigManager {


    private Tokens plugin = Tokens.getPlugin(Tokens.class);

    //Files & Config Files
    public static FileConfiguration playersC;
    public static File playersF;
    public static FileConfiguration configC;
    public static File configF;
    public static FileConfiguration shopC;
    public static File shopF;
    //--------------------


    public void setup() {
        playersF = new File(plugin.getDataFolder(), "players.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!playersF.exists()) {
            try {
                playersF.createNewFile();
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The players.yml file has been created");
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Could not create the players.yml file");
            }
        }
        playersC = YamlConfiguration.loadConfiguration(playersF);


        configF = new File(plugin.getDataFolder(), "config.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!configF.exists()) {
            try {
                configF.createNewFile();
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The config.yml file has been created");
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Could not create the config.yml file");
            }
        }
        configC = YamlConfiguration.loadConfiguration(configF);


        shopF = new File(plugin.getDataFolder(), "shop.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!shopF.exists()) {
            try {
                shopF.createNewFile();
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The shop.yml file has been created");
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Could not create the shop.yml file");
            }
        }
        shopC = YamlConfiguration.loadConfiguration(shopF);


    }


    public FileConfiguration getPlayers() {
        return playersC;
    }

    public FileConfiguration getShop() {
        return shopC;
    }

    public FileConfiguration getConfig() {
        return configC;
    }


    public void savePlayers() {
        playersF = new File(plugin.getDataFolder(), "players.yml");
        try {
            playersC.save(playersF);
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The players.yml file has been saved");
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Could not save the players.yml file");
        }
    }

    public void saveShop() {
        shopF = new File(plugin.getDataFolder(), "shop.yml");
        try {
            shopC.save(shopF);
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The shop.yml file has been saved");
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Could not save the shop.yml file");
        }
    }

    public void saveConfig() {
        configF = new File(plugin.getDataFolder(), "config.yml");
        try {
            configC.save(configF);
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The config.yml file has been saved");
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Could not save the config.yml file");
        }
    }


    public void reloadplayers() {
        playersC = YamlConfiguration.loadConfiguration(playersF);
        Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The players.yml file has been reloaded");
    }

    public void reloadShop() {
        shopC = YamlConfiguration.loadConfiguration(shopF);
        Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The shop.yml file has been reloaded");
    }

    public void reloadConfig() {
        configC = YamlConfiguration.loadConfiguration(configF);
        Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The config.yml file has been reloaded");
    }


}
