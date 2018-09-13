package com.twanl.tokens.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class ConstructTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equals("bank")) {
            if (cmd.getName().equals("user")) {

                if (!args[0].equals("")) {

                }


            }

        }



        return null;
    }
}
