package com.twanl.tokens.lib;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.sql.SQLlib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Functions;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.loadManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Lib {

    private ConfigManager config = new ConfigManager();

    public String transactionPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionPlayer = config.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.player");

        if (transactionPlayer == null) {
            return "No player";
        }
        return String.valueOf(transactionPlayer);
    }

    public String transactionDate(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionDate = config.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.date");

        if (transactionDate == null) {
            return "No data";
        }
        return String.valueOf(transactionDate);
    }

    public String transactionAmount(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionDate = config.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.amount");

        if (transactionDate == null) {
            return "No value";
        }
        return String.valueOf(transactionDate);
    }


    // check if the plugin use is else file
    public boolean sqlUse() {
//        return plugin.getConfig().get("database").equals("sql");
        return loadManager.database().equals("sql");
    }


    //    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private Functions F = new Functions();
    private Economy economy = Tokens.economy;
    private SQLlib sql = new SQLlib();

    public void addTokens(UUID uuid, UUID target, int tokens) {
        if (sqlUse()) {
            sql.addTokens(target, tokens);

            if (uuid != null) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are added to " + Strings.green + F.getName(String.valueOf(target)));
            }
        } else {

            int playerTokens = config.getPlayers().getInt(target + ".tokens");

            config.getPlayers().set(target + ".tokens", playerTokens + tokens);
            config.savePlayers();
            if (uuid != null) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are added to " + Strings.green + F.getName(String.valueOf(target)));
            }
        }
    }


    public void removeTokens(UUID uuid, UUID target, int tokens) {
        if (sqlUse()) {
            sql.removeTokens(target, tokens);

            if (uuid != null) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are removed from " + Strings.green + F.getName(String.valueOf(target)));
            }
        } else {
            int playerTokens = config.getPlayers().getInt(target + ".tokens");

            config.getPlayers().set(target + ".tokens", playerTokens - tokens);
            config.savePlayers();

            if (uuid != null) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are removed from " + Strings.green + F.getName(String.valueOf(target)));
            }
        }
    }

    public void setTokens(UUID uuid, UUID target, int tokens) {
        if (sqlUse()) {
            sql.setTokens(target, tokens);

            if (uuid != null) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are set to " + Strings.green + F.getName(String.valueOf(target)));
            }
        } else {

            config.getPlayers().set(target + ".tokens", tokens);
            config.savePlayers();

            if (uuid != null) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are set to " + Strings.green + F.getName(String.valueOf(target)));
            }
        }

    }

    public void giveallTokens(int tokens) {
        if (sqlUse()) {
            String tokens1 = String.valueOf(tokens);
//            String defaultMessage = plugin.getConfig().getString("bonus_message");
            String defaultMessage = loadManager.bonus_message();
            String replacedMessage = defaultMessage.replace("{tokens}", tokens1).replace("{prefix}", getPrefix() + Strings.reset);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                UUID uuid = onlinePlayer.getUniqueId();
                sql.addTokens(uuid, sql.getTokens(uuid) + tokens);

                Strings.translateColorCodesPlayer(onlinePlayer, replacedMessage);
            }
        } else {

            String tokens1 = String.valueOf(tokens);
//            String defaultMessage = plugin.getConfig().getString("bonus_message");
            String defaultMessage = loadManager.bonus_message();
            String replacedMessage = defaultMessage.replace("{tokens}", tokens1).replace("{prefix}", getPrefix() + Strings.reset);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                int playerTokens = config.getPlayers().getInt(onlinePlayer.getUniqueId() + ".tokens");
                config.getPlayers().set(onlinePlayer.getUniqueId() + ".tokens", playerTokens + tokens);

                Strings.translateColorCodesPlayer(onlinePlayer, replacedMessage);
            }
            config.savePlayers();
        }
    }


    public boolean convertToTokens(UUID playerUUID, int amount) {
        if (sqlUse()) {
            Player p = Bukkit.getPlayer(playerUUID);
            // getting some information
//            int tokensValue = plugin.getConfig().getInt("tokens.buy_price");
            int tokensValue = loadManager.tokens_buy();
            int totalCheckOut = amount * tokensValue;
            Player buyer = p.getPlayer();

            // check if the player has enough balance to continue the transaction
            if (totalCheckOut > economy.getBalance(buyer)) {
                p.sendMessage(Strings.red + "You don't Fhave enough money to buy " + getPrefix() + "\n" +
                        " \n" +
                        Strings.white + "Total costs: $" + Strings.green + totalCheckOut);
                return true;
            }

            // ....
            EconomyResponse r = economy.withdrawPlayer(buyer, totalCheckOut);
            if (r.transactionSuccess()) {
                sql.addTokens(playerUUID, amount);
                p.sendMessage(Strings.gray + "You bought " + Strings.green + amount + " Tokens " + Strings.gray + "for " + Strings.green + "$" + totalCheckOut);
                return true;
            } else {
                p.sendMessage("An error accured!");
                return true;
            }


        } else {

            Player p = Bukkit.getPlayer(playerUUID);
            // getting some information
//            int tokensValue = plugin.getConfig().getInt("tokens.buy_price");
            int tokensValue = loadManager.tokens_buy();
            int totalCheckOut = amount * tokensValue;
            Player buyer = p.getPlayer();
            int playerTokens = config.getPlayers().getInt(playerUUID + ".tokens");

            // check if the player has enough balance to continue the transaction
            if (totalCheckOut > economy.getBalance(buyer)) {
                p.sendMessage(Strings.red + "You don't Fhave enough money to buy " + getPrefix() + "\n" +
                        " \n" +
                        Strings.white + "Total costs: $" + Strings.green + totalCheckOut);
                return true;
            }


            // ....
            EconomyResponse r = economy.withdrawPlayer(buyer, totalCheckOut);
            if (r.transactionSuccess()) {
                config.getPlayers().set(playerUUID + ".tokens", playerTokens + amount);
                config.savePlayers();
                p.sendMessage(Strings.gray + "You bought " + Strings.green + amount + " Tokens " + Strings.gray + "for " + Strings.green + "$" + totalCheckOut);
                return true;
            } else {
                p.sendMessage("An error accured!");
                return true;
            }
        }
    }

    //TODO: need to be a void, i don't no why its a boolean
    public boolean convertToMoney(UUID playerUUID, int amount) {
        if (sqlUse()) {
            Player p = Bukkit.getPlayer(playerUUID);
            // getting some information
//            int tokensValue = plugin.getConfig().getInt("tokens.sell_price");
            int tokensValue = loadManager.token_sell();
            int playerTokens = balanceInt(playerUUID);
            int totalCheckOut = amount * tokensValue;
            Player buyer = p.getPlayer();


            // Check if player has enough tokens
            if (amount > playerTokens) {
                p.sendMessage(Strings.red + "You don't have that many " + getPrefix() + " to sell!\n" +
                        " \n" +
                        Strings.green + "Total " + getPrefix() + ": " + playerTokens);
                return true;
            }


            // ....
            EconomyResponse r = economy.depositPlayer(buyer, totalCheckOut);
            if (r.transactionSuccess()) {
                sql.removeTokens(playerUUID, amount);
                p.sendMessage(Strings.gray + "You sold " + Strings.green + amount + " " + getPrefix() + " " + Strings.gray + "for " + Strings.green + "$" + totalCheckOut);

                return true;
            } else {
                p.sendMessage("An error accured!");
                return true;
            }


        } else {

            Player p = Bukkit.getPlayer(playerUUID);
            // getting some information
//            int tokensValue = plugin.getConfig().getInt("tokens.sell_price");
            int tokensValue = loadManager.token_sell();
            int playerTokens = config.getPlayers().getInt(playerUUID + ".tokens");
            int totalCheckOut = amount * tokensValue;
            Player buyer = p.getPlayer();


            // Check if player has enough tokens
            if (amount > playerTokens) {
                p.sendMessage(Strings.red + "You don't have that many " + getPrefix() + " to sell!\n" +
                        " \n" +
                        Strings.green + "Total " + getPrefix() + ": " + playerTokens);
                return true;
            }


            // ....
            EconomyResponse r = economy.depositPlayer(buyer, totalCheckOut);
            if (r.transactionSuccess()) {

                config.getPlayers().set(playerUUID + ".tokens", playerTokens - amount);
                config.savePlayers();
                p.sendMessage(Strings.gray + "You sold " + Strings.green + amount + " " + getPrefix() + " " + Strings.gray + "for " + Strings.green + "$" + totalCheckOut);

                return true;
            } else {
                p.sendMessage("An error accured!");
                return true;
            }
        }

    }


    public void balance(UUID uuid) {
        if (sqlUse()) {
            int tokensBalance = sql.getTokens(uuid);

            Player p = Bukkit.getPlayer(uuid);
            p.sendMessage(Strings.translateColorCodes(loadManager.prefix()));
            p.sendMessage(Strings.gray + "You have " + Strings.green + tokensBalance + " " + getPrefix());
        } else {

            int tokensBalance = config.getPlayers().getInt(uuid + ".tokens");
            Player p = Bukkit.getPlayer(uuid);
            p.sendMessage(Strings.gray + "You have " + Strings.green + tokensBalance + " " + getPrefix());
        }
    }


    public void balance(UUID uuid, UUID target) {
        if (sqlUse()) {
            if (!sql.hasAccount(target)) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.red + "Player not found");
            } else {
                int tokensBalance = sql.getTokens(target);
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.gray + F.getName(target.toString()) + " has " + Strings.green + tokensBalance + " " + getPrefix());
            }
        } else {
            // check if player exist before paying
            if (!config.getPlayers().contains(String.valueOf(target))) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.red + "Player not found");
            } else {
                int tokensBalance = config.getPlayers().getInt(target + ".tokens");
                //Player p = Bukkit.getPlayer(playerUUID);
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.gray + F.getName(target.toString()) + " has " + Strings.green + tokensBalance + " " + getPrefix());
            }
        }
    }


    public void payPlayer(UUID uuid, UUID target, int tokens) {
        if (sqlUse()) {
            if (!sql.hasAccount(target)) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.red + "Player not found");
            } else {
                // get some information
                int targetTokens = sql.getTokens(target);
                int playerTokens = sql.getTokens(uuid);

                // ....??
                sql.removeTokens(uuid, tokens);
                sql.addTokens(target, tokens);

                // send the player message he payed the player
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.gray + "You payed " + Strings.green + F.getName(String.valueOf(target)) + " " + tokens);

                // tells the target thath he received an amount of tokens
                @SuppressWarnings("deprecation") OfflinePlayer playerReceiver = Bukkit.getOfflinePlayer(F.getName(String.valueOf(target)));
                if (playerReceiver.isOnline()) {
                    Player pOnline = Bukkit.getPlayer(playerReceiver.getUniqueId());
                    pOnline.sendMessage(Strings.gray + "You received " + Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "from " + Strings.green + p.getName());
                }

            }

        } else {


            // check if player exist before paying
            if (!config.getPlayers().contains(String.valueOf(target))) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.red + "Player not found");
            } else {

                int targetTokens = config.getPlayers().getInt(target + ".tokens");
                int playerTokens = config.getPlayers().getInt(uuid + ".tokens");

                config.getPlayers().set(uuid + ".tokens", playerTokens - tokens);
                config.getPlayers().set(target + ".tokens", tokens + targetTokens);
                config.savePlayers();

                Player p = (Player) Bukkit.getOfflinePlayer(uuid);
                p.sendMessage(Strings.gray + "You payed " + Strings.green + F.getName(String.valueOf(target)) + " " + tokens);

                @SuppressWarnings("deprecation") OfflinePlayer playerReceiver = Bukkit.getOfflinePlayer(F.getName(String.valueOf(target)));
                if (playerReceiver.isOnline()) {
                    Player pOnline = Bukkit.getPlayer(playerReceiver.getUniqueId());
                    pOnline.sendMessage(Strings.gray + "You received " + Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "from " + Strings.green + p.getName());
                }

                // we don't use this methode anymore
                /*
                Object transactionTime_HMS = new SimpleDateFormat("HH:mm:ss").format(new Date());
                Object transactionTime_YMD = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                config.getPlayers().set(target + ".LastTransactionActivity.date", transactionTime_HMS + " | " + transactionTime_YMD);
                config.getPlayers().set(target + ".LastTransactionActivity.player", F.getName(uuid.toString()));
                config.getPlayers().set(target + ".LastTransactionActivity.amount", tokens);
                config.savePlayers();
                */
            }
        }
    }


    // add player to database if player not exist
    public void creatAccount(UUID uuid) {
        if (!config.getPlayers().contains(String.valueOf(uuid))) {
            config.getPlayers().set(uuid + ".tokens", 0);
            config.savePlayers();
        }
    }


    // Value of the balance of the player :: INT
    public int balanceInt(UUID uuid) {
        if (sqlUse()) {
            return sql.getTokens(uuid);
        } else {
            return config.getPlayers().getInt(uuid + ".tokens");
        }
    }


    public String getPrefix() {
        if (loadManager.prefix_boolean()) {
            return Strings.translateColorCodes(loadManager.prefix());
        } else {
            return "Tokens";
        }
    }

