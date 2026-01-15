package com.twanluttik.tokens;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileParser {
    final private Plugin plugin;
    public File configFile;

    public FileParser(Plugin plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }

    public FileConfiguration loadFile(String fileName) {
        configFile = new File(plugin.getDataFolder(), fileName);
        if(!configFile.exists()) {
            plugin.saveResource((fileName), false);
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }
//    Reload function
}
