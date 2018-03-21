package com.twanl.tokens.utils;

import com.twanl.tokens.Tokens;
import org.bukkit.Bukkit;
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

    }


    public FileConfiguration getPlayers() {
        return playersC;

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


    public void reloadplayers() {
        playersC = YamlConfiguration.loadConfiguration(playersF);
        Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The players.yml file has been reloaded");

    }




    /*
    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public static File playersFile;
    public static File playerFolder;

    //Files & Config Files
    public FileConfiguration playersC;

    public File playersF;

    //--------------------




    public boolean setup() {
        /*
        playerFolder = new File(plugin.getDataFolder(), "players");
        playersFile = new File(playerFolder, uuid.toString());

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

        return false;
    }



    public FileConfiguration getPlayers() {
        return playersC;

    }


    public void savePlayers(UUID uuid) {

        playerFolder = new File(plugin.getDataFolder(), "players.yml");
        playersF = new File(playerFolder, uuid.toString());


        try {
            playersC.save(playersF);
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The players.yml file has been saved");

        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Could not save the players.yml file");

        }
    }


    public void reloadplayers() {
        playersC = YamlConfiguration.loadConfiguration(playersF);
        Bukkit.getServer().getConsoleSender().sendMessage(Strings.green + "The players.yml file has been reloaded");

    }

*/



}
