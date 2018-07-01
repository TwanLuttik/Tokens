package com.twanl.tokens.lib;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Functions;
import com.twanl.tokens.utils.Strings;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Lib {

    private ConfigManager cfgM = new ConfigManager();

    public String transactionPlayer (UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionPlayer = cfgM.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.player");

        if (transactionPlayer == null) {
            return "No player";
        }
        return String.valueOf(transactionPlayer);
    }

    public String transactionDate (UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionDate = cfgM.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.date");

        if (transactionDate == null) {
            return "No data";
        }
        return String.valueOf(transactionDate);
    }

    public String transactionAmount (UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        Object transactionDate = cfgM.getPlayers().get(p.getUniqueId() + ".LastTransactionActivity.amount");

        if (transactionDate == null) {
            return "No value";
        }
        return String.valueOf(transactionDate);
    }








    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private Functions F = new Functions();
    private Economy economy = Tokens.economy;



    public void addTokens (UUID targetUUID, UUID playerUUID, int tokens) {
        int playerTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");

        cfgM.getPlayers().set(targetUUID + ".tokens", playerTokens + tokens);
        cfgM.savePlayers();

        Player p = (Player) Bukkit.getOfflinePlayer(playerUUID);
        p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are added to " + Strings.green + F.getName(String.valueOf(targetUUID)));
    }

    public void removeTokens (UUID targetUUID, UUID playerUUID, int tokens) {

        int playerTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");

        cfgM.getPlayers().set(targetUUID + ".tokens", playerTokens - tokens);
        cfgM.savePlayers();

        Player p = (Player) Bukkit.getOfflinePlayer(playerUUID);
        p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are removed from " + Strings.green +  F.getName(String.valueOf(targetUUID)));
    }

    public void setTokens (UUID targetUUID, UUID playerUUID, int tokens) {

        cfgM.getPlayers().set(targetUUID + ".tokens", tokens);
        cfgM.savePlayers();

        Player p = Bukkit.getPlayer(playerUUID);
        p.sendMessage(Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "are set to " + Strings.green + F.getName(String.valueOf(targetUUID)));
    }

    public void giveallTokens (int tokens) {

        String tokens1 = String.valueOf(tokens);
        String defaultMessage = plugin.getConfig().getString("bonus_message");
        String replacedMessage = defaultMessage.replace("{tokens}", tokens1 ).replace("{prefix}", getPrefix() + Strings.reset);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int playerTokens = cfgM.getPlayers().getInt(onlinePlayer.getUniqueId() + ".tokens");
            cfgM.getPlayers().set(onlinePlayer.getUniqueId() + ".tokens", playerTokens + tokens);

            Strings.translateColorCodesPlayer(onlinePlayer, replacedMessage);
        }
        cfgM.savePlayers();
    }


    public boolean convertToTokens (UUID playerUUID, int amount) {

        Player p = Bukkit.getPlayer(playerUUID);
        // getting some information
        int tokensValue = plugin.getConfig().getInt("tokens.buy_price");
        int totalCheckOut = amount * tokensValue;
        Player buyer = p.getPlayer();
        int playerTokens = cfgM.getPlayers().getInt(playerUUID + ".tokens");

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
            cfgM.getPlayers().set(playerUUID + ".tokens", playerTokens + amount);
            cfgM.savePlayers();
            p.sendMessage(Strings.gray + "You bought " + Strings.green + amount + " Tokens " + Strings.gray + "for " + Strings.green + "$" + totalCheckOut);
            return true;
        } else {
            p.sendMessage("An error accured!");
            return true;
        }
    }

    public boolean convertToMoney (UUID playerUUID, int amount) {

        Player p = Bukkit.getPlayer(playerUUID);
        // getting some information
        int tokensValue = plugin.getConfig().getInt("tokens.sell_price");
        int playerTokens = cfgM.getPlayers().getInt(playerUUID + ".tokens");
        int totalCheckOut = amount * tokensValue;
        Player buyer = p.getPlayer();


        // Check if player has enough tokens
        if (amount > playerTokens) {
            p.sendMessage(Strings.red + "You don't have that many " + getPrefix() +" to sell!\n" +
                    " \n" +
                    Strings.green + "Total " + getPrefix() + ": " + playerTokens);
            return true;
        }


        // ....
        EconomyResponse r = economy.depositPlayer(buyer, totalCheckOut);
        if (r.transactionSuccess()) {

            cfgM.getPlayers().set(playerUUID + ".tokens", playerTokens - amount);
            cfgM.savePlayers();
            p.sendMessage(Strings.gray + "You sold " + Strings.green + amount + " " + getPrefix() + " " + Strings.gray + "for " + Strings.green + "$" + totalCheckOut);

            return true;
        } else {
            p.sendMessage("An error accured!");
            return true;
        }

    }

    public void balance (UUID playerUUID) {
        int tokensBalance = cfgM.getPlayers().getInt(playerUUID + ".tokens");
        Player p = Bukkit.getPlayer(playerUUID);
        p.sendMessage(Strings.gray + "You have " + Strings.green + tokensBalance + " " + getPrefix());
    }


    public void balance (UUID playerUUID, UUID targetUUID) {
        // check if player exist before paying
        if (!cfgM.getPlayers().contains(String.valueOf(targetUUID))) {
            Player p = Bukkit.getPlayer(playerUUID);
            p.sendMessage(Strings.red + "Player not found");
        } else {
            int tokensBalance = cfgM.getPlayers().getInt(targetUUID + ".tokens");
            //Player p = Bukkit.getPlayer(playerUUID);
            Player p = Bukkit.getPlayer(playerUUID);
            p.sendMessage(Strings.gray + F.getName(targetUUID.toString()) + " has " + Strings.green + tokensBalance + " " + getPrefix());
        }
    }



    public void payPlayer(UUID playerUUID, UUID targetUUID, int tokens) {

        // check if player exist before paying
        if (!cfgM.getPlayers().contains(String.valueOf(targetUUID))) {
            Player p = Bukkit.getPlayer(playerUUID);
            p.sendMessage(Strings.red + "Player not found");
        } else {

            int targetTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");
            int playerTokens = cfgM.getPlayers().getInt(playerUUID + ".tokens");

            cfgM.getPlayers().set(playerUUID + ".tokens", playerTokens - tokens);
            cfgM.getPlayers().set(targetUUID + ".tokens", tokens + targetTokens);
            cfgM.savePlayers();

            Player p = (Player) Bukkit.getOfflinePlayer(playerUUID);
            p.sendMessage(Strings.gray + "You payed " + Strings.green + F.getName(String.valueOf(targetUUID)) + " " + tokens);

            OfflinePlayer playerReceiver = Bukkit.getOfflinePlayer(F.getName(String.valueOf(targetUUID)));
            if (playerReceiver.isOnline()) {
                Player pOnline = Bukkit.getPlayer(playerReceiver.getUniqueId());
                pOnline.sendMessage(Strings.gray + "You received " + Strings.green + tokens + " " + getPrefix() + " " + Strings.gray + "from " + Strings.green + p.getName());
            }


            Object transactionTime_HMS = new SimpleDateFormat("HH:mm:ss").format(new Date());
            Object transactionTime_YMD = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            cfgM.getPlayers().set(targetUUID + ".LastTransactionActivity.date", transactionTime_HMS + " | " + transactionTime_YMD);
            cfgM.getPlayers().set(targetUUID + ".LastTransactionActivity.player", F.getName(playerUUID.toString()));
            cfgM.getPlayers().set(targetUUID + ".LastTransactionActivity.amount", tokens);
            cfgM.savePlayers();


        }
    }


    // add player to database if player not exist
    public void creatAccount(UUID uuid) {
        if (!cfgM.getPlayers().contains(String.valueOf(uuid))) {
            cfgM.getPlayers().set(uuid + ".tokens", 0);
            cfgM.savePlayers();
        }
    }


    // Value of the balance of the player :: INT
    public int balanceInt (UUID playerUUID) {
        int playerTokens = cfgM.getPlayers().getInt(playerUUID + ".tokens");
        return playerTokens;
    }


    public void transactionSuccess(UUID uuid, UUID targetUUID, int tokens) {
        Player p = Bukkit.getPlayer(uuid);
        int targetPBalance_before = balanceInt(targetUUID);


        payPlayer(uuid, targetUUID, tokens);

        int targetPBalance_after = balanceInt(targetUUID);

        if (targetPBalance_before == targetPBalance_after) {
            p.sendMessage(Strings.redI + "failed to send the tokens!");
        } else {
            // send nothing when transaction worked
        }

    }


    public String getPrefix() {
        if (plugin.getConfig().getBoolean("prefix.enable")) {
            return Strings.translateColorCodes(plugin.getConfig().getString("prefix.prefix"));
        } else {
            return "Tokens";
        }
    }


}
