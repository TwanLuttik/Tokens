package com.twanl.tokens;

import com.twanl.tokens.events.JoinEvent;
import com.twanl.tokens.items.TokenItem;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Metrics;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.UpdateChecker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public class Tokens extends JavaPlugin {

    //TODO: SQL Support
    //TODO: Vault Support
    //TODO: Getting the 2 commands working
    //TODO: Change the reload command for better checking for file or path or someting.
    //TODO: API Support so thath other Dev can use this Plugin


    private static final Logger log = Logger.getLogger("Minecraft");
    protected PluginDescriptionFile pdfFile = getDescription();
    private final String PluginVersionOn = ChatColor.GREEN + "(" + pdfFile.getVersion() + ")";
    private final String PluginVersionOff = ChatColor.RED + "(" + pdfFile.getVersion() + ")";
    private UpdateChecker checker;

    public ConfigManager cfgM;
    //public TokensAPI tokensApi;



    public void onEnable() {
        //tokensApi = new TokensAPI();
        Metrics metrics = new Metrics(this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + "Detected Vault.");
            setupEconomy();
        } else {
            getServer().getConsoleSender().sendMessage(Strings.green + Strings.logName + Strings.red + "VAULT NOT DETECTED, Some commands won't work.");
        }


/*
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            //getServer().getPluginManager().disablePlugin(this);
            return;
        }
        */




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





    public static Economy economy;

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = (Economy)economyProvider.getProvider();
        }
        return economy != null;
    }


}
