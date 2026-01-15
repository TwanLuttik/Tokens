# Live Debugging Setup for Tokens Plugin

This guide explains how to set up IntelliJ IDEA to live debug your Tokens plugin running on a Minecraft server.

## Prerequisites

1. **Minecraft Server**: Paper or Spigot server running on your local machine
2. **Java 21**: Required for this project
3. **IntelliJ IDEA**: Community or Ultimate edition
4. **gradle.properties**: Must have `MC_SERVER_PLUGINS_DIR` configured

## Part 1: Configure Your Minecraft Server for Remote Debugging

### Step 1: Start Server with Debug Port

Edit your server startup script (`start_server.bat` or equivalent) to include debug parameters:

**Windows (start_server.bat)**:
```batch
@echo off
cd /d "C:\Users\twanl\Desktop\mc_local_server"
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xmx2G -Xms2G -jar spigot-1.21.10.jar nogui
pause
```

**Key parameters**:
- `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`
  - `transport=dt_socket`: Use socket transport
  - `server=y`: Server mode (IntelliJ connects to it)
  - `suspend=n`: Don't wait for debugger to attach before starting
  - `address=5005`: Debug port (can be changed if needed)

### Step 2: Start Your Server

Run your server with the debug parameters. You should see output like:
```
Listening for transport dt_socket at address: 5005
```

## Part 2: Configure IntelliJ for Remote Debugging

### Step 1: Create a Remote Debug Configuration

1. Go to **Run** → **Edit Configurations**
2. Click the **+** button and select **Remote JVM Debug**
3. Configure as follows:
   - **Name**: `Minecraft Server Debug`
   - **Host**: `localhost`
   - **Port**: `5005` (must match server's debug port)
   - **Use module classpath**: Select your `Tokens` module
   - Click **OK**

### Step 2: Set Up Build Configuration

1. Go to **Run** → **Edit Configurations** again
2. Create a new **Gradle** configuration:
   - **Name**: `Build and Deploy`
   - **Gradle project**: Select your project root
   - **Tasks**: `copyToServer`
   - Click **OK**

## Part 3: Live Debugging Workflow

### Quick Start

1. **Start your Minecraft server** with debug parameters (see Part 1)
2. **In IntelliJ**, select `Minecraft Server Debug` from the run configuration dropdown
3. **Click the Debug button** (green bug icon) or press `Shift+F9`
4. You should see: `Connected to the target VM, address: 'localhost:5005', transport: 'socket'`

### Setting Breakpoints

1. Open any Java file in your plugin code
2. Click on the line number where you want to break
3. A red dot appears - that's your breakpoint
4. When that code executes, IntelliJ will pause execution

### Example: Debug Player Token Balance

1. Open `@/c:\Users\twanl\Desktop\Tokens\src\main\java\com\twanluttik\tokens\Placeholder.java:43-44`
2. Click on line 44 (the `onPlaceholderRequest` method)
3. Set a breakpoint
4. In-game, use the placeholder `%tokens_player_tokens_balance%`
5. IntelliJ will pause at your breakpoint
6. Inspect variables in the **Variables** panel

## Part 4: Typical Development Workflow

### Scenario: Making Code Changes

1. **Make changes** to your plugin code
2. **Build and Deploy**: 
   - Select `Build and Deploy` configuration
   - Click the green play button or press `Ctrl+F5`
   - This builds the JAR and copies it to your server
3. **Reload in-game**:
   - Type `/reload confirm` in Minecraft chat
   - Or restart the server
4. **Debug**:
   - Switch to `Minecraft Server Debug` configuration
   - Press `Shift+F9` to attach debugger
   - Trigger the code path you want to debug
   - IntelliJ pauses at breakpoints

## Part 5: Useful Debugging Features

### Inspect Variables

When paused at a breakpoint:
- **Variables panel** (left): Shows all local variables and their values
- **Hover over code**: Hover over any variable to see its value
- **Evaluate expression**: Right-click → `Evaluate Expression` to run code

### Conditional Breakpoints

1. Right-click on a breakpoint (red dot)
2. Select **Edit Breakpoint**
3. Add a condition, e.g., `player.getName().equals("YourName")`
4. Breakpoint only triggers when condition is true

### Step Through Code

- **Step Over** (`F10`): Execute current line, move to next
- **Step Into** (`F11`): Enter method calls
- **Step Out** (`Shift+F11`): Exit current method
- **Resume** (`F9`): Continue execution until next breakpoint

### Watch Expressions

1. In the **Variables** panel, right-click a variable
2. Select **Add to Watches**
3. The variable appears in the **Watches** panel
4. You can monitor it across multiple breakpoints

## Part 6: Troubleshooting

### Debugger Won't Connect

**Problem**: "Connection refused" or timeout

**Solutions**:
1. Verify server is running with debug parameters
2. Check that port 5005 is not blocked by firewall
3. Ensure `address=5005` in server startup matches IntelliJ config
4. Try a different port (e.g., 5006) if 5005 is in use

### Breakpoints Not Hit

**Problem**: Code runs but breakpoints don't trigger

**Solutions**:
1. Ensure you've **built and deployed** the latest code
2. Verify the breakpoint is in the actual code being executed
3. Check server logs for plugin load errors
4. Reload the plugin with `/reload confirm` after deploying

### Changes Not Taking Effect

**Problem**: Code changes don't appear in running server

**Solutions**:
1. Always run `Build and Deploy` after code changes
2. Reload the plugin with `/reload confirm`
3. For major changes, restart the server completely
4. Check that `MC_SERVER_PLUGINS_DIR` is correctly configured

## Part 7: Advanced: Debugging Database Calls

### Example: Debug Token Retrieval

1. Open `@/c:\Users\twanl\Desktop\Tokens\src\main\java\com\twanluttik\tokens\Database.java:35-37`
2. Set a breakpoint on `getConnection()`
3. In-game, use a command that retrieves tokens
4. IntelliJ pauses and you can inspect:
   - `configManager` values
   - Connection properties
   - SQL query parameters

### Example: Debug Placeholder Resolution

1. Open `@/c:\Users\twanl\Desktop\Tokens\src\main\java\com\twanluttik\tokens\Placeholder.java:43-44`
2. Set a breakpoint at the start of `onPlaceholderRequest`
3. Use the placeholder in-game
4. Inspect:
   - `player` object and its properties
   - `params` string value
   - Database query results

## Part 8: Quick Reference

| Action | Shortcut |
|--------|----------|
| Build and Deploy | `Ctrl+F5` |
| Debug | `Shift+F9` |
| Step Over | `F10` |
| Step Into | `F11` |
| Step Out | `Shift+F11` |
| Resume | `F9` |
| Toggle Breakpoint | `Ctrl+F8` |
| Evaluate Expression | `Alt+F9` |

## Part 9: Environment Variables

Make sure your `gradle.properties` has:

```properties
org.gradle.java.home=C:/Program Files/Zulu/zulu-21
MC_SERVER_PLUGINS_DIR=C:/Users/twanl/Desktop/mc_local_server/plugins
```

If not set, the `copyToServer` task won't know where to deploy the JAR.

## Conclusion

You now have a complete live debugging setup! You can:
- ✅ Build and deploy changes instantly
- ✅ Set breakpoints and inspect code execution
- ✅ Watch variables in real-time
- ✅ Step through code line-by-line
- ✅ Debug database queries and placeholder resolution

Happy debugging!
