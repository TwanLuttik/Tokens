package com.twanl.tokens.items;


import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CustomItems implements Listener {


    private ConfigManager cfgM = new ConfigManager();


    public void addToken(Player p, int tokens) {
        ItemStack item = new ItemStack(Material.DOUBLE_PLANT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Strings.goldB + tokens + " Tokens");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(Strings.goldB + "/tokens redeem " + Strings.white + "to redeem the tokens");
        meta.setLore(lore);
        item.setItemMeta(meta);

        p.getInventory().addItem(item);


        int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens - tokens);
        cfgM.savePlayers();
    }


    @SuppressWarnings("deprecation")
    public boolean removeToken(Player p) {

        // get the first word(int in this case) and convert to to a string without any color codes
        String[] tokenItem = ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).split(" ");
        String firstword = tokenItem[0];

        // contruct of the custom item
        ItemStack item = new ItemStack(Material.DOUBLE_PLANT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Strings.goldB + firstword + " Tokens");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(Strings.goldB + "/tokens redeem " + Strings.white + "to redeem the tokens");
        meta.setLore(lore);
        item.setItemMeta(meta);


        int tokensAmount = Integer.parseInt(firstword);
        int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");


        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens + tokensAmount);
        cfgM.savePlayers();

        p.getInventory().remove(item);



        return false;
    }
}





