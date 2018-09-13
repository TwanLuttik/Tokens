package com.twanl.tokens.commands;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.items.TokenItem;
import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.menu.confirm;
import com.twanl.tokens.sql.SQLlib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Functions;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.loadManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Twan on 3/22/2018.
 **/

@SuppressWarnings("deprecation")
public class Commands implements CommandExecutor, TabCompleter {


    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager config = new ConfigManager();
    private TokenItem CI = new TokenItem();
    private Lib lib = new Lib();
    private Functions F = new Functions();
    private SQLlib sql = new SQLlib();
    public static HashMap<UUID, Integer> map = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Strings.logName + Strings.red + "Only a player can execute commands!");
            return true;
        }
        Player p = (Player) sender;

        config.setup();


        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) {
                if (p.hasPermission("tokens.tokens")) {
                    p.sendMessage(Strings.gray + "do " + Strings.green + "/tokens help" + Strings.gray + " for more information.");

                }
            } else if (args[0].equalsIgnoreCase("note")) {
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
                    int playerTokens = config.getPlayers().getInt(p.getUniqueId() + ".tokens");

                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "Please enter a number higher than 0");
                        return true;
                    }

                    // Check if the player has enough coins to remove
                    if (lib.sqlUse()) {
                        if (tokenCommand > sql.getTokens(p.getUniqueId())) {
                            p.sendMessage(Strings.red + "You don't have enough " + lib.getPrefix() + "!");
                            return true;
                        }
                    } else {
                        if (tokenCommand > playerTokens) {
                            p.sendMessage(Strings.red + "You don't have enough " + lib.getPrefix() + "!");
                            return true;
                        }
                    }


                    // check if the player has enough space in thear inventory
                    if (p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(Strings.red + "You don't have enough inventory space!");
                        return true;
                    }


                    CI.addTokenNote(p, tokenCommand);
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
                    UUID targetUUID;
                    targetUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId();


                    // Check if player exist, if not adding the player
                    //tokenApi.hasAccount(UUID.fromString(targetUUID));


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "Please enter a valid number.");
                        return true;
                    }


                    lib.addTokens(p.getUniqueId(), targetUUID, tokenCommand);
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

                    int intTokens = config.getPlayers().getInt(targetUUID + ".tokens");


                    // Check if player exist, if not adding the player
                    if (!config.getPlayers().contains(targetUUID)) {
                        config.getPlayers().set(targetUUID + ".tokens", 0);
                        config.savePlayers();
                    }


                    // If 0 it will not execute
                    if (tokenCommand == 0) {
                        p.sendMessage(Strings.red + "Please enter a valid number.");
                        return true;
                    }


                    // Check if the player has enough coins to remove
                    if (lib.sqlUse()) {
                        if (tokenCommand > sql.getTokens(UUID.fromString(targetUUID))) {
                            p.sendMessage(Strings.red + F.getName(targetUUID) + " does not have that much " + lib.getPrefix() + "!");
                            return true;
                        }
                    } else {
                        if (tokenCommand > intTokens) {
                            p.sendMessage(Strings.red + F.getName(targetUUID) + " does not have that much " + lib.getPrefix() + "!");
                            return true;
                        }
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

                    int playerTokens = config.getPlayers().getInt(p.getUniqueId() + ".tokens");


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
                    if (lib.sqlUse()) {
                        if (tokenCommand > sql.getTokens(p.getUniqueId())) {
                            p.sendMessage(Strings.red + "You don't have enough " + lib.getPrefix() + "!");
                            return true;
                        }

                    } else {
                        if (tokenCommand > playerTokens) {
                            p.sendMessage(Strings.red + "You don't have enough " + lib.getPrefix() + "!");
                            return true;
                        }
                    }

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

                    config.savePlayers();
                    config.reloadplayers();
                    loadManager.loadHashSet();

                    p.sendMessage(Strings.prefix + Strings.greenI + "configuration files are reloaded");
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

                    if (args.length == 1) {
                        p.sendMessage(Strings.DgrayBS + "                                     \n" +
                                Strings.greenB + "       Tokens " + Strings.green + plugin.getDescription().getVersion() + "\n" +
                                " \n" +
                                "     " + Strings.green + "Command Types");
                        plugin.nms.sendClickableHoverableMessage(p, Strings.gray + "  Default ", Strings.yellow + " [X]", Strings.yellowB + "Click To Navigate", "t help default");
                        plugin.nms.sendClickableHoverableMessage(p, Strings.gray + "  Admin ", Strings.yellow + " [X]", Strings.yellowB + "Click To Navigate", "t help admin");
                        plugin.nms.sendClickableHoverableMessage(p, Strings.gray + "  Bank ", Strings.yellow + " [X]", Strings.yellowB + "Click To Navigate", "t help bank");

                        p.sendMessage(" \n" +
                                Strings.DgrayBS + "                                     \n");

                    } else if (args[1].equalsIgnoreCase("default")) {

                        p.sendMessage(" \n" +
                                Strings.DgrayBS + "                                    \n" +
                                Strings.greenB + "       Tokens " + Strings.green + plugin.getDescription().getVersion() + "\n" +
                                " \n" +
                                "     " + Strings.green + "Default Commands\n" +
                                Strings.gray + "/tokens\n" +
                                Strings.gray + "/tokens balance\n" +
                                Strings.gray + "/tokens balance <player>\n" +
                                Strings.gray + "/tokens pay <amount> <player>\n" +
                                Strings.gray + "/tokens note <amount>\n" +
                                Strings.gray + "/tokens buy <amount>\n" +
                                Strings.gray + "/tokens sell <amount>\n" +
                                Strings.gray + "/tokens top\n" +
                                Strings.gray + "/tokens help\n" +
                                " \n");
                        plugin.nms.sendClickableMessage(p, "", Strings.yellow + "<-", "t help");
                        p.sendMessage(Strings.DgrayBS + "                                     \n");


                    } else if (args[1].equalsIgnoreCase("admin")) {

                        p.sendMessage(" \n" +
                                Strings.DgrayBS + "                                    \n" +
                                Strings.greenB + "       Tokens " + Strings.green + plugin.getDescription().getVersion() + "\n" +
                                " \n" +
                                "     " + Strings.green + "Admin Commands\n" +
                                Strings.gray + "/tokens reload\n" +
                                Strings.gray + "/tokens convert sql\n" +
                                Strings.gray + "/tokens remove <amount> <player>\n" +
                                Strings.gray + "/tokens add <amount> <player>\n" +
                                Strings.gray + "/tokens set <amount> <player>\n" +
                                " \n");
                        plugin.nms.sendClickableMessage(p, "", Strings.yellow + "<-", "t help");
                        p.sendMessage(Strings.DgrayBS + "                                     \n");

                    } else if (args[1].equalsIgnoreCase("bank")) {
                        p.sendMessage(" \n" +
                                Strings.DgrayBS + "                                    \n" +
                                Strings.greenB + "       Tokens " + Strings.green + plugin.getDescription().getVersion() + "\n" +
                                " \n" +
                                "     " + Strings.green + "Bank Commands\n" +
                                Strings.gray + "/tokens bank\n" +
                                Strings.gray + "/tokens bank create\n" +
                                Strings.gray + "/tokens bank delete\n" +
                                Strings.gray + "/tokens bank leave\n" +
                                Strings.gray + "/tokens bank user\n" +
                                Strings.gray + "/tokens bank user invite <player>\n" +
                                Strings.gray + "/tokens bank user remove <player>\n" +
                                Strings.gray + "/tokens bank user makeowner <player>\n" +
                                " \n");
                        plugin.nms.sendClickableMessage(p, "", Strings.yellow + "<-", "t help");
                        p.sendMessage(Strings.DgrayBS + "                                     \n");
                    }
                }

            } else if (args[0].equalsIgnoreCase("top")) {
                if (p.hasPermission("tokens.top")) {


                    if (lib.sqlUse()) {
                        sql.getAllRowstoHashMap();
//                        HashMap<UUID, Integer> map = new HashMap<UUID, Integer>();

//                        for (String i : config.getPlayers().getConfigurationSection("").getKeys(false)) {
//
//                            int getTokens = config.getPlayers().getInt(i + ".tokens");
//                            UUID uuid = UUID.fromString(i);
//
//                            map.put(uuid, getTokens);
//                        }

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


                    } else {
//                        HashMap<UUID, Integer> map = new HashMap<UUID, Integer>();

                        for (String i : config.getPlayers().getConfigurationSection("").getKeys(false)) {

                            int getTokens = config.getPlayers().getInt(i + ".tokens");
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
            } else if (args[0].equalsIgnoreCase("convert")) {
                if (p.hasPermission("tokens.admin.convert")) {

                    if (args.length == 1) {
                        p.sendMessage(Strings.prefix + Strings.gray + "Only from file -> sql supported");
                        return true;
                    }

                    String convertTO = args[1];
                    if (convertTO.equals("sql")) {


                        for (String key : config.getPlayers().getConfigurationSection("").getKeys(false)) {
                            UUID uuid = UUID.fromString(key);
                            int tokens = config.getPlayers().getInt(key + ".tokens");

                            OfflinePlayer p1 = Bukkit.getOfflinePlayer(uuid);

                            if (sql.hasAccount(p1.getUniqueId())) {
                                sql.setTokens(uuid, tokens);
                            } else {
                                sql.addPlayer(uuid);
                                sql.setTokens(uuid, tokens);
                            }

                        }
                        p.sendMessage(Strings.prefix + "Data from flat-file converted to the sql database.");


                    } else {
                        p.sendMessage(Strings.prefix + Strings.red + "You can only convert to " + Strings.redB + "sql");
                        return true;
                    }


                }

            } else if (args[0].equalsIgnoreCase("bank")) {
                if (p.hasPermission("tokens.bank")) {

                    if (args.length == 1) {

                        if (lib.playerIsRegisteredInBank(p.getUniqueId())) {
                            p.sendMessage(Strings.DgrayBS + "                           \n" + Strings.reset +
                                    Strings.greenB + "     Bank" + Strings.blue + " #" + lib.userBankID(p.getUniqueId()) + "\n" +
                                    " \n" +
                                    Strings.gray + "Owner: " + Strings.white + lib.getBankOwner(p.getUniqueId()) + "\n" +
                                    Strings.gray + "Bank Balance: " + Strings.white + lib.bankBalance(p.getUniqueId()) + "\n" +
                                    Strings.gray + "Sub Users:");

                            if (!lib.bankHasUsers(p.getUniqueId())) {
                                p.sendMessage(Strings.green + "  You can invite members!");
                            } else {
                                // get all the user from the bank
                                for (String key : config.getBank().getStringList(lib.userBankID(p.getUniqueId()) + ".users")) {
                                    String[] pName = key.split(" ");
                                    p.sendMessage(Strings.gray + " - " + Strings.white + pName[1]);
                                }
                            }

                            p.sendMessage(" \n" +
                                    Strings.DgrayBS + "                           ");
                        } else {
                            p.sendMessage(Strings.red + "You are not a particient of a bank. You can create your own bank with /tokens bank create");
                        }


                    } else if (args[1].equalsIgnoreCase("create")) {
                        if (p.hasPermission("tokens.bank.create")) {


                            if (lib.playerIsRegisteredInBank(p.getUniqueId())) {
                                if (lib.isBankOwner(p.getUniqueId())) {
                                    p.sendMessage(Strings.red + "You are already a owner from a bank!");
                                } else {
                                    p.sendMessage(Strings.red + "You already a member form a bank!");
                                }
                                return true;
                            }

                            lib.createBank(p.getUniqueId());
                            p.sendMessage(Strings.green + "You succsessfully created a new bank!");

                        }
                    } else if (args[1].equalsIgnoreCase("leave")) {
                        if (p.hasPermission("tokens.bank.leave")) {
                            // check if the player is registrated in abank
                            if (lib.playerIsRegisteredInBank(p.getUniqueId())) {
                                // check if the player is a member
                                if (!lib.isBankOwner(p.getUniqueId())) {
                                    lib.bankUserRemove(p.getUniqueId());
                                    p.sendMessage(Strings.green + "You successfully left the bank");
                                    return true;
                                } else {
                                    p.sendMessage(Strings.red + "Owner's cannot leave the bank, If you want to delete the bank do /tokens bank delete!");
                                    return true;
                                }
                            } else {
                                p.sendMessage(Strings.red + "You are not registrated in a bank!");
                                return true;
                            }
                        }

                    } else if (args[1].equalsIgnoreCase("delete")) {
                        if (p.hasPermission("tokens.bank.delete")) {
                            // check if the player registrated in a bank
                            if (lib.playerIsRegisteredInBank(p.getUniqueId())) {
                                // check if the player is the owner
                                if (lib.isBankOwner(p.getUniqueId())) {
                                    lib.bankDelete(p.getUniqueId());
                                    p.sendMessage(Strings.green + "Succsessfully deleted the bank!");
                                    return true;
                                } else {
                                    p.sendMessage(Strings.red + "You are not the owner of the bank!");
                                    return true;
                                }
                            } else {
                                p.sendMessage(Strings.red + "You are not registrated in a bank!");
                                return true;
                            }
                        }

                    } else if (args[1].equalsIgnoreCase("user")) {

                        // check if the sender is the owner of the bank
                        if (!lib.isBankOwner(p.getUniqueId())) {
                            p.sendMessage(Strings.red + "You are not the owner of the bank!");
                            return true;
                        }

                        if (args.length == 2) {
                            p.sendMessage(Strings.prefix + "Usage: /tokens bank user add/remove");
                            return true;

                        } else if (args[2].equalsIgnoreCase("invite")) {
                            if (p.hasPermission("tokens.bank.user.invite")) {


                                // check if the player name is filled in for preventing errors
                                if (args.length == 3) {
                                    p.sendMessage(Strings.red + "You need specify a player!");
                                    return true;
                                }

                                Player pInvite = Bukkit.getPlayer(args[3]);


                                // check if he is trying to invite himself
                                if (pInvite == p.getPlayer()) {
                                    p.sendMessage(Strings.red + "You cannot invite yourself!");
                                    return true;
                                }

                                // Check if the player is already registrated to a bank
                                if (lib.playerIsRegisteredInBank(pInvite.getUniqueId())) {
                                    p.sendMessage(Strings.red + "Player already registrated to a bank!");
                                    return true;
                                }

                                hasInvite.put(pInvite, true);
                                inviteSender.put(pInvite, p);
                                p.sendMessage(Strings.green + "Invitation sent.");
                                plugin.nms.sendClickableMessage(pInvite, p.getName() + " invited you for joining his bank, ", Strings.greenB + "JOIN", "tokens bank accept");
                                return true;
                            }

                        } else if (args[2].equalsIgnoreCase("remove")) {
                            if (p.hasPermission("tokens.bank.user.remove")) {

                                // check if the player name is filled in for preventing errors
                                if (args.length == 3) {
                                    p.sendMessage(Strings.red + "You need specify a player!");
                                    return true;
                                }

                                OfflinePlayer pTarget = Bukkit.getOfflinePlayer(args[3]);


                                // check if he is not trying to remove himself
                                if (pTarget == p.getPlayer()) {
                                    p.sendMessage(Strings.red + "You cannot remove yourself from your bank!");
                                    return true;
                                }

                                // Check if the player is a member of your bank
                                if (lib.userBankID(p.getUniqueId()) != lib.userBankID(pTarget.getUniqueId())) {
                                    p.sendMessage(Strings.red + pTarget.getName() + " is not a member of you bank!");
                                    return true;
                                }


                                lib.bankUserRemove(pTarget.getUniqueId());
                                p.sendMessage("You succsesfully removed " + Strings.green + pTarget.getName() + Strings.white + " from the bank!");


                            }
                        } else if (args[2].equalsIgnoreCase("makeowner")) {
                            if (p.hasPermission("tokens.bank.user.makeowner")) {

                                confirm a = new confirm();
                                a.popupMenu(p);

                                if (!a.a.get(p)) {
                                    p.sendMessage("is false!!");
                                    return true;
                                }

                                // check if the player name is filled in for preventing errors
                                if (args.length == 3) {
                                    p.sendMessage(Strings.red + "You need specify a player!");
                                    return true;
                                }

                                // define the target
                                Player pTarget = Bukkit.getPlayer(args[3]);


                                // check if the player is a member of the bank
                                if (!lib.playerIsRegisteredInBank(pTarget.getUniqueId())) {
                                    p.sendMessage(Strings.red + "User is not a member of your bank!");
                                    return true;
                                }

                                // check if he is not tryin to re-own himself
                                if (pTarget == p.getPlayer()) {
                                    p.sendMessage(Strings.red + "You cannot make yourself owner again!");
                                    return true;
                                }


//                                plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin) this, () ->
//                                        p.sendMessage("test")
//
//                                        , 60L);// 60 L == 3 sec, 20 ticks == 1 sec



                                lib.bankOwnershipTransfer(p.getUniqueId(), pTarget.getUniqueId());
                                p.sendMessage(Strings.green + "Succsessfully transfered the new owner to " + pTarget.getName());

                            }

                        }
                    } else if (args[1].equalsIgnoreCase("accept")) {
                        if (p.hasPermission("tokens.bank.user.accept")) {

                            // check if the player has an invite
                            if (!hasInvite.containsKey(p)) {
                                p.sendMessage(Strings.red + "You tried to accept an already joined bank!");
                                return true;
                            }


                            if (hasInvite.containsKey(p) || hasInvite.get(p)) {

                                hasInvite.remove(p);
                                p.sendMessage(Strings.green + "You accepted the invite.");

                                lib.bankUserAdd(inviteSender.get(p).getUniqueId(), p.getUniqueId());

                                // send the inviteSender an message that the player has accept the invite
                                Player pInviteSender = inviteSender.get(p);
                                pInviteSender.sendMessage(Strings.green + p.getName() + " has accepted your invite.");

                                return true;
                            }
                        }

                    } else if (args[1].equalsIgnoreCase("add")) {
                        if (p.hasPermission("tokens.bank.add")) {


                            // check if the player has no bank
                            if (!lib.playerIsRegisteredInBank(p.getUniqueId())) {
                                p.sendMessage(Strings.red + "You are not a member of a bank!");
                                return true;
                            }


                            // check if its a valid number
                            try {
                                Integer.parseInt(args[2]);
                            } catch (Exception e) {
                                p.sendMessage(Strings.red + "use a valid number!");
                                return true;
                            }


                            int commandINT = Integer.parseInt(args[2]);

                            // check if the player has enough balance
                            if (commandINT > lib.balanceInt(p.getUniqueId())) {
                                p.sendMessage(Strings.red + "You don't have enough to deposit!");
                                return true;
                            }


                            lib.bankAddBalance(p.getUniqueId(), commandINT);
                            lib.removeTokens(null, p.getUniqueId(), commandINT);
                            p.sendMessage(Strings.green + "Deposit successfully " + Strings.white + commandINT + " " + lib.getPrefix());
                        }
                    } else if (args[1].equalsIgnoreCase("get")) {
                        if (p.hasPermission("tokens.bank.get")) {


                            // check if its a valid number
                            try {
                                Integer.parseInt(args[2]);
                            } catch (Exception e) {
                                p.sendMessage(Strings.red + "use a valid number!");
                                return true;
                            }

                            int commandINT = Integer.parseInt(args[2]);


                            // check if the bank has enough to withdraw
                            if (commandINT > lib.bankBalance(p.getUniqueId())) {
                                p.sendMessage(Strings.red + "The bank doesn't have enough balance to get the requested amount!");
                                return true;

                            }

                            lib.bankRemoveBalance(p.getUniqueId(), commandINT);
                            lib.addTokens(null, p.getUniqueId(), commandINT);
                            p.sendMessage(Strings.green + "You withdraw " + Strings.white + commandINT + " " + lib.getPrefix());

                        }
                    }


                }
            }
            return true;
        }
        return true;
    }

    private HashMap<Player, Boolean> hasInvite = new HashMap<>();
    private HashMap<Player, Player> inviteSender = new HashMap<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String String, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("balance", "pay", "remove", "add", "set", "get", "bonus", "reload", "buy", "sell", "help", "bank", "top", "convert");
        }

        return null;
    }

}

