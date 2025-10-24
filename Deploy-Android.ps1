# DrillDown - Android Deployment Script
# Builds and deploys the APK to connected Android device

Write-Host ""
Write-Host "========================================"
Write-Host "  DrillDown - Android Deployment"
Write-Host "========================================"
Write-Host ""

# Find ADB
$adbCmd = $null
$adbLocations = @(
    "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools\adb.exe",
    "C:\Android\Sdk\platform-tools\adb.exe",
    "C:\Program Files\Android\Sdk\platform-tools\adb.exe",
    "C:\Program Files (x86)\Android\Sdk\platform-tools\adb.exe"
)

# Check if adb is in PATH
try {
    $null = Get-Command adb -ErrorAction Stop
    $adbCmd = "adb"
    Write-Host "ADB found in PATH"
} catch {
    Write-Host "ADB not in PATH, searching common locations..."
    foreach ($location in $adbLocations) {
        if (Test-Path $location) {
            $adbCmd = $location
            Write-Host "Found at: $adbCmd"
            break
        }
    }
}

if (-not $adbCmd) {
    Write-Host ""
    Write-Host "ERROR: ADB not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Android SDK and ensure ADB is available."
    exit 1
}

# Check for connected device
Write-Host "Checking for connected device..."
$devicesOutput = & $adbCmd devices 2>&1
$devices = $devicesOutput | Select-String "^\w+" | Where-Object { $_ -notmatch "List of" -and $_ -notmatch "^$" }

if (-not $devices) {
    Write-Host ""
    Write-Host "ERROR: No Android device connected!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please:"
    Write-Host "  1. Connect your phone via USB cable"
    Write-Host "  2. Enable USB Debugging (Settings > Developer options > USB Debugging)"
    Write-Host "  3. Run this script again"
    exit 1
}

$deviceId = ($devices[0] -split "\s+")[0]
Write-Host "Device found: $deviceId"
Write-Host ""

# Stop Gradle daemons to release file locks
Write-Host "Stopping Gradle daemons..."
& .\gradlew.bat --stop 2>&1 | Out-Null
Start-Sleep -Seconds 2

# Kill any remaining Java processes
Write-Host "Cleaning up processes..."
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Clean build directories manually
Write-Host "Removing old build artifacts..."
if (Test-Path "android\build") {
    Remove-Item -Recurse -Force "android\build" -ErrorAction SilentlyContinue
}
if (Test-Path "gdx-sfx\android\build") {
    Remove-Item -Recurse -Force "gdx-sfx\android\build" -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 1

# Build the APK
Write-Host ""
Write-Host "Building Android APK (Release)..."
Write-Host ""

& .\gradlew.bat android:assembleFullRelease
$buildExit = $LASTEXITCODE

if ($buildExit -ne 0) {
    Write-Host ""
    Write-Host "BUILD FAILED with exit code $buildExit!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Troubleshooting:"
    Write-Host "  - Check gradle.properties for keystore configuration"
    Write-Host "  - Ensure keystore file exists"
    Write-Host "  - Run: gradlew --stop (to reset daemon)"
    exit 1
}

Write-Host ""
Write-Host "========================================"
Write-Host "BUILD SUCCESSFUL!" -ForegroundColor Green
Write-Host "========================================"
Write-Host ""

# Check if APK exists
$apkPath = "android\build\outputs\apk\full\release\android-full-release.apk"
if (-not (Test-Path $apkPath)) {
    Write-Host ""
    Write-Host "ERROR: APK not found at $apkPath" -ForegroundColor Red
    exit 1
}

# Install the APK
Write-Host "Installing APK to device: $deviceId"
Write-Host ""
Write-Host "Installing/updating app on device..."

$installResult = & $adbCmd install -r $apkPath 2>&1
$success = $installResult -match "Success"

if (-not $success) {
    Write-Host ""
    Write-Host "Installation failed - attempting to uninstall old version first..." -ForegroundColor Yellow
    & $adbCmd uninstall de.dakror.quarry 2>&1 | Out-Null
    Start-Sleep -Seconds 2
    
    Write-Host "Retrying installation..."
    $installResult = & $adbCmd install $apkPath 2>&1
    $success = $installResult -match "Success"
    
    if (-not $success) {
        Write-Host ""
        Write-Host "INSTALLATION FAILED!" -ForegroundColor Red
        Write-Host ""
        Write-Host "Possible reasons:"
        Write-Host "  - Not enough storage on phone"
        Write-Host "  - USB connection issue"
        Write-Host "  - Device not authorized"
        Write-Host ""
        Write-Host "Output: $installResult"
        exit 1
    }
}

Write-Host ""
Write-Host "========================================"
Write-Host "  DEPLOYMENT SUCCESSFUL!" -ForegroundColor Green
Write-Host "========================================"
Write-Host ""
Write-Host "The app is now installed on your phone."
Write-Host ""
Write-Host "IMPORTANT - For next update:"
Write-Host "  1. Edit: android\build.gradle"
Write-Host "  2. Increment versionCode: 122 -> 123"
Write-Host "  3. Run this script again"
Write-Host ""

