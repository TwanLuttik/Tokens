package com.twanl.tokens.menu;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * @author Twan
 */
public class editMenu implements Listener {

    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager config = new ConfigManager();
    private invAPI inv = new invAPI();
    private Lib lib = new Lib();

    private String editM = Strings.DgrayB + "Shop edit menu";
    private String editMode_TITLE = Strings.DgrayB + " - " + Strings.redB + "EDIT MODE";


    @EventHandler
    public void invenClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory open = e.getInventory();
        ItemStack item = e.getCurrentItem();
        config.setup();


        if (open == null) {
            return;
        }


//        // check if the player is in edit mode && aand is the
//        if (!util.editMode.containsKey(p) || !util.editMode.get(p)) {
//            if (p.hasPermission("tokens.shop.edit")) {
//                return;
//            }
//        }


        if (open.getName().equals(editM)) {
            e.setCancelled(true);
            if (item == null || !item.hasItemMeta()) {
                return;
            }


            if (config.getShop().isSet("shop")) {
                for (String shops : config.getShop().getConfigurationSection("shop").getKeys(false)) {
                    if (item.getItemMeta().getDisplayName().equals(shops)) {

                        if (e.getClick() == ClickType.LEFT) {
                            util.editModeShop.put(p, shops);
                            util.editMode.put(p, true);
                            ShopMenu mm = new ShopMenu();
                            mm.openMenu_EDIT(p, shops);
                        }

                        //TODO:  in game edit the details of the item like the price and the data inside
//                    if (e.getClick() == ClickType.RIGHT) {
//                    }

                        if (e.getClick() == ClickType.MIDDLE) {
                            int shopCount = 0;
                            for (String shop : config.getShop().getConfigurationSection("shop").getKeys(false)) {
                                shopCount++;
                            }
                            if (shopCount == 1) {
                                config.getShop().set("shop", null);
                                config.saveShop();
                                p.sendMessage(Strings.prefix + Strings.green + "shop is removed from the shop.yml!");


                                config.getShop().set("default-shop", null);
                                config.saveShop();
                                p.sendMessage("last shop removed from config");
                                menuEditMenu(p);
                                return;
                            }


                            config.getShop().set("shop." + shops, null);
                            config.saveShop();
                            menuEditMenu(p);
                            p.sendMessage(Strings.prefix + Strings.green + "shop is removed from the shop.yml!");

                        }

                    }
                }


            }

            if (item.getItemMeta().getDisplayName().equals(Strings.white + "Close")) {
                p.closeInventory();
            }


        }

    }


    // cusotm inv
    public void menuEditMenu(Player p) {
        config.setup();


        Inventory i = plugin.getServer().createInventory(null, 36, editM);

        // check if there is no shop made yet
//        if (!config.getShop().isSet("shop")) {
//            inv.addItem(i, "No shop", 1, 0, Material.BARRIER);
//            inv.addItem(i, "Close", 1, 31, 14, Material.STAINED_GLASS_PANE);
//            p.openInventory(i);
//            return;
//        }

        // get all the shops and put them into the GUI
        int slotI = 0;
        if (config.getShop().isSet("shop")) {
            for (String shop : config.getShop().getConfigurationSection("shop").getKeys(false)) {
                inv.addItem(i, shop, 1, slotI, Material.PAPER);
                slotI++;
            }
        } else {
            inv.addItem(i, Strings.redB + "No shop's", 1, 0, Material.BARRIER);
        }


        // the book with the information in lore's
        ArrayList<String> info = new ArrayList<>();
        info.add(Strings.yellowB + "Left Click " + Strings.DgrayB + "» " + Strings.white + "edit the items of the shop");
//        info.add(Strings.yellowB + "Right Click " + Strings.DgrayB + "» " + Strings.white + "edit the details of the item in the shop");
        info.add(Strings.yellowB + "Middle Mouse Click " + Strings.DgrayB + "» " + Strings.white + "remove the shop");


        inv.addItem(i, Strings.whiteB + "Information", 1, 35, Material.BOOK, info);
        inv.addItem(i, Strings.white + "Close", 1, 31, 14, Material.STAINED_GLASS_PANE);
        p.openInventory(i);
    }

}
