#!/bin/bash

# Minecraft Server Launcher with Plugin Auto-Deploy
# This script starts your Minecraft server and watches for plugin changes

# Configuration - EDIT THESE
SERVER_DIR="/Users/twanluttik/Desktop/mc_server"
SERVER_JAR="spigot-26.1.2.jar"
JAVA_MEMORY="2G"
EULA_AGREED="true"

# Validate server directory
if [ ! -d "$SERVER_DIR" ]; then
    echo "Error: Server directory not found: $SERVER_DIR"
    echo "Please edit this script and set SERVER_DIR to your server location"
    exit 1
fi

if [ ! -f "$SERVER_DIR/$SERVER_JAR" ]; then
    echo "Error: Server JAR not found: $SERVER_DIR/$SERVER_JAR"
    echo "Please edit this script and set SERVER_JAR to your server JAR filename"
    exit 1
fi

# Set environment variable for plugin deployment
export MC_SERVER_PLUGINS_DIR="$SERVER_DIR/plugins"

echo ""
echo "========================================"
echo "Minecraft Server Launcher"
echo "========================================"
echo "Server Directory: $SERVER_DIR"
echo "Plugins Directory: $MC_SERVER_PLUGINS_DIR"
echo "Java Memory: $JAVA_MEMORY"
echo ""

# Create plugins directory if it doesn't exist
if [ ! -d "$MC_SERVER_PLUGINS_DIR" ]; then
    mkdir -p "$MC_SERVER_PLUGINS_DIR"
    echo "Created plugins directory"
fi

# Agree to EULA
if [ ! -f "$SERVER_DIR/eula.txt" ]; then
    echo "eula=$EULA_AGREED" > "$SERVER_DIR/eula.txt"
    echo "Created eula.txt"
fi

# Start the server
cd "$SERVER_DIR"
echo "Starting Minecraft server..."
echo ""
java \
  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
  -Xmx"$JAVA_MEMORY" -Xms"$JAVA_MEMORY" \
  -jar "$SERVER_JAR" nogui
