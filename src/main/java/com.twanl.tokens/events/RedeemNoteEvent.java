package com.twanl.tokens.events;


import com.twanl.tokens.items.TokenItem;
import com.twanl.tokens.utils.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * @author Twan
 */
public class RedeemNoteEvent implements Listener {

    private TokenItem item = new TokenItem();

    @EventHandler
    public void redeemEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();


        EquipmentSlot es = e.getHand();


        if (!p.getItemInHand().hasItemMeta()) {
            return;
        }


        if (p.isSneaking()) {
            if (es.equals(EquipmentSlot.HAND)) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(Strings.green + "Note"))
                        e.setCancelled(true);
                        item.removeTokenNote(p);
                }
            }
        }
    }


}


