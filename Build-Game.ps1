# DrillDown - Build Script
# Rebuilds the desktop version of DrillDown

Write-Host ""
Write-Host "========================================"
Write-Host "  DrillDown - Build Script"
Write-Host "========================================"
Write-Host ""

# Get the script directory and change to it
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

Write-Host "Building DrillDown for Desktop..."
Write-Host ""

# Stop Gradle daemons to release file locks
Write-Host "Stopping Gradle daemons..."
& .\gradlew.bat --stop 2>&1 | Out-Null
Start-Sleep -Seconds 2

# Kill any remaining Java processes
Write-Host "Cleaning up Java processes..."
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Kill any gradle processes
Write-Host "Cleaning up Gradle processes..."
Get-Process | Where-Object { $_.ProcessName -like "*gradle*" -or $_.ProcessName -like "*javaw*" } | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 1

# Manually remove old build artifacts to avoid gradle clean issues
Write-Host "Removing old build artifacts..."
$buildDirs = @(
    "desktop\build",
    "core\build",
    "commons\core\build",
    "commons\annotations\build",
    "gdx-sfx\core\build",
    "gdx-sfx\desktop\build"
)

foreach ($dir in $buildDirs) {
    if (Test-Path $dir) {
        Write-Host "  Removing: $dir"
        try {
            Remove-Item -Recurse -Force $dir -ErrorAction SilentlyContinue
            Start-Sleep -Milliseconds 500
        } catch {
            Write-Host "    Warning: Could not fully remove $dir, gradle will attempt cleanup"
        }
    }
}

Start-Sleep -Seconds 2

# Build the desktop version
Write-Host ""
Write-Host "Running: gradlew desktop:clean desktop:dist"
Write-Host ""

# Only clean desktop-related projects, not android (to avoid lock issues)
& .\gradlew.bat desktop:clean core:clean desktop:dist
$buildExit = $LASTEXITCODE

if ($buildExit -ne 0) {
    Write-Host ""
    Write-Host "BUILD FAILED with exit code $buildExit!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Troubleshooting:"
    Write-Host "  - Ensure Java Development Kit (JDK) is installed"
    Write-Host "  - Check build.gradle for configuration errors"
    Write-Host "  - Try running: gradlew --stop (to reset daemon)"
    Write-Host "  - On Windows, file locks may persist - try:"
    Write-Host "      1. Close any file explorers viewing the build directory"
    Write-Host "      2. Disable antivirus scanning of the project directory"
    Write-Host "      3. Restart the machine"
    Write-Host ""
    pause
    exit 1
}

Write-Host ""
Write-Host "========================================"
Write-Host "BUILD SUCCESSFUL!" -ForegroundColor Green
Write-Host "========================================"
Write-Host ""

# Check if output JAR exists
$jarPath = "desktop\build\libs\desktop-1.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host ""
    Write-Host "WARNING: JAR not found at $jarPath" -ForegroundColor Yellow
    Write-Host "Build may have completed but output location differs."
    Write-Host ""
} else {
    Write-Host "Output JAR: $jarPath"
    
    # Get file size
    $file = Get-Item $jarPath
    $sizeMB = [math]::Round($file.Length / 1MB, 1)
    Write-Host "Size: ~$sizeMB MB"
    Write-Host ""
}

Write-Host "To run the game, execute: RUN_GAME.bat or .\RUN_GAME.bat"
Write-Host ""
pause
