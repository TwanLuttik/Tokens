package com.twanluttik.tokens.gui;

import com.twanluttik.tokens.BankDatabase;
import com.twanluttik.tokens.CheckManager;
import com.twanluttik.tokens.Database;
import com.twanluttik.tokens.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all Tokens plugin GUIs.
 * Provides main menu, bank list, bank actions, and check creation interfaces.
 */
public class GuiManager {

    private final Tokens plugin;

    // PersistentDataContainer keys for clean action transport (instead of lore hacks)
    private final NamespacedKey ACTION_KEY;
    private final NamespacedKey VALUE_STRING_KEY;   // for string payloads (e.g. "DEPOSIT" for custom actions)
    private final NamespacedKey VALUE_INT_KEY;      // for integer payloads (e.g. amounts for presets)
    private final NamespacedKey BANK_ID_KEY;

    // Used to identify which bank/action a player is currently inputting an amount for via chat
    private final Map<UUID, PendingAction> pendingActions = new ConcurrentHashMap<>();

    public GuiManager(Tokens plugin) {
        this.plugin = plugin;
        this.ACTION_KEY = new NamespacedKey(plugin, "gui_action");
        this.VALUE_STRING_KEY = new NamespacedKey(plugin, "gui_action_value_str");
        this.VALUE_INT_KEY = new NamespacedKey(plugin, "gui_action_value_int");
        this.BANK_ID_KEY = new NamespacedKey(plugin, "bank_id");
    }

    // ==================== PUBLIC OPEN METHODS ====================

    public void openMainGui(Player player) {
        Inventory inv = Bukkit.createInventory(new TokensGuiHolder("MAIN", -1), 27, ChatColor.GOLD + "Tokens Menu");

        // Fill background
        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        // Balance display (center)
        int balance = getPlayerBalance(player);
        ItemStack balanceItem = createBalanceItem(player, balance);
        inv.setItem(13, balanceItem);

        // Banks button
        ItemStack banksItem = createGuiItem(
                Material.CHEST,
                ChatColor.GREEN + "My Banks",
                ChatColor.GRAY + "View and manage your banks",
                ChatColor.GRAY + "Click to open"
        );
        attachAction(banksItem, "BANKS", (String) null);
        inv.setItem(10, banksItem);

        // Checks button
        ItemStack checksItem = createGuiItem(
                Material.PAPER,
                ChatColor.AQUA + "Create Check",
                ChatColor.GRAY + "Create a tradable token check",
                ChatColor.GRAY + "Click to choose amount"
        );
        attachAction(checksItem, "CHECKS", (String) null);
        inv.setItem(12, checksItem);

        // Close button
        ItemStack closeItem = createGuiItem(
                Material.BARRIER,
                ChatColor.RED + "Close",
                ChatColor.GRAY + "Click to close this menu"
        );
        inv.setItem(22, closeItem);

        player.openInventory(inv);
    }

    public void openBankList(Player player) {
        List<Integer> bankIds;
        try {
            bankIds = BankDatabase.getPlayerBanks(player.getUniqueId().toString());
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Failed to load your banks.");
            plugin.getLogger().warning("Failed to load banks for GUI: " + e.getMessage());
            return;
        }

        int size = Math.max(27, ((bankIds.size() / 7) + 2) * 9); // dynamic-ish sizing
        if (size > 54) size = 54;

        Inventory inv = Bukkit.createInventory(new TokensGuiHolder("BANK_LIST", -1), size, ChatColor.GOLD + "Your Banks");

        // Fill
        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < size; i++) inv.setItem(i, filler);

        // Create New Bank button (always available)
        int maxBanks = plugin.getConfigManager().getMaxBanksPerPlayer();
        ItemStack createBank = createGuiItem(
                Material.EMERALD,
                ChatColor.GREEN + "" + ChatColor.BOLD + "Create New Bank",
                ChatColor.GRAY + "Create a new shared bank",
                ChatColor.GRAY + "Current banks: " + bankIds.size() + "/" + maxBanks,
                "",
                ChatColor.YELLOW + "Click to name your new bank"
        );
        attachAction(createBank, "CREATE_BANK", (String) null);
        inv.setItem(4, createBank);

