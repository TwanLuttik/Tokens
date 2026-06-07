package com.twanluttik.tokens.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Fired after a player successfully redeems a token check.
 * Tokens have been added to the redeemer.
 */
public class CheckRedeemedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player redeemer;
    private final int amount;
    private final ItemStack checkItem;
    private final UUID originalCreator;
    private final String checkId;

    public CheckRedeemedEvent(Player redeemer, int amount, ItemStack checkItem, UUID originalCreator, String checkId) {
        this.redeemer = redeemer;
        this.amount = amount;
        this.checkItem = checkItem;
        this.originalCreator = originalCreator;
        this.checkId = checkId;
    }

    public Player getRedeemer() {
        return redeemer;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getCheckItem() {
        return checkItem;
    }

    public UUID getOriginalCreator() {
        return originalCreator;
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
