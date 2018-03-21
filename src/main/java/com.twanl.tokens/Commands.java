package com.twanl.tokens;

import com.twanl.tokens.api.TokensAPI;
import com.twanl.tokens.items.TokenItem;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@SuppressWarnings("deprecation")
public  class Commands implements CommandExecutor, TabCompleter {



    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public ConfigManager cfgM;
    private TokenItem CI = new TokenItem();
    private TokensAPI tokenApi = new TokensAPI();
    private Functions F = new Functions();




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;

        cfgM = new ConfigManager();
        cfgM.setup();




        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) {
                p.sendMessage(Strings.DgrayBS + "-                                    \n"
                        + Strings.goldB + "       Tokens " + Strings.gold + plugin.getDescription().getVersion() + "\n"
                        + " \n"
                        + "     " + Strings.gold + "Users Commands\n" + Strings.reset
                        + Strings.white + "/tokens balance\n"
                        + Strings.white + "/tokens balance <player>\n"
                        + Strings.white + "/tokens pay <amount> <player>\n"
                        + Strings.white + "/tokens get <amount>\n"
                        + Strings.white + "/tokens redeem\n"
                        + Strings.white + "/tokens buy <amount>\n"
                        + Strings.white + "/tokens sell <amount>\n"
                        + " \n"
                        + "     " + Strings.gold + "Admin Commands\n" + Strings.reset
                        + Strings.white + "/tokens reload\n"
                        + Strings.white + "/tokens bonus <amount>\n"
                        + Strings.white + "/tokens remove <amount> <player>\n"
                        + Strings.white + "/tokens add <amount> <player>\n"
                        + Strings.white + "/tokens set <amount> <player>\n"
                        + Strings.DgrayBS + "-                                    \n");



            } else if (args[0].equalsIgnoreCase("get")) {
                if (p.hasPermission("tokens.get")) {

                    // shows the command usage
                    if (args.length == 1) {
                        p.sendMessage(Strings.red + "/tokens get <amount>");
                        return true;
                    }

                    // check if the its a number instead of a character
                    try {
                        int tokens = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        int tokens;
                        p.sendMessage(Strings.red + "Use a valid number!");
                        return true;
                    }


                    int tokenCommand = Integer.parseInt(args[1]);
                    int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");

                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "Please enter a number higher than 0");
                        return true;
                    }

                    // Check if the player has enough coins to remove
                    if (tokenCommand > playerTokens) {
                        p.sendMessage(Strings.red + "You don't have enough tokens!");
                        return true;
                    }


                    CI.addToken(p, tokenCommand);


                    return true;
                }
            } else if (args[0].equalsIgnoreCase("redeem")) {
                if (p.hasPermission("tokens.redeem")) {

                    //check if player
                    if (p.getItemInHand().getType() != Material.DOUBLE_PLANT) {
                        p.sendMessage(Strings.red + "You don't have any Tokens in your hand!");
                        return true;
                    }

                    CI.removeToken(p);


                    return true;
                }
            } else if (args[0].equalsIgnoreCase("balance")) {
                if (p.hasPermission("tokens.balance")) {

                    // Check if player exist, if not adding the player
                    tokenApi.playerCheck(p.getUniqueId());

                    int intTokens = cfgM.getPlayers().getInt(p.getUniqueId().toString() + ".tokens");
                    if (args.length == 1) {
                        p.sendMessage(Strings.green + "You have " + intTokens + " Tokens");
                        return true;
                    }


                    // Check if its a real player
                    Player playerCheck = Bukkit.getPlayerExact(args[1]);
                    if (playerCheck == null){
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }




                    String targetUUID;
                    targetUUID = Bukkit.getPlayerExact(args[1]).getUniqueId().toString();
                    int targetTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");

                    // Check if player exist, if not adding the player
                    tokenApi.playerCheck(UUID.fromString(targetUUID));

                    if (args.length == 2) {
                        p.sendMessage(Strings.green + F.getName(targetUUID) + " has " + targetTokens + " Tokens");
                        return true;
                    }


                    return true;
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (p.hasPermission("tokens.admin.add")) {


                    // shows the command usage
                    if (args.length == 1) {
                        p.sendMessage(Strings.red + "/tokens add <amount> <player>");
                        return true;
                    }

                    // check if the its a number instead of a character
                    try {
                        int tokens = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        int tokens;
                        p.sendMessage(Strings.red + "Use a valid number!");
                        return true;
                    }

                    // ...
                    if (args.length == 2) {
                        p.sendMessage(Strings.red + "Please specify a player!");
                        return true;
                    }

                    // Check if its a real player
                    Player playerCheck = Bukkit.getPlayerExact(args[2]);
                    if (playerCheck == null){
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }


                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getPlayerExact(args[2]).getUniqueId().toString();

                    String pathTokens;
                    pathTokens = String.valueOf(cfgM.getPlayers().get(targetUUID + ".tokens"));

                    int intTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");



                    /*
                    if (!cfgM.getPlayers().contains(targetUUID)) {
                        cfgM.getPlayers().set(targetUUID + ".tokens", 0);
                        cfgM.savePlayers();
                    }
                    */

                    // Check if player exist, if not adding the player
                    tokenApi.playerCheck(UUID.fromString(targetUUID));


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "Please enter a valid number.");
                        return true;
                    }



                    tokenApi.addTokens(UUID.fromString(targetUUID), p, tokenCommand);
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("remove")) {
                if (p.hasPermission("tokens.admin.remove")) {

                    // shows the command usage
                    if (args.length == 1) {
                        p.sendMessage(Strings.red + "/tokens remove <amount> <player>");
                        return true;
                    }

                    // check if the its a number instead of a character
                    try {
                        int tokens = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        int tokens;
                        p.sendMessage(Strings.red + "Use a valid number!");
                        return true;
                    }

                    // ...
                    if (args.length == 2) {
                        p.sendMessage(Strings.red + "Please specify a player!");
                        return true;
                    }

                    // Check if its a real player
                    Player playerCheck = Bukkit.getPlayerExact(args[2]);
                    if (playerCheck == null){
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }



                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getPlayerExact(args[2]).getUniqueId().toString();

                    String pathTokens;
                    pathTokens = String.valueOf(cfgM.getPlayers().get(targetUUID + ".tokens"));

                    int intTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");




                    // Check if player exist, if not adding the player
                    if (!cfgM.getPlayers().contains(targetUUID)) {
                        cfgM.getPlayers().set(targetUUID + ".tokens", 0);
                        cfgM.savePlayers();
                    }


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "Please enter a valid number.");
                        return true;
                    }


                    // Check if the player has enough coins to remove
                    if (tokenCommand > intTokens) {
                        p.sendMessage(Strings.red + F.getName(targetUUID) + " does not have that much Tokens!");
                        return true;
                    }


                    tokenApi.removeTokens(UUID.fromString(targetUUID), p, tokenCommand);
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("set")) {
                if (p.hasPermission("tokens.admin.set")) {

                    // shows the command usage
                    if (args.length == 1) {
                        p.sendMessage(Strings.red + "/tokens set <amount> <player>");
                        return true;
                    }

                    // check if the its a number instead of a character
                    try {
                        int tokens = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        int tokens;
                        p.sendMessage(Strings.red + "Use a valid number!");
                        return true;
                    }


                    if (args.length == 2) {
                        p.sendMessage(Strings.red + "Please specify a player!");
                        return true;
                    }


                    // Check if its a real player
                    Player playerCheck = Bukkit.getPlayerExact(args[2]);
                    if (playerCheck == null){
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }


                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getPlayerExact(args[2]).getUniqueId().toString();

                    String pathTokens;
                    pathTokens = String.valueOf(cfgM.getPlayers().get(targetUUID + ".tokens"));

                    int intTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");


                    // If 0 it will not execute
                    if (tokenCommand < 0) {
                        p.sendMessage(Strings.red + "Please don't enter a minus number!.");
                        return true;
                    }



                    tokenApi.setTokens(UUID.fromString(targetUUID), p, tokenCommand);
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("pay")) {
                if (p.hasPermission("tokens.pay")) {

                    // shows the command usage
                    if (args.length == 1) {
                        p.sendMessage(Strings.red + "/tokens pay <amount> <player>");
                        return true;
                    }

                    // check if the its a number instead of a character
                    try {
                        int tokens = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        int tokens;
                        p.sendMessage(Strings.red + "Use a valid number!");
                        return true;
                    }

                    // ...
                    if (args.length == 2) {
                        p.sendMessage(Strings.red + "Please specify a player!");
                        return true;
                    }

                    // Check if its a real player
                    Player playerCheck = Bukkit.getPlayerExact(args[2]);
                    if (playerCheck == null){
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }

                    String PA;
                    PA = String.valueOf(p.getUniqueId());

                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getPlayerExact(args[2]).getUniqueId().toString();

                    String pathTokens;
                    pathTokens = String.valueOf(cfgM.getPlayers().get(targetUUID + ".tokens"));

                    int targetTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");
                    int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");



                    // if he try to pay himself it wil exit the command
                    String potentialPlayer = args[2];
                    if (Bukkit.getPlayerExact(potentialPlayer) == p.getPlayer()) {
                        p.sendMessage(Strings.red + "You cannot pay yourself");
                        return true;
                    }


                    // Check if player exist, if not adding the player
                    tokenApi.playerCheck(UUID.fromString(targetUUID));


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "You cannot pay with 0.");
                        return true;
                    }

                    // Check if the player has enough coins to remove
                    if (tokenCommand > playerTokens) {
                        p.sendMessage(Strings.red + "You don't have enough tokens!");
                        return true;
                    }


                    cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens - tokenCommand);
                    cfgM.getPlayers().set(targetUUID + ".tokens", tokenCommand + targetTokens);
                    cfgM.savePlayers();


                    p.sendMessage(Strings.green + "You payed " + F.getName(targetUUID) + " " + tokenCommand);

                    Player playerReceiver = Bukkit.getPlayerExact(F.getName(targetUUID));
                    playerReceiver.sendMessage(Strings.green + "You received " + tokenCommand + " Tokens from " + p.getName());


                    return true;

                }
            } else if (args[0].equalsIgnoreCase("bonus")) {
                if (p.hasPermission("tokens.admin.bonus")) {

                    // shows the command usage
                    if (args.length == 1) {
                        p.sendMessage(Strings.red + "/tokens bonus <amount>");
                        return true;
                    }

                    // check if the its a number instead of a character
                    try {
                        int tokens = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        int tokens;
                        p.sendMessage(Strings.red + "Use a valid number!");
                        return true;
                    }

                    int tokenCommand = Integer.parseInt(args[1]);

                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "You cannot pay with 0.");
                        return true;
                    }


                    tokenApi.giveallTokens(p, tokenCommand);
                }

            } else if (args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("tokens.admin.reload")) {

                    plugin.saveDefaultConfig();
                    plugin.reloadConfig();

                    cfgM.savePlayers();
                    cfgM.reloadplayers();
                    p.sendMessage(Strings.greenI + "configuration files are reloaded");
                }

            } else if (args[0].equalsIgnoreCase("buy")) {
                if (p.hasPermission("tokens.buy")) {
                    if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
                        // shows the command usage
                        if (args.length == 1) {
                            p.sendMessage(Strings.red + "/tokens buy <tokens> ");
                            return true;
                        }

                        // check if the its a number instead of a character
                        try {
                            int tokens = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex) {
                            int tokens;
                            p.sendMessage(Strings.red + "Use a valid number!");
                            return true;
                        }

                        int token = Integer.parseInt(args[1]);

                        // If 0 it will not execute
                        if (token == 0) {
                            p.sendMessage(Strings.red + "You cannot buy 0 Tokens");
                            return true;
                        }

                        tokenApi.convertToTokens(p.getUniqueId(), token, p);
                    } else {
                        p.sendMessage(Strings.red + "Command is disabled!");
                    }

                    return true;
                }
            } else if (args[0].equalsIgnoreCase("sell")) {
                if (p.hasPermission("tokens.sell")) {
                    if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
                        // shows the command usage
                        if (args.length == 1) {
                            p.sendMessage(Strings.red + "/tokens sell <tokens> ");
                            return true;
                        }

                        // check if the its a number instead of a character
                        try {
                            int tokens = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex) {
                            int tokens;
                            p.sendMessage(Strings.red + "Use a valid number!");
                            return true;
                        }

                        int token = Integer.parseInt(args[1]);

                        // If 0 it will not execute
                        if (token == 0) {
                            p.sendMessage(Strings.red + "You cannot sell 0 Tokens");
                            return true;
                        }

                        tokenApi.convertToMoney(p.getUniqueId(), token, p);
                    } else {
                        p.sendMessage(Strings.red + "Command is disabled!");
                    }

                    return true;

                }
            }

            return true;
        }
        return true;
    }




    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String String, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("balance", "pay", "remove", "add", "set", "get", "redeem", "bonus", "reload", "buy", "sell");
        }

        return null;
    }

}
