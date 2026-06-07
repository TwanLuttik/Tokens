package com.twanluttik.tokens.gui;

import com.twanluttik.tokens.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Handles all interactions with Tokens GUIs, including clicks and chat-based amount input.
 */
public class GuiListener implements Listener {

    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof TokensGuiHolder guiHolder)) {
            return; // Not one of our GUIs
        }

        // Always cancel so players can't take items or shift-click them out
        event.setCancelled(true);

        // Ignore clicks outside the inventory area
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        String guiType = guiHolder.getGuiType();
        int bankId = guiHolder.getBankId();

        String actionTag = guiManager.getGuiAction(clicked);
        String valueTag = guiManager.getGuiActionValue(clicked);          // string payloads (e.g. custom type)
        Integer intValueTag = guiManager.getGuiActionIntValue(clicked);  // int payloads (e.g. preset amounts)
        Integer bankIdFromItem = guiManager.getBankIdFromItem(clicked);

        switch (guiType) {
            case "MAIN" -> handleMainMenuClick(player, clicked, actionTag);
            case "BANK_LIST" -> handleBankListClick(player, clicked, actionTag, valueTag, bankIdFromItem);
            case "BANK_ACTIONS" -> handleBankActionsClick(player, bankId, clicked, actionTag, valueTag, intValueTag);
            case "CHECK_CREATE" -> handleCheckCreateClick(player, clicked, actionTag, intValueTag);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof TokensGuiHolder) {
            event.setCancelled(true); // Prevent dragging items into/out of our GUIs
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clicked, String actionTag) {
        if (actionTag != null) {
            if (actionTag.equals("BANKS")) {
                Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openBankList(player));
                return;
            }
            if (actionTag.equals("CHECKS")) {
                Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openCheckAmountSelector(player));
                return;
            }
        }

        // Fallback to display name (for robustness)
        String name = clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()
                ? ChatColor.stripColor(clicked.getItemMeta().getDisplayName())
                : "";

        if (name.contains("My Banks")) {
            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openBankList(player));
        } else if (name.contains("Create Check")) {
            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openCheckAmountSelector(player));
        } else if (name.contains("Close")) {
            player.closeInventory();
        }
    }

    private void handleBankListClick(Player player, ItemStack clicked, String actionTag, String valueTag, Integer bankIdFromItem) {
        String name = clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()
                ? ChatColor.stripColor(clicked.getItemMeta().getDisplayName())
                : "";

        if (name.contains("Back")) {
            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openMainGui(player));
            return;
        }

        // Create Bank button (via PDC action)
        if (actionTag != null && actionTag.equals("CREATE_BANK")) {
            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.startCreateBankInput(player));
            return;
        }

        // Get bank ID from PDC (preferred) or fallback to display name
        int bankId = -1;
        if (bankIdFromItem != null) {
            bankId = bankIdFromItem;
        } else if (name.contains("#")) {
            try {
                String num = name.substring(name.lastIndexOf('#') + 1).trim();
                bankId = Integer.parseInt(num);
            } catch (Exception ignored) {}
        }

        if (bankId != -1) {
            final int finalBankId = bankId;
            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openBankActions(player, finalBankId));
        }
    }

    private void handleBankActionsClick(Player player, int bankId, ItemStack clicked, String actionTag, String valueTag, Integer intValueTag) {
        if (bankId == -1) return;

        String name = clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()
                ? ChatColor.stripColor(clicked.getItemMeta().getDisplayName())
                : "";

        if (name.contains("Back")) {
            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openBankList(player));
            return;
        }
        if (name.contains("Close")) {
            player.closeInventory();
            return;
        }

        if (actionTag == null) return;

        switch (actionTag) {
            case "DEPOSIT":
                Integer depAmt = (intValueTag != null) ? intValueTag : guiManager.getGuiActionIntValue(clicked);
                if (depAmt != null) {
                    guiManager.handleBankAmountClick(player, bankId, "DEPOSIT", depAmt);
                }
                break;
            case "WITHDRAW":
                Integer witAmt = (intValueTag != null) ? intValueTag : guiManager.getGuiActionIntValue(clicked);
                if (witAmt != null) {
                    guiManager.handleBankAmountClick(player, bankId, "WITHDRAW", witAmt);
                }
                break;
            case "WITHDRAW_ALL":
                guiManager.handleWithdrawAll(player, bankId);
                break;
            case "CUSTOM":
                String customType = valueTag; // "DEPOSIT" or "WITHDRAW"
                if ("DEPOSIT".equals(customType)) {
                    guiManager.startCustomAmountInput(player, bankId, "DEPOSIT");
                } else if ("WITHDRAW".equals(customType)) {
                    guiManager.startCustomAmountInput(player, bankId, "WITHDRAW");
                }
                break;
            case "INVITE":
                guiManager.startInviteInput(player, bankId);
                break;
            case "REMOVE_MEMBER":
                guiManager.startRemoveMemberInput(player, bankId);
                break;
            case "DELETE_BANK":
                guiManager.startDeleteBankConfirmation(player, bankId);
                break;
        }
    }

    private void handleCheckCreateClick(Player player, ItemStack clicked, String actionTag, Integer intValueTag) {
        String name = clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()
                ? ChatColor.stripColor(clicked.getItemMeta().getDisplayName())
                : "";

        if (name.contains("Back")) {
            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> guiManager.openMainGui(player));
            return;
        }

        if (actionTag == null) return;

        if (actionTag.equals("CUSTOM")) {
            String val = guiManager.getGuiActionValue(clicked);
            if ("CREATE_CHECK".equals(val)) {
                guiManager.startCustomCheckInput(player);
            }
            return;
        }

        if (actionTag.equals("CHECK_CREATE")) {
            Integer amount = (intValueTag != null) ? intValueTag : guiManager.getGuiActionIntValue(clicked);
            if (amount != null) {
                guiManager.handleCheckCreation(player, amount);
                player.closeInventory();
            }
        }
    }

    // ==================== CHAT INPUT FOR CUSTOM AMOUNTS ====================

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        GuiManager.PendingAction pending = guiManager.getPendingAction(uuid);
        if (pending == null) return;

        event.setCancelled(true); // Don't broadcast what they typed in chat

        String message = event.getMessage().trim();

        if (message.equalsIgnoreCase("cancel")) {
            guiManager.clearPendingAction(uuid);
            player.sendMessage(ChatColor.YELLOW + "Action cancelled.");
            return;
        }

        String actionType = pending.actionType();

        // String-based actions (no number parsing needed)
        if (actionType.equals("CREATE_BANK") ||
            actionType.equals("INVITE") ||
            actionType.equals("REMOVE_MEMBER") ||
            actionType.equals("DELETE_BANK")) {

            guiManager.clearPendingAction(uuid);

            Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> {
                switch (actionType) {
                    case "CREATE_BANK" -> guiManager.handleCreateBank(player, message);
                    case "INVITE" -> guiManager.handleInvite(player, pending.bankId(), message);
                    case "REMOVE_MEMBER" -> guiManager.handleRemoveMember(player, pending.bankId(), message);
                    case "DELETE_BANK" -> guiManager.handleDeleteBankConfirmation(player, pending.bankId(), message);
                }
            });
            return;
        }

        // Numeric actions (deposit, withdraw, create check)
        int amount;
        try {
            amount = Integer.parseInt(message);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "Amount must be positive!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Please enter a valid number, or type 'cancel'.");
            return;
        }

        guiManager.clearPendingAction(uuid);

        // Execute the action on the main thread
        final int finalAmount = amount;
        Bukkit.getScheduler().runTask(Tokens.getInstance(), () -> {
            switch (actionType) {
                case "DEPOSIT", "WITHDRAW" -> guiManager.handleBankAmountClick(player, pending.bankId(), actionType, finalAmount);
                case "CREATE_CHECK" -> guiManager.handleCheckCreation(player, finalAmount);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        guiManager.clearAllPendingForPlayer(event.getPlayer().getUniqueId());
    }

    // Note: Action and Bank ID data is now read via GuiManager.getGuiAction(...) / getBankIdFromItem(...)
    // which uses PersistentDataContainer instead of parsing colored lore.
}
