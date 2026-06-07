package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Fired after a player successfully creates a physical token check.
 * The tokens have already been deducted from the creator.
 */
public class CheckCreatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player creator;
    private final int amount;
    private final ItemStack checkItem;
    private final String checkId;

    public CheckCreatedEvent(Player creator, int amount, ItemStack checkItem, String checkId) {
        this.creator = creator;
        this.amount = amount;
        this.checkItem = checkItem;
        this.checkId = checkId;
    }

    public Player getCreator() {
        return creator;
    }

    public UUID getCreatorUuid() {
        return creator != null ? creator.getUniqueId() : null;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getCheckItem() {
        return checkItem;
    }

    public String getCheckId() {
        return checkId;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
