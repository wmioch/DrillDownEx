@echo off
REM DrillDown Game Launcher
REM This script runs the desktop version of DrillDown

cd /d "%~dp0"

echo.
echo ========================================
echo     DrillDown - Factory Building Game
echo ========================================
echo.
echo Launching game from: %cd%
echo.

REM Use full path to avoid relative path issues
java -jar "%~dp0\desktop\build\libs\desktop-1.0.jar" debug

if errorlevel 1 (
    echo.
    echo ERROR: Failed to start the game.
    echo.
    echo Troubleshooting:
    echo - Ensure Java is installed (java -version)
    echo - Ensure desktop-1.0.jar exists in desktop\build\libs\
    echo - Try running from the project root directory
    echo.
    pause
)
