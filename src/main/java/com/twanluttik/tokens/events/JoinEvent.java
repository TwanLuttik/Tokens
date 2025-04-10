package com.twanluttik.tokens.events;

import com.twanluttik.tokens.core.Actions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class JoinEvent implements Listener {

  Actions actions = new Actions();


  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) throws SQLException {
    Player p = e.getPlayer();

    Bukkit.getConsoleSender().sendMessage(p.getName() + "has joined");

    if (actions.playerExists(p.getUniqueId())) {
      p.sendMessage("You need to register for a bank account");
    } else {
      actions.createPlayer(p.getUniqueId());
      p.sendMessage("Welcome! Your bank account has been created.");
    }
  }

}
