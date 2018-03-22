package com.twanl.tokens.events;

import com.twanl.tokens.Tokens;
import com.twanl.tokens.api.TokensAPI;
import com.twanl.tokens.utils.Strings;
import com.twanl.tokens.utils.UpdateChecker;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Twan on 3/22/2018.
 **/

public class JoinEvent implements Listener {

    private Tokens plugin = Tokens.getPlugin(Tokens.class);
    private TokensAPI tokenApi = new TokensAPI();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();


        // Updata message
        if (plugin.getConfig().getBoolean("update_message")) {
            if (p.hasPermission("tokens.update")) {
                final Player p1 = e.getPlayer();
                final PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                final PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Tokens is outdated!\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/tokens.53944/\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click to download the newest version of Tokens\",\"color\":\"gold\"}]}}}"));

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public UpdateChecker checker;

                    public void run() {
                        checker = new UpdateChecker(plugin);

                        if (checker.isConnected()) {
                            if (checker.hasUpdate()) {
                                p1.sendMessage(Strings.DgrayBS + "----------------------\n");
                                connection.sendPacket(packet);
                                p1.sendMessage(" \n" +
                                        Strings.white + "Your version: " + plugin.getDescription().getVersion() + "\n" +
                                        Strings.white + "Newest version: " + Strings.green + checker.getLatestVersion() + "\n" +
                                        Strings.DgrayBS + "----------------------");
                            } else {
                                p1.sendMessage(Strings.DgrayBS + "----------------------\n" +
                                        Strings.green + "Tokens is up to date.\n" +
                                        Strings.DgrayBS + "----------------------");
                            }
                        }
                    }
                }, 20);

            }
        }


        tokenApi.hasAccount(p.getUniqueId());
    }
}
