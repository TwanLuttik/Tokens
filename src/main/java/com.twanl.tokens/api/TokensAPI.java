package com.twanl.tokens.api;

import com.twanl.tokens.Functions;
import com.twanl.tokens.Tokens;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;


public class TokensAPI {

    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager cfgM = new ConfigManager();
    private Functions F = new Functions();




    public void addTokens (UUID uuid, Player p, int tokens) {
        int playerTokens = cfgM.getPlayers().getInt(uuid + ".tokens");

        cfgM.getPlayers().set(uuid + ".tokens", playerTokens + tokens);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + tokens + " Tokens are added to " + F.getName(String.valueOf(uuid)));
    }

    public void removeTokens (UUID uuid, Player p, int tokens) {

        int playerTokens = cfgM.getPlayers().getInt(uuid + ".tokens");

        cfgM.getPlayers().set(uuid + ".tokens", playerTokens - tokens);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + tokens + " Tokens are removed from " + F.getName(String.valueOf(uuid)));
    }

    public void setTokens (UUID uuid, Player p, int tokens) {

        cfgM.getPlayers().set(uuid + ".tokens", tokens);
        cfgM.savePlayers();

        p.sendMessage(Strings.green + tokens + " Tokens are set to " + F.getName(String.valueOf(uuid)));
    }

    public void giveallTokens (Player p, int tokens) {

        String tokens1 = String.valueOf(tokens);
        String defaultMessage = plugin.getConfig().getString("bonus_message");
        String replacedMessage = defaultMessage.replace("{tokens}", tokens1);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int playerTokens = cfgM.getPlayers().getInt(onlinePlayer.getUniqueId() + ".tokens");
            cfgM.getPlayers().set(onlinePlayer.getUniqueId() + ".tokens", playerTokens + tokens);

            Strings.translateColorCodes(onlinePlayer, replacedMessage);
        }
        cfgM.savePlayers();
    }

/*
    public void balance(UUID uuid, Player p) {
        String balanceTokens = String.valueOf(cfgM.getPlayers().getInt(uuid + ".tokens"));
        p.sendMessage(Strings.green + "You have " + balanceTokens + " Tokens");
    }


    public void hasAcount (UUID uuid, Player p) {

        if (!cfgM.getPlayers().contains(uuid.toString())) {

        }

    }

*/


    /**
     *
     * Example:
     *
     * 1 Tokens costs = 100 money
     * /tokens buy <amount_of_tokens>
     *
     *
     *
     *
     * 1 Tokens is worth = 90 Money
     * /tokens sell <amount_of_tokens>
     *
     */


    private Economy economy = Tokens.economy;

    @SuppressWarnings("deprecation")
    public boolean convertToTokens (UUID uuid, int amount, Player p) {

        // getting some information
        int tokensValue = plugin.getConfig().getInt("tokens.buy_price");
        int totalCheckOut = amount * tokensValue;
        Player buyer = p.getPlayer();
        int playerTokens = cfgM.getPlayers().getInt(uuid + ".tokens");

        // check if the player has enough balance to continue the transaction
        if (totalCheckOut > economy.getBalance(buyer)) {
            p.sendMessage(Strings.red + "You don't have enough money to buy Tokens!\n" +
            " \n" +
            Strings.gold + "Total costs: $" + totalCheckOut);
            return true;
        }


        // ....
        EconomyResponse r = economy.withdrawPlayer(buyer, totalCheckOut);
        if (r.transactionSuccess()) {


            cfgM.getPlayers().set(uuid + ".tokens", playerTokens + amount);
            cfgM.savePlayers();
            p.sendMessage(Strings.green + "You bought " + amount + " Tokens for " + totalCheckOut);

            return true;
        } else {
            p.sendMessage("An error accured!");
            return true;
        }
    }

    public boolean
    convertToMoney (UUID uuid, int amount, Player p) {

        // getting some information
        int tokensValue = plugin.getConfig().getInt("tokens.sell_price");
        int playerTokens = cfgM.getPlayers().getInt(uuid + ".tokens");
        int totalCheckOut = amount * tokensValue;
        Player buyer = p.getPlayer();


        // Check if player has enough tokens
        if (amount > playerTokens) {
            p.sendMessage(Strings.red + "You don't have that many tokens to sell!\n" +
                    " \n" +
                    Strings.gold + "Total Tokens: " + playerTokens);
            return true;
        }


        // ....
        EconomyResponse r = economy.depositPlayer(buyer, totalCheckOut);
        if (r.transactionSuccess()) {

            cfgM.getPlayers().set(uuid + ".tokens", playerTokens - amount);
            cfgM.savePlayers();
            p.sendMessage(Strings.green + "You sold " + amount + " Tokens for " + totalCheckOut);

            return true;
        } else {
            p.sendMessage("An error accured!");
            return true;
        }

    }






    // Check if player exist in file
    public boolean playerCheck (UUID uuid) {
        if (!cfgM.getPlayers().contains(String.valueOf(uuid))) {
            cfgM.getPlayers().set(uuid + ".tokens", 0);
            cfgM.savePlayers();
            return false;
        }
        return false;
    }







}
