package com.twanl.tokens.api;

import com.twanl.tokens.Functions;
import com.twanl.tokens.Tokens;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class TokensAPI {

    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    public static File playersFile;
    public static File playerFolder;
    private ConfigManager cfgM;
    //public ConfigManager cfgM;

    public static Functions F;


    public Boolean CheckPlayer(UUID uuid, Player p) {

        //playerFolder = new File(plugin.getDataFolder(), "players");
        playersFile = new File(plugin.getDataFolder(), "players.yml");


        if (!playersFile.exists()) {
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "players file not found, creating a new one.");

            try {
                playersFile.mkdir();
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "successful created a new players file.");
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.red + "Failed to create an new file.");
            }
        }

        cfgM = new ConfigManager();
        cfgM.setup();
        cfgM.getPlayers().set(uuid.toString() + ".name", F.getName(String.valueOf(uuid)));



/*
        if (!cfgM.getPlayers().contains(String.valueOf(uuid))) {
            p.sendMessage(Strings.redI + "Player not found in database.\n" + Strings.white + "Adding the player into the database.");
            try {
                cfgM.getPlayers().set(uuid.toString() + ".", " s");
                p.sendMessage(Strings.green + "Player successful added.");
            } catch (IOException e) {
                p.sendMessage(Strings.red + "Failed to add the player to the database.");
                e.printStackTrace();
            }
        }
        */

/*
        if (!playersFile.exists()) {
            p.sendMessage(Strings.redI + "Player not found in database.\n" + Strings.white + "Adding the player into the database.");
            try {
                playersFile.createNewFile();
                p.sendMessage(Strings.green + "Player successful added.");
            } catch (IOException e) {
                p.sendMessage(Strings.red + "Failed to add the player to the database.");
                e.printStackTrace();
            }
        }
        */
        return null;
    }



    public void setTokens(UUID uuid, Player p, int tokens) {

        if (CheckPlayer(uuid, p)) {
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.goldB + "test");
        }

        /*
        cfgM.setup();
        cfgM.getPlayers().set("tokens", tokens);
        cfgM.savePlayers(UUID.fromString(uuid.toString()));
*/


    }


    public static Boolean isPlayerOnline(Player p) { //We put Player p so that way we can
        //access their player of choice later


        if (p.isOnline()){
            return true; //Say true if they are on
        } else {
            return false; //Say false if they are offline
        }
    }



    public void setcoins1(UUID uuid, int tokens) {




    }





/*
    public void message(Player p) {
        p.sendMessage(Strings.goldB + "This is a message.");

    }

        /*
        playersFile = new File(plugin.getDataFolder(), String.valueOf(uuid));

        if (!playersFile.exists()) {
            p.sendMessage(Strings.green + "Player doesn't yet exist, trying creating a new one!");
            try {
                playersFile.createNewFile();
                p.sendMessage(Strings.green + "Player doesn't yet exist, trying creating a new one!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }






    public synchronized void checkPlayer1 (UUID uuid) {
        F = new Functions(plugin);
        cfgM = new ConfigManager();
        cfgM.setup();


        if (!cfgM.getPlayers().contains("uuid." + uuid)) {
            cfgM.getPlayers().set("uuid." + uuid + "playername", F.getName(String.valueOf(uuid)));
            cfgM.savePlayers();
            return;

        }
    }


/*
    public void 111setTokens(String playerUUID, int tokens) {
        if (tokens < 0) {
            return;
        }
        this.datastore.setCoins(playerUUID, Integer.valueOf(tokens));
    }

    public boolean setTokens(String playerUUID, Integer tokens) {

        cfgM.setup();
        boolean path1 = cfgM.getPlayers().contains("uuid." + playerUUID);

        if (!path1) {
            return false;
        }

        return path1;
    }
    */


}
