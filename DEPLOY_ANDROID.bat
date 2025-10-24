@echo off
REM DrillDown Android Deployment Script
REM Builds and deploys the APK to connected Android device

setlocal enabledelayedexpansion

echo.
echo ========================================
echo   DrillDown - Android Deployment
echo ========================================
echo.

REM Try to find ADB
set "ADB_CMD=adb"

REM Check if adb is in PATH
adb version >nul 2>&1
if errorlevel 1 (
    echo ADB not in PATH, searching common locations...
    
    REM Search common locations
    if exist "C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe" (
        set "ADB_CMD=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe"
        echo Found at: !ADB_CMD!
        goto adb_found
    )
    
    if exist "C:\Android\Sdk\platform-tools\adb.exe" (
        set "ADB_CMD=C:\Android\Sdk\platform-tools\adb.exe"
        echo Found at: !ADB_CMD!
        goto adb_found
    )
    
    if exist "C:\Program Files\Android\Sdk\platform-tools\adb.exe" (
        set "ADB_CMD=C:\Program Files\Android\Sdk\platform-tools\adb.exe"
        echo Found at: !ADB_CMD!
        goto adb_found
    )
    
    if exist "C:\Program Files (x86)\Android\Sdk\platform-tools\adb.exe" (
        set "ADB_CMD=C:\Program Files (x86)\Android\Sdk\platform-tools\adb.exe"
        echo Found at: !ADB_CMD!
        goto adb_found
    )
    
    echo.
    echo ERROR: ADB not found!
    echo.
    echo Please:
    echo   1. Run: SETUP_ADB_PATH.bat
    echo   2. Choose option A (Automatic)
    echo   3. CLOSE and REOPEN Cursor completely
    echo   4. Try DEPLOY_ANDROID.bat again
    echo.
    echo Or manually specify ADB path:
    echo   Edit this script and set ADB_CMD to your adb.exe location
    echo.
    exit /b 1
)

:adb_found
echo Checking for connected device...
"!ADB_CMD!" devices > nul 2>&1
if errorlevel 1 (
    echo.
    echo ERROR: Could not run ADB!
    echo.
    exit /b 1
)

REM Check for connected devices
for /f "skip=1 tokens=1" %%A in ('"!ADB_CMD!" devices') do (
    set "device=%%A"
    if not "!device!"=="" (
        if not "!device!"=="List of attached devices and emulators:" (
            goto device_found
        )
    )
)

echo ERROR: No Android device connected!
echo.
echo Please:
echo   1. Connect your phone via USB cable
echo   2. Enable USB Debugging (Settings ^> Developer options ^> USB Debugging)
echo   3. Run this script again
echo.
exit /b 1

:device_found
echo Device found: !device!
echo.

REM Stop any running Gradle daemons to release file locks
echo Stopping Gradle daemons...
cd /d "%~dp0"
call gradlew.bat --stop >nul 2>&1
timeout /t 2 /nobreak >nul

REM Kill any remaining Java processes to ensure clean build
echo Cleaning up processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak >nul

REM Clean build directories manually to avoid file lock issues
echo Removing old build artifacts...
if exist "android\build" (
    rmdir /S /Q "android\build" 2>nul
)
if exist "gdx-sfx\android\build" (
    rmdir /S /Q "gdx-sfx\android\build" 2>nul
)
timeout /t 1 /nobreak >nul

REM Build the APK (without clean task since we manually cleaned)
echo Building Android APK (Release)...
echo.
call gradlew.bat android:assembleFullRelease
echo DEBUG: Gradle call completed
set BUILD_EXIT=%ERRORLEVEL%
echo DEBUG: BUILD_EXIT=%BUILD_EXIT%

if %BUILD_EXIT% NEQ 0 (
    echo.
    echo BUILD FAILED with exit code %BUILD_EXIT%!
    echo.
    echo Troubleshooting:
    echo   - Check gradle.properties for keystore configuration
    echo   - Ensure keystore file exists
    echo   - Run: gradlew --stop (to reset daemon)
    echo.
    exit /b 1
)

echo.
echo ========================================
echo BUILD SUCCESSFUL!
echo ========================================
echo.

REM Deploy the APK
echo Installing APK to device: !device!
echo.

set "APK_PATH=android\build\outputs\apk\full\release\android-full-release.apk"

if not exist "%APK_PATH%" (
    echo.
    echo ERROR: APK not found at %APK_PATH%
    echo.
    exit /b 1
)

REM Install with -r flag to allow reinstalling/updating
echo Installing/updating app on device...
"!ADB_CMD!" install -r "%APK_PATH%" 2>&1 | findstr /C:"Success" > nul

if errorlevel 1 (
    echo.
    echo Installation failed - attempting to uninstall old version first...
    "!ADB_CMD!" uninstall de.dakror.quarry >nul 2>&1
    timeout /t 2 /nobreak >nul
    echo Retrying installation...
    "!ADB_CMD!" install "%APK_PATH%"
    
    if errorlevel 1 (
        echo.
        echo INSTALLATION FAILED!
        echo.
        echo Possible reasons:
        echo   - Not enough storage on phone
        echo   - USB connection issue
        echo   - Device not authorized
        echo.
        exit /b 1
    )
)

echo.
echo ========================================
echo   DEPLOYMENT SUCCESSFUL!
echo ========================================
echo.
echo The app is now installed on your phone.
echo.
echo IMPORTANT - For next update:
echo   1. Edit: android\build.gradle
echo   2. Increment versionCode (android\build.gradle): 122 ^-^> 123
echo   3. Run this script again
echo.
