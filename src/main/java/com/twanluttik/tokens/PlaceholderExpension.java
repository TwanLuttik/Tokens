package com.twanluttik.tokens;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class PlaceholderExpension extends PlaceholderExpansion {

    public PlaceholderExpension() {
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tokens";
    }

    @Override
    public @NotNull String getAuthor() {
        return "twanluttik";
    }

    @Override
    public @NotNull String getVersion() {
        return Tokens.getInstance() != null
                ? Tokens.getInstance().getDescription().getVersion()
                : "unknown";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getRequiredPlugin() {
        return "Tokens";
    }

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        try {
            String lower = params.toLowerCase().trim();

            // === Player Balance ===
            if (lower.equals("balance") || lower.equals("player_balance")) {
                int balance = Database.getTokens(player.getUniqueId().toString());
                return String.valueOf(balance);
            }
            if (lower.equals("balance_formatted") || lower.equals("player_balance_formatted")) {
                int balance = Database.getTokens(player.getUniqueId().toString());
                return formatShort(balance);
            }

            // === Player Bank Stats ===
            if (lower.equals("banks_count") || lower.equals("bank_count")) {
                List<Integer> banks = BankDatabase.getPlayerBanks(player.getUniqueId().toString());
                return String.valueOf(banks.size());
            }

            // === Specific Bank Placeholders ===
            // %tokens_bank_<id>_name%
            if (lower.startsWith("bank_") && lower.endsWith("_name")) {
                String idPart = lower.substring(5, lower.length() - 5);
                try {
                    int bankId = Integer.parseInt(idPart);
                    String name = BankDatabase.getBankName(bankId);
                    return name != null ? name : "";
                } catch (NumberFormatException e) {
                    return "";
                }
            }

            // %tokens_bank_<id>_balance%
            if (lower.startsWith("bank_") && lower.endsWith("_balance")) {
                String idPart = lower.substring(5, lower.length() - 8);
                try {
                    int bankId = Integer.parseInt(idPart);
                    int balance = BankDatabase.getBankBalance(bankId);
                    return balance >= 0 ? String.valueOf(balance) : "0";
                } catch (NumberFormatException e) {
                    return "0";
                }
            }

            // %tokens_bank_<id>_balance_formatted%
            if (lower.startsWith("bank_") && lower.endsWith("_balance_formatted")) {
                String idPart = lower.substring(5, lower.length() - 18);
                try {
                    int bankId = Integer.parseInt(idPart);
                    int balance = BankDatabase.getBankBalance(bankId);
                    return balance >= 0 ? formatNumber(balance) : "0";
                } catch (NumberFormatException e) {
                    return "0";
                }
            }

            // %tokens_bank_<id>_owner%
            if (lower.startsWith("bank_") && lower.endsWith("_owner")) {
                String idPart = lower.substring(5, lower.length() - 6);
                try {
                    int bankId = Integer.parseInt(idPart);
                    String ownerUuid = BankDatabase.getBankOwner(bankId);
                    if (ownerUuid == null) return "";
                    var offline = Bukkit.getOfflinePlayer(UUID.fromString(ownerUuid));
                    return offline.getName() != null ? offline.getName() : "Unknown";
                } catch (NumberFormatException e) {
                    return "";
                }
            }

            // %tokens_bank_<id>_member_count% or _members
            if (lower.startsWith("bank_") && (lower.endsWith("_member_count") || lower.endsWith("_members"))) {
                String idPart = lower.substring(5, lower.length() - (lower.endsWith("_member_count") ? 13 : 8));
                try {
                    int bankId = Integer.parseInt(idPart);
                    return String.valueOf(BankDatabase.getBankMembers(bankId).size());
                } catch (NumberFormatException e) {
                    return "0";
                }
            }

            // === Leaderboard / Top Players ===
            if (lower.equals("top_name_1") || lower.equals("top_name")) {
                return getTopPlayerName(1);
            }
            if (lower.startsWith("top_name_")) {
                try {
                    int rank = Integer.parseInt(lower.substring(9));
                    return getTopPlayerName(rank);
                } catch (NumberFormatException e) {
                    return "";
                }
            }

            if (lower.equals("top_balance_1") || lower.equals("top_balance")) {
                return getTopPlayerBalance(1);
            }
            if (lower.startsWith("top_balance_")) {
                try {
                    int rank = Integer.parseInt(lower.substring(12));
                    return getTopPlayerBalance(rank);
                } catch (NumberFormatException e) {
                    return "0";
                }
            }

            if (lower.equals("top_balance_formatted_1") || lower.equals("top_balance_formatted")) {
                return getTopPlayerBalanceFormatted(1);
            }
            if (lower.startsWith("top_balance_formatted_")) {
                try {
                    int rank = Integer.parseInt(lower.substring(21));
                    return getTopPlayerBalanceFormatted(rank);
                } catch (NumberFormatException e) {
                    return "0";
                }
            }

            // === Player Rank ===
            if (lower.equals("rank")) {
                int rank = Database.getPlayerRank(player.getUniqueId());
                return String.valueOf(rank);
            }
            if (lower.equals("rank_formatted")) {
                int rank = Database.getPlayerRank(player.getUniqueId());
                return "#" + rank;
            }

        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Tokens] Placeholder error for " + params + ": " + e.getMessage());
            return "0";
        }

        return null;
    }

    private String formatNumber(int number) {
        return NUMBER_FORMAT.format(number);
    }

    private String formatShort(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        }
        if (number < 1_000_000) {
            return formatScaled(number / 1000.0, "k", "m");
        }
        if (number < 1_000_000_000) {
            return formatScaled(number / 1_000_000.0, "m", "b");
        }
        if (number < 1_000_000_000_000L) {
            return formatScaled(number / 1_000_000_000.0, "b", "t");
        }
        return formatScaled(number / 1_000_000_000_000.0, "t", "t");
    }

    private String formatScaled(double value, String suffix, String nextSuffix) {
        String s = stripTrailingZero(String.format("%.1f", value));
        if (s.equals("1000")) {
            return "1" + nextSuffix;
        }
        return s + suffix;
    }

    private String stripTrailingZero(String s) {
        if (s.endsWith(".0")) {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }

    private String getTopPlayerName(int rank) throws SQLException {
        if (rank < 1) return "";
        List<UUID> top = Database.getTopPlayers(rank);
        if (top.size() < rank) return "";
        UUID uuid = top.get(rank - 1);
        var offline = Bukkit.getOfflinePlayer(uuid);
        return offline.getName() != null ? offline.getName() : "Unknown";
    }

    private String getTopPlayerBalance(int rank) throws SQLException {
        if (rank < 1) return "0";
        List<UUID> top = Database.getTopPlayers(rank);
        if (top.size() < rank) return "0";
        UUID uuid = top.get(rank - 1);
        return String.valueOf(Database.getPlayerTokens(uuid));
    }

    private String getTopPlayerBalanceFormatted(int rank) throws SQLException {
        if (rank < 1) return "0";
        List<UUID> top = Database.getTopPlayers(rank);
        if (top.size() < rank) return "0";
        UUID uuid = top.get(rank - 1);
        return formatNumber(Database.getPlayerTokens(uuid));
    }
}
