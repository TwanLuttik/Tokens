package com.twanl.tokens.sql;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.commands.Commands;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.sql.*;
import java.util.*;

/**
 * @author Twan
 */
public class SQLlib implements Listener {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);


    // Set an amount of token for the player
    public void setTokens(UUID uuid, int amount) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', TOKENS=" + amount + " WHERE UUID='" + uuid + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Remove tokens from a player
    public void removeTokens(UUID uuid, int amount) {
        try {
            int updatedValue = getTokens(uuid) - amount;
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', TOKENS=" + updatedValue + " WHERE UUID='" + uuid + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Add tokens to a player
    public void addTokens(UUID uuid, int amount) {
        try {
            int updatedValue = getTokens(uuid) + amount;
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', TOKENS=" + updatedValue + " WHERE UUID='" + uuid + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Retreive tokens from a player (return an int)
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


    // Add the player to the database
    public void addPlayer(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.table + " (UUID, TOKENS, BANK_ID) VALUES (?,?,?)");
            statement.setString(1, uuid.toString());
            statement.setInt(2, 0);
            statement.setInt(3, 0);
            statement.executeUpdate();

//            Player p = (Player) Bukkit.getOfflinePlayer(uuid);
//            Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.green + p.getName() + " has been added to the database!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Check if player has account (return an boolean)
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


    // Create table if not exist
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

    // Check if the table exist (return boolean)
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


    // Get all the information from the table and put it in an HashMap
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


    //TODO: !!Importent
//		on start for loop the sql and put all the data into a hashmap and after that delete the table and create the new table and put all the data from the hashmaps to the sql table(NEW)
//
//
//		!! This wil be for existing servers:
//		when sql is enabled
//			check if column BANK_ID
//				IF EXIST - than do nothing becase everything is fine
//				ELSE - *
//
//		*
//		for loop the sql player data and save into hasmaps or a temporarily file
//		delete the currunt table and create the new talbe with the new column
// 		put all the data from the hasmap or file and put them into the sql table


    //check if column exist in sql table
    public boolean checkColoumnExist() {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table);
            ResultSet rs = statement.executeQuery();

            String columnName = "BANK_ID";
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            for (int x = 1; x <= columns; x++) {
                if (columnName.equals(rsmd.getColumnName(x))) {
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // this wil create the new updated table with the bank id
    public void createUpdatedTable() {
        if (!tableExist()) {
            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + plugin.table + " (UUID varchar(255), TOKENS INT, BANK_ID INT);");
                statement.executeUpdate();
                Bukkit.getConsoleSender().sendMessage(Strings.logName + "created a table: " + Strings.green + plugin.table);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    // this will update the current table from the server to the new table, without any data loses
    private HashMap<UUID, Integer> a = new HashMap<>();

    public void updateTableV1() {

        // for loop the sql and put it into hasmaps
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT *FROM " + plugin.table);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                UUID b = UUID.fromString(rs.getString("UUID"));
                int c = rs.getInt("TOKENS");
                a.put(b, c);
            }

            // remove the current table
            statement.execute("DROP TABLE " + plugin.table);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // create the updated table with the BANK_ID
        createUpdatedTable();

        // put all the information from the hasmaps to the sql table
        for (Object o : a.keySet()) {
//            Bukkit.getConsoleSender().sendMessage(o + "    " + String.valueOf(a.get(o)));
        }

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.table + " (UUID, TOKENS, BANK_ID) VALUES (?,?,?)");
            for (Object o : a.keySet()) {
                statement.setString(1, o.toString());
                statement.setInt(2, a.get(o));
                statement.setInt(3, 0);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Get the player registered number of the bank
    public int getUserBankID(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID='" + uuid + "';");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int i = rs.getInt("BANK_ID");
                rs.close();
                return i;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    // get the balance of the bank
    public int getBankBalance(int BankID) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM `bank_data` WHERE BANK_ID='" + BankID + "';");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int i = rs.getInt("BALANCE");
                rs.close();
                return i;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //
    public String getBankOwner(int BankID) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM `bank_data` WHERE BANK_ID='" + BankID + "';");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String a = rs.getString("OWNER");
                Bukkit.getConsoleSender().sendMessage(Strings.red + "[DEBUg]  " + Strings.white + a);
                rs.close();
                return a;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Get the owner UUID from the bank where the player is registered
    public UUID getBankOwnerUUID(UUID uuid) {
        int i = getUserBankID(uuid);
        String[] a = getBankOwner(i).split(" ");
        return UUID.fromString(a[0]);
    }

    // Get the owner playername from the bank where the player is registered
    public String getBankOwnerName(UUID uuid) {
        int i = getUserBankID(uuid);
        String[] a = getBankOwner(i).split(" ");
        return a[1];
    }

    public void bankBalanceDeposit(int bankID, int amount) {
        try {
            int updatedValue = getBankBalance(bankID) + amount;
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE `bank_data` SET BANK_ID = '" + bankID + "', BALANCE=" + updatedValue + " WHERE BANK_ID='" + bankID + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void bankBalanceWithdraw(int bankID, int amount) {
        try {
            int updatedValue = getBankBalance(bankID) - amount;
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE `bank_data` SET BANK_ID = '" + bankID + "', BALANCE=" + updatedValue + " WHERE BANK_ID='" + bankID + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void createBank(UUID uuid, String player) {

        int i = nextBankID();
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO bank_data (BANK_ID, OWNER, BALANCE, USER) VALUES (?,?,?,?)");
            statement.setInt(1, i);
            statement.setString(2, uuid + " " + player);
            statement.setInt(3, 0);
            statement.setString(4, "");
            statement.executeUpdate();
            bankIDS.clear();

            statement.close();

            PreparedStatement statement1 = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', BANK_ID='" + i + "' WHERE UUID= '" + uuid + "';");
            statement1.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean bankIsEmpty() {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM bank_data");
            ResultSet rs = statement.executeQuery();

            return !rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void bankUserAdd(UUID uuid, UUID subUser) {

//        String[] a = fieldBankUSER(getUserBankID(uuid)).split(" ");
//        for (int i = 0; i < 50 ; i++) {
//
//            // Check if its not null
//            if (a[i] == null) {
//                break;
//            }
//
//
//
//        }
        int i = getUserBankID(uuid);
        String a = fieldBankUSER(i) + subUser + " @";
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE bank_data SET BANK_ID=" + i + ", USER='" + a + "' WHERE BANK_ID=" + i + ";");

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

//        String[] b = subUser.split(" ");

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + subUser + "', BANK_ID=" + i +" WHERE UUID= '" + subUser + "';");
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void bankUserRemove(UUID uuid) {
        String a = fieldBankUSER(getUserBankID(uuid)).replace(uuid + " @", "");

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE bank_data SET USER='" + a + "' WHERE BANK_ID=" + getUserBankID(uuid) +  ";");
            statement.executeUpdate();
            statement.close();

            PreparedStatement statement1 = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET UUID = '" + uuid + "', BANK_ID=0 WHERE UUID= '" + uuid + "';");
            statement1.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void bankDelete(UUID uuid) {
        try {
            // Get the members and change the BANK_ID to 0 for each member
            int bankID = getUserBankID(uuid);
            int B = fieldBankUSER(bankID).split("@").length;

            String[] a = fieldBankUSER(bankID).split(" ");
            for (int i = 0; i < B; i++) {
                userUpdateBANKID(UUID.fromString(a[i].replace("@", "")), 0);
            }

            int c = getUserBankID(uuid);
            // Get the owner and change his BANK_ID to 0
            userUpdateBANKID(getBankOwnerUUID(uuid), 0);

            // Delete the record of the bank
            PreparedStatement statement = plugin.getConnection().prepareStatement("DELETE FROM bank_data WHERE BANK_ID='" + c + "';");
            statement.executeUpdate();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // This will update the BANK_ID of the player with a int
    public void userUpdateBANKID(UUID uuid, int ID) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET BANK_ID='" + ID + "' WHERE UUID='" + uuid + "';");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    // Get the field USER of the bank from ID
    public String fieldBankUSER(int bankID) {
        //TODO: replace the int into uuid and get the user BANK_ID in this methode
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM bank_data WHERE BANK_ID='" + bankID + "';");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String a = rs.getString("USER");
                rs.close();
                if (a == null && a.isEmpty()) {
                    return "";
                }
                return a;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    // Get the next following BANK_ID (it will check also for the skipped number from deleted banks)
    private List<Integer> bankIDS = new ArrayList<>();

    public int nextBankID() {

        // Initialize
        int i = 1;

        // If the file is empty it will start at 1
        if (bankIsEmpty()) {
            return 1;
        }

        // get all the BANK_ID and put into a List
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM bank_data");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                bankIDS.add(rs.getInt("BANK_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // sort out the numbers from low -> high
        Collections.sort(bankIDS);

        // get the next lowest number (it will search if a bank has deleted)
        for (Integer bankID : bankIDS) {
            if (bankID == i) {
                i++;
            }
        }
        return i;
    }

    public boolean bankHasUsers(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM bank_data WHERE BANK_ID='" + getUserBankID(uuid) + "';");
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String a = rs.getString("USER");
                if (a.isEmpty()) {
                    return false;
                }
                if (a.contains("null")) {
                    return false;
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}



