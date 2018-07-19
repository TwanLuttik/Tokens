package com.twanl.tokens.items;

import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

import static org.bukkit.Material.DOUBLE_PLANT;

/**
 * Created by Twan on 3/22/2018.
 **/

public class TokenItem implements Listener{

    private ConfigManager cfgM = new ConfigManager();
    private Lib lib = new Lib();

    public void addToken (Player p, int tokens) {

        // get a random number to prevent stacking items
        Random rand = new Random();
        int n = rand.nextInt(1000000) + 1;

        // CODE of the custom item
        ItemStack item = new ItemStack(Material.DOUBLE_PLANT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Strings.gold + tokens +  Strings.gray + " " + lib.getPrefix());
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(Strings.gold + "/tokens redeem " + Strings.gray + "to redeem the " + lib.getPrefix());
        lore.add(" ");
        lore.add(Strings.grayI + "ID: " + String.valueOf(n));
        meta.setLore(lore);
        item.setItemMeta(meta);

        p.getInventory().addItem(item);


        int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
        p.sendMessage(Strings.green + tokens + " " + lib.getPrefix() + " " + Strings.gray + "are converted to your inventory");

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens - tokens);
        cfgM.savePlayers();
    }


    //TODO: is not removing the item from the player
    @SuppressWarnings("deprecation")
    public void removeToken(Player p) {

        // get the int from the itemlore(Custom item) specific line
        String[] getLoreList = Strings.stripColor(p.getItemInHand().getItemMeta().getLore().toString()).split(" ");


        String getLoreLine = getLoreList[9];
        String getLoreId = getLoreLine.replaceAll("]", "");


        // get the first word(int in this case) and convert to to a string without any color codes
        String[] tokenItem = ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName()).split(" ");
        String firstword = tokenItem[0];

        // CODE for getting the item
        ItemStack item = new ItemStack(DOUBLE_PLANT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Strings.gold + firstword +  Strings.gray + " " + lib.getPrefix());
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(Strings.gold + "/tokens redeem " + Strings.gray + "to redeem the " + lib.getPrefix());
        lore.add(" ");
        lore.add(Strings.grayI + "ID: " + getLoreId);
        meta.setLore(lore);
        item.setItemMeta(meta);


        int tokensAmount = Integer.parseInt(firstword);
        int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");
        p.sendMessage(Strings.gray + "You converted " + Strings.green + tokensAmount + " " + lib.getPrefix() + " " + Strings.gray + " to your account");

        cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens + tokensAmount);
        cfgM.savePlayers();

        p.getInventory().remove(item);

    }

}