//    public boolean versionMatchForShop() {
//        Tokens plugin = Tokens.getPlugin(Tokens.class);
//        String a = plugin.getServer().getClass().getPackage().getName();
//        String version = a.substring(a.lastIndexOf('.') + 1);
//
//        // Check if the server has the same craftbukkit version as this plugin
//        if (version.equalsIgnoreCase("v1_8_R1")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_8_R2")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_8_R3")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_9_R1")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_9_R2")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_10_R1")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_11_R1")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_12_R1")) {
//            return true;
//        } else if (version.equalsIgnoreCase("v1_13_R1")) {
//            return false;
//        }
//        return false;
//    }


    public int bankBalance(UUID uuid) {
        if (sqlUse()) {
            int BankID = sql.getUserBankID(uuid);

            return sql.getBankBalance(BankID);
        } else {
            config.setup();
            int playerBankID = userBankID(uuid);

            return config.getBank().getInt(playerBankID + ".balance");
        }
    }

    public void bankAddBalance(UUID uuid, int amount) {
        if (sqlUse()) {
            sql.bankBalanceDeposit(userBankID(uuid), amount);
        } else {
            config.setup();
            int result = bankBalance(uuid) + amount;
            int playerBankID = userBankID(uuid);

            config.getBank().set(playerBankID + ".balance", result);
            config.saveBank();
        }
    }

    public void bankRemoveBalance(UUID uuid, int amount) {
        if (sqlUse()) {
            sql.bankBalanceWithdraw(userBankID(uuid), amount);
        } else {
            config.setup();
            int result = bankBalance(uuid) - amount;
            int playerBankID = userBankID(uuid);

            config.getBank().set(playerBankID + ".balance", result);
            config.saveBank();
        }
    }

    public void bankUserAdd(UUID uuid, UUID subUser) {
        if (sqlUse()) {
            Player p = Bukkit.getPlayer(subUser);
            String a = subUser + " " + p.getName();
            sql.bankUserAdd(uuid, subUser);
        } else {
            config.setup();
            Player p = Bukkit.getPlayer(subUser);
            int playerBankID = userBankID(uuid);

            List<String> list = config.getBank().getStringList(playerBankID + ".users");
            list.add(subUser + " " + p.getName() + " 1");
            config.getBank().set(playerBankID + ".users", list);

            config.getPlayers().set(subUser + ".bankID", playerBankID);


            config.savePlayers();
            config.saveBank();
        }

    }


    public void bankUserRemove(UUID uuid) {
        if (sqlUse()) {
            sql.bankUserRemove(uuid);
        } else {
            config.setup();
            int bankID = userBankID(uuid);

            List<String> list = config.getBank().getStringList(bankID + ".users");

            int i = 0;
            for (Object a : list) {
                String b = a.toString();

                // Check if the Object contains the UUID
                if (b.contains(uuid.toString())) {
                    list.remove(i);
                    break;
                }
                i++;
            }
            config.getBank().set(bankID + ".users", list);
            config.saveBank();

            config.getPlayers().set(uuid + ".bankID", null);
            config.savePlayers();
        }
    }

    public boolean playerIsRegisteredInBank(UUID uuid) {
        if (sqlUse()) {
            if (sql.getUserBankID(uuid) == 0) {
                return false;
            }
            return true;
        } else {
            config.setup();
            int playerBankID = userBankID(uuid);

            if (!config.getPlayers().isSet(uuid + ".bankID")) {
                return false;
            }
            return true;
        }
    }


    public void createBank(UUID uuid) {
        if (sqlUse()) {
            int i = sql.nextBankID();
            Player p = Bukkit.getPlayer(uuid);

            sql.createBank(uuid, p.getName());
        } else {
            config.setup();
            int i = nextBankID();
            Player p = Bukkit.getPlayer(uuid);

            config.getPlayers().set(uuid + ".bankID", i);

            config.getBank().set(i + ".owner", uuid + " " + p.getName());
            config.getBank().set(i + ".balance", 0);
            config.getBank().set(i + ".users", "");


            config.savePlayers();
            config.saveBank();
            bankIDS.clear();
        }
    }


    public void bankDelete(UUID uuid) {
        if (sqlUse()) {
            sql.bankDelete(uuid);
        } else {
            config.setup();
            int bankID = userBankID(uuid);

            // remove the bank path
            config.getBank().set(String.valueOf(bankID), null);

            // remove the bankID path from the player
            config.getPlayers().set(uuid + ".bankID", null);

            config.saveBank();
            config.savePlayers();
        }
    }

    // test

    private List<Integer> bankIDS = new ArrayList<>();


    // Get the next following BANK_ID (it will check also for the skipped number from deleted banks)
    private int nextBankID() {
        config.setup();

        int i = 1;

        // If the file is empty it will start at 1
        if (config.getBank().getKeys(false).size() == 0) {
            return 1;
        }

        // put all the bankIDS to a List
        for (String key : config.getBank().getConfigurationSection("").getKeys(false)) {
            bankIDS.add(Integer.valueOf(key));
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

    public int userBankID(UUID uuid) {
        if (sqlUse()) {
            return sql.getUserBankID(uuid);
        } else {
            config.setup();
            return config.getPlayers().getInt(uuid + ".bankID");
        }
    }

    public boolean isBankOwner(UUID uuid) {
        if (sqlUse()) {
            int i = sql.getUserBankID(uuid);
            String[] a = sql.getBankOwner(i).split(" ");
            String b = a[0];

            return b.equals(uuid.toString());
        } else {
            config.setup();
            int bankID = userBankID(uuid);
            String[] a = config.getBank().getString(bankID + ".owner").split(" ");
            UUID owner = UUID.fromString(a[0]);

            return owner.equals(uuid);
        }
    }

    public String getBankOwner(UUID uuid) {
        if (sqlUse()) {
            return sql.getBankOwnerName(uuid);
        } else {
            config.setup();
            int playerBankID = userBankID(uuid);

            String[] a = config.getBank().getString(playerBankID + ".owner").split(" ");
            return a[1];
        }
    }


    public void bankOwnershipTransfer(UUID uuid, UUID newOwner) {
        config.setup();
        int bankID = userBankID(uuid);
        OfflinePlayer p = Bukkit.getOfflinePlayer(newOwner);


        // Put the powner in the user list
        bankUserAdd(uuid, uuid);
        // Replace the NEW owner in the owner PATH
        String[] a = bankUserPath(newOwner).split(" ");
        config.getBank().set(bankID + ".owner", a[0] + " " + a[1]);
        config.saveBank();
        // Remove the new owner from the user list
        bankUserRemove(newOwner);

        config.getPlayers().set(newOwner + ".bankID", bankID);
        config.savePlayers();

    }

    // return to the hole path of the user : <uuid> <playername> <rank>
    private String bankUserPath(UUID uuid) {
        config.setup();
        int playerBankID = userBankID(uuid);

        List<String> list = config.getBank().getStringList(playerBankID + ".users");
        for (Object a : list) {
            if (a.toString().contains(uuid.toString())) {

                Bukkit.getConsoleSender().sendMessage(a + "");
                return (String) a;
            }
        }
        return null;
    }

    // get the rank number of the player member of the bank
    public int bankUserRank(UUID uuid) {
        String[] a = Objects.requireNonNull(bankUserPath(uuid)).split(" ");
        return Integer.parseInt(a[2]);
    }

    public boolean bankHasUsers(UUID uuid) {
        if (sqlUse()) {
            return sql.bankHasUsers(uuid);
        } else {
            if (config.getBank().get(userBankID(uuid) + ".users").toString().isEmpty() && config.getBank().get(userBankID(uuid) + ".users").toString().contains("")) {
                return false;
            }
        }
        return false;
    }


}
