package com.twanl.tokens.items;

import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.sql.SQLlib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


/**
 * Created by Twan on 3/22/2018.
 **/

public class TokenItem implements Listener {

    private ConfigManager cfgM = new ConfigManager();
    private Lib lib = new Lib();
    private SQLlib sql = new SQLlib();

    @SuppressWarnings("deprecation")
    public void addTokenNote(Player p, int amount) {

        // The custom item
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        //noinspection RedundantArrayCreation
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        meta.setDisplayName(Strings.green + "Note");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Strings.white + amount + " " + lib.getPrefix());
        lore.add(" ");
        lore.add(Strings.gray + "Created By: " + p.getName());
        meta.setLore(lore);
        item.setItemMeta(meta);

        // add the item to the player
        p.getInventory().addItem(item);

        // remove the tokens from the player
        if (lib.sqlUse()) {
            sql.removeTokens(p.getUniqueId(), amount);
        } else {
            cfgM.getPlayers().set(p.getUniqueId() + ".tokens", lib.balanceInt(p.getUniqueId()) - amount);
            cfgM.savePlayers();
        }
        p.sendMessage(Strings.green + amount + " " + lib.getPrefix() + " " + Strings.gray + "are converted to your inventory");

    }

    @SuppressWarnings("deprecation")
    public void removeTokenNote(Player p) {
        String[] getLoreList = Strings.stripColor(p.getItemInHand().getItemMeta().getLore().toString()).split(" ");

        String a = getLoreList[0];
        String amount = a.replace("[", "");

        String b = getLoreList[6];
        String ownerName = b.replace("]", "");


        // the custom item with the dynamic lore
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        //noinspection RedundantArrayCreation
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(Strings.green + "Note");
        lore.add(Strings.white + amount + " " + lib.getPrefix());
        lore.add(" ");
        lore.add(Strings.gray + "Created By: " + ownerName);
        meta.setLore(lore);
        item.setItemMeta(meta);


        //noinspection deprecation
        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);


        if (lib.sqlUse()) {
            sql.addTokens(p.getUniqueId(), Integer.parseInt(amount));
        } else {
            int tokensAmount = Integer.parseInt(amount);
            int playerTokens = cfgM.getPlayers().getInt(p.getUniqueId() + ".tokens");

            cfgM.getPlayers().set(p.getUniqueId() + ".tokens", playerTokens + tokensAmount);
            cfgM.savePlayers();
        }
        p.sendMessage(Strings.gray + "You converted " + Strings.green + amount + " " + lib.getPrefix() + Strings.gray + " to your account");

    }


}