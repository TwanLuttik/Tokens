package com.twanl.tokens;

import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Tokens extends JavaPlugin {


    private UpdateChecker checker;
    protected PluginDescriptionFile pdfFile = getDescription();
    private final String PluginVersionOn = ChatColor.GREEN + "(" + pdfFile.getVersion() + ")";
    private final String PluginVersionOff = ChatColor.RED + "(" + pdfFile.getVersion() + ")";


    public void onEnable() {


        /*
        this.checker = new UpdateChecker(this);
        if (this.checker.isConnected()) {
            if (this.checker.hasUpdate()) {
                getServer().getConsoleSender().sendMessage(Strings.green + "");
                getServer().getConsoleSender().sendMessage(Strings.green + "------------------------");
                getServer().getConsoleSender().sendMessage(Strings.red + "PlayerCount is outdated!");
                getServer().getConsoleSender().sendMessage(Strings.white + "Newest version: " + this.checker.getLatestVersion());
                getServer().getConsoleSender().sendMessage(Strings.white + "Your version: " + Strings.green + this.getDescription().getVersion());
                getServer().getConsoleSender().sendMessage("Please download the new version at https://www.spigotmc.org/resources/playercount.52758/download?version=208548");
                getServer().getConsoleSender().sendMessage(Strings.green + "------------------------");
                getServer().getConsoleSender().sendMessage(Strings.green + "");
            } else {
                getServer().getConsoleSender().sendMessage(Strings.green + "");
                getServer().getConsoleSender().sendMessage(Strings.green + "---------------------------------");
                getServer().getConsoleSender().sendMessage(Strings.green + "PlayerCount is up to date.");
                getServer().getConsoleSender().sendMessage(Strings.green + "---------------------------------");
                getServer().getConsoleSender().sendMessage(Strings.green + "");
            }
        }
        */
        Load();
        Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.green + "Has been enabled " + PluginVersionOn);

    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.red + "Has been disabled " + PluginVersionOff);

    }

    public void Load() {

        // Register Command Class
        Commands commands = new Commands();
        getCommand("tokens").setExecutor(commands);

    }

}
