package com.twanl.tokens;

import com.twanl.tokens.api.TokensAPI;
import com.twanl.tokens.commands.Commands;
import com.twanl.tokens.events.JoinEvent;
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

/**
 * Created by Twan on 3/22/2018.
 **/

public class Tokens extends JavaPlugin {

    //TODO: SQL Support
    //TODO: OfflinePlayer Support (uuid)
    //TODO: SubCommands in a other class
    //TODO: make TabCompletion better
    //TODO: make the config file with comments


    protected PluginDescriptionFile pdfFile = getDescription();
    private final String PluginVersionOn = Strings.green + "(" + pdfFile.getVersion() + ")";
    private final String PluginVersionOff = Strings.red + "(" + pdfFile.getVersion() + ")";
    private UpdateChecker checker;

    public static Economy economy;
    public ConfigManager cfgM;

    public TokensAPI tokensApi;




    public void onEnable() {
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


}
