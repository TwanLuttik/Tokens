package com.twanl.tokens.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * @author Twan
 */
public class invAPI {

    public void addItem(Inventory inv, String ItemName, int Amount, int itemLocation, Material itemType) {
        ItemStack I = new ItemStack(itemType, Amount);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);
        I.setItemMeta(IMeta);

        inv.setItem(itemLocation, I);
    }

    public void addItem(Inventory inv, String ItemName, int Amount, int itemLocation, Material itemType, Enchantment Enchant, int i, boolean b) {
        ItemStack I = new ItemStack(itemType, Amount);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);
        IMeta.addEnchant(Enchant, i, b);
        I.setItemMeta(IMeta);

        inv.setItem(itemLocation, I);
    }


    public void addItem(Inventory inv, String ItemName, int Amount, int itemLocation, int bty, Material itemType) {
        ItemStack I = new ItemStack(itemType, Amount, (short)bty);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);
        I.setItemMeta(IMeta);

        inv.setItem(itemLocation, I);
    }


    public void addItem(Inventory inv, String ItemName, int Amount, int itemLocation, Material itemType, String lore) {
        ItemStack I = new ItemStack(itemType, Amount);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);



        ArrayList<String> lore1 = new ArrayList();
        lore1.add(String.valueOf(lore));
        IMeta.setLore(lore1);
        I.setItemMeta(IMeta);

        inv.setItem(itemLocation, I);
    }

    public void addItem(Inventory inv, String ItemName, int Amount, int itemLocation, int bty, Material itemType, String lore1, String lore2) {
        ItemStack I = new ItemStack(itemType, Amount, (short)bty);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);
        ArrayList<String> lore = new ArrayList();
        if (!lore1.isEmpty()) {
            lore.add(String.valueOf(lore1));
        }
        if (!lore2.isEmpty()) {
            lore.add(String.valueOf(lore2));
        }
        IMeta.setLore(lore);
        I.setItemMeta(IMeta);
        inv.setItem(itemLocation, I);
    }


}
