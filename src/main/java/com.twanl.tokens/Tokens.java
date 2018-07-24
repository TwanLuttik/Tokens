package com.twanl.tokens;

import com.twanl.tokens.NMS.VersionHandler;
import com.twanl.tokens.NMS.v1_10.v1_10_R1;
import com.twanl.tokens.NMS.v1_11.v1_11_R1;
import com.twanl.tokens.NMS.v1_12.v1_12_R1;
import com.twanl.tokens.NMS.v1_13.v1_13_R1;
import com.twanl.tokens.NMS.v1_8.v1_8_R1;
import com.twanl.tokens.NMS.v1_8.v1_8_R2;
import com.twanl.tokens.NMS.v1_8.v1_8_R3;
import com.twanl.tokens.NMS.v1_9.v1_9_R1;
import com.twanl.tokens.NMS.v1_9.v1_9_R2;
import com.twanl.tokens.api.TokensAPI;
import com.twanl.tokens.commands.Commands;
import com.twanl.tokens.events.JoinEvent;
import com.twanl.tokens.events.SignEvent;
import com.twanl.tokens.items.TokenItem;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Metrics;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.UpdateChecker;
import com.twanl.tokenshop.TokenShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Twan on 3/22/2018.
 **/

public class Tokens extends JavaPlugin {

    //TODO: SQL Support
    //TODO: add a cooldown when clicking on the sign


    protected PluginDescriptionFile pdfFile = getDescription();
    private final String PluginVersionOn = Strings.green + "(" + pdfFile.getVersion() + ")";
    private final String PluginVersionOff = Strings.red + "(" + pdfFile.getVersion() + ")";
    private UpdateChecker checker;
    public VersionHandler nms;

    public static Economy economy;
    private ConfigManager cfgM;

    @SuppressWarnings("unused")
    private TokenShop tshopApi = (TokenShop) Bukkit.getServer().getPluginManager().getPlugin("TokenShop");



    public void onEnable() {

        getServerVersion();

        //noinspection unused
        Metrics metrics = new Metrics(this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + "++ Vault HOOKED");
            setupEconomy();
        } else {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + Strings.red + "VAULT NOT DETECTED, Some commands won't work.");
        }

        if (getServer().getPluginManager().getPlugin("TokenShop") != null) {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + "++ TokenShop HOOKED.");
        } else {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + Strings.red + "TokenShop is not found. /token shop is disabled");
        }


        checker = new UpdateChecker(this);
        if (checker.isConnected()) {
            if (checker.hasUpdate()) {
                getServer().getConsoleSender().sendMessage(Strings.green + "");
                getServer().getConsoleSender().sendMessage(Strings.green + "------------------------");
                getServer().getConsoleSender().sendMessage(Strings.red + "Tokens is outdated!");
                getServer().getConsoleSender().sendMessage(Strings.white + "Newest version: " + checker.getLatestVersion());
                getServer().getConsoleSender().sendMessage(Strings.white + "Your version: " + Strings.green + getDescription().getVersion());
                getServer().getConsoleSender().sendMessage("Please download the new version at https://www.spigotmc.org/resources/tokens.53944/");
                getServer().getConsoleSender().sendMessage(Strings.green + "------------------------");
                getServer().getConsoleSender().sendMessage(Strings.green + "");
            } else {
                getServer().getConsoleSender().sendMessage(Strings.green + "");
                getServer().getConsoleSender().sendMessage(Strings.green + "---------------------------------");
                getServer().getConsoleSender().sendMessage(Strings.green + "Tokens is up to date.");
                getServer().getConsoleSender().sendMessage(Strings.green + "---------------------------------");
                getServer().getConsoleSender().sendMessage(Strings.green + "");
            }
        }

        Load();
        loadPlayers();
        Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.green + "Has been enabled " + PluginVersionOn);

    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.red + "Has been disabled " + PluginVersionOff);
        loadPlayers();
    }

    public void Load() {
        // Register listeners
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new TokenItem(), this);
        getServer().getPluginManager().registerEvents(new SignEvent(), this);

        // Register Command Class
        Commands commands = new Commands();
        getCommand("tokens").setExecutor(commands);

        // Config File


        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveConfig();
            return;
        }


        // check if path is set else create an new file
        if (!getConfig().isSet("ConfigVersion")) {

            File configFile = new File(getDataFolder(), "config.yml");
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getConfig().options().copyDefaults(true);
            saveConfig();

            /*
            getConfig().options().copyDefaults(true);
            saveConfig();
            */


            return;
        } else {
            // if configversion is not match, than back-up the file and create the updated file
            double a = getConfig().getDouble("ConfigVersion");
            if (a != 1.0) {
                File configFile = new File(getDataFolder(), "config.yml");
                File file2 = new File(getDataFolder(), "config_old.yml");

                if (configFile.exists()) {
                    Bukkit.getConsoleSender().sendMessage(Strings.green + "Succsesfully created a new config file!");

                    configFile.renameTo(file2);

                    getConfig().options().copyDefaults(true);
                    saveDefaultConfig();
                    reloadConfig();

                }

            } else {
                // just the default reload
                getConfig().options().copyDefaults(true);
                saveDefaultConfig();
                reloadConfig();
            }
        }


    }

    public void loadPlayers() {
        cfgM = new ConfigManager();
        cfgM.setup();
        cfgM.savePlayers();
        cfgM.reloadplayers();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            //noinspection RedundantCast
            economy = (Economy)economyProvider.getProvider();
        }
        return economy != null;
    }


    private void getServerVersion() {
        String a = getServer().getClass().getPackage().getName();
        String version = a.substring(a.lastIndexOf('.') + 1);

        // Check
        if (version.equalsIgnoreCase("v1_8_R1")) {
            nms = new v1_8_R1();
        } else if (version.equalsIgnoreCase("v1_8_R2")) {
            nms = new v1_8_R2();
        } else if (version.equalsIgnoreCase("v1_8_R3")) {
            nms = new v1_8_R3();
        } else if (version.equalsIgnoreCase("v1_9_R1")) {
            nms = new v1_9_R1();
        } else if (version.equalsIgnoreCase("v1_9_R2")) {
            nms = new v1_9_R2();
        } else if (version.equalsIgnoreCase("v1_10_R1")) {
            nms = new v1_10_R1();
        } else if (version.equalsIgnoreCase("v1_11_R1")) {
            nms = new v1_11_R1();
        } else if (version.equalsIgnoreCase("v1_12_R1")) {
            nms = new v1_12_R1();
        } else if (version.equalsIgnoreCase("v1_13_R1")) {
            nms = new v1_13_R1();
        } else {
            getServer().getConsoleSender().sendMessage(Strings.logName + Strings.red + "This plugin wil not work properly with version " + version);
        }
    }


}
