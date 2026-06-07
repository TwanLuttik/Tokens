package com.twanluttik.tokens;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

import com.twanluttik.tokens.TokensAPI;

public class Commands implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
      return true;
    }

    if (args.length == 0) {
      sendHelp(player);
      return true;
    }

    try {
      switch (args[0].toLowerCase()) {
        case "info":

          if (!player.hasPermission("tokens.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
          }
          sendInfoMessages(player);
          return true;
        case "balance":
          if (args.length > 1 && player.hasPermission("tokens.admin")) {
            // Admin checking another player's balance
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
              player.sendMessage(ChatColor.RED + "Player not found!");
              return true;
            }
            int balance = Database.getTokens(target.getUniqueId().toString());
            player.sendMessage(ChatColor.GREEN + target.getName() + "'s balance: " + balance + " tokens");
          } else {
            // Player checking their own balance
            int balance = Database.getTokens(player.getUniqueId().toString());
            player.sendMessage(ChatColor.GREEN + "Your balance: " + balance + " tokens");
          }
          break;

        case "give":
          if (!player.hasPermission("tokens.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
          }
          if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /tokens give <player> <amount>");
            return true;
          }
          Player targetGive = Bukkit.getPlayer(args[1]);
          if (targetGive == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
          }
          try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
              player.sendMessage(ChatColor.RED + "Amount must be positive!");
              return true;
            }
            Database.addTokens(targetGive.getUniqueId().toString(), amount);
            player.sendMessage(ChatColor.GREEN + "Gave " + amount + " tokens to " + targetGive.getName());
            targetGive.sendMessage(ChatColor.GREEN + "You received " + amount + " tokens from " + player.getName());
          } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount!");
          }
          break;

        case "take":
          if (!player.hasPermission("tokens.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
          }
          if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /tokens take <player> <amount>");
            return true;
          }
          Player targetTake = Bukkit.getPlayer(args[1]);
          if (targetTake == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
          }
          try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
              player.sendMessage(ChatColor.RED + "Amount must be positive!");
              return true;
            }
            Database.removeTokens(targetTake.getUniqueId().toString(), amount);
            player.sendMessage(ChatColor.GREEN + "Took " + amount + " tokens from " + targetTake.getName());
            targetTake.sendMessage(ChatColor.RED + player.getName() + " took " + amount + " tokens from you");
          } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount!");
          }
          break;

        case "set":
          if (!player.hasPermission("tokens.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
          }
          if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /tokens set <player> <amount>");
            return true;
          }
          Player targetSet = Bukkit.getPlayer(args[1]);
          if (targetSet == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
          }
          try {
            int amount = Integer.parseInt(args[2]);
            if (amount < 0) {
              player.sendMessage(ChatColor.RED + "Amount cannot be negative!");
              return true;
            }
            Database.setTokens(targetSet.getUniqueId().toString(), amount);
            player.sendMessage(ChatColor.GREEN + "Set " + targetSet.getName() + "'s balance to " + amount + " tokens");
            targetSet
                .sendMessage(ChatColor.GREEN + "Your balance was set to " + amount + " tokens by " + player.getName());
          } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount!");
          }
          break;

        // Bank commands
        case "bank":
          if (args.length < 2) {
            sendBankHelp(player);
            return true;
          }
          switch (args[1].toLowerCase()) {
            case "create":
              if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /tokens bank create <name>");
                return true;
              }
              String bankName = args[2];
              TokensAPI apiCreate = TokensAPI.getAPI();
              int bankId;
              if (apiCreate != null) {
                bankId = apiCreate.createBank(bankName, player.getUniqueId());
              } else {
                bankId = BankDatabase.createBank(bankName, player.getUniqueId().toString());
              }
              if (bankId != -1) {
                player.sendMessage(ChatColor.GREEN + "Created bank '" + bankName + "' successfully!");
              } else {
                player.sendMessage(ChatColor.RED + "Failed to create bank (limit reached or error)!");
              }
              break;

            case "list":
              List<Integer> banks = BankDatabase.getPlayerBanks(player.getUniqueId().toString());
              if (banks.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "You don't have any banks!");
                return true;
              }
              player.sendMessage(ChatColor.GOLD + "=== Your Banks ===");
              for (int id : banks) {
                String name = BankDatabase.getBankName(id);
                int balance = BankDatabase.getBankBalance(id);
                String owner = BankDatabase.getBankOwner(id);
                boolean isOwner = owner.equals(player.getUniqueId().toString());
                player.sendMessage(ChatColor.YELLOW + name + " (ID: " + id + ")" +
                    (isOwner ? ChatColor.GREEN + " [Owner]" : "") +
                    ChatColor.WHITE + " - Balance: " + balance + " tokens");
              }
              break;

            case "deposit":
              if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /tokens bank deposit <bank_id> <amount>");
                return true;
              }
              try {
                int depositBankId = Integer.parseInt(args[2]);
                int depositAmount = Integer.parseInt(args[3]);
                if (depositAmount <= 0) {
                  player.sendMessage(ChatColor.RED + "Amount must be positive!");
                  return true;
                }
                TokensAPI api = TokensAPI.getAPI();
                if (api != null) {
                  if (!api.hasBankAccess(player.getUniqueId(), depositBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not a member of this bank!");
                    return true;
                  }
                  if (!api.depositToBank(player.getUniqueId(), depositBankId, depositAmount)) {
                    player.sendMessage(ChatColor.RED + "You don't have enough tokens!");
                    return true;
                  }
                  player.sendMessage(ChatColor.GREEN + "Deposited " + depositAmount + " tokens to bank '" +
                      api.getBankName(depositBankId) + "'");
                } else {
                  // Fallback
                  if (!isBankMember(player.getUniqueId().toString(), depositBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not a member of this bank!");
                    return true;
                  }
                  if (Database.getTokens(player.getUniqueId().toString()) < depositAmount) {
                    player.sendMessage(ChatColor.RED + "You don't have enough tokens!");
                    return true;
                  }
                  Database.removeTokens(player.getUniqueId().toString(), depositAmount);
                  BankDatabase.addToBank(depositBankId, depositAmount);
                  player.sendMessage(ChatColor.GREEN + "Deposited " + depositAmount + " tokens to bank '" +
                      BankDatabase.getBankName(depositBankId) + "'");
                }
              } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid bank ID or amount!");
              }
              break;

            case "withdraw":
              if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /tokens bank withdraw <bank_id> <amount>");
                return true;
              }
              try {
                int withdrawBankId = Integer.parseInt(args[2]);
                int withdrawAmount = Integer.parseInt(args[3]);
                if (withdrawAmount <= 0) {
                  player.sendMessage(ChatColor.RED + "Amount must be positive!");
                  return true;
                }
                TokensAPI api = TokensAPI.getAPI();
                if (api != null) {
                  if (!api.hasBankAccess(player.getUniqueId(), withdrawBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not a member of this bank!");
                    return true;
                  }
                  if (!api.withdrawFromBank(player.getUniqueId(), withdrawBankId, withdrawAmount)) {
                    player.sendMessage(ChatColor.RED + "Bank doesn't have enough tokens!");
                    return true;
                  }
                  player.sendMessage(ChatColor.GREEN + "Withdrew " + withdrawAmount + " tokens from bank '" +
                      api.getBankName(withdrawBankId) + "'");
                } else {
                  if (!isBankMember(player.getUniqueId().toString(), withdrawBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not a member of this bank!");
                    return true;
                  }
                  if (BankDatabase.getBankBalance(withdrawBankId) < withdrawAmount) {
                    player.sendMessage(ChatColor.RED + "Bank doesn't have enough tokens!");
                    return true;
                  }
                  BankDatabase.removeFromBank(withdrawBankId, withdrawAmount);
                  Database.addTokens(player.getUniqueId().toString(), withdrawAmount);
                  player.sendMessage(ChatColor.GREEN + "Withdrew " + withdrawAmount + " tokens from bank '" +
                      BankDatabase.getBankName(withdrawBankId) + "'");
                }
              } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid bank ID or amount!");
              }
              break;

            case "invite":
              if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /tokens bank invite <bank_id> <player>");
                return true;
              }
              try {
                int inviteBankId = Integer.parseInt(args[2]);
                Player inviteTarget = Bukkit.getPlayer(args[3]);
                if (inviteTarget == null) {
                  player.sendMessage(ChatColor.RED + "Player not found!");
                  return true;
                }
                TokensAPI apiInv = TokensAPI.getAPI();
                if (apiInv != null) {
                  if (!apiInv.isBankOwner(player.getUniqueId(), inviteBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of this bank!");
                    return true;
                  }
                  apiInv.addBankMember(inviteBankId, inviteTarget.getUniqueId());
                  player.sendMessage(ChatColor.GREEN + "Invited " + inviteTarget.getName() + " to bank '" +
                      apiInv.getBankName(inviteBankId) + "'");
                  inviteTarget.sendMessage(ChatColor.GREEN + "You have been invited to bank '" +
                      apiInv.getBankName(inviteBankId) + "' by " + player.getName());
                } else {
                  if (!isBankOwner(player.getUniqueId().toString(), inviteBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of this bank!");
                    return true;
                  }
                  BankDatabase.addMember(inviteBankId, inviteTarget.getUniqueId().toString());
                  player.sendMessage(ChatColor.GREEN + "Invited " + inviteTarget.getName() + " to bank '" +
                      BankDatabase.getBankName(inviteBankId) + "'");
                  inviteTarget.sendMessage(ChatColor.GREEN + "You have been invited to bank '" +
                      BankDatabase.getBankName(inviteBankId) + "' by " + player.getName());
                }
              } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid bank ID!");
              }
              break;

            case "remove":
              if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /tokens bank remove <bank_id> <player>");
                return true;
              }
              try {
                int removeBankId = Integer.parseInt(args[2]);
                Player removeTarget = Bukkit.getPlayer(args[3]);
                if (removeTarget == null) {
                  player.sendMessage(ChatColor.RED + "Player not found!");
                  return true;
                }
                TokensAPI apiRem = TokensAPI.getAPI();
                if (apiRem != null) {
                  if (!apiRem.isBankOwner(player.getUniqueId(), removeBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of this bank!");
                    return true;
                  }
                  if (removeTarget.getUniqueId().equals(apiRem.getBankOwner(removeBankId))) {
                    player.sendMessage(ChatColor.RED + "You cannot remove the bank owner!");
                    return true;
                  }
                  apiRem.removeBankMember(removeBankId, removeTarget.getUniqueId());
                  player.sendMessage(ChatColor.GREEN + "Removed " + removeTarget.getName() + " from bank '" +
                      apiRem.getBankName(removeBankId) + "'");
                  removeTarget.sendMessage(ChatColor.RED + "You have been removed from bank '" +
                      apiRem.getBankName(removeBankId) + "' by " + player.getName());
                } else {
                  if (!isBankOwner(player.getUniqueId().toString(), removeBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of this bank!");
                    return true;
                  }
                  if (removeTarget.getUniqueId().toString().equals(BankDatabase.getBankOwner(removeBankId))) {
                    player.sendMessage(ChatColor.RED + "You cannot remove the bank owner!");
                    return true;
                  }
                  BankDatabase.removeMember(removeBankId, removeTarget.getUniqueId().toString());
                  player.sendMessage(ChatColor.GREEN + "Removed " + removeTarget.getName() + " from bank '" +
                      BankDatabase.getBankName(removeBankId) + "'");
                  removeTarget.sendMessage(ChatColor.RED + "You have been removed from bank '" +
                      BankDatabase.getBankName(removeBankId) + "' by " + player.getName());
                }
              } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid bank ID!");
              }
              break;

            case "delete":
              if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /tokens bank delete <bank_id>");
                return true;
              }
              try {
                int deleteBankId = Integer.parseInt(args[2]);
                TokensAPI apiDel = TokensAPI.getAPI();
                if (apiDel != null) {
                  if (!apiDel.isBankOwner(player.getUniqueId(), deleteBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of this bank!");
                    return true;
                  }
                  String bankNameToDelete = apiDel.getBankName(deleteBankId);
                  apiDel.deleteBank(deleteBankId);
                  player.sendMessage(ChatColor.GREEN + "Deleted bank '" + bankNameToDelete + "'");
                } else {
                  if (!isBankOwner(player.getUniqueId().toString(), deleteBankId)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of this bank!");
                    return true;
                  }
                  String bankNameToDelete = BankDatabase.getBankName(deleteBankId);
                  BankDatabase.deleteBank(deleteBankId);
                  player.sendMessage(ChatColor.GREEN + "Deleted bank '" + bankNameToDelete + "'");
                }
              } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid bank ID!");
              }
              break;

            default:
              sendBankHelp(player);
              break;
          }
          break;

        case "check":
          if (args.length < 2) {
            sendCheckHelp(player);
            return true;
          }
          switch (args[1].toLowerCase()) {
            case "create":
              if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /tokens check create <amount>");
                return true;
              }
              try {
                int amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                  player.sendMessage(ChatColor.RED + "Amount must be positive!");
                  return true;
                }
                if (Database.getTokens(player.getUniqueId().toString()) < amount) {
                  player.sendMessage(ChatColor.RED + "You don't have enough tokens!");
                  return true;
                }
                // Remove tokens from player
                Database.removeTokens(player.getUniqueId().toString(), amount);
                // Create check
                ItemStack check = CheckManager.createCheck(player, amount);
                // Give check to player
                player.getInventory().addItem(check);
                player.sendMessage(ChatColor.GREEN + "Created a check for " + amount + " tokens");
              } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid amount!");
              }
              break;

            case "redeem":
              ItemStack itemInHand = player.getInventory().getItemInMainHand();
              TokensAPI api = TokensAPI.getAPI();
              if (api != null && api.isValidCheck(itemInHand)) {
                if (api.redeemCheck(player, itemInHand)) {
                  // Properly remove or decrement the check
                  if (itemInHand.getAmount() <= 1) {
                    player.getInventory().setItemInMainHand(null);
                  } else {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                  }
                  player.sendMessage(ChatColor.GREEN + "Successfully redeemed the check!");
                } else {
                  player.sendMessage(ChatColor.RED + "This check has already been redeemed!");
                }
              } else if (CheckManager.isValidCheck(itemInHand)) {
                // Fallback to direct manager if API not available (shouldn't happen)
                try {
                  if (CheckManager.redeemCheck(player, itemInHand)) {
                    if (itemInHand.getAmount() <= 1) {
                      player.getInventory().setItemInMainHand(null);
                    } else {
                      itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }
                    player.sendMessage(ChatColor.GREEN + "Successfully redeemed the check!");
                  } else {
                    player.sendMessage(ChatColor.RED + "This check has already been redeemed!");
                  }
                } catch (SQLException ex) {
                  player.sendMessage(ChatColor.RED + "An error occurred while redeeming the check.");
                }
              } else {
                player.sendMessage(ChatColor.RED + "You must hold a valid check in your hand!");
              }
              break;

            default:
              sendCheckHelp(player);
              break;
          }
          break;

        case "hologram":
          if (!player.hasPermission("tokens.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
          }
          if (!LibraryIntegration.isDecentHologramsAvailable()) {
            player.sendMessage(ChatColor.RED + "This feature requires DecentHolograms to be installed on the server!");
            return true;
          }
          HologramManager.getInstance().createHologram(player.getLocation());
          player.sendMessage(ChatColor.GREEN + "Created top 10 players hologram at your location!");
          break;

        case "gui":
          if (Tokens.getInstance().getGuiManager() != null) {
            Tokens.getInstance().getGuiManager().openMainGui(player);
          } else {
            player.sendMessage(ChatColor.RED + "The GUI is not ready yet. Please try again in a moment.");
          }
          break;

        default:
          sendHelp(player);
          break;
      }
    } catch (SQLException e) {
      player.sendMessage(ChatColor.RED + "An error occurred while processing your command!");
      Tokens.getInstance().getLogger().severe("Command error: " + e.getMessage());
    }

    return true;
  }

  private void sendHelp(Player player) {
    player.sendMessage(ChatColor.GOLD + "=== Tokens Commands ===");
    player.sendMessage(ChatColor.YELLOW + "/tokens balance [player] - Check your or another player's balance");
    player.sendMessage(ChatColor.YELLOW + "/tokens gui - Open the tokens GUI menu");
    player.sendMessage(ChatColor.YELLOW + "/tokens give <player> <amount> - Give tokens to a player");
    player.sendMessage(ChatColor.YELLOW + "/tokens take <player> <amount> - Take tokens from a player");
    player.sendMessage(ChatColor.YELLOW + "/tokens set <player> <amount> - Set a player's token balance");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank - Bank related commands");
    player.sendMessage(ChatColor.YELLOW + "/tokens check - Check related commands");
    if (player.hasPermission("tokens.admin") && LibraryIntegration.isDecentHologramsAvailable()) {
      player.sendMessage(ChatColor.YELLOW + "/tokens hologram - Place the top 10 players hologram");
    }
  }

  private void sendBankHelp(Player player) {
    player.sendMessage(ChatColor.GOLD + "=== Bank Commands ===");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank create <name> - Create a new bank");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank list - List your banks");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank deposit <bank_id> <amount> - Deposit tokens to a bank");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank withdraw <bank_id> <amount> - Withdraw tokens from a bank");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank invite <bank_id> <player> - Invite a player to your bank");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank remove <bank_id> <player> - Remove a player from your bank");
    player.sendMessage(ChatColor.YELLOW + "/tokens bank delete <bank_id> - Delete a bank");
  }

  private void sendCheckHelp(Player player) {
    player.sendMessage(ChatColor.GOLD + "=== Check Commands ===");
    player.sendMessage(ChatColor.YELLOW + "/tokens check create <amount> - Create a check for the specified amount");
    player.sendMessage(ChatColor.YELLOW + "/tokens check redeem - Redeem the check you're holding");
  }

  private boolean isBankMember(String uuid, int bankId) throws SQLException {
    // Use hasAccess so bank owners are always treated as having member access
    // (createBank now also inserts owners into bank_members, but this covers legacy banks too)
    return BankDatabase.hasAccess(uuid, bankId);
  }

  private boolean isBankOwner(String uuid, int bankId) throws SQLException {
    return BankDatabase.isOwner(uuid, bankId);
  }

  private void sendInfoMessages(Player player) {
    ConfigManager config = Tokens.getInstance().getConfigManager();
    String pluginVersion = Tokens.getInstance().getDescription().getVersion();
    String serverVersion = Bukkit.getVersion();
    String serverType = Bukkit.getName();

    player.sendMessage(ChatColor.GOLD + "=== Tokens Plugin Info ===");
    player.sendMessage(ChatColor.YELLOW + "Plugin version: " + ChatColor.WHITE + pluginVersion);
    player.sendMessage(ChatColor.YELLOW + "Update notifier: " + ChatColor.WHITE
        + (config.isUpdateCheckerEnabled() ? "Enabled" : "Disabled"));
    player.sendMessage(ChatColor.YELLOW + "Server version: " + ChatColor.WHITE + serverVersion);
    player.sendMessage(ChatColor.YELLOW + "Server type: " + ChatColor.WHITE + serverType);
    player.sendMessage(
        ChatColor.YELLOW + "Database: " + ChatColor.WHITE + config.getDatabaseHost() + ":" + config.getDatabasePort());
    player.sendMessage(ChatColor.YELLOW + "Database type: " + ChatColor.WHITE + "PostgreSQL");
    player.sendMessage(ChatColor.YELLOW + "Start amount: " + ChatColor.WHITE + config.getInitialTokens() + " tokens");
    player.sendMessage(ChatColor.YELLOW + "Bank info: " + ChatColor.WHITE + "Enabled");
    player.sendMessage(ChatColor.YELLOW + "Auto create bank: " + ChatColor.WHITE
        + (config.isAutoCreateBank() ? "Enabled" : "Disabled"));
    player.sendMessage(ChatColor.YELLOW + "Max bank players: " + ChatColor.WHITE + config.getMaxBanksPerPlayer());
    player.sendMessage(ChatColor.YELLOW + "Max members per bank: " + ChatColor.WHITE + config.getMaxMembersPerBank());
  }
}
