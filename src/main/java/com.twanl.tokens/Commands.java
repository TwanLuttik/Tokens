package com.twanl.tokens;

import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public ConfigManager cfgM;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) {
                p.sendMessage(Strings.DgreenB + "Tokens " + plugin.getDescription().getVersion() + "\n"
                        + " \n"
                        + Strings.green + "This is plugin is in early access!\n"
                        + Strings.green + "Thank you for participate.\n"
                        + " \n"
                        + Strings.white + "There will be a lot of updates");

            }
        }




        return true;
    }




}