        if (bankIds.isEmpty()) {
            ItemStack empty = createGuiItem(
                    Material.BARRIER,
                    ChatColor.RED + "No Banks Yet",
                    ChatColor.GRAY + "You are not a member of any banks.",
                    ChatColor.GRAY + "Click the button above to create your first bank!"
            );
            inv.setItem(13, empty);
        } else {
            int slot = 10;
            for (int bankId : bankIds) {
                if (slot >= size - 9) break; // leave space for back button

                try {
                    String name = BankDatabase.getBankName(bankId);
                    int balance = BankDatabase.getBankBalance(bankId);
                    String ownerUuid = BankDatabase.getBankOwner(bankId);
                    boolean isOwner = ownerUuid != null && ownerUuid.equals(player.getUniqueId().toString());

                    ItemStack bankItem = createGuiItem(
                            isOwner ? Material.EMERALD_BLOCK : Material.EMERALD,
                            ChatColor.GREEN + (name != null ? name : "Bank #" + bankId),
                            ChatColor.GRAY + "Balance: " + ChatColor.GOLD + balance + " tokens",
                            isOwner ? ChatColor.GREEN + "[Owner]" : ChatColor.YELLOW + "[Member]",
                            "",
                            ChatColor.YELLOW + "Click to manage this bank"
                    );
                    attachBankId(bankItem, bankId);
                    inv.setItem(slot, bankItem);
                    slot++;
                    if (slot % 9 == 8) slot += 2; // skip edges
                } catch (SQLException e) {
                    plugin.getLogger().warning("Error loading bank " + bankId + " for GUI: " + e.getMessage());
                }
            }
        }

        // Back button
        ItemStack back = createGuiItem(Material.ARROW, ChatColor.YELLOW + "Back to Main Menu");
        inv.setItem(size - 5, back);

