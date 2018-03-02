package com.twanl.tokens;

import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commands implements CommandExecutor {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public ConfigManager cfgM;
    public Functions F;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        Player p = (Player) sender;

        F = new Functions(plugin);
        cfgM = new ConfigManager();
        cfgM.setup();


        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) {
                p.sendMessage(Strings.DgreenB + "Tokens " + plugin.getDescription().getVersion() + "\n"
                        + " \n"
                        + Strings.green + "This is plugin is in early access!\n"
                        + Strings.green + "Thank you for participate.\n"
                        + " \n"
                        + Strings.white + "There will be a lot of updates");

            } else if (args[0].equalsIgnoreCase("reset")) {

                p.sendMessage("test");
                return true;


            } else if (args[0].equalsIgnoreCase("add")) {
                if (p.hasPermission("tokens.admin.add")) {
                    String targetUUID;
                    int tokens = Integer.parseInt(args[1]);
                    targetUUID = Bukkit.getPlayerExact(args[2]).getUniqueId().toString();
                    int tokens1 = cfgM.getPlayers().getInt("uuid." + targetUUID + ".tokens");



                    if (!cfgM.getPlayers().contains("uuid." + targetUUID)) {
                        cfgM.getPlayers().set("uuid." + targetUUID + ".name", F.getName(targetUUID));
                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", 0);

                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", tokens1 + tokens);
                        cfgM.savePlayers();
                        p.sendMessage(Strings.green + tokens + " tokens are added to " + F.getName(targetUUID));
                    } else {
                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", tokens1 + tokens);
                        cfgM.savePlayers();
                        p.sendMessage(Strings.green + tokens + " tokens are added to " + F.getName(targetUUID));

                    }
                    return true;

                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (p.hasPermission("tokens.admin.remove")) {
                    String targetUUID;
                    int tokens = Integer.parseInt(args[1]);
                    targetUUID = Bukkit.getPlayerExact(args[2]).getUniqueId().toString();
                    int tokens1 = cfgM.getPlayers().getInt("uuid." + targetUUID + ".tokens");


                    if (!cfgM.getPlayers().contains("uuid." + targetUUID)) {
                        cfgM.getPlayers().set("uuid." + targetUUID + ".name", F.getName(targetUUID));
                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", 0);

                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", tokens1 - tokens);
                        cfgM.savePlayers();
                        p.sendMessage(Strings.green + tokens + " tokens are removed from " + F.getName(targetUUID));
                    } else {
                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", tokens1 - tokens);
                        cfgM.savePlayers();
                        p.sendMessage(Strings.green + tokens + " tokens are removed from " + F.getName(targetUUID));

                    }
                    return true;

                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (p.hasPermission("tokens.admin.set")) {
                    String targetUUID;
                    int tokens = Integer.parseInt(args[1]);
                    targetUUID = Bukkit.getPlayerExact(args[2]).getUniqueId().toString();


                    if (!cfgM.getPlayers().contains("uuid." + targetUUID)) {
                        cfgM.getPlayers().set("uuid." + targetUUID + ".name", F.getName(targetUUID));
                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", 0);

                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", tokens);
                        cfgM.savePlayers();
                        p.sendMessage(Strings.green + "Tokens set to " + tokens + " for " + F.getName(targetUUID));
                    } else {
                        cfgM.getPlayers().set("uuid." + targetUUID + ".tokens", tokens);
                        cfgM.savePlayers();
                        p.sendMessage(Strings.green + "Tokens set to " + tokens + " for " + F.getName(targetUUID));

                    }
                    return true;

                }

            }
            return true;
        }
        return true;
    }
}










