package com.twanluttik.tokens;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides smart tab completion for the /tokens command.
 */
public class TokensTabCompleter implements TabCompleter {

    private static final List<String> BASE_SUBCOMMANDS = Arrays.asList(
            "balance", "gui", "bank", "check"
    );

    private static final List<String> ADMIN_SUBCOMMANDS = Arrays.asList(
            "give", "take", "set", "hologram", "info"
    );

    private static final List<String> BANK_SUBCOMMANDS = Arrays.asList(
            "create", "list", "deposit", "withdraw", "invite", "remove", "delete"
    );

    private static final List<String> CHECK_SUBCOMMANDS = Arrays.asList(
            "create", "redeem"
    );

    private static final List<String> AMOUNT_SUGGESTIONS = Arrays.asList(
            "100", "250", "500", "1000", "5000"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First level subcommands
            List<String> available = new ArrayList<>(BASE_SUBCOMMANDS);
            if (player.hasPermission("tokens.admin")) {
                available.addAll(ADMIN_SUBCOMMANDS);
            }
            StringUtil.copyPartialMatches(args[0], available, completions);
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();

            switch (sub) {
                case "bank":
                    StringUtil.copyPartialMatches(args[1], BANK_SUBCOMMANDS, completions);
                    break;

                case "check":
                    StringUtil.copyPartialMatches(args[1], CHECK_SUBCOMMANDS, completions);
                    break;

                case "balance":
                case "give":
                case "take":
                case "set":
                case "invite":
                case "remove":
                    // Player name suggestions
                    List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                    StringUtil.copyPartialMatches(args[1], playerNames, completions);
                    break;

                case "deposit":
                case "withdraw":
                case "delete":
                    // Suggest bank IDs (best effort, non-blocking feel)
                    // For simplicity we don't hit DB here to keep tab snappy.
                    // Users can type the ID they saw in /tokens gui or /tokens bank list.
                    break;
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            String sub2 = args[1].toLowerCase();

            if (sub.equals("bank") && (sub2.equals("deposit") || sub2.equals("withdraw") || sub2.equals("invite") || sub2.equals("remove"))) {
                if (sub2.equals("deposit") || sub2.equals("withdraw")) {
                    StringUtil.copyPartialMatches(args[2], AMOUNT_SUGGESTIONS, completions);
                } else {
                    // Player name for invite/remove
                    List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                    StringUtil.copyPartialMatches(args[2], playerNames, completions);
                }
            } else if ((sub.equals("give") || sub.equals("take") || sub.equals("set"))) {
                StringUtil.copyPartialMatches(args[2], AMOUNT_SUGGESTIONS, completions);
            } else if (sub.equals("check") && sub2.equals("create")) {
                StringUtil.copyPartialMatches(args[2], AMOUNT_SUGGESTIONS, completions);
            }
        } else if (args.length == 4) {
            String sub = args[0].toLowerCase();
            String sub2 = args[1].toLowerCase();

            if (sub.equals("bank") && (sub2.equals("deposit") || sub2.equals("withdraw"))) {
                StringUtil.copyPartialMatches(args[3], AMOUNT_SUGGESTIONS, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}
