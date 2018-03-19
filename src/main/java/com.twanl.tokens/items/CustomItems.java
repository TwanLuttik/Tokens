package com.twanl.tokens.items;


import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.FlowerPot;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

public class CustomItems implements Listener {


    private ConfigManager cfgM = new ConfigManager();




    public void addToken(Player p, int tokens) {

        // get a random number to prevent stacking items
        Random rand = new Random();
        int n = rand.nextInt(1000000) + 1;

        // CODE of the custom item
        ItemStack item = new ItemStack(Material.DOUBLE_PLANT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Strings.goldB + tokens + " Tokens");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(Strings.goldB + "/tokens redeem " + Strings.white + "to redeem the tokens");
        lore.add(" ");
        lore.add(Strings.grayI + "ID: " + String.valueOf(n));
        meta.setLore(lore);
        meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        p.getInventory().addItem(item);


        int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
        p.sendMessage(Strings.greenB + tokens + " Tokens " + Strings.green + "are converted to your inventory");

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens - tokens);
        cfgM.savePlayers();
    }


    @SuppressWarnings("deprecation")
    public boolean removeToken(Player p) {

        // get the int from the itemlore(Custom item) specific line
        String[] getLoreList = ChatColor.stripColor(p.getInventory().getItemInMainHand().getItemMeta().getLore().toString()).split(" ");
        String getLoreLine = getLoreList[9];
        String getLoreId = getLoreLine.replaceAll("]", "");

        // get the first word(int in this case) and convert to to a string without any color codes
        String[] tokenItem = ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).split(" ");
        String firstword = tokenItem[0];

        // CODE for getting the item
        ItemStack item = new ItemStack(Material.DOUBLE_PLANT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Strings.goldB + firstword + " Tokens");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(Strings.goldB + "/tokens redeem " + Strings.white + "to redeem the tokens");
        lore.add(" ");
        lore.add(Strings.grayI + "ID: " + getLoreId);
        meta.setLore(lore);
        item.setItemMeta(meta);


        int tokensAmount = Integer.parseInt(firstword);
        int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
        p.sendMessage(Strings.green + "You converted " + Strings.greenB + tokensAmount + " Tokens" + Strings.green + " to your account");

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens + tokensAmount);
        cfgM.savePlayers();

        p.getInventory().remove(item);


        return false;
    }


    // preventing from placing a Token
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        // get the first word(int in this case) and convert to to a string without any color codes
        String[] tokenItem = ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).split(" ");
        String firstword = tokenItem[0];

        if (p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(Strings.goldB + firstword + " Tokens")){
            p.sendMessage(ChatColor.RED + "You cannot place your token");
            e.setCancelled(true);
        }
    }

}





