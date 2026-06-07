package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Fired when a member is removed from a bank.
 */
public class BankMemberRemovedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final int bankId;
    private final String bankName;
    private final UUID ownerUuid;
    private final UUID removedMemberUuid;
    private final Player remover; // may be null

    public BankMemberRemovedEvent(int bankId, String bankName, UUID ownerUuid, UUID removedMemberUuid, Player remover) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.ownerUuid = ownerUuid;
        this.removedMemberUuid = removedMemberUuid;
        this.remover = remover;
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

    public UUID getRemovedMemberUuid() {
        return removedMemberUuid;
    }

    public Player getRemover() {
        return remover;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
