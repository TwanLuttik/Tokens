package com.twanluttik.tokens;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CheckManager {
    private static final String CHECK_TABLE = "checks";
    
    public static void initializeTables() throws SQLException {
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + CHECK_TABLE + " (" +
                        "check_id VARCHAR(36) PRIMARY KEY, " +
                        "amount INT NOT NULL, " +
                        "creator_uuid VARCHAR(36) NOT NULL, " +
                        "redeemed BOOLEAN DEFAULT FALSE, " +
                        "redeemer_uuid VARCHAR(36), " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")")) {
            statement.execute();
        }
    }
    
    public static ItemStack createCheck(Player creator, int amount) throws SQLException {
        // Generate a unique ID for the check
        String checkId = UUID.randomUUID().toString();
        
        // Create the check item
        ItemStack check = new ItemStack(Material.PAPER);
        ItemMeta meta = check.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.setDisplayName(ChatColor.GOLD + "Token Check");
        meta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Amount: " + ChatColor.GREEN + amount,
                ChatColor.GRAY + "ID: " + checkId,
                ChatColor.GRAY + "Created by: " + creator.getName()
        ));
        check.setItemMeta(meta);
        
        // Store the check in the database
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO " + CHECK_TABLE + " (check_id, amount, creator_uuid) VALUES (?, ?, ?)")) {
            statement.setString(1, checkId);
            statement.setInt(2, amount);
            statement.setString(3, creator.getUniqueId().toString());
            statement.execute();
        }
        
        return check;
    }
    
    public static boolean redeemCheck(Player redeemer, ItemStack check) throws SQLException {
        if (check == null || check.getType() != Material.PAPER) {
            return false;
        }
        
        ItemMeta meta = check.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return false;
        }
        
        // Extract check ID from lore
        String checkId = null;
        for (String line : meta.getLore()) {
            if (line.startsWith(ChatColor.GRAY + "ID: ")) {
                checkId = line.substring(line.indexOf(": ") + 2);
                break;
            }
        }
        
        if (checkId == null) {
            return false;
        }
        
        // Verify and redeem the check
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT amount FROM " + CHECK_TABLE + 
                " WHERE check_id = ? AND redeemed = FALSE")) {
            statement.setString(1, checkId);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                int amount = result.getInt("amount");
                
                // Mark check as redeemed
                try (PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE " + CHECK_TABLE + 
                        " SET redeemed = TRUE, redeemer_uuid = ? WHERE check_id = ?")) {
                    updateStatement.setString(1, redeemer.getUniqueId().toString());
                    updateStatement.setString(2, checkId);
                    updateStatement.execute();
                }
                
                // Add tokens to redeemer
                Database.addTokens(redeemer.getUniqueId().toString(), amount);
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean isValidCheck(ItemStack check) {
        if (check == null || check.getType() != Material.PAPER) {
            return false;
        }
        
        ItemMeta meta = check.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return false;
        }
        
        // Check if it has the required lore format
        boolean hasAmount = false;
        boolean hasId = false;
        boolean hasCreator = false;
        
        for (String line : meta.getLore()) {
            if (line.startsWith(ChatColor.GRAY + "Amount: ")) {
                hasAmount = true;
            } else if (line.startsWith(ChatColor.GRAY + "ID: ")) {
                hasId = true;
            } else if (line.startsWith(ChatColor.GRAY + "Created by: ")) {
                hasCreator = true;
            }
        }
        
        return hasAmount && hasId && hasCreator;
    }
} 