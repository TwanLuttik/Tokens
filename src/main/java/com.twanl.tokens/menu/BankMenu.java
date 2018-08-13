package com.twanl.tokens.menu;

import org.bukkit.event.Listener;

/**
 * @author Twan
 */
public class BankMenu implements Listener {
    /*



    private static Tokens plugin = Tokens.getPlugin(Tokens.class);
    private ConfigManager config = new ConfigManager();
    private invAPI inv = new invAPI();
    private Lib lib = new Lib();


    //Some Strings

    private String B = Strings.DgrayB + "Bank";
    private String BS = Strings.DgrayB + "Bank Settings";
    private String removePB = Strings.Dgreen + "Remove player from bank";

    @EventHandler
    public void invenClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory open = e.getInventory();
        ItemStack item = e.getCurrentItem();
        config.setup();


        if (open == null) {
            return;
        }


        if (open.getName().equals(B)) {
            e.setCancelled(true);
            if (item == null || !item.hasItemMeta()) {
                return;
            }


            if (item.getItemMeta().getDisplayName().equals(Strings.white + "Close")) {
                p.closeInventory();
            }

            if (item.getItemMeta().getDisplayName().equals("Settings")) {
                bankSettings(p);
            }

        }


        if (open.getName().equals(BS)) {
            e.setCancelled(true);
            if (item == null || !item.hasItemMeta()) {
                return;
            }

            if (item.getItemMeta().getDisplayName().equals(removePB)) {
                for (String key : config.getBank().getConfigurationSection("bank-1.sub-users").getKeys(false)) {
                    p.sendMessage(key);
                }
                playerList(p);
            }


            if (item.getItemMeta().getDisplayName().equals("Back")) {
                inv(p);
            }


        }


        // PlayerList menu
        if (open.getName().equals("Players")) {
            e.setCancelled(true);
            if (item == null || !item.hasItemMeta()) {
                return;
            }

            // for loop trough the sub-users
            for (String key : config.getBank().getConfigurationSection("bank-1.sub-users").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                String pName = util.UUIDtoName.get(uuid);

                if (item.getItemMeta().getDisplayName().equals(Strings.white + pName)) {
                    config.getBank().set("bank-1.sub-users." + key, null);
                    config.saveBank();
                    p.sendMessage(Strings.prefix + Strings.green + pName + Strings.gray + " has been removed!");

                    playerList(p);
                }
            }


            if (item.getItemMeta().getDisplayName().equals("Back")) {
                bankSettings(p);

            }

        }


    }


    public void inv(Player p) {
        config.setup();

        String total = Strings.green + "Total: " + Strings.white + lib.bankTotal(p.getUniqueId()) + " " + lib.getPrefix();

        Inventory i = plugin.getServer().createInventory(null, 36, B);


        inv.addItem(i, "Settings", 1, 6, Material.BOOK_AND_QUILL);
        ArrayList<String> pList = new ArrayList<>();
        for (String key : config.getBank().getConfigurationSection("bank-1.sub-users").getKeys(false)) {
            Functions f = new Functions();
            String pName = f.getName(key);
            pList.add(Strings.white + pName);
        }
        p.sendMessage(pList.size()+"");

        inv.addItem(i, Strings.greenB + "sub-users:", 1, 1, Material.STONE, pList);
        inv.addItem(i, total, 1, 0, Material.BOOK);
        inv.addItem(i, Strings.white + "Close", 1, 35, 13, Material.STAINED_GLASS_PANE);


        p.openInventory(i);


    }


    private void bankSettings(Player p) {
        config.setup();
        Inventory i = plugin.getServer().createInventory(null, 27, BS);

        inv.addItem(i, removePB, 1, 0, Material.BARRIER);
        inv.addItem(i, "Back", 1, 22, 13, Material.STAINED_GLASS_PANE);

        p.openInventory(i);
    }




    private int i1 = 0;
    private void playerList(Player p) {
        config.setup();
        Inventory i = plugin.getServer().createInventory(null, 54, "Players");


        for (String key : config.getBank().getConfigurationSection("bank-1.sub-users").getKeys(false)) {
            Functions f = new Functions();
            UUID uuid = UUID.fromString(key);

            if (util.UUIDtoName.get(uuid) == null) {
                util.UUIDtoName.put(uuid, f.getName(key));
            }

            String pName = util.UUIDtoName.get(uuid);

            ItemStack item = new ItemStack(Material.SKULL_ITEM);
            item.setDurability((short) 3);
            SkullMeta sMeta = (SkullMeta) item.getItemMeta();
            sMeta.setOwner(pName);
            sMeta.setDisplayName(Strings.white + pName);
            item.setItemMeta(sMeta);

            i.setItem(i1, item);
            i1++;
        }

        i1 = 0;
        inv.addItem(i, "Back", 1, 53, 13, Material.STAINED_GLASS_PANE);


        p.openInventory(i);
    }

*/
}
