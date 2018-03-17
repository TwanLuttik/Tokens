package com.twanl.tokens;

import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;


public class Commands implements CommandExecutor {



    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public ConfigManager cfgM;




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;

        cfgM = new ConfigManager();
        cfgM.setup();


        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) {
                p.sendMessage(Strings.goldB + "       Tokens " + Strings.gold + plugin.getDescription().getVersion() + "\n"
                        + " \n"
                        + Strings.gold + "     Users Commands\n"
                        + Strings.white + "/tokens balance\n"
                        + Strings.white + "/tokens balance <player>\n"
                        + Strings.white + "/tokens pay <amount> <player>\n"
                        + " \n"
                        + Strings.gold + "     Admin Commands\n"
                        + Strings.white + "/tokens remove <amount> <player>\n"
                        + Strings.white + "/tokens add <amount> <player>\n"
                        + Strings.white + "/tokens set <amount> <player>\n");

            } else if (args[0].equalsIgnoreCase("balance")) {
                if (p.hasPermission("tokens.balance")) {
                    int intTokens = cfgM.getPlayers().getInt(p.getUniqueId().toString() + ".tokens");
                    if (args.length == 1) {
                        p.sendMessage(Strings.green + "You have " + intTokens + " Tokens");
                        return true;
                    }

                    String targetUUID;
                    targetUUID = Bukkit.getPlayerExact(args[1]).getUniqueId().toString();
                    int targetTokens = cfgM.getPlayers().getInt(targetUUID + ".tokens");







                    if (args.length == 2) {
                        p.sendMessage(Strings.green + getName(targetUUID) + " has " + targetTokens + " Tokens");
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


                    cfgM.getPlayers().set(targetUUID + ".tokens", intTokens + tokenCommand);
                    cfgM.savePlayers();

                    p.sendMessage(Strings.green + tokenCommand + " Tokens are added to " + getName(targetUUID));



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
                        p.sendMessage(Strings.red + getName(targetUUID) + " does not have that much Tokens!");
                        return true;
                    }

                    cfgM.getPlayers().set(targetUUID + ".tokens", intTokens - tokenCommand);
                    cfgM.savePlayers();

                    p.sendMessage(Strings.green + tokenCommand + " Tokens are removed from " + getName(targetUUID));


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

                    // ...
                    if (args.length == 2) {
                        p.sendMessage(Strings.red + "Please specify a player!");
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



                    cfgM.getPlayers().set(targetUUID + ".tokens", tokenCommand);
                    cfgM.savePlayers();

                    p.sendMessage(Strings.green + tokenCommand + " Tokens are set to " + getName(targetUUID));


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
                    if (!cfgM.getPlayers().contains(targetUUID)) {
                        cfgM.getPlayers().set(targetUUID + ".tokens", 0);
                        cfgM.savePlayers();
                    }


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "You cannot pay with 0.");
                        return true;
                    }

                    // Check if the player has enough coins to remove
                    if (tokenCommand > playerTokens) {
                        p.sendMessage(Strings.red +"You don't have enough tokens!");
                        return true;
                    }


                    cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens - tokenCommand);
                    cfgM.getPlayers().set(targetUUID + ".tokens", tokenCommand + targetTokens);
                    cfgM.savePlayers();


                    p.sendMessage(Strings.green + "You payed " + getName(targetUUID) + " " + tokenCommand);

                    Player playerReceiver = Bukkit.getPlayerExact(getName(targetUUID));
                    playerReceiver.sendMessage(Strings.green + "You received " + tokenCommand + " Tokens from " + p.getName());


                    return true;

                }
            }

            return true;
        }
        return true;
    }




    // convert UUID to playername(String)
    public String getName(String uuid) {
        String url = "https://api.mojang.com/user/profiles/"+uuid.replace("-", "")+"/names";
        try {
            @SuppressWarnings("deprecation")
            String nameJson = IOUtils.toString(new URL(url));
            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
            String playerSlot = nameValue.get(nameValue.size()-1).toString();
            JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
            return nameObject.get("name").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
