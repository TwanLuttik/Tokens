package com.twanluttik.tokens.events;

import com.twanluttik.tokens.Database;
import com.twanluttik.tokens.Tokens;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JoinEvent implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    int initial = 0;

    try {
      if (Tokens.getInstance() != null && Tokens.getInstance().getConfigManager() != null) {
        initial = Tokens.getInstance().getConfigManager().getInitialTokens();
      }

      // Insert player with initial tokens only if they don't exist yet
      Connection conn = Database.getConnection();
      try (PreparedStatement ps = conn.prepareStatement(
              "INSERT INTO players (uuid, tokens) VALUES (?, ?) ON CONFLICT (uuid) DO NOTHING")) {
        ps.setString(1, p.getUniqueId().toString());
        ps.setInt(2, initial);
        ps.executeUpdate();
      }
    } catch (SQLException ex) {
      if (Tokens.getInstance() != null) {
        Tokens.getInstance().getLogger().warning("Failed to initialize player on join: " + ex.getMessage());
      }
    }
  }
}
