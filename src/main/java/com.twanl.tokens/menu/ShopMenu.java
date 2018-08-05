package com.twanl.tokens.menu;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.utils.ConfigManager;
import com.twanl.tokens.utils.ReflectionDisplayname;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


/**
 * @author Twan
 */
public class ShopMenu implements Listener {

    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager config = new ConfigManager();
    private invAPI inv = new invAPI();
    private Lib lib = new Lib();



    @EventHandler
    public void invenClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory open = e.getInventory();
        ItemStack item = e.getCurrentItem();
        config.setup();


        if (open == null) {
            return;
        }

//        if (!util.editMode.get(p)) {
//
//
//            for (String key : config.getShop().getConfigurationSection("shop").getKeys(false)) {
//                p.sendMessage(Strings.yellowB + key);
//
//                String colorTitle = Strings.translateColorCodes(lib.shopGetTitle(key));
//
//                if (open.getName().equals(colorTitle)) {
//                    e.setCancelled(true);
//                    if (item == null || !item.hasItemMeta()) {
//                        return;
//                    }
//
//                    for (String key1 : config.getShop().getConfigurationSection("shop." +key + ".slots").getKeys(false)) {
//                        int i = Integer.parseInt(key1);
//                        p.sendMessage(Strings.aquaB + key1);
//
//
//                        String colorText = Strings.translateColorCodes(lib.itemName(key, i));
//
//                        if (item.getItemMeta().getDisplayName().equals(colorText)) {
//                            e.setCancelled(true);
//
//
//                        }
//                    }
//                }
//            }
//        }





