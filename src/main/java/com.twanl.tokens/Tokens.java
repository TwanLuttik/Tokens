package com.twanl.tokens;

import com.twanl.tokens.NMS.VersionHandler;
import com.twanl.tokens.NMS.v1_12.v1_12_R1;
import com.twanl.tokens.NMS.v1_11.v1_11_R1;
import com.twanl.tokens.NMS.v1_10.v1_10_R1;
import com.twanl.tokens.NMS.v1_9.v1_9_R2;
import com.twanl.tokens.NMS.v1_9.v1_9_R1;
import com.twanl.tokens.NMS.v1_8.v1_8_R3;
import com.twanl.tokens.NMS.v1_8.v1_8_R2;
import com.twanl.tokens.NMS.v1_8.v1_8_R1;
import com.twanl.tokens.api.TokensAPI;
import com.twanl.tokens.commands.Commands;
import com.twanl.tokens.events.JoinEvent;
import com.twanl.tokens.events.SignEvent;
import com.twanl.tokens.items.TokenItem;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Metrics;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.UpdateChecker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.logging.Level;

/**
 * Created by Twan on 3/22/2018.
 **/

public class Tokens extends JavaPlugin {

    //TODO: SQL Support
    //TODO: SubCommands in a other class
    //TODO: make TabCompletion better
    //TODO: Top 10 command


    protected PluginDescriptionFile pdfFile = getDescription();
    private final String PluginVersionOn = Strings.green + "(" + pdfFile.getVersion() + ")";
    private final String PluginVersionOff = Strings.red + "(" + pdfFile.getVersion() + ")";
    private UpdateChecker checker;
    public VersionHandler nms;

    public static Economy economy;
    public ConfigManager cfgM;

    public TokensAPI tokensApi;



    public void onEnable() {


        getServerVersion();
        // Api for other DEV
        tokensApi = new TokensAPI();
        Metrics metrics = new Metrics(this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + "DETECTED VAULT.");
            setupEconomy();
        } else {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + Strings.red + "VAULT NOT DETECTED, Some commands won't work.");
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

        //LoadConfig
        getConfig().options().copyDefaults(true);
        saveConfig();

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
        } else {
            getServer().getConsoleSender().sendMessage(Strings.red + "This plugin wil not work properly with version" + version);
        }
    }

}
