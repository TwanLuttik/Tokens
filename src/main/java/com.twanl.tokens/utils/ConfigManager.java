package com.twanl.tokens.utils;

import com.twanl.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Twan on 3/22/2018.
 **/

public class ConfigManager {


    private Tokens plugin = Tokens.getPlugin(Tokens.class);

    //Files & Config Files
    public static FileConfiguration playersC;
    public static File playersF;

    public static FileConfiguration shopC;
    public static File shopF;

    public static FileConfiguration bankC;
    public static File bankF;
    //--------------------


    public void setup() {
        playersF = new File(plugin.getDataFolder(), "players.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!playersF.exists()) {
            try {
                playersF.createNewFile();
            } catch (IOException e) {
            }
        }
        playersC = YamlConfiguration.loadConfiguration(playersF);


        shopF = new File(plugin.getDataFolder(), "shop.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!shopF.exists()) {
            try {
                shopF.createNewFile();
            } catch (IOException e) {
            }
        }
        shopC = YamlConfiguration.loadConfiguration(shopF);

        bankF = new File(plugin.getDataFolder(), "bank.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!bankF.exists()) {
            try {
                bankF.createNewFile();
            } catch (IOException e) {
            }
        }
        bankC = YamlConfiguration.loadConfiguration(bankF);


    }


    public FileConfiguration getPlayers() {
        return playersC;
    }

    public FileConfiguration getShop() {
        return shopC;
    }

    public FileConfiguration getBank() {
        return bankC;
    }



    public void savePlayers() {
        playersF = new File(plugin.getDataFolder(), "players.yml");
        try {
            playersC.save(playersF);
        } catch (IOException e) {
        }
    }

    public void saveShop() {
        shopF = new File(plugin.getDataFolder(), "shop.yml");
        try {
            shopC.save(shopF);
        } catch (IOException e) {
        }
    }

    public void saveBank() {
        bankF = new File(plugin.getDataFolder(), "bank.yml");
        try {
            bankC.save(bankF);
        } catch (IOException e) {
        }
    }




    public void reloadplayers() {
        playersC = YamlConfiguration.loadConfiguration(playersF);
    }

    public void reloadShop() {
        shopC = YamlConfiguration.loadConfiguration(shopF);
    }

    public void reloadBank() {
        bankC = YamlConfiguration.loadConfiguration(bankF);
    }



}
