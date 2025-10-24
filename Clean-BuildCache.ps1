# DrillDown - Build Cache Cleaner
# Forcefully removes build artifacts and clears Gradle cache

Write-Host ""
Write-Host "========================================"
Write-Host "  DrillDown - Build Cache Cleaner"
Write-Host "========================================"
Write-Host ""
Write-Host "WARNING: This script will remove all build artifacts!"
Write-Host ""

# Get the script directory and change to it
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# Ask for confirmation
Write-Host "Continue? (Y/N): " -NoNewline -ForegroundColor Yellow
$confirm = Read-Host

if ($confirm -ne 'Y' -and $confirm -ne 'y') {
    Write-Host ""
    Write-Host "Cancelled." -ForegroundColor Yellow
    exit 0
}

Write-Host ""

# Step 1: Stop all Gradle and Java processes
Write-Host "Step 1: Stopping Gradle and Java processes..."
& .\gradlew.bat --stop 2>&1 | Out-Null
Start-Sleep -Seconds 1

Write-Host "  Killing Java processes..."
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process javaw -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process | Where-Object { $_.ProcessName -like "*gradle*" } | Stop-Process -Force -ErrorAction SilentlyContinue

Start-Sleep -Seconds 2

# Step 2: Remove all build directories
Write-Host "Step 2: Removing build directories..."
$buildDirs = @(
    "android\build",
    "desktop\build",
    "core\build",
    "commons\core\build",
    "commons\annotations\build",
    "gdx-sfx\core\build",
    "gdx-sfx\android\build",
    "gdx-sfx\desktop\build"
)

foreach ($dir in $buildDirs) {
    if (Test-Path $dir) {
        Write-Host "  Removing: $dir"
        $maxAttempts = 3
        $attempt = 0
        $removed = $false
        
        while ($attempt -lt $maxAttempts -and -not $removed) {
            try {
                Remove-Item -Recurse -Force $dir -ErrorAction SilentlyContinue
                Start-Sleep -Milliseconds 500
                
                if (-not (Test-Path $dir)) {
                    $removed = $true
                    Write-Host "    ✓ Removed"
                } else {
                    $attempt++
                    if ($attempt -lt $maxAttempts) {
                        Start-Sleep -Seconds 1
                    }
                }
            } catch {
                $attempt++
                if ($attempt -lt $maxAttempts) {
                    Start-Sleep -Seconds 1
                }
            }
        }
        
        if (-not $removed) {
            Write-Host "    ⚠ Could not fully remove (file lock persists)"
        }
    }
}

Start-Sleep -Seconds 1

# Step 3: Clear Gradle cache
Write-Host "Step 3: Clearing Gradle cache..."
$gradleCache = "$env:USERPROFILE\.gradle\caches"

if (Test-Path $gradleCache) {
    Write-Host "  Removing: $gradleCache"
    try {
        Remove-Item -Recurse -Force $gradleCache -ErrorAction SilentlyContinue
        Start-Sleep -Milliseconds 500
        Write-Host "    ✓ Gradle cache cleared"
    } catch {
        Write-Host "    ⚠ Could not clear Gradle cache (file lock persists)"
    }
}

Write-Host ""
Write-Host "========================================"
Write-Host "  CLEANUP COMPLETE!" -ForegroundColor Green
Write-Host "========================================"
Write-Host ""
Write-Host "If you still encounter build issues:"
Write-Host "  1. Close all file explorer windows"
Write-Host "  2. Disable antivirus scanning of the project"
Write-Host "  3. Restart your computer"
Write-Host ""
Write-Host "Then try building again with: .\Build-Game.ps1"
Write-Host ""