        player.openInventory(inv);
    }

    public void openBankActions(Player player, int bankId) {
        String bankName;
        int balance;
        boolean isOwner;

        try {
            bankName = BankDatabase.getBankName(bankId);
            balance = BankDatabase.getBankBalance(bankId);
            isOwner = BankDatabase.isOwner(player.getUniqueId().toString(), bankId);
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Failed to load bank data.");
            return;
        }

        if (bankName == null) {
            player.sendMessage(ChatColor.RED + "That bank no longer exists.");
            return;
        }

        Inventory inv = Bukkit.createInventory(new TokensGuiHolder("BANK_ACTIONS", bankId), 36, ChatColor.GOLD + "Bank: " + bankName);

        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 36; i++) inv.setItem(i, filler);

        // Bank info
        ItemStack info = createGuiItem(
                Material.EMERALD_BLOCK,
                ChatColor.GREEN + bankName,
                ChatColor.GRAY + "Current Balance: " + ChatColor.GOLD + balance + " tokens",
                isOwner ? ChatColor.GREEN + "You are the owner" : ChatColor.YELLOW + "You are a member"
        );
        inv.setItem(4, info);

        // Deposit section header
        ItemStack depositHeader = createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "Deposit Tokens");
        inv.setItem(10, depositHeader);

        // Deposit presets
        inv.setItem(11, createAmountButton(Material.LIME_STAINED_GLASS_PANE, 100, "DEPOSIT"));
        inv.setItem(12, createAmountButton(Material.LIME_STAINED_GLASS_PANE, 500, "DEPOSIT"));
        inv.setItem(13, createAmountButton(Material.LIME_STAINED_GLASS_PANE, 1000, "DEPOSIT"));
        inv.setItem(14, createGuiItem(
                Material.WRITABLE_BOOK,
                ChatColor.GREEN + "Custom Amount",
                ChatColor.GRAY + "Click to type a custom deposit amount in chat"
        ));
        // We will mark the custom button specially in lore
        markCustomButton(inv.getItem(14), "DEPOSIT");

        // Withdraw section header
        ItemStack withdrawHeader = createGuiItem(Material.RED_DYE, ChatColor.RED + "Withdraw Tokens");
        inv.setItem(19, withdrawHeader);

        // Withdraw presets
        inv.setItem(20, createAmountButton(Material.RED_STAINED_GLASS_PANE, 100, "WITHDRAW"));
        inv.setItem(21, createAmountButton(Material.RED_STAINED_GLASS_PANE, 500, "WITHDRAW"));
        inv.setItem(22, createAmountButton(Material.RED_STAINED_GLASS_PANE, 1000, "WITHDRAW"));
        inv.setItem(23, createGuiItem(
                Material.WRITABLE_BOOK,
                ChatColor.RED + "Custom Amount",
                ChatColor.GRAY + "Click to type a custom withdraw amount in chat"
        ));
        markCustomButton(inv.getItem(23), "WITHDRAW");

        // All button for withdraw
        ItemStack withdrawAll = createGuiItem(
                Material.GOLD_BLOCK,
                ChatColor.GOLD + "Withdraw All",
                ChatColor.GRAY + "Withdraw the entire bank balance"
        );
        attachAction(withdrawAll, "WITHDRAW_ALL", (String) null);
        inv.setItem(24, withdrawAll);

        // Owner-only control buttons
        if (isOwner) {
            ItemStack invite = createGuiItem(
                    Material.WRITABLE_BOOK,
                    ChatColor.GREEN + "Invite Player",
                    ChatColor.GRAY + "Invite another player to this bank"
            );
            attachAction(invite, "INVITE", (String) null);
            inv.setItem(28, invite);

            ItemStack removeMember = createGuiItem(
                    Material.REDSTONE,
                    ChatColor.RED + "Remove Member",
                    ChatColor.GRAY + "Remove a player from this bank"
            );
            attachAction(removeMember, "REMOVE_MEMBER", (String) null);
            inv.setItem(29, removeMember);

            ItemStack deleteBank = createGuiItem(
                    Material.TNT,
                    ChatColor.RED + "" + ChatColor.BOLD + "Delete Bank",
                    ChatColor.RED + "Permanently delete this bank",
                    ChatColor.RED + "This cannot be undone!"
            );
            attachAction(deleteBank, "DELETE_BANK", (String) null);
            inv.setItem(31, deleteBank);
        }

        // Back + Close
        ItemStack back = createGuiItem(Material.ARROW, ChatColor.YELLOW + "Back to Bank List");
        inv.setItem(27, back);

        ItemStack close = createGuiItem(Material.BARRIER, ChatColor.RED + "Close");
        inv.setItem(35, close);

        player.openInventory(inv);
    }

    public void openCheckAmountSelector(Player player) {
        Inventory inv = Bukkit.createInventory(new TokensGuiHolder("CHECK_CREATE", -1), 27, ChatColor.GOLD + "Create Token Check");

        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        int balance = getPlayerBalance(player);

        ItemStack info = createGuiItem(
                Material.PAPER,
                ChatColor.AQUA + "Create a Check",
                ChatColor.GRAY + "Your current balance: " + ChatColor.GOLD + balance + " tokens",
                ChatColor.GRAY + "Select an amount below"
        );
        inv.setItem(4, info);

        // Preset amounts
        inv.setItem(10, createCheckAmountButton(100));
        inv.setItem(11, createCheckAmountButton(250));
        inv.setItem(12, createCheckAmountButton(500));
        inv.setItem(13, createCheckAmountButton(1000));
        inv.setItem(14, createCheckAmountButton(5000));

        ItemStack custom = createGuiItem(
                Material.WRITABLE_BOOK,
                ChatColor.YELLOW + "Custom Amount",
                ChatColor.GRAY + "Enter any amount in chat"
        );
        markCustomButton(custom, "CREATE_CHECK");
        inv.setItem(16, custom);

        ItemStack back = createGuiItem(Material.ARROW, ChatColor.YELLOW + "Back to Main Menu");
        inv.setItem(22, back);

        player.openInventory(inv);
    }

    // ==================== ACTION HANDLERS ====================

    public void handleBankAmountClick(Player player, int bankId, String action, int amount) {
        try {
            if ("DEPOSIT".equals(action)) {
                int playerTokens = Database.getTokens(player.getUniqueId().toString());
                if (playerTokens < amount) {
                    player.sendMessage(ChatColor.RED + "You don't have enough tokens!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return;
                }
                if (!BankDatabase.hasAccess(player.getUniqueId().toString(), bankId)) {
                    player.sendMessage(ChatColor.RED + "You are not a member of this bank.");
                    return;
                }
                Database.removeTokens(player.getUniqueId().toString(), amount);
                BankDatabase.addToBank(bankId, amount);
                player.sendMessage(ChatColor.GREEN + "Deposited " + amount + " tokens into the bank.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
            } else if ("WITHDRAW".equals(action)) {
                int bankBalance = BankDatabase.getBankBalance(bankId);
                if (bankBalance < amount) {
                    player.sendMessage(ChatColor.RED + "The bank doesn't have that many tokens.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return;
                }
                if (!BankDatabase.hasAccess(player.getUniqueId().toString(), bankId)) {
                    player.sendMessage(ChatColor.RED + "You are not a member of this bank.");
                    return;
                }
                BankDatabase.removeFromBank(bankId, amount);
                Database.addTokens(player.getUniqueId().toString(), amount);
                player.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " tokens from the bank.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
            }
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "A database error occurred.");
            plugin.getLogger().warning("GUI bank action error: " + e.getMessage());
            return;
        }

        // Reopen the bank actions menu so they see the updated balance
        Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
    }

    public void handleWithdrawAll(Player player, int bankId) {
        try {
            if (!BankDatabase.hasAccess(player.getUniqueId().toString(), bankId)) {
                player.sendMessage(ChatColor.RED + "You are not a member of this bank.");
                return;
            }
            int bankBalance = BankDatabase.getBankBalance(bankId);
            if (bankBalance <= 0) {
                player.sendMessage(ChatColor.RED + "This bank has no tokens to withdraw.");
                return;
            }
            BankDatabase.removeFromBank(bankId, bankBalance);
            Database.addTokens(player.getUniqueId().toString(), bankBalance);
            player.sendMessage(ChatColor.GREEN + "Withdrew all " + bankBalance + " tokens from the bank.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "A database error occurred.");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
    }

    public void startCustomAmountInput(Player player, int bankId, String actionType) {
        pendingActions.put(player.getUniqueId(), new PendingAction(bankId, actionType));

        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Please type the amount you want to " +
                (actionType.equals("DEPOSIT") ? "deposit" : "withdraw") + " in chat.");
        player.sendMessage(ChatColor.GRAY + "Type 'cancel' to abort.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
    }

    public void startCustomCheckInput(Player player) {
        pendingActions.put(player.getUniqueId(), new PendingAction(-1, "CREATE_CHECK"));

        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Please type the amount for the check in chat.");
        player.sendMessage(ChatColor.GRAY + "Type 'cancel' to abort.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
    }

    public void handleCheckCreation(Player player, int amount) {
        try {
            int balance = Database.getTokens(player.getUniqueId().toString());
            if (balance < amount) {
                player.sendMessage(ChatColor.RED + "You don't have enough tokens to create this check!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            Database.removeTokens(player.getUniqueId().toString(), amount);
            ItemStack check = CheckManager.createCheck(player, amount);
            player.getInventory().addItem(check);

            player.sendMessage(ChatColor.GREEN + "Created a check for " + amount + " tokens.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Failed to create check due to a database error.");
            plugin.getLogger().warning("Check creation from GUI failed: " + e.getMessage());
        }
    }

    // ==================== PENDING ACTION HANDLING ====================

    public PendingAction getPendingAction(UUID uuid) {
        return pendingActions.get(uuid);
    }

    public void clearPendingAction(UUID uuid) {
        pendingActions.remove(uuid);
    }

    public void clearAllPendingForPlayer(UUID uuid) {
        pendingActions.remove(uuid);
    }

    // ==================== HELPER METHODS ====================

    private int getPlayerBalance(Player player) {
        try {
            return Database.getTokens(player.getUniqueId().toString());
        } catch (SQLException e) {
            return 0;
        }
    }

    private ItemStack createBalanceItem(Player player, int balance) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(ChatColor.GOLD + "Your Balance");
            meta.setLore(Arrays.asList(
                    ChatColor.WHITE + "" + ChatColor.BOLD + balance + ChatColor.RESET + ChatColor.GRAY + " tokens",
                    "",
                    ChatColor.GRAY + "Click any menu option below"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createAmountButton(Material material, int amount, String action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String actionColor = action.equals("DEPOSIT") ? ChatColor.GREEN + "+" : ChatColor.RED + "-";
            meta.setDisplayName(actionColor + amount + " tokens");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Click to " + action.toLowerCase() + " " + amount + " tokens"
            ));
            item.setItemMeta(meta);
            // Attach action via PDC (clean, no lore dependency)
            attachAction(item, action, amount);
        }
        return item;
    }

    private ItemStack createCheckAmountButton(int amount) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "" + amount + " Token Check");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Create a check worth " + amount + " tokens"
            ));
            item.setItemMeta(meta);
            attachAction(item, "CHECK_CREATE", amount);
        }
        return item;
    }

    private void markCustomButton(ItemStack item, String action) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Use PDC for logic, keep lore clean for display
            meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, "CUSTOM");
            meta.getPersistentDataContainer().set(VALUE_STRING_KEY, PersistentDataType.STRING, action);
            item.setItemMeta(meta);
        }
    }

    /**
     * Attaches a GUI action to an item using PersistentDataContainer.
     * This replaces the old fragile lore-based "Action:XXX" tags.
     */
    private void attachAction(ItemStack item, String action, String value) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
            if (value != null) {
                meta.getPersistentDataContainer().set(VALUE_STRING_KEY, PersistentDataType.STRING, value);
            }
            item.setItemMeta(meta);
        }
    }

    private void attachAction(ItemStack item, String action, int value) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
            meta.getPersistentDataContainer().set(VALUE_INT_KEY, PersistentDataType.INTEGER, value);
            item.setItemMeta(meta);
        }
    }

    private void attachBankId(ItemStack item, int bankId) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(BANK_ID_KEY, PersistentDataType.INTEGER, bankId);
            item.setItemMeta(meta);
        }
    }

    // ==================== PDC READERS (used by GuiListener) ====================

    public String getGuiAction(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(ACTION_KEY, PersistentDataType.STRING);
    }

    public String getGuiActionValue(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(VALUE_STRING_KEY, PersistentDataType.STRING);
    }

    public Integer getGuiActionIntValue(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(VALUE_INT_KEY, PersistentDataType.INTEGER);
    }

    public Integer getBankIdFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(BANK_ID_KEY, PersistentDataType.INTEGER);
    }

    // ==================== BANK CREATION & OWNER CONTROLS ====================

    public void startCreateBankInput(Player player) {
        pendingActions.put(player.getUniqueId(), new PendingAction(-1, "CREATE_BANK"));

        player.closeInventory();
        int current = 0;
        try {
            current = BankDatabase.getPlayerBanks(player.getUniqueId().toString()).size();
        } catch (SQLException ignored) {}
        int max = plugin.getConfigManager().getMaxBanksPerPlayer();

        player.sendMessage(ChatColor.YELLOW + "Enter a name for your new bank (max 32 characters).");
        player.sendMessage(ChatColor.GRAY + "You currently have " + current + "/" + max + " banks.");
        player.sendMessage(ChatColor.GRAY + "Type 'cancel' to abort.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
    }

    public void handleCreateBank(Player player, String bankName) {
        if (bankName.length() > 32) {
            player.sendMessage(ChatColor.RED + "Bank name cannot be longer than 32 characters!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }
        if (bankName.trim().isEmpty()) {
            player.sendMessage(ChatColor.RED + "Bank name cannot be empty!");
            return;
        }

        try {
            int currentBanks = BankDatabase.getPlayerBanks(player.getUniqueId().toString()).size();
            int maxBanks = plugin.getConfigManager().getMaxBanksPerPlayer();

            if (currentBanks >= maxBanks) {
                player.sendMessage(ChatColor.RED + "You have reached the maximum number of banks (" + maxBanks + ").");
                return;
            }

            int newBankId = BankDatabase.createBank(bankName.trim(), player.getUniqueId().toString());
            if (newBankId == -1) {
                player.sendMessage(ChatColor.RED + "Failed to create bank. Please try a different name.");
                return;
            }

            player.sendMessage(ChatColor.GREEN + "Bank '" + bankName.trim() + "' created successfully!");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);

            // Open the new bank's management screen
            Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, newBankId));
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "A database error occurred while creating the bank.");
            plugin.getLogger().warning("GUI create bank failed: " + e.getMessage());
        }
    }

    public void startInviteInput(Player player, int bankId) {
        pendingActions.put(player.getUniqueId(), new PendingAction(bankId, "INVITE"));

        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Enter the exact username of the player you want to invite.");
        player.sendMessage(ChatColor.GRAY + "The player must be online. Type 'cancel' to abort.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
    }

    public void handleInvite(Player player, int bankId, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player '" + targetName + "' not found (must be online).");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
            return;
        }

        try {
            if (!BankDatabase.isOwner(player.getUniqueId().toString(), bankId)) {
                player.sendMessage(ChatColor.RED + "You are no longer the owner of this bank.");
                return;
            }

            int currentMembers = BankDatabase.getBankMembers(bankId).size();
            int maxMembers = plugin.getConfigManager().getMaxMembersPerBank();

            if (currentMembers >= maxMembers) {
                player.sendMessage(ChatColor.RED + "This bank has reached the maximum number of members (" + maxMembers + ").");
                Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
                return;
            }

            if (BankDatabase.isMember(target.getUniqueId().toString(), bankId)) {
                player.sendMessage(ChatColor.YELLOW + target.getName() + " is already a member of this bank.");
                Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
                return;
            }

            BankDatabase.addMember(bankId, target.getUniqueId().toString());

            String bankName = BankDatabase.getBankName(bankId);
            player.sendMessage(ChatColor.GREEN + "Invited " + target.getName() + " to bank '" + bankName + "'.");
            target.sendMessage(ChatColor.GREEN + "You have been invited to bank '" + bankName + "' by " + player.getName() + ".");

            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
            if (target.isOnline()) {
                target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
            }
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Failed to invite player due to a database error.");
            plugin.getLogger().warning("GUI invite failed: " + e.getMessage());
        }

        Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
    }

    public void startRemoveMemberInput(Player player, int bankId) {
        pendingActions.put(player.getUniqueId(), new PendingAction(bankId, "REMOVE_MEMBER"));

        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Enter the exact username of the member you want to remove.");
        player.sendMessage(ChatColor.GRAY + "Type 'cancel' to abort.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
    }

    public void handleRemoveMember(Player player, int bankId, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player '" + targetName + "' not found (must be online to remove via GUI).");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
            return;
        }

        try {
            if (!BankDatabase.isOwner(player.getUniqueId().toString(), bankId)) {
                player.sendMessage(ChatColor.RED + "You are no longer the owner of this bank.");
                return;
            }

            String ownerUuid = BankDatabase.getBankOwner(bankId);
            if (target.getUniqueId().toString().equals(ownerUuid)) {
                player.sendMessage(ChatColor.RED + "You cannot remove the bank owner!");
                Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
                return;
            }

            if (!BankDatabase.isMember(target.getUniqueId().toString(), bankId)) {
                player.sendMessage(ChatColor.YELLOW + target.getName() + " is not a member of this bank.");
                Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
                return;
            }

            BankDatabase.removeMember(bankId, target.getUniqueId().toString());

            String bankName = BankDatabase.getBankName(bankId);
            player.sendMessage(ChatColor.GREEN + "Removed " + target.getName() + " from bank '" + bankName + "'.");
            if (target.isOnline()) {
                target.sendMessage(ChatColor.RED + "You have been removed from bank '" + bankName + "' by " + player.getName() + ".");
            }
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Failed to remove member due to a database error.");
            plugin.getLogger().warning("GUI remove member failed: " + e.getMessage());
        }

        Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
    }

    public void startDeleteBankConfirmation(Player player, int bankId) {
        try {
            String bankName = BankDatabase.getBankName(bankId);
            if (bankName == null) {
                player.sendMessage(ChatColor.RED + "This bank no longer exists.");
                return;
            }

            pendingActions.put(player.getUniqueId(), new PendingAction(bankId, "DELETE_BANK"));

            player.closeInventory();
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING: Deleting this bank is permanent!");
            player.sendMessage(ChatColor.YELLOW + "To confirm, type the exact bank name: " + ChatColor.WHITE + bankName);
            player.sendMessage(ChatColor.GRAY + "Type 'cancel' to abort.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.8f);
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Failed to load bank information.");
        }
    }

    public void handleDeleteBankConfirmation(Player player, int bankId, String confirmation) {
        try {
            String actualName = BankDatabase.getBankName(bankId);
            if (actualName == null) {
                player.sendMessage(ChatColor.RED + "This bank no longer exists.");
                return;
            }

            if (!confirmation.equalsIgnoreCase(actualName)) {
                player.sendMessage(ChatColor.RED + "Bank name did not match. Deletion cancelled.");
                Bukkit.getScheduler().runTask(plugin, () -> openBankActions(player, bankId));
                return;
            }

            if (!BankDatabase.isOwner(player.getUniqueId().toString(), bankId)) {
                player.sendMessage(ChatColor.RED + "You are no longer the owner of this bank.");
                return;
            }

            BankDatabase.deleteBank(bankId);
            player.sendMessage(ChatColor.GREEN + "Bank '" + actualName + "' has been permanently deleted.");
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1f);

            // Return to bank list
            Bukkit.getScheduler().runTask(plugin, () -> openBankList(player));
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "A database error occurred while deleting the bank.");
            plugin.getLogger().warning("GUI delete bank failed: " + e.getMessage());
            Bukkit.getScheduler().runTask(plugin, () -> openBankList(player));
        }
    }

    /**
     * Simple data class for pending chat-based amount input.
     */
    public record PendingAction(int bankId, String actionType) {
        // actionType examples: DEPOSIT, WITHDRAW, CREATE_CHECK, CREATE_BANK, INVITE, REMOVE_MEMBER, DELETE_BANK
    }
}

/**
 * InventoryHolder used to identify Tokens plugin GUIs and carry context (like bank ID).
 */
class TokensGuiHolder implements InventoryHolder {
    private final String guiType;
    private final int bankId;

    public TokensGuiHolder(String guiType, int bankId) {
        this.guiType = guiType;
        this.bankId = bankId;
    }

    public String getGuiType() {
        return guiType;
    }

    public int getBankId() {
        return bankId;
    }

    @Override
    public Inventory getInventory() {
        return null; // Standard for custom holders that don't own the inventory
    }
}
