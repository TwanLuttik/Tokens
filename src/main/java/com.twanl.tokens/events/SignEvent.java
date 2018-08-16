package com.twanl.tokens.events;

import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.loadManager;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Twan on 3/29/2018.
 **/
public class SignEvent implements Listener {

//    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    private Lib lib = new Lib();

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("tokens.sign.place")) {
            if (e.getLine(0).equalsIgnoreCase("[tokens]")) {
                int line = 0;
                for (String key : loadManager.sign()) {
                    e.setLine(line, Strings.translateColorCodes(key));
                    line++;
                }
//                e.setLine(0, Strings.translateColorCodes(plugin.getConfig().getString("sign.line1")));
//                e.setLine(1, Strings.translateColorCodes(plugin.getConfig().getString("sign.line2")));
//                e.setLine(2, Strings.translateColorCodes(plugin.getConfig().getString("sign.line3")));
//                e.setLine(3, Strings.translateColorCodes(plugin.getConfig().getString("sign.line4")));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        if (e.getClickedBlock().getState() instanceof Sign) {
            Sign s = (Sign) e.getClickedBlock().getState();

//            if (s.getLine(0).equalsIgnoreCase(Strings.translateColorCodes(plugin.getConfig().getString("sign.line1")))) {
            if (s.getLine(0).equalsIgnoreCase(Strings.translateColorCodes(loadManager.sign().get(0)))) {
                Player p = e.getPlayer();
                String balance = String.valueOf(lib.balanceInt(p.getUniqueId()));
                e.getPlayer().sendMessage(Strings.gray + "You have " + Strings.green + balance + " " + lib.getPrefix());
            }

        }
    }

}
