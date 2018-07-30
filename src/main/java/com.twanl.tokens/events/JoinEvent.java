package com.twanl.tokens.events;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.lib.Lib;
import com.twanl.tokens.sql.SQLlib;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

/**
 * Created by Twan on 3/22/2018.
 **/

public class JoinEvent implements Listener {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    private Lib lib = new Lib();
    private SQLlib sql = new SQLlib();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();


        // Update message
        if (plugin.getConfig().getBoolean("update_message")) {
            if (p.hasPermission("tokens.update")) {


                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public UpdateChecker checker;

                    public void run() {
                        checker = new UpdateChecker(plugin);

                        if (checker.isConnected()) {
                            if (checker.hasUpdate()) {

                                p.sendMessage(Strings.DgrayBS + "----------------------\n");
                                plugin.nms.sendClickableHovarableMessageURL(p, Strings.red + "Tokens is outdated!", Strings.gold + "Click to go to the download page", "https://www.spigotmc.org/resources/tokens.53944/");
                                p.sendMessage(" \n" +
                                        Strings.white + "Your version: " + plugin.getDescription().getVersion() + "\n" +
                                        Strings.white + "Newest version: " + Strings.green + checker.getLatestVersion() + "\n" +
                                        Strings.DgrayBS + "----------------------");

                            } else {

                                p.sendMessage(Strings.DgrayBS + "----------------------\n" +
                                        Strings.green + "Tokens is up to date.\n" +
                                        Strings.DgrayBS + "----------------------");

                            }
                        }
                    }
                }, 20);

            }
        }


        if (lib.sqlUse()) {
            try {
                if (!sql.hasAccount(p.getUniqueId())) {
                    sql.addPlayer(p.getUniqueId());
                }
            } catch (Exception e1) {
                p.sendMessage(Strings.prefix + Strings.red + "error, check console!");
                Bukkit.getConsoleSender().sendMessage(Strings.logName + Strings.red + "Probably failed to connect to the mySQL database");
            }
        } else {
            lib.creatAccount(p.getUniqueId());
        }
    }
}
