package com.twanluttik.tokens.api;

import java.util.List;
import java.util.UUID;

/**
 * Immutable data class representing a Token Bank.
 * <p>
 * This is a snapshot of bank data at the time it was retrieved from the API.
 * It does not automatically stay in sync with the database.
 */
public final class Bank {

    private final int id;
    private final String name;
    private final UUID owner;
    private final int balance;
    private final List<UUID> members;

    public Bank(int id, String name, UUID owner, int balance, List<UUID> members) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.balance = balance;
        this.members = members == null ? List.of() : List.copyOf(members);
    }

    /**
     * @return The unique numeric ID of this bank.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The display name of the bank.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The UUID of the player who owns this bank.
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * @return The current token balance stored in this bank.
     */
    public int getBalance() {
        return balance;
    }

    /**
     * @return An unmodifiable list of all member UUIDs (including the owner).
     */
    public List<UUID> getMembers() {
        return members;
    }

    /**
     * @return The number of members in this bank.
     */
    public int getMemberCount() {
        return members.size();
    }

    /**
     * Checks if the given player is the owner of this bank.
     *
     * @param playerUuid The player to check
     * @return true if this player owns the bank
     */
    public boolean isOwner(UUID playerUuid) {
        return owner != null && owner.equals(playerUuid);
    }

    /**
     * Checks if the given player is a member of this bank (includes owner).
     *
     * @param playerUuid The player to check
     * @return true if the player can access this bank
     */
    public boolean hasMember(UUID playerUuid) {
        return members.contains(playerUuid);
    }

    @Override
    public String toString() {
        return "Bank{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", balance=" + balance +
                ", members=" + members.size() +
                '}';
    }
}
