@echo off
REM Setup ADB Path Helper
REM This script helps you set up ADB in your system PATH

echo.
echo ========================================
echo     ADB Path Configuration Helper
echo ========================================
echo.

REM Try common locations
set "ADB_PATH="

if exist "C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools"
    echo Found ADB at: %ADB_PATH%
    goto found
)

if exist "C:\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=C:\Android\Sdk\platform-tools"
    echo Found ADB at: %ADB_PATH%
    goto found
)

if exist "C:\Program Files\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=C:\Program Files\Android\Sdk\platform-tools"
    echo Found ADB at: %ADB_PATH%
    goto found
)

if exist "C:\Program Files (x86)\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=C:\Program Files (x86)\Android\Sdk\platform-tools"
    echo Found ADB at: %ADB_PATH%
    goto found
)

echo ADB not found in common locations!
echo.
echo Please tell me where your Android SDK is installed:
echo (Typically in one of these locations:)
echo   - C:\Users\YourUsername\AppData\Local\Android\Sdk
echo   - C:\Android\Sdk
echo   - C:\Program Files\Android\Sdk
echo.
set /p ADB_PATH="Enter the full path to platform-tools directory: "

if not exist "%ADB_PATH%\adb.exe" (
    echo.
    echo ERROR: adb.exe not found at: %ADB_PATH%
    echo.
    pause
    exit /b 1
)

:found
echo.
echo ADB found at: %ADB_PATH%
echo.
echo Setting up PATH...
echo.

REM Add to PATH (permanent - requires admin)
echo This requires Administrator privileges.
echo.
echo Option 1: Automatic (requires Admin)
echo   This will permanently add ADB to your PATH
echo.
echo Option 2: Manual
echo   You can manually add the path through Settings
echo.

set /p METHOD="Do you want to add automatically (A) or see manual steps (M)? "

if /i "%METHOD%"=="A" (
    goto auto_setup
) else if /i "%METHOD%"=="M" (
    goto manual_setup
) else (
    echo Invalid choice
    pause
    exit /b 1
)

:auto_setup
echo.
echo Attempting to add to PATH...
echo.

REM Check if running as admin
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ERROR: This operation requires Administrator privileges!
    echo.
    echo Please:
    echo   1. Right-click this script
    echo   2. Select "Run as administrator"
    echo   3. Try again
    echo.
    pause
    exit /b 1
)

REM Add to PATH
setx PATH "%PATH%;%ADB_PATH%"

if errorlevel 1 (
    echo.
    echo ERROR setting PATH!
    echo Please try manual setup instead.
    echo.
    goto manual_setup
)

echo.
echo SUCCESS! ADB has been added to your PATH.
echo.
echo You may need to:
    echo   1. Close and reopen Command Prompt/PowerShell
    echo   2. Close and reopen the terminal in Cursor
    echo   3. Try DEPLOY_ANDROID.bat again
echo.
pause
exit /b 0

:manual_setup
echo.
echo ========================================
echo   Manual Setup Instructions
echo ========================================
echo.
echo ADB location: %ADB_PATH%
echo.
echo To add it to PATH manually:
echo.
echo 1. Right-click "This PC" or "My Computer"
echo 2. Select "Properties"
echo 3. Click "Advanced system settings"
echo 4. Click "Environment Variables" button
echo 5. Under "System variables", click "New"
echo 6. Variable name: PATH
echo 7. Variable value: %ADB_PATH%
echo    (Or append to existing PATH if it already exists)
echo 8. Click OK on all dialogs
echo 9. Close and reopen terminal
echo 10. Run DEPLOY_ANDROID.bat again
echo.
echo Alternative (PowerShell as Admin):
echo   $env:Path += ";%ADB_PATH%"
echo   [Environment]::SetEnvironmentVariable("Path", $env:Path, "Machine")
echo.
pause
exit /b 0

