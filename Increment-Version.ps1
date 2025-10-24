# DrillDown - Version Increment Script
# Increments the Android app version code and version name AND the Desktop version

Write-Host ""
Write-Host "========================================"
Write-Host "  DrillDown - Version Increment"
Write-Host "========================================"
Write-Host ""

# Get the script directory and change to it
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# Path to the build files
$androidBuildGradlePath = "android\build.gradle"
$desktopLauncherPath = "desktop\src\de\dakror\quarry\desktop\DesktopLauncher.java"

# Check if files exist
if (-not (Test-Path $androidBuildGradlePath)) {
    Write-Host "ERROR: $androidBuildGradlePath not found!" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $desktopLauncherPath)) {
    Write-Host "ERROR: $desktopLauncherPath not found!" -ForegroundColor Red
    exit 1
}

Write-Host "Reading: $androidBuildGradlePath"
Write-Host "Reading: $desktopLauncherPath"
Write-Host ""

# Read the Android build.gradle file
$androidContent = Get-Content $androidBuildGradlePath -Raw

# Extract current version code using regex
if ($androidContent -match 'versionCode\s+(\d+)') {
    $currentVersionCode = [int]$matches[1]
    $newVersionCode = $currentVersionCode + 1
    Write-Host "Android versionCode: $currentVersionCode → $newVersionCode" -ForegroundColor Cyan
} else {
    Write-Host "ERROR: Could not find versionCode in $androidBuildGradlePath" -ForegroundColor Red
    exit 1
}

# Extract current version name using regex
if ($androidContent -match 'versionName\s+"v?(\d+)"') {
    $currentVersionNum = [int]$matches[1]
    $newVersionNum = $currentVersionNum + 1
    $currentVersionName = "v$currentVersionNum"
    $newVersionName = "v$newVersionNum"
    Write-Host "Android versionName: $currentVersionName → $newVersionName" -ForegroundColor Cyan
} else {
    Write-Host "ERROR: Could not find versionName in $androidBuildGradlePath" -ForegroundColor Red
    exit 1
}

# Read the Desktop launcher file
$desktopContent = Get-Content $desktopLauncherPath -Raw

# Extract current Desktop version code
if ($desktopContent -match 'int versionCode = (\d+);') {
    $desktopVersionCode = [int]$matches[1]
    Write-Host "Desktop versionCode: $desktopVersionCode → $newVersionCode" -ForegroundColor Cyan
} else {
    Write-Host "ERROR: Could not find versionCode in $desktopLauncherPath" -ForegroundColor Red
    exit 1
}

# Extract current Desktop version name
if ($desktopContent -match 'String version = "v?(\d+)";') {
    $desktopVersionNum = [int]$matches[1]
    Write-Host "Desktop version:     v$desktopVersionNum → $newVersionName" -ForegroundColor Cyan
} else {
    Write-Host "ERROR: Could not find version string in $desktopLauncherPath" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Ask for confirmation
Write-Host "Continue with version increment? (Y/N): " -NoNewline -ForegroundColor Yellow
$confirm = Read-Host

if ($confirm -ne 'Y' -and $confirm -ne 'y') {
    Write-Host ""
    Write-Host "Cancelled." -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Updating Android $androidBuildGradlePath..."

# Update Android file - Replace versionCode
$androidContent = $androidContent -replace "versionCode\s+$currentVersionCode", "versionCode $newVersionCode"

# Update Android file - Replace versionName
$androidContent = $androidContent -replace "versionName\s+""v?$currentVersionNum""", "versionName ""$newVersionName"""

# Write back to Android file
Set-Content -Path $androidBuildGradlePath -Value $androidContent -NoNewline

Write-Host "Updating Desktop $desktopLauncherPath..."

# Update Desktop file - Replace versionCode
$desktopContent = $desktopContent -replace "int versionCode = $desktopVersionCode;", "int versionCode = $newVersionCode;"

# Update Desktop file - Replace version string - need to handle regex special chars and use literal string
$oldVersionPattern = 'String version = "v' + $desktopVersionNum + '";'
$newVersionString = 'String version = "' + $newVersionName + '";'
$desktopContent = $desktopContent -replace [regex]::Escape($oldVersionPattern), $newVersionString

# Write back to Desktop file
Set-Content -Path $desktopLauncherPath -Value $desktopContent -NoNewline

Write-Host ""
Write-Host "========================================"
Write-Host "  VERSION UPDATED SUCCESSFULLY!" -ForegroundColor Green
Write-Host "========================================"
Write-Host ""
Write-Host "Changes made:"
Write-Host "  Android versionCode: $currentVersionCode → $newVersionCode"
Write-Host "  Android versionName: $currentVersionName → $newVersionName"
Write-Host "  Desktop versionCode: $desktopVersionCode → $newVersionCode"
Write-Host "  Desktop version:     v$desktopVersionNum → $newVersionName"
Write-Host ""
Write-Host "Next step: Deploy with Deploy-Android.ps1"
Write-Host ""


