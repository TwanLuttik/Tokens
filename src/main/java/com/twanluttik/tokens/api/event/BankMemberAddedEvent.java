package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Fired when a player is added as a member to a bank (invite).
 */
public class BankMemberAddedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final int bankId;
    private final String bankName;
    private final UUID ownerUuid;
    private final UUID newMemberUuid;
    private final Player inviter; // may be null

    public BankMemberAddedEvent(int bankId, String bankName, UUID ownerUuid, UUID newMemberUuid, Player inviter) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.ownerUuid = ownerUuid;
        this.newMemberUuid = newMemberUuid;
        this.inviter = inviter;
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

    public UUID getNewMemberUuid() {
        return newMemberUuid;
    }

    public Player getInviter() {
        return inviter;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
