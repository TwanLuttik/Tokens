package com.twanl.tokens;

import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Commands implements CommandExecutor {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public ConfigManager cfgM;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        Player p = (Player) sender;

        cfgM = new ConfigManager();
        cfgM.setup();

        UUID uuid = p.getUniqueId();


        if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (args.length == 0) {
                p.sendMessage(Strings.DgreenB + "Tokens " + plugin.getDescription().getVersion() + "\n"
                        + " \n"
                        + Strings.green + "This is plugin is in early access!\n"
                        + Strings.green + "Thank you for participate.\n"
                        + " \n"
                        + Strings.white + "There will be a lot of updates");

            } else if (args[0].equalsIgnoreCase("add")) {
                int tokens = Integer.parseInt(args[1]);
                int tokens1 = cfgM.getPlayers().getInt("uuid." + uuid + ".tokens");

                if (!cfgM.getPlayers().contains("uuid." + uuid)) {

                    cfgM.getPlayers().set("uuid." + uuid + ".name", p.getName());
                    cfgM.getPlayers().set("uuid." + uuid + ".tokens", 0);

                    cfgM.getPlayers().set("uuid." + uuid + ".tokens", tokens1 + tokens);
                    cfgM.savePlayers();
                    p.sendMessage(Strings.green + tokens + " tokens are added!");
                } else {
                    cfgM.getPlayers().set("uuid." + uuid + ".tokens", tokens1 + tokens);
                    cfgM.savePlayers();
                    p.sendMessage(Strings.green + tokens + " tokens are added!");

                }
                return true;

            } else if (args[0].equalsIgnoreCase("remove")) {
                int tokens = Integer.parseInt(args[1]);
                int tokens1 = cfgM.getPlayers().getInt("uuid." + uuid + ".tokens");


                if (!cfgM.getPlayers().contains("uuid." + uuid)) {

                    cfgM.getPlayers().set("uuid." + uuid + ".name", p.getName());
                    cfgM.getPlayers().set("uuid." + uuid + ".tokens", 0);

                    cfgM.getPlayers().set("uuid." + uuid + ".tokens", tokens1 - tokens);
                    cfgM.savePlayers();
                    p.sendMessage(Strings.green + tokens + " tokens are removed!");
                } else {
                    cfgM.getPlayers().set("uuid." + uuid + ".tokens", tokens1 - tokens);
                    cfgM.savePlayers();
                    p.sendMessage(Strings.green + tokens + " tokens are removed!");

                }
                return true;



            }
        }
        return true;
    }

}







