package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Fired after a new bank has been successfully created.
 */
public class BankCreatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final int bankId;
    private final String bankName;
    private final UUID ownerUuid;
    private final Player creator; // may be null

    public BankCreatedEvent(int bankId, String bankName, UUID ownerUuid, Player creator) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.ownerUuid = ownerUuid;
        this.creator = creator;
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

    /**
     * @return The player who created the bank, if they were online.
     */
    public Player getCreator() {
        return creator;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
