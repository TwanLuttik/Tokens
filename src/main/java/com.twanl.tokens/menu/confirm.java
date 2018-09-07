package com.twanl.tokens.menu;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.utils.Strings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class confirm implements Listener {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    public HashMap<Player, Boolean> a = new HashMap<>();
    private invAPI inv = new invAPI();


    @EventHandler
    public void invClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        ItemStack item = e.getCurrentItem();
        if (inv == null) {
            return;
        }




        if (inv.getName().equals(Strings.redB + "YOU ARE SURE")) {
            e.setCancelled(true);
            if (item == null || !item.hasItemMeta()) {
                return;
            }


            if (item.getItemMeta().getDisplayName().equals(Strings.red + "NO")) {
                a.put(p, false);
                p.closeInventory();
                return;
            }

            if (item.getItemMeta().getDisplayName().equals(Strings.green + "YES")) {
                a.put(p, true);
                p.closeInventory();
            }

        }

    }


    public void popupMenu(Player p) {
        Inventory i = plugin.getServer().createInventory(null, 9, Strings.redB + "YOU ARE SURE");

        inv.addItem(i, Strings.red + "NO", 1, 3, Material.RED_STAINED_GLASS_PANE, null);
        inv.addItem(i, Strings.green + "YES", 1, 5, Material.GREEN_STAINED_GLASS_PANE, null);

        p.openInventory(i);

    }

    public boolean confirmResult(Player p) {
//        if (a.get(p) == null) { return false; }
        p.sendMessage(String.valueOf(a.get(p)));
        return a.get(p);
    }


}