        // check if the edit mode is enalbed for the player thath opens the shop
        if (!util.editMode.get(p)) {


            // default shopName and pageName
//            String menuName = config.getShop().getString("default-shop");


//            String colorTitle = Strings.translateColorCodes(lib.shopGetTitle("member", "main"));

            for (String key1 : config.getShop().getConfigurationSection("shop").getKeys(false)) {
                //                String colorTitle = Strings.translateColorCodes(lib.shopGetTitle(menuName));
                String colorTitle = Strings.translateColorCodes(lib.shopGetTitle(key1));

                if (open.getName().equals(colorTitle)) {
                    e.setCancelled(true);
                    if (item == null || !item.hasItemMeta()) {
                        return;
                    }


//                for (String key : config.getShop().getConfigurationSection("member.main.slots").getKeys(false)) {
                    // for loop, for the items
                    for (String key : config.getShop().getConfigurationSection("shop." + key1 + ".slots").getKeys(false)) {
                        int i = Integer.parseInt(key);


//                    String colorText = Strings.translateColorCodes(lib.itemName("member", "main", i));
                        String colorText = Strings.translateColorCodes(lib.itemName(key1, i));

                        if (item.getItemMeta().getDisplayName().equals(colorText)) {

                            // checks if the palyer has permission
                            if (config.getShop().isSet("shop." + key1 + ".slots." + i + ".permissions")) {
                                if (!p.hasPermission(config.getShop().getString("shop." + key1 + ".slots." + i + ".permissions"))) {
                                    p.sendMessage(Strings.red + "You don't have permission for that!");
                                    p.closeInventory();
                                    return;
                                }
                            }


                            // methode for the command path
                            if (config.getShop().isSet("shop." + key1 + ".slots." + i + ".command")) {

                                // close the shop
                                if (lib.itemCommand(key1, i).contains("<close>")) {
                                    p.closeInventory();
                                    return;
                                }

                                // open a menu from the menu name
                                if (lib.itemCommand(key1, i).contains("<open>")) {
                                    String[] page = lib.itemCommand(key1, i).split(" ");
                                    String menuFinal = page[1];

                                    openMenu(p, menuFinal);
                                    return;
                                }



                                // some base information
                                int itemPrice = lib.itemPrice(key1, i);
                                int playerBalance = lib.balanceInt(p.getUniqueId());


                                if (itemPrice > playerBalance) { // check if the player has enough tokens
                                    p.closeInventory();
                                    p.updateInventory(); // it will prevent from shift clicking the item to the inventory
                                    p.sendMessage(Strings.red + "You don't have enought to buy this!");
                                    return;
                                } else { // the payment methode


                                    // so you don't need t write a command to give the player the iten with the amount (just for faster configuring)
                                    if (lib.itemCommand(key1, i).contains("<item>")) {
                                        if (p.getInventory().firstEmpty() == -1) {
                                            p.closeInventory();
                                            p.updateInventory();
                                            p.sendMessage(Strings.red + "You don't have enough inventory space");
                                            return;
                                        }

                                        int itemID = lib.itemId(key1, i);
                                        int itemByte = lib.itemByte(key1, i);
                                        int itemAmount = lib.itemAmount(key1, i);
                                        ItemStack item1 = new ItemStack(Material.getMaterial(itemID), itemAmount, (short) itemByte);

                                        p.closeInventory();
                                        p.getInventory().addItem(item1);
                                        lib.removeTokens(null, p.getUniqueId(), itemPrice);
                                        p.updateInventory(); // it will prevent from shift clicking the item to the inventory
                                        p.sendMessage(Strings.gray + "You have got " + Strings.green + itemAmount + "X " + Strings.translateColorCodes(lib.itemName(key1, i)));
                                    }

                                    // for executing a custom command
                                    if (lib.itemCommand(key1, i).contains("<command>")) {
                                        String command = lib.itemCommand(key1, i).replace("<command>", "");
                                        String command1 = command.replace("{player}", p.getName());

                                        p.closeInventory();
                                        lib.removeTokens(null, p.getUniqueId(), itemPrice);
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command1);
                                        return;
                                    }
                                }


                            }

                        }

                    }


                }

            }
        }

    }



    @EventHandler
    public void invMovement(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        config.setup();

        if (!p.hasPermission("test")) {
            return;
        }

        util.editMode.putIfAbsent(p, false);

        if (util.editMode.get(p)) {

            String menu = util.editModeShop.get(p);

            String colorTitle = Strings.translateColorCodes(lib.shopGetTitle(menu));
            if (e.getInventory().getName().equals(colorTitle)) {


                for (int i = 0; i < e.getInventory().getSize(); i++) {
                    //if (e.getInventory().getItem(i) != null) {

                    if (e.getInventory().getItem(i) == null) {
                        config.getShop().set("shop." + menu + ".slots." + i, null);
                    } else if (e.getInventory().getItem(i) != null) {


                        int amount = e.getInventory().getItem(i).getAmount();
                        int itemID = e.getInventory().getItem(i).getType().getId();
                        int itemByte = e.getInventory().getItem(i).getDurability();
                        String B = ReflectionDisplayname.getFriendlyName(e.getInventory().getItem(i), true);


                        config.getShop().set("shop." + menu + ".slots." + i + ".name", B);
                        config.getShop().set("shop." + menu + ".slots." + i + ".slot", i);
                        config.getShop().set("shop." + menu + ".slots." + i + ".amount", amount);
                        config.getShop().set("shop." + menu + ".slots." + i + ".Id", itemID);
                        config.getShop().set("shop." + menu + ".slots." + i + ".Data", itemByte);
                        config.getShop().set("shop." + menu + ".slots." + i + ".cost", 0);


                        p.sendMessage(Strings.greenB + B);
                    }
                }

                config.saveShop();
                p.sendMessage(Strings.prefix + Strings.green + "inventory saved!");
                util.editMode.put(p, false);
            }


        }

    }

    public void openMenu(Player p, String menu) {
        config.setup();


        String colorTitle = Strings.translateColorCodes(lib.shopGetTitle(menu));
        Inventory i = plugin.getServer().createInventory(null, lib.shopGetSlots(menu), colorTitle);

        if (!config.getShop().isSet("shop." + menu + ".slots")) {
            Bukkit.getConsoleSender().sendMessage(Strings.red + "DEBUG " + menu);
            p.openInventory(i);
            return;
        }

        for (String key : config.getShop().getConfigurationSection("shop." + menu + ".slots").getKeys(false)) {
            int i1 = Integer.parseInt(key);

            // if the itemname has color codes than translate it to colored text
            String itemNameColor = Strings.translateColorCodes(lib.itemName(menu, i1));

            inv.addItem(i, itemNameColor, lib.itemAmount(menu, i1), lib.itemSlot(menu, i1), lib.itemByte(menu, i1), Material.getMaterial(lib.itemId(menu, i1)), " ", Strings.gray + "Price: " + Strings.green + lib.itemPrice(menu, i1) + " " + lib.getPrefix());
        }

        p.openInventory(i);
    }


}
