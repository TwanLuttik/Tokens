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

import java.util.UUID;

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

            if (uuid != null ) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are added to " + Strings.green + F.getName(String.valueOf(target)));
            }
        } else {

            int playerTokens = config.getPlayers().getInt(target + ".tokens");

            config.getPlayers().set(target + ".tokens", playerTokens + tokens);
            config.savePlayers();
            if (uuid != null ) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are added to " + Strings.green + F.getName(String.valueOf(target)));
            }
        }
    }


    public void removeTokens(UUID uuid, UUID target, int tokens) {
        if (sqlUse()) {
            sql.removeTokens(target, tokens);

            Player p = Bukkit.getPlayer(uuid);
            if (uuid != null) {
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are removed from " + Strings.green + F.getName(String.valueOf(target)));
            }
        } else {
            int playerTokens = config.getPlayers().getInt(target + ".tokens");

            config.getPlayers().set(target + ".tokens", playerTokens - tokens);
            config.savePlayers();

            Player p = Bukkit.getPlayer(uuid);
            if (uuid != null) {
                p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are removed from " + Strings.green + F.getName(String.valueOf(target)));
            }
        }
    }

    public void setTokens(UUID uuid, UUID target, int tokens) {
        if (sqlUse()) {
            sql.setTokens(target, tokens);

            Player p = Bukkit.getPlayer(uuid);
            p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are set to " + Strings.green + F.getName(String.valueOf(target)));
        } else {

            config.getPlayers().set(target + ".tokens", tokens);
            config.savePlayers();

            Player p = Bukkit.getPlayer(uuid);
            p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are set to " + Strings.green + F.getName(String.valueOf(target)));
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
//        if (plugin.getConfig().getBoolean("prefix.enable")) {
        if (loadManager.prefix_boolean()) {
//            return Strings.translateColorCodes(plugin.getConfig().getString("prefix.prefix"));
            return Strings.translateColorCodes(loadManager.prefix());
        } else {
            return "Tokens";
        }
    }

    public boolean versionMatchForShop() {
        Tokens plugin = Tokens.getPlugin(Tokens.class);
        String a = plugin.getServer().getClass().getPackage().getName();
        String version = a.substring(a.lastIndexOf('.') + 1);

        // Check if the server has the same craftbukkit version as this plugin
        if (version.equalsIgnoreCase("v1_8_R1")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_8_R2")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_8_R3")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_9_R1")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_9_R2")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_10_R1")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_11_R1")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_12_R1")) {
            return true;
        } else if (version.equalsIgnoreCase("v1_13_R1")) {
            return false;
        }
        return false;
    }


    // the amount of inventory slots
    public int shopGetSlots(String menu) {
        config.setup();
        return config.getShop().getInt("shop." + menu + ".options.slot");
    }

    public String shopGetTitle(String menu) {
        config.setup();
        return config.getShop().getString("shop." + menu + ".options.title");
    }

    public String itemName(String menu, int i) {
        config.setup();
        if (!config.getShop().isSet("shop." + menu + ".slots." + i + ".name")) {
            return "";
        } else {
            return config.getShop().getString("shop." + menu + ".slots." + i + ".name");
        }
    }


    public int itemSlot(String menu, int i) {
        config.setup();
        return config.getShop().getInt("shop." + menu + ".slots." + i + ".slot");
    }

    public int itemAmount(String menu, int i) {
        config.setup();
        return config.getShop().getInt("shop." + menu + ".slots." + i + ".amount");
    }

    public int itemId(String menu, int i) {
        config.setup();
        return config.getShop().getInt("shop." + menu + ".slots." + i + ".Id");
    }

    public int itemByte(String menu, int i) {
        config.setup();
        return config.getShop().getInt("shop." + menu + ".slots." + i + ".Data");
    }

    public int itemBuyPrice(String menu, int i) {
        config.setup();
        return config.getShop().getInt("shop." + menu + ".slots." + i + ".buy");
    }

    public int itemSellPrice(String menu, int i) {
        config.setup();
        return config.getShop().getInt("shop." + menu + ".slots." + i + ".sell");
    }

    public String itemCommand(String menu, int i) {
        config.setup();
        return config.getShop().getString("shop." + menu + ".slots." + i + ".command");
    }

    public int bankTotal(UUID uuid) {
        config.setup();

        return config.getBank().getInt("bank-1.tokens");
    }

}
