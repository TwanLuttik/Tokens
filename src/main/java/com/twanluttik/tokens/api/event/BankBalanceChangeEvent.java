package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Fired when tokens are added to or removed from a bank balance.
 */
public class BankBalanceChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final int bankId;
    private final String bankName;
    private final UUID actor; // player causing the change (may be null for API/plugin)
    private final Player actorPlayer; // may be null
    private final int oldBalance;
    private int newBalance;
    private final boolean isDeposit;
    private boolean cancelled = false;

    public BankBalanceChangeEvent(int bankId, String bankName, UUID actor, Player actorPlayer,
                                  int oldBalance, int newBalance, boolean isDeposit) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.actor = actor;
        this.actorPlayer = actorPlayer;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.isDeposit = isDeposit;
    }

    public int getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public UUID getActor() {
        return actor;
    }

    public Player getActorPlayer() {
        return actorPlayer;
    }

    public int getOldBalance() {
        return oldBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(int newBalance) {
        this.newBalance = newBalance;
    }

    /**
     * @return true if tokens are being added to the bank (deposit), false for withdraw.
     */
    public boolean isDeposit() {
        return isDeposit;
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
}
