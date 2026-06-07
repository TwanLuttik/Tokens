# IntelliJ Setup for Minecraft Plugin Development

This guide helps you set up IntelliJ to automatically build and deploy your Tokens plugin to a running Minecraft server.

## Prerequisites

1. **Minecraft Server**: You need a running Spigot/Paper server
2. **Java 21**: Required for this project
3. **IntelliJ IDEA**: Community or Ultimate edition

## Setup Steps

### Step 1: Configure Server Location

Set the `MC_SERVER_PLUGINS_DIR` environment variable to point to your server's plugins directory:

**Windows (PowerShell)**:
```powershell
$env:MC_SERVER_PLUGINS_DIR = "C:\path\to\your\server\plugins"
```

**Windows (Command Prompt)**:
```cmd
set MC_SERVER_PLUGINS_DIR=C:\path\to\your\server\plugins
```

**Or set it permanently in IntelliJ**:
1. Go to `Run` → `Edit Configurations`
2. Select `Build and Deploy`
3. Under `Environment variables`, add:
   ```
   MC_SERVER_PLUGINS_DIR=C:\path\to\your\server\plugins
   ```

### Step 2: Use the Build and Deploy Configuration

1. Open IntelliJ
2. In the top-right, select the `Build and Deploy` run configuration from the dropdown
3. Click the green play button to build and deploy

### Step 3: Automate on Build (Optional)

To automatically deploy whenever you build:

1. Go to `File` → `Settings` → `Build, Execution, Deployment` → `Compiler`
2. Under "On 'Build' project", add a new external tool:
   - Program: `gradlew.bat` (Windows) or `./gradlew` (Mac/Linux)
   - Arguments: `copyToServer`
   - Working directory: `$ProjectFileDir$`

### Step 4: Run Your Server

You have two options:

**Option A: Manual Server Start**
- Start your Minecraft server separately
- Use the `Build and Deploy` configuration to deploy changes

**Option B: Automated with Script**
- Use the provided `start_server.bat` script (Windows) or `start_server.sh` (Mac/Linux)
- This starts the server and watches for plugin changes

## Workflow

1. **Make code changes** in your plugin
2. **Press Ctrl+Shift+F10** (or click the play button) to run `Build and Deploy`
3. The plugin JAR will be built and copied to your server's plugins directory
4. **Reload the plugin** in-game with `/reload confirm` or restart the server

## Gradle Tasks

- `./gradlew build` - Build the plugin JAR
- `./gradlew copyToServer` - Build and copy JAR to server
- `./gradlew clean` - Clean build artifacts
- `./gradlew shadowJar` - Build the fat JAR with dependencies

## Troubleshooting

**JAR not copying?**
- Check that `MC_SERVER_PLUGINS_DIR` environment variable is set correctly
- Verify the plugins directory exists and is writable
- Check IntelliJ console output for error messages

**Server not reloading the plugin?**
- Use `/reload confirm` command in-game
- Or restart the server completely
- Check server logs for plugin load errors

**Build failing?**
- Ensure Java 21 is installed and set as the project SDK
- Run `./gradlew clean build` to rebuild from scratch
- Check that all dependencies are downloaded (may take a moment on first build)
