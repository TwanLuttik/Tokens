# Tokens Plugin

A Minecraft plugin that implements a token-based economy system with bank functionality. This plugin allows players to manage their tokens and create/join banks for collaborative token management.

## Features

- Token economy system
- Bank system with multiple members
- Check system for token transfers
- Admin commands for token management
- SQL database integration (PostgreSQL)
- Automatic player registration
- Update checker
- Configurable settings and messages

## Supported Minecraft Versions

- 1.19.4
- 1.20.x
- 1.21.x
- 26.x builds (certain Paper/Purpur/etc. forks that use this versioning)

The plugin uses only the public Bukkit/Spigot API (no NMS), so it should work across the supported range when compiled against the 1.19.4 API.

## Configuration

The plugin uses a `config.yml` file for configuration. Here are the available settings:

### Database Settings
```yaml
database:
  host: "0.0.0.0"  # Database host
  port: 5432       # Database port
  database: "mydatabase"  # Database name
  username: "myuser"     # Database username
  password: "mypassword" # Database password
  ssl: false       # Enable/disable SSL
```

### General Settings
```yaml
settings:
  initial-tokens: 0        # Initial token balance for new players
  auto-create-bank: false  # Automatically create a bank for new players
  max-banks-per-player: 3  # Maximum number of banks a player can own
  max-members-per-bank: 10 # Maximum number of members per bank
  check-for-updates: true  # Enable/disable update checker
  update-checker-enabled: true  # Enable/disable update checker notifications
```

### Messages
```yaml
messages:
  prefix: "&6[Tokens] &r"  # Chat message prefix
  # Various message templates with color codes and placeholders
```

## Commands

### Token Commands

