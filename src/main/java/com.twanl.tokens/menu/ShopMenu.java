package com.twanl.tokens.menu;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.reflection.ReflectionDisplayname;
import com.twanl.tokens.utils.ConfigManager;
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


        // check if the player is in edit mode
        if (!util.editMode.containsKey(p) || util.editMode.get(p)) {
            if (p.hasPermission("tokens.shop.edit")) {
                return;
            }
        }



        //TODO: <PRIO = LOW> instead of looking when a player click on an item with displayname, check for WhiceSlotClicked?
        //TODO: <PRIO = MED> support for Potions, enchanted Books and arrows

        // check if the edit mode is enalbed for the player thath opens the shop
        if (!util.editMode.get(p)) {


            // default shopName and pageName


            for (String key1 : config.getShop().getConfigurationSection("shop").getKeys(false)) {
                String colorTitle = Strings.translateColorCodes(lib.shopGetTitle(key1));

                if (open.getName().equals(colorTitle)) {
                    e.setCancelled(true);
                    if (item == null || !item.hasItemMeta()) {
                        return;
                    }


                    // for loop, for the items
                    for (String key : config.getShop().getConfigurationSection("shop." + key1 + ".slots").getKeys(false)) {
                        int i = Integer.parseInt(key);


                        String colorText = Strings.translateColorCodes(lib.itemName(key1, i));
                        String colorText1 = Strings.reset + colorText;
                        if (item.getItemMeta().getDisplayName().equals(colorText1)) {


                            // checks if the palyer has permission
                            if (config.getShop().isSet("shop." + key1 + ".slots." + i + ".permissions")) {
                                if (!p.hasPermission(config.getShop().getString("shop." + key1 + ".slots." + i + ".permissions"))) {
                                    p.sendMessage(Strings.red + "You don't have permission for that!");
                                    p.closeInventory();
                                    return;
                                }
                            }

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

//                                    p.closeInventory();
                                    p.getInventory().addItem(item1);
                                    lib.removeTokens(null, p.getUniqueId(), itemPrice);
                                    p.updateInventory(); // it will prevent from shift clicking the item to the inventory
                                    p.sendMessage(Strings.gray + "You have got " + Strings.green + itemAmount + "X " + Strings.translateColorCodes(lib.itemName(key1, i)));
                                    return;
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




    @EventHandler
    public void invMovement(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        config.setup();


        util.editMode.putIfAbsent(p, false);

        if (util.editMode.get(p)) {

            String menu = util.editModeShop.get(p);

            String colorTitle = Strings.translateColorCodes(lib.shopGetTitle(menu));
            if (e.getInventory().getName().equals(colorTitle)) {


//                Bukkit.getConsoleSender().sendMessage("----------------------------");
                for (int i = 0; i < e.getInventory().getSize(); i++) {

                    // check if the slot is empty than clear that in the file
                    if (e.getInventory().getItem(i) == null) {
                        if (config.getShop().isSet("shop." + menu + ".slots." + i)) {
                            config.getShop().set("shop." + menu + ".slots." + i, null);
//                            Bukkit.getConsoleSender().sendMessage(Strings.red + i + " REMOVED FROM FILE");
                        } else {
//                            Bukkit.getConsoleSender().sendMessage(Strings.blue + i + " NULL");
                        }


                    } else
                        // if the slot has a item in, check if the item is the same as in the config file for preventing removing edited data from the file else remove the data from the file and put new data into the file
                        if (e.getInventory().getItem(i).getType().getId() == lib.itemId(menu, i) && e.getInventory().getItem(i).getDurability() == lib.itemByte(menu, i)) {

//                        Bukkit.getConsoleSender().sendMessage(i + " ITEM IS THE SAME AS IN FILE!");


                    } else
                        // set the data into a hashmap than after the forloop get all the data from the hashmap and save that
                        if (e.getInventory().getItem(i) != null) {

                        int amount1 = e.getInventory().getItem(i).getAmount();
                        int itemID = e.getInventory().getItem(i).getType().getId();
                        int itemByte = e.getInventory().getItem(i).getDurability();
                        String B = ReflectionDisplayname.getFriendlyName(e.getInventory().getItem(i), true);



                        config.getShop().set("shop." + menu + ".slots." + i + ".name", B);
                        config.getShop().set("shop." + menu + ".slots." + i + ".slot", i);
                        config.getShop().set("shop." + menu + ".slots." + i + ".amount", amount1);
                        config.getShop().set("shop." + menu + ".slots." + i + ".Id", itemID);
                        config.getShop().set("shop." + menu + ".slots." + i + ".Data", itemByte);
                        config.getShop().set("shop." + menu + ".slots." + i + ".cost", 0);
                        config.getShop().set("shop." + menu + ".slots." + i + ".command", "<item>");

//                        Bukkit.getConsoleSender().sendMessage(Strings.green + i + " ITEM HAS BEEN ADDED");
                        config.saveShop();
                    }

                }

                config.saveShop();
//                Bukkit.getConsoleSender().sendMessage("----------------------------");
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
            p.openInventory(i);
            return;
        }

        for (String key : config.getShop().getConfigurationSection("shop." + menu + ".slots").getKeys(false)) {
            int i1 = Integer.parseInt(key);

            // if the itemname has color codes than translate it to colored text
            String itemNameColor = Strings.translateColorCodes(lib.itemName(menu, i1));
            String itemNameColor1 = Strings.reset + itemNameColor;

            inv.addItem(i, itemNameColor1, lib.itemAmount(menu, i1), lib.itemSlot(menu, i1), lib.itemByte(menu, i1), Material.getMaterial(lib.itemId(menu, i1)), " ", Strings.gray + "Price: " + Strings.green + lib.itemPrice(menu, i1) + " " + lib.getPrefix());
        }

        p.openInventory(i);
    }


}
