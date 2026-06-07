# Tokens Plugin — Developer API

This document describes how other plugins can integrate with the **Tokens** economy plugin.

## Table of Contents

- [Getting Started](#getting-started)
- [Depending on Tokens](#depending-on-tokens)
- [Accessing the API](#accessing-the-api)
- [Player Tokens](#player-tokens)
- [Banks](#banks)
- [Checks (Physical Token Notes)](#checks)
- [Leaderboards & Ranks](#leaderboards--ranks)
- [Events](#events)
- [Configuration & Limits](#configuration--limits)
- [Async Operations](#async-operations)
- [Threading & Best Practices](#threading--best-practices)
- [Full Example](#full-example)

---

## Getting Started

The recommended entry point is:

```java
import com.twanluttik.tokens.TokensAPI;

TokensAPI api = TokensAPI.getAPI();
if (api == null) {
    getLogger().warning("Tokens plugin is not installed or not yet enabled!");
    return;
}
```

> **Note:** Always null-check the result of `getAPI()`. Never assume the plugin is present.

---

## Depending on Tokens

### Gradle (recommended)

In your `build.gradle`:

```gradle
repositories {
    mavenCentral()
    // If you want to depend on a published version later, add your repo here.
}

dependencies {
    // Compile against Tokens, but do NOT bundle it into your jar
    compileOnly 'com.twanluttik:Tokens:3.0.0-PRE-v1'   // or whatever version you have

    // If you are developing against a local build, use a flat dir or file dependency:
    // compileOnly files('path/to/Tokens.jar')
}
```

In `plugin.yml` of your plugin:

```yaml
softdepend: [Tokens]
# or
depend: [Tokens]   # if Tokens is absolutely required
```

### Maven

```xml
<dependency>
    <groupId>com.twanluttik</groupId>
    <artifactId>Tokens</artifactId>
    <version>3.0.0-PRE-v1</version>
    <scope>provided</scope>
</dependency>
```

And in your `plugin.yml`:

```yaml
softdepend: [Tokens]
```

**Important:** Use `provided` / `compileOnly` scope. Do **not** shade Tokens into your plugin.

---

## Accessing the API

```java
TokensAPI api = TokensAPI.getAPI();
if (api == null) return;

// Safe to use
int balance = api.getPlayerTokens(player.getUniqueId());
```

Legacy (still works):

```java
TokensAPI api = TokensAPI.getInstance();
```

---

## Player Tokens

```java
UUID uuid = player.getUniqueId();

// Read
int balance = api.getPlayerTokens(uuid);

// Modify (these fire TokenBalanceChangeEvent)
api.addPlayerTokens(uuid, 250);
api.removePlayerTokens(uuid, 100);           // returns false if insufficient funds
api.setPlayerTokens(uuid, 5000);

// Create player record explicitly (usually not needed)
api.createPlayer(uuid);
api.createPlayer(uuid, 100);                 // with custom starting balance

// Existence check
boolean exists = api.playerExists(uuid);
```

**Async versions** (callback runs on main thread):

```java
api.addPlayerTokensAsync(uuid, 1000, success -> {
    if (success) {
        player.sendMessage("You received 1000 tokens!");
    }
});

api.removePlayerTokensAsync(uuid, 500, removed -> {
    if (!removed) player.sendMessage("Not enough tokens!");
});
```

---

## Banks

```java
// Create a bank (returns bank ID or -1 on failure / limit reached)
int bankId = api.createBank("MyCoolBank", player.getUniqueId());

// Retrieve full bank data (snapshot)
Bank bank = api.getBank(bankId);
if (bank != null) {
    System.out.println(bank.getName() + " has " + bank.getBalance() + " tokens");
    System.out.println("Owner: " + bank.getOwner());
    System.out.println("Members: " + bank.getMemberCount());
}

// Lightweight access
int balance = api.getBankBalance(bankId);
String name = api.getBankName(bankId);
UUID owner = api.getBankOwner(bankId);
List<UUID> members = api.getBankMembers(bankId);

// Player's banks
List<Bank> banks = api.getPlayerBanks(player.getUniqueId());
List<Integer> bankIds = api.getPlayerBankIds(player.getUniqueId());

// Deposit / Withdraw (recommended over raw add/remove)
boolean deposited = api.depositToBank(player.getUniqueId(), bankId, 500);
boolean withdrew = api.withdrawFromBank(player.getUniqueId(), bankId, 200);

// Membership
api.addBankMember(bankId, target.getUniqueId());
api.removeBankMember(bankId, target.getUniqueId());

// Delete
api.deleteBank(bankId);

// Permission helpers
boolean canAccess = api.hasBankAccess(player.getUniqueId(), bankId);
boolean isOwner   = api.isBankOwner(player.getUniqueId(), bankId);
boolean isMember  = api.isBankMember(player.getUniqueId(), bankId);
```

**Bank limits** (respect these in your plugin for consistency):

```java
int maxBanks   = api.getMaxBanksPerPlayer();
int maxMembers = api.getMaxMembersPerBank();
```

---

## Checks

Checks are physical paper items that can be traded and redeemed for tokens.

```java
// Create a check (deducts tokens from creator)
ItemStack check = api.createCheck(player, 1000);
if (check != null) {
    player.getInventory().addItem(check);
}

// Validate
if (api.isValidCheck(itemInHand)) {
    // ...
}

// Redeem (consumes the item and gives tokens)
boolean redeemed = api.redeemCheck(player, itemInHand);
```

Events are fired for both creation and redemption (see below).

---

## Leaderboards & Ranks

```java
// Player's position (1 = richest)
int rank = api.getPlayerRank(player.getUniqueId());

// Top 10 richest players (UUIDs)
List<UUID> top = api.getTopPlayers(10);

for (int i = 0; i < top.size(); i++) {
    UUID id = top.get(i);
    String name = Bukkit.getOfflinePlayer(id).getName();
    int bal = api.getPlayerTokens(id);
    // ...
}
```

---

## Events

All events are located in `com.twanluttik.tokens.api.event.*`.

Register listeners normally:

```java
@EventHandler
public void onBalanceChange(TokenBalanceChangeEvent event) {
    if (event.getReason() == TokenBalanceChangeEvent.ChangeReason.API) {
        // Your plugin caused this
    }
    
    if (event.getNewBalance() > 1_000_000) {
        event.getPlayer().sendMessage("You're rich!");
    }
    
    // You can modify the resulting balance (before it is applied)
    // event.setNewBalance(0);
    
    // Cancel the transaction
    // event.setCancelled(true);
}
```

### Available Events

| Event                        | Cancellable | Description                                      |
|-----------------------------|-------------|--------------------------------------------------|
| `TokenBalanceChangeEvent`   | Yes         | Player's personal token balance is changing      |
| `BankCreatedEvent`          | No          | A new bank was created                           |
| `BankDeletedEvent`          | No          | A bank was deleted                               |
| `BankBalanceChangeEvent`    | Yes         | Tokens moved into/out of a bank                  |
| `BankMemberAddedEvent`      | No          | Player was invited to a bank                     |
| `BankMemberRemovedEvent`    | No          | Player was removed from a bank                   |
| `CheckCreatedEvent`         | No          | A physical check was created (tokens deducted)   |
| `CheckRedeemedEvent`        | No          | A check was redeemed                             |

`TokenBalanceChangeEvent.ChangeReason` values: `ADMIN_COMMAND`, `PLAYER_COMMAND`, `CHECK_REDEEM`, `BANK_DEPOSIT`, `BANK_WITHDRAW`, `CHECK_CREATE`, `PLUGIN`, `API`, `UNKNOWN`.

Example bank listener:

```java
@EventHandler
public void onBankBalance(BankBalanceChangeEvent e) {
    if (e.isDeposit()) {
        // money going in
    }
    // e.setNewBalance(...) to modify
    // e.setCancelled(true) to block
}
```

---

## Configuration & Limits

```java
api.getInitialTokens();          // starting balance for new players
api.getMaxBanksPerPlayer();
api.getMaxMembersPerBank();
api.getPluginVersion();
```

You can also query other plugin availability:

```java
boolean hasPapi = api.isIntegrationAvailable("PlaceholderAPI");
```

---

## Async Operations

Database calls are blocking. The following methods are safe to call from async contexts or use the provided async wrappers:

- `addPlayerTokensAsync(...)`
- `removePlayerTokensAsync(...)`

For other heavy operations (large leaderboards, many banks, etc.), run them yourself on an async scheduler and call back to the main thread when presenting results to players.

---

## Threading & Best Practices

1. **Never** perform long database operations on the main thread.
2. Always null-check `TokensAPI.getAPI()`.
3. Prefer the high-level methods (`depositToBank`, `withdrawFromBank`, `createCheck`, `redeemCheck`) over raw `addToBank` + manual token removal — they fire the correct events and respect limits.
4. Respect the config limits returned by the API (`getMaxBanksPerPlayer()` etc.).
5. Use softdepend in your `plugin.yml`.
6. Events are fired on the thread that performed the action. Most API calls happen on the main thread unless you use async variants.

---

## Full Example

```java
public class MyTokensHook implements Listener {

    private TokensAPI api;

    @Override
    public void onEnable() {
        api = TokensAPI.getAPI();
        if (api == null) {
            getLogger().severe("Tokens is required but not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;

        // Reward 25 tokens asynchronously
        api.addPlayerTokensAsync(killer.getUniqueId(), 25, success -> {
            if (success) {
                killer.sendMessage(ChatColor.GREEN + "+25 tokens for the kill!");
            }
        });
    }

    @EventHandler
    public void onTokensChange(TokenBalanceChangeEvent e) {
        if (e.getNewBalance() >= 100_000 && e.getOldBalance() < 100_000) {
            Player p = e.getPlayer();
            if (p != null) {
                p.sendTitle("§6§lRICH!", "You reached 100k tokens!", 10, 60, 20);
            }
        }
    }
}
```

---

## Versioning & Compatibility

- The API is currently **unstable** in the sense that new methods may be added.
- We will try hard not to remove or change signatures of existing methods without a major version bump.
- Check `api.getPluginVersion()` at runtime if you need feature detection.

For questions or feature requests for the API, please reach out on the Spigot resource page or open an issue.

Happy integrating!
