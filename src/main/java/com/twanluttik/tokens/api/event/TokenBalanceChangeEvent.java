package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Fired when a player's personal token balance is about to change or has changed.
 * <p>
 * This event is cancellable for operations that support cancellation (add/remove via API).
 * The final new balance can be read after the change, or you can modify it using {@link #setNewBalance(int)}.
 */
public class TokenBalanceChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID playerUuid;
    private final Player player; // may be null if offline
    private final int oldBalance;
    private int newBalance;
    private final ChangeReason reason;
    private boolean cancelled = false;

    public TokenBalanceChangeEvent(UUID playerUuid, Player player, int oldBalance, int newBalance, ChangeReason reason) {
        super();
        this.playerUuid = playerUuid;
        this.player = player;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.reason = reason;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    /**
     * @return The player involved, or null if they are not online.
     */
    public Player getPlayer() {
        return player;
    }

    public int getOldBalance() {
        return oldBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }

    /**
     * Changes what the new balance will be (only has effect before the change is applied).
     */
    public void setNewBalance(int newBalance) {
        this.newBalance = newBalance;
    }

    public ChangeReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum ChangeReason {
        ADMIN_COMMAND,
        PLAYER_COMMAND,
        CHECK_REDEEM,
        BANK_DEPOSIT,
        BANK_WITHDRAW,
        CHECK_CREATE,
        PLUGIN,
        API,
        UNKNOWN
    }
}
