# Tokens Plugin

A Minecraft plugin that implements a token-based economy system with bank functionality. This plugin allows players to manage their tokens and create/join banks for collaborative token management.

## Features

- Token economy system
- Bank system with multiple members
- Admin commands for token management
- SQL database integration (PostgreSQL)
- Automatic player registration
- Update checker
- Configurable settings and messages

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

- Spigot API 1.21.4
- PostgreSQL JDBC Driver 42.7.2
- Gson 2.10.1

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` directory
3. Start your server to generate the default config.yml
4. Edit the config.yml file with your database settings
5. Restart your server

## API Usage

The plugin provides a public API (`TokensAPI.java`) that other plugins can use to interact with the token system. Here are some key methods:

```java
// Get the API instance
TokensAPI api = TokensAPI.getInstance();

// Create a new bank
int bankId = api.createBank("BankName", playerUuid);

// Get bank balance
int balance = api.getBankBalance(bankId);

// Add/remove tokens from bank
api.addToBank(bankId, amount);
api.removeFromBank(bankId, amount);

// Get player tokens
int playerTokens = api.getPlayerTokens(playerUuid);

// Add/remove/set player tokens
api.addPlayerTokens(playerUuid, amount);
api.removePlayerTokens(playerUuid, amount);
api.setPlayerTokens(playerUuid, amount);
```

## Version

Current version: 2.0.0-SHAPSHOT-1

## Support

For support, please visit the plugin's Spigot page: https://www.spigotmc.org/resources/tokens-economy-1-8-x-1-13-x-bank-system-sql-api.53944/ 