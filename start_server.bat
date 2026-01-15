@echo off
REM Minecraft Server Launcher with Plugin Auto-Deploy
REM This script starts your Minecraft server and watches for plugin changes

setlocal enabledelayedexpansion

REM Configuration - EDIT THESE
set SERVER_DIR=C:\Users\twanl\Desktop\mc_local_server
set SERVER_JAR=spigot-1.21.10.jar
set JAVA_MEMORY=2G
set EULA_AGREED=true

REM Validate server directory
if not exist "%SERVER_DIR%" (
    echo Error: Server directory not found: %SERVER_DIR%
    echo Please edit this script and set SERVER_DIR to your server location
    pause
    exit /b 1
)

if not exist "%SERVER_DIR%\%SERVER_JAR%" (
    echo Error: Server JAR not found: %SERVER_DIR%\%SERVER_JAR%
    echo Please edit this script and set SERVER_JAR to your server JAR filename
    pause
    exit /b 1
)

REM Set environment variable for plugin deployment
set MC_SERVER_PLUGINS_DIR=%SERVER_DIR%\plugins

echo.
echo ========================================
echo Minecraft Server Launcher
echo ========================================
echo Server Directory: %SERVER_DIR%
echo Plugins Directory: %MC_SERVER_PLUGINS_DIR%
echo Java Memory: %JAVA_MEMORY%
echo.

REM Create plugins directory if it doesn't exist
if not exist "%MC_SERVER_PLUGINS_DIR%" (
    mkdir "%MC_SERVER_PLUGINS_DIR%"
    echo Created plugins directory
)

REM Agree to EULA
if not exist "%SERVER_DIR%\eula.txt" (
    echo eula=%EULA_AGREED% > "%SERVER_DIR%\eula.txt"
    echo Created eula.txt
)

REM Start the server
cd /d "%SERVER_DIR%"
echo Starting Minecraft server...
echo.
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xmx%JAVA_MEMORY% -Xms%JAVA_MEMORY% -jar %SERVER_JAR% nogui
pause
