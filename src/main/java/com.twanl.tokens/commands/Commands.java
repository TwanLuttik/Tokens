package com.twanl.tokens.commands;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.api.TokensAPI;
import com.twanl.tokens.items.TokenItem;
import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Functions;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokenshop.TokenShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

/**
 * Created by Twan on 3/22/2018.
 **/

@SuppressWarnings("deprecation")
public  class Commands implements CommandExecutor, TabCompleter {


    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager cfgM = new ConfigManager();
    private TokenItem CI = new TokenItem();
    private Lib lib = new Lib();
    private Functions F = new Functions();




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Strings.logName + Strings.red + "Only a player can execute commands!");
            return true;
        }

        Player p = (Player) sender;

        cfgM.setup();


        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) {
                if (p.hasPermission("tokens.tokens")) {


                    String transactionPlayer = lib.transactionPlayer(p.getUniqueId());
                    String transactionDate = lib.transactionDate(p.getUniqueId());
                    String transactionAmount = lib.transactionAmount(p.getUniqueId());

                    String PlayerTokens = String.valueOf(lib.balanceInt(p.getUniqueId()));


                    p.sendMessage(Strings.DgrayBS + "-                                    \n"
                            + Strings.gray + "Balance: " + Strings.green + PlayerTokens + " " + lib.getPrefix() + "\n"
                            + Strings.gray + "Latest received transaction:\n"
                            + Strings.gray + "  Data: " + Strings.green + transactionDate + "\n"
                            + Strings.gray + "  From: " + Strings.green + transactionPlayer + "\n"
                            + Strings.gray + "  Amount: " + Strings.green + transactionAmount + "\n"
                            + Strings.DgrayBS + "-                                    \n");


                }
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
                        p.sendMessage(Strings.red + "You don't have enough " + lib.getPrefix() + "!");
                        return true;
                    }

                    if (p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(Strings.red + "You don't have enough inventory space!");
                        return true;
                    }


                    CI.addToken(p, tokenCommand);
                }
            } else if (args[0].equalsIgnoreCase("redeem")) {
                if (p.hasPermission("tokens.redeem")) {

                    //check if player
                    if (p.getItemInHand().getType() != Material.DOUBLE_PLANT) {
                        p.sendMessage(Strings.red + "You don't have any Tokens in your hand!");
                        return true;
                    }


                    CI.removeToken(p);


                }
            } else if (args[0].equalsIgnoreCase("balance")) {
                if (p.hasPermission("tokens.balance")) {

                    // Check if player exist, if not adding the player
                    //tokenApi.hasAccount(p.getUniqueId());

                    if (args.length == 1) {
                        lib.balance(p.getUniqueId());
                        return true;
                    }


                    // Check if its a real player
                    OfflinePlayer playerCheck = Bukkit.getOfflinePlayer(args[1]);
                    if (playerCheck == null) {
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }


                    String targetUUID;
                    targetUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();

                    // Check if player exist, if not adding the player
                    //tokenApi.hasAccount(UUID.fromString(targetUUID));


                    if (args.length == 2) {
                        lib.balance(p.getUniqueId(), UUID.fromString(targetUUID));
                        return true;
                    }


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
                    OfflinePlayer playerCheck = Bukkit.getOfflinePlayer(args[2]);
                    if (playerCheck == null) {
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }


                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();


                    // Check if player exist, if not adding the player
                    //tokenApi.hasAccount(UUID.fromString(targetUUID));


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "Please enter a valid number.");
                        return true;
                    }


                    lib.addTokens(UUID.fromString(targetUUID), p.getUniqueId(), tokenCommand);
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
                    OfflinePlayer playerCheck = Bukkit.getOfflinePlayer(args[2]);
                    if (playerCheck == null) {
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }


                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

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
                        p.sendMessage(Strings.red + F.getName(targetUUID) + " does not have that much " + lib.getPrefix() + "!");
                        return true;
                    }


                    lib.removeTokens(UUID.fromString(targetUUID), p.getUniqueId(), tokenCommand);
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
                    OfflinePlayer playerCheck = Bukkit.getOfflinePlayer(args[2]);
                    if (playerCheck == null) {
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }


                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                    // If 0 it will not execute
                    if (tokenCommand < 0) {
                        p.sendMessage(Strings.red + "Please don't enter a minus number!.");
                        return true;
                    }


                    lib.setTokens(UUID.fromString(targetUUID), p.getUniqueId(), tokenCommand);
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
                    OfflinePlayer playerCheck = Bukkit.getOfflinePlayer(args[2]);
                    if (playerCheck == null) {
                        p.sendMessage(Strings.red + "Player offline or invalid!");
                        return true;
                    }


                    int tokenCommand = Integer.parseInt(args[1]);
                    String targetUUID;
                    targetUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                    int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");


                    // if he try to pay himself it wil exit the command
                    String potentialPlayer = args[2];
                    if (Bukkit.getPlayerExact(potentialPlayer) == p.getPlayer()) {
                        p.sendMessage(Strings.red + "You cannot pay yourself");
                        return true;
                    }


                    // Check if player exist, if not adding the player
                    //tokenApi.hasAccount(UUID.fromString(targetUUID));


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "You cannot pay with 0.");
                        return true;
                    }

                    // Check if the player has enough coins to remove

                    if (tokenCommand > playerTokens) {
                        p.sendMessage(Strings.red + "You don't have enough " + lib.getPrefix() + "!");
                        return true;
                    }

                    //tokenApi.transactionSuccess(p.getUniqueId(), UUID.fromString(targetUUID), tokenCommand);
                    lib.payPlayer(p.getUniqueId(), UUID.fromString(targetUUID), tokenCommand);

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


                    lib.giveallTokens(tokenCommand);
                }

            } else if (args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("tokens.admin.reload")) {


                    plugin.saveDefaultConfig();
                    plugin.reloadConfig();

                    cfgM.savePlayers();
                    cfgM.reloadplayers();

                    if (getServer().getPluginManager().getPlugin("TokenShop") != null) {
                        tshopApi.menuApi.saveData();
                    }

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
                            p.sendMessage(Strings.red + "You cannot buy 0 " + lib.getPrefix());
                            return true;
                        }

                        lib.convertToTokens(p.getUniqueId(), token);
                    } else {
                        p.sendMessage(Strings.red + "Command is disabled!");
                    }

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
                            p.sendMessage(Strings.red + "You cannot sell 0 " + lib.getPrefix());
                            return true;
                        }

                        lib.convertToMoney(p.getUniqueId(), token);
                    } else {
                        p.sendMessage(Strings.red + "Command is disabled!");
                    }


                }
            } else if (args[0].equalsIgnoreCase("help")) {
                if (p.hasPermission("tokens.help")) {


                    p.sendMessage(Strings.DgrayBS + "-                                    \n"
                            + Strings.greenB + "       Tokens " + Strings.green + plugin.getDescription().getVersion() + "\n"
                            + " \n"
                            + "     " + Strings.green + "Users Commands\n" + Strings.reset
                            + Strings.gray + "/tokens balance\n"
                            + Strings.gray + "/tokens balance <player>\n"
                            + Strings.gray + "/tokens pay <amount> <player>\n"
                            + Strings.gray + "/tokens get <amount>\n"
                            + Strings.gray + "/tokens redeem\n"
                            + Strings.gray + "/tokens buy <amount>\n"
                            + Strings.gray + "/tokens sell <amount>\n"
                            + Strings.gray + "/tokens top\n"
                            + Strings.gray + "/tokens shop\n"
                            + Strings.gray + "/tokens help"
                            + " \n"
                            + "     " + Strings.green + "Admin Commands\n" + Strings.reset
                            + Strings.gray + "/tokens reload\n"
                            + Strings.gray + "/tokens bonus <amount>\n"
                            + Strings.gray + "/tokens remove <amount> <player>\n"
                            + Strings.gray + "/tokens add <amount> <player>\n"
                            + Strings.gray + "/tokens set <amount> <player>\n"
                            + Strings.DgrayBS + "-                                    \n");

                }
            } else if (args[0].equalsIgnoreCase("shop")) {
                if (p.hasPermission("tokens.shop")) {
                    if (args.length == 1) {
                        if (getServer().getPluginManager().getPlugin("TokenShop") != null) {

                            //tshopApi.menuApi.inventory(p);
                            tshopApi.menuApi.defaultInv(p);
                        }
                    } else if (args[1].equalsIgnoreCase("edit")) {

                        // shows the command usage
                        if (args.length == 2) {
                            p.sendMessage(Strings.red + "/tokens shop edit");
                            return true;
                        }


                    }
                }
            } else if (args[0].equalsIgnoreCase("top")) {
                if (p.hasPermission("tokens.top")) {


                    HashMap<UUID, Integer> map = new HashMap<UUID, Integer>();

                    for (String i : cfgM.getPlayers().getConfigurationSection("").getKeys(false)) {

                        int getTokens = cfgM.getPlayers().getInt(i + ".tokens");
                        UUID uuid = UUID.fromString(i);

                        map.put(uuid, getTokens);
                    }
                    Object[] a = map.entrySet().toArray();

                    //noinspection unchecked
                    Arrays.sort(a, new Comparator() {
                        @SuppressWarnings("unchecked")
                        public int compare(Object o1, Object o2) {
                            return ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue());
                        }
                    });

                    p.sendMessage(Strings.DgrayBS + "----------" + Strings.reset + " " + Strings.greenB + "Top 10" + Strings.reset + " " + Strings.DgrayBS + "-----------");

                    // i want to loop this "for loop" 10 times, how can i do thah?
                    int count = 0;
                    for (Object e : a) {
                        if (count == 10) {
                            break;
                        }

                        int playerPlace = count + 1;
                        @SuppressWarnings("unchecked") OfflinePlayer p1 = Bukkit.getOfflinePlayer(((Map.Entry<UUID, Integer>) e).getKey());
                        //noinspection unchecked
                        p.sendMessage(Strings.yellow + "#" + playerPlace + " " + Strings.gold + p1.getName() + Strings.gray + ": " + Strings.white + ((Map.Entry<UUID, Integer>) e).getValue());
                        count++;
                    }
                    p.sendMessage(Strings.DgrayBS + "----------------------------");


                }
            }

            return true;
        }
        return false;
    }


    private TokenShop tshopApi = (TokenShop) getServer().getPluginManager().getPlugin("TokenShop");


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String String, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("balance", "pay", "remove", "add", "set", "get", "redeem", "bonus", "reload", "buy", "sell", "help", "shop", "top");
        }

        return null;
    }

}

