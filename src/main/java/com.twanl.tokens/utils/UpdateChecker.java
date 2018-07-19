package com.twanl.tokens.utils;

import com.twanl.tokens.Tokens;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Twan on 3/22/2018.
 **/

public class UpdateChecker {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public String version;


    public UpdateChecker(Tokens plugin) {
        this.plugin = plugin;
        this.version = getLatestVersion();
    }

    public String getLatestVersion() {
        try {
            int resource = 53944;
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resource).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            String key = "key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + resource;
            con.getOutputStream().write(key.getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (version.length() <= 7) {
                return version;
            }
        } catch (Exception ex) {
            System.out.println("---------------------------------");
            plugin.getLogger().info("Failed to check for a update on spigot.");
            System.out.println("---------------------------------");
        }
        return null;

    }

    public boolean isConnected() {
        return this.version != null;
    }

    public boolean hasUpdate() {
        return !this.version.equals(this.plugin.getDescription().getVersion());
    }
}