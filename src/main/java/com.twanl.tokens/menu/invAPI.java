package com.twanl.tokens.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Twan
 */
public class invAPI {

    // DEFAULT
    public void addItem(Inventory inv, String ItemName, int Amount, int slot, Material itemType, List<String> list) {
        ItemStack I = new ItemStack(itemType, Amount);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);

        if (list != null) {
            ArrayList<String> lore = new ArrayList();
            for (String s : list) {
                lore.add(s);
            }
            IMeta.setLore(lore);
        }

        I.setItemMeta(IMeta);
        inv.setItem(slot, I);
    }

    // BYTE
    public void addItem(Inventory inv, String ItemName, int Amount, int slot, int bty, Material itemType, List<String> list) {
        ItemStack I = new ItemStack(itemType, Amount, (short) bty);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);

        if (list != null) {
            ArrayList<String> lore = new ArrayList();
            for (String s : list) {
                lore.add(s);
            }
            IMeta.setLore(lore);
        }

        I.setItemMeta(IMeta);
        inv.setItem(slot, I);
    }

    // ENCHANTS
    public void addItem(Inventory inv, String ItemName, int Amount, int slot, Material itemType, Enchantment Enchant, int i, boolean b, List<String> list) {
        ItemStack I = new ItemStack(itemType, Amount);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);
        IMeta.addEnchant(Enchant, i, b);

        if (list != null) {
            ArrayList<String> lore = new ArrayList();
            for (String s : list) {
                lore.add(s);
            }
            IMeta.setLore(lore);
        }

        I.setItemMeta(IMeta);
        inv.setItem(slot, I);
    }

    // BYTE + ENCHANTS
    public void addItem(Inventory inv, String ItemName, int Amount, int slot, int bty, Material itemType, Enchantment Enchant, int i, boolean b, List<String> list) {
        ItemStack I = new ItemStack(itemType, Amount, (short) bty);
        ItemMeta IMeta = I.getItemMeta();
        IMeta.setDisplayName(ItemName);
        IMeta.addEnchant(Enchant, i, b);

        if (list != null) {
            ArrayList<String> lore = new ArrayList();
            for (String s : list) {
                lore.add(s);
            }
            IMeta.setLore(lore);
        }

        I.setItemMeta(IMeta);
        inv.setItem(slot, I);
    }


}
