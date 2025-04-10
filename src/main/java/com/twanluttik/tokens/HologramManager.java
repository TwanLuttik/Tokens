package com.twanluttik.tokens;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class HologramManager {
  private static final String HOLOGRAM_NAME = "tokens_top10";
  private static HologramManager instance;
  private Hologram hologram;
  private Boolean isSet = false;


  private HologramManager() {
    if (DHAPI.getHologram(HOLOGRAM_NAME) != null) {
      hologram = DHAPI.getHologram(HOLOGRAM_NAME);
      isSet = true;
    }

  }

  public static HologramManager getInstance() {
    if (instance == null) {
      instance = new HologramManager();
    }
    return instance;
  }

  public void createHologram(Location location) {
    if (DHAPI.getHologram(HOLOGRAM_NAME) != null) {
      deleteHologram();
    }

    // Create new hologram
    hologram = DHAPI.createHologram(HOLOGRAM_NAME, location, true);
    DHAPI.addHologramLine(hologram, "&6&lTop 10 Token Holders");
    DHAPI.addHologramLine(hologram, "&7&m-------------------");

    try {
      // Get top 10 players from database
      List<UUID> topPlayers = Database.getTopPlayers(10);

      // Add player lines
      for (int i = 0; i < topPlayers.size(); i++) {
        UUID playerId = topPlayers.get(i);
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
        String playerName = player.getName() != null ? player.getName() : "Unknown";
        int tokens = Database.getPlayerTokens(playerId);

        DHAPI.addHologramLine(hologram,
         String.format("&e#%d &7%s &8- &6%,d tokens",
          i + 1,
          playerName,
          tokens
         )
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    isSet = true;
  }

  public void updateHologram() {
    if (!isSet)
      return;



    try {
      // Get top 10 players from database
      List<UUID> topPlayers = Database.getTopPlayers(10);

      // Add player lines
      for (int i = 1; i < topPlayers.size(); i++) {
        UUID playerId = topPlayers.get(i);
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
        String playerName = player.getName() != null ? player.getName() : "Unknown";
        int tokens = Database.getPlayerTokens(playerId);

        DHAPI.setHologramLine(hologram,
         i + 1,
         String.format("&e#%d &7%s &8- &6%,d tokens",
          i + 1,
          playerName,
          tokens
         )
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void deleteHologram() {
    if (DHAPI.getHologram(HOLOGRAM_NAME) != null) {
      DHAPI.removeHologram(HOLOGRAM_NAME);
    }
  }
} 