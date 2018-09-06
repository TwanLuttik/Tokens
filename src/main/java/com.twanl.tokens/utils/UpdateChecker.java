package com.twanl.tokens.utils;

import com.twanl.tokens.Tokens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Twan on 3/22/2018.
 **/

public class UpdateChecker {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);


    // get the updated version from the spigotMC API
    public String getUpdatedVersion() {
        StringBuilder sb = new StringBuilder();

        try {
            int version = 53944;
            URLConnection connection = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + version).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("---------------------------------");
            plugin.getLogger().info("Failed to check for a update on spigot.");
            System.out.println("---------------------------------");
        }
        return sb.toString();
    }

    // check if the plugin is in a pre releaswe
    public boolean isPreRelease() {
        return plugin.getDescription().getVersion().contains("pre");
    }

    // check if the plugin has a up[update
    public boolean hasUpdate() {
        return !plugin.getDescription().getVersion().equals(getUpdatedVersion());
    }
}