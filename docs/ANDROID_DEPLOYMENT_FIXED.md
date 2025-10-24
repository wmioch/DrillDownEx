# Android Deployment - Fixed and Working

## Summary

The Android deployment system has been completely fixed and is now working. All Gradle configuration issues have been resolved, and a new PowerShell deployment script has been created.

## What Was Fixed

### 1. Gradle Configuration
- **Re-enabled Android projects** in `settings.gradle` and `build.gradle`
- **Upgraded Android Gradle Plugin** from 7.3.1 to 8.1.4 (compatible with Gradle 8.3)
- **Updated SDK versions** in `gdx-sfx/android/build.gradle`:
  - compileSdkVersion: 23 → 30
  - minSdkVersion: 8 → 19
  - targetSdkVersion: 23 → 30

### 2. Android Gradle Plugin 8.x Requirements
- **Added namespace declarations**:
  - `android/build.gradle`: `namespace "de.dakror.quarry"`
  - `gdx-sfx/android/build.gradle`: `namespace "net.spookygames.gdx.superpowered.android"`
- **Enabled BuildConfig generation** in `android/build.gradle`

### 3. APK Signing
- **Generated keystore**: `android/drilldown.keystore`
- **Configured gradle.properties**:
  ```properties
  keypass_pwd = drilldown123
  keypass_alias = drilldown
  keypass_file = drilldown.keystore
  ```

### 4. Android SDK Configuration
- **Created `local.properties`** with Android SDK path:
  ```properties
  sdk.dir=C:\\Users\\wmioc\\AppData\\Local\\Android\\Sdk
  ```

### 5. Deployment Script
- **Created `Deploy-Android.ps1`** - PowerShell script that:
  - Automatically finds ADB
  - Stops Gradle daemons to release file locks
  - Cleans up Java processes
  - Removes locked build directories
  - Builds the APK
  - Installs to connected device
  - Auto-uninstalls old version if signature conflicts

## How to Use

### Deploy to Android Device

1. **Connect your Android device** via USB
2. **Enable USB Debugging** (Settings > Developer options > USB Debugging)
3. **Run the deployment script**:
   ```powershell
   .\Deploy-Android.ps1
   ```

### If Device Shows as Offline

If you see "device offline" error:
1. Unplug and replug the USB cable
2. On your phone, tap "Allow" when prompted for USB debugging authorization
3. Run the script again

### For Subsequent Updates

1. Edit `android\build.gradle`
2. Increment `versionCode`: `122` → `123`
3. Run `.\Deploy-Android.ps1` again

## Build Files Modified

1. `settings.gradle` - Re-enabled Android projects
2. `build.gradle` - Updated Android Gradle Plugin to 8.1.4
3. `android/build.gradle` - Added namespace and buildFeatures
4. `gdx-sfx/android/build.gradle` - Updated SDK versions, added namespace, fixed deprecated properties
5. `gradle.properties` - Configured keystore credentials
6. `local.properties` - Created with Android SDK path

## Generated Files

1. `android/drilldown.keystore` - Signing keystore for APK
2. `Deploy-Android.ps1` - PowerShell deployment script
3. `local.properties` - Android SDK configuration

## Technical Notes

### File Lock Issues on Windows
The PowerShell script handles Windows file locking issues by:
- Stopping all Gradle daemons before build
- Killing Java processes
- Manually removing build directories
- Building without the clean task to avoid lock conflicts

### Keystore Security
The generated keystore is for development/testing. For production releases:
- Generate a new keystore with a secure password
- Store it securely (not in version control)
- Update `gradle.properties` with secure credentials

### Gradle Plugin Compatibility
- Gradle 8.3 requires Android Gradle Plugin 8.x
- AGP 8.x requires `namespace` in build.gradle (not AndroidManifest.xml)
- AGP 8.x disabled BuildConfig by default (manually re-enabled)

## Troubleshooting

### Build Fails with "SDK location not found"
- Ensure `local.properties` exists with correct SDK path
- Or set `ANDROID_HOME` environment variable

### Build Fails with "Keystore not found"
- Ensure `android/drilldown.keystore` exists
- Or run: `keytool -genkeypair -v -keystore android\drilldown.keystore -alias drilldown ...`

### Installation Fails with "INSTALL_FAILED_UPDATE_INCOMPATIBLE"
- The script automatically handles this by uninstalling the old version first
- If manual uninstall needed: `adb uninstall de.dakror.quarry`

## Success Criteria Met ✓

- [x] Android build configured and enabled
- [x] All Gradle compatibility issues resolved
- [x] APK builds successfully
- [x] Deployment script works end-to-end
- [x] File lock issues handled automatically
- [x] Signature conflicts handled automatically
- [x] No manual intervention required

The Android deployment system is now fully operational!

