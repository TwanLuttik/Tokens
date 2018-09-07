package com.twanl.tokens.sql;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.commands.Commands;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Twan
 */
public class SQLlib implements Listener {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);


    //TODO: set an amount of token for the player
    public void setTokens(UUID uuid, int amount) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', TOKENS=" + amount + " WHERE UUID='" + uuid + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //TODO: remove tokens from a player
    public void removeTokens(UUID uuid, int amount) {
        try {
            int updatedValue = getTokens(uuid) - amount;
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', TOKENS=" + updatedValue + " WHERE UUID='" + uuid + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //TODO: add tokens to a player
    public void addTokens(UUID uuid, int amount) {
        try {
            int updatedValue = getTokens(uuid) + amount;
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', TOKENS=" + updatedValue + " WHERE UUID='" + uuid + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //TODO: retreive tokens from a player (return an int)
    public int getTokens(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID='" + uuid + "';");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int i = rs.getInt("TOKENS");
                rs.close();
                return i;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    //TODO: add the player to the database
    public void addPlayer(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.table + " (UUID, TOKENS) VALUES (?,?)");
            statement.setString(1, uuid.toString());
            statement.setInt(2, 0);
            statement.executeUpdate();

//            Player p = (Player) Bukkit.getOfflinePlayer(uuid);
//            Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.green + p.getName() + " has been added to the database!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //TODO: check if player has account (return an boolean)
    public boolean hasAccount(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
//                Bukkit.getConsoleSender().sendMessage(Strings.green + "player has been found!");
                return true;
            }
//            Bukkit.getConsoleSender().sendMessage(Strings.red + "player has not been found!");
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    //TODO: create table if not exist
    public void createTable() {
        if (!tableExist()) {
            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + plugin.table + " (UUID varchar(255), TOKENS INT);");
                statement.executeUpdate();
                Bukkit.getConsoleSender().sendMessage(Strings.logName + "created a table: " + Strings.green + plugin.table);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    //TODO: check if the table exist (return boolean)
    private boolean tableExist() {
        try {
            DatabaseMetaData dbm = plugin.getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, plugin.table, null);
            if (tables.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    //TODO: get all the information from the table and put it in an HashMap
    public void getAllRowstoHashMap() {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("UUID"));
                int tokens = rs.getInt("TOKENS");
                Commands.map.put(uuid, tokens);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //TODO: !!
//		on start for loop the sql and put all the data into a hashmap and after that delete the table and create the new table and put all the data from the hashmaps to the sql table(NEW)
//
//		1. on server startup check if sql enabled
//				IF NOT - check if table exist
//						IF - table exist than delete the table
//						IF NOT - ..
//
//		2. for loop the sql player data and save into hasmaps or a temporarily file
//		3. delete the currunt table and create the new talbe with the new column
//		4. put all the data from the hasmap or file and put them into the sql table

}



