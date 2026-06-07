package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Fired after a bank has been deleted.
 */
public class BankDeletedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final int bankId;
    private final String bankName;
    private final UUID ownerUuid;
    private final Player deleter; // may be null

    public BankDeletedEvent(int bankId, String bankName, UUID ownerUuid, Player deleter) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.ownerUuid = ownerUuid;
        this.deleter = deleter;
    }

    public int getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public Player getDeleter() {
        return deleter;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