- `/tokens balance [player]` - Check your token balance (or another player's if you have admin permissions)
- `/tokens give <player> <amount>` - Give tokens to a player (admin only)
- `/tokens take <player> <amount>` - Take tokens from a player (admin only)
- `/tokens set <player> <amount>` - Set a player's token balance (admin only)

### Bank Commands

- `/tokens bank create <name>` - Create a new bank
- `/tokens bank list` - List all banks you have access to
- `/tokens bank deposit <bank_id> <amount>` - Deposit tokens into a bank
- `/tokens bank withdraw <bank_id> <amount>` - Withdraw tokens from a bank
- `/tokens bank invite <bank_id> <player>` - Invite a player to your bank
- `/tokens bank remove <bank_id> <player>` - Remove a player from your bank
- `/tokens bank delete <bank_id>` - Delete a bank (bank owner only)

### Check Commands

- `/tokens check create <amount>` - Create a check for the specified amount of tokens
- `/tokens check redeem` - Redeem the check you're holding in your hand

## Permissions

- `tokens.admin` - Grants access to admin token commands (default: op)

## Database Setup

The plugin uses PostgreSQL for data storage. You'll need to configure the following in the `Database.java` file:

```java
private static final String JDBC_URL = "jdbc:postgresql://0.0.0.0:5432/mydatabase";
private static final String USERNAME = "myuser";
private static final String PASSWORD = "mypassword";
```

## Dependencies

- Spigot/Paper 1.19.4 or newer (compile target 1.19.4)
- Java 17+
- PostgreSQL JDBC Driver 42.7.2
- Gson 2.10.1

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` directory
3. Start your server to generate the default config.yml
4. Edit the config.yml file with your database settings
5. Restart your server

## PlaceholderAPI / PowerBoard Placeholders

The plugin registers the following placeholders (usable in PowerBoard, FeatherBoard, other scoreboards, and any PlaceholderAPI-compatible plugin):

### Player Placeholders
| Placeholder                                      | Description                          | Example Output    |
|--------------------------------------------------|--------------------------------------|-------------------|
| `%tokens_balance%` / `%tokens_player_balance%`   | Player's raw token balance           | `1234567`         |
| `%tokens_balance_formatted%` / `%tokens_player_balance_formatted%` | Shortened balance with suffix (k/m/b/t) | `1.2m`     |
| `%tokens_banks_count%`                           | Number of banks the player has access to | `3`            |
| `%tokens_rank%`                                  | Player's rank on the leaderboard (1 = richest) | `5`     |
| `%tokens_rank_formatted%`                        | Formatted rank                       | `#5`              |

### Bank Placeholders
Replace `<id>` with the bank ID (visible in `/tokens bank list` or the GUI).

| Placeholder                              | Description                     |
|------------------------------------------|---------------------------------|
| `%tokens_bank_<id>_name%`                | Name of the bank                |
| `%tokens_bank_<id>_balance%`             | Raw balance of the bank         |
| `%tokens_bank_<id>_balance_formatted%`   | Formatted bank balance          |
| `%tokens_bank_<id>_owner%`               | Name of the bank owner          |
| `%tokens_bank_<id>_member_count%`        | Number of members in the bank   |
| `%tokens_bank_<id>_members%`             | Alias for member count          |

### Top Leaderboard Placeholders
Excellent for PowerBoard top lists.

| Placeholder                                   | Description                          |
|-----------------------------------------------|--------------------------------------|
| `%tokens_top_name%` / `%tokens_top_name_<n>%` | Name of the player at rank N (1-10+) |
| `%tokens_top_balance%` / `%tokens_top_balance_<n>%` | Raw balance at rank N         |
| `%tokens_top_balance_formatted%` / `%tokens_top_balance_formatted_<n>%` | Formatted balance at rank N |

Example usage on a PowerBoard scoreboard:
```
&6Your Balance: &e%tokens_balance_formatted%
&6Your Rank: &e%tokens_rank_formatted%
&6&lTop Players
&f#1 &a%tokens_top_name_1% &7- &e%tokens_top_balance_formatted_1%
&f#2 &a%tokens_top_name_2% &7- &e%tokens_top_balance_formatted_2%
```

## API for Developers

The plugin provides a rich public API so other plugins can integrate with the token economy, banks, and checks.

**Full documentation:** see [API.md](API.md)

### Quick Start

```java
import com.twanluttik.tokens.TokensAPI;

TokensAPI api = TokensAPI.getAPI();
if (api == null) {
    // Tokens is not installed or not loaded yet
    return;
}

int balance = api.getPlayerTokens(player.getUniqueId());
api.addPlayerTokens(player.getUniqueId(), 250);

int bankId = api.createBank("MyFactionBank", player.getUniqueId());
api.depositToBank(player.getUniqueId(), bankId, 1000);
```

### Key Capabilities

- Player token management (get/add/remove/set + async variants)
- Full bank management + membership + permission helpers
- Physical token **checks** (create/redeem)
- Leaderboard access (`getTopPlayers`, `getPlayerRank`)
- **Events** for almost every mutation (`TokenBalanceChangeEvent`, `BankBalanceChangeEvent`, `CheckRedeemedEvent`, etc.)
- Configuration limits (`getMaxBanksPerPlayer()`, etc.)
- Proper Bukkit service registration (`TokensAPI.getAPI()`)

### Events Example

```java
@EventHandler
public void onTokenChange(TokenBalanceChangeEvent e) {
    if (e.getNewBalance() > 100000) {
        // ...
    }
}
```

### Depending on Tokens (Gradle)

```gradle
dependencies {
    compileOnly 'com.twanluttik:Tokens:3.0.0-PRE-v1' // adjust version
}
```

In your `plugin.yml`:
```yaml
softdepend: [Tokens]
```

See [API.md](API.md) for the complete reference, async usage, best practices, and all available events.

## Version

Current version: 2.0.0-SHAPSHOT-1

## Support

For support, please visit the plugin's Spigot page: https://www.spigotmc.org/resources/tokens-economy-1-8-x-1-13-x-bank-system-sql-api.53944/ 