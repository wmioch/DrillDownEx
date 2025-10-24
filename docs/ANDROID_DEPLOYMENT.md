# 🚀 Android Deployment Guide - DrillDown

## Package Name Information

### Current Package Name
```
de.dakror.quarry
```

**Location in Project:**
- `android/build.gradle` (line 43): `applicationId "de.dakror.quarry"`
- `android/AndroidManifest.xml` (line 3): `package="de.dakror.quarry"`

### Why This Matters
- **Same package name** = App updates (keeps user data)
- **Different package name** = New separate app install

---

## Understanding Android Package Names

### What is a Package Name?
A package name is a **unique identifier** for your app on Android devices. It's defined in your **build configuration**, not found on the device.

### How Updates Work
1. First install: App with package `de.dakror.quarry` installs
2. Same package update: Overwrites the existing app (preserves app data)
3. Different package: Installs as completely new app

### Finding Installed Apps on Your Samsung Device
To see installed apps on your Samsung phone:
1. Go to **Settings** → **Apps**
2. Scroll through the list
3. Look for "Quarry" or the custom app name
4. The package name is shown in app details (Android 6+)

**But you don't need to modify anything on the phone!** The package name comes from your build.

---

## Prerequisites for Android Build

### 1. Android SDK & NDK
Download and install:
- **Android Studio:** https://developer.android.com/studio
- Or just SDK Tools: https://developer.android.com/tools

**Minimum Requirements:**
- Android SDK: API 30+ (currently using API 30)
- NDK: Version 21.4.7075529 (specified in project)

### 2. Set ANDROID_HOME Environment Variable
After installing Android SDK:

**Windows (via Command Prompt):**
```batch
setx ANDROID_HOME "C:\Users\YourUsername\AppData\Local\Android\Sdk"
```

Or set it manually:
1. Right-click **This PC** → **Properties**
2. Click **Advanced system settings**
3. Click **Environment Variables**
4. Click **New** → Variable name: `ANDROID_HOME`
5. Variable value: `C:\Users\YourUsername\AppData\Local\Android\Sdk`

**Verify it's set:**
```batch
echo %ANDROID_HOME%
```

### 3. Create Android Keystore (for signing)
Signing is required to build release APKs.

**Generate a keystore (one-time setup):**
```batch
keytool -genkey -v -keystore my-release-key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

**When prompted:**
- Keystore password: `[Remember this!]`
- Key password: `[Remember this!]`
- Common name (CN): Your name
- Organizational Unit (OU): Your company
- Organization (O): Your company
- City/Locality (L): Your city
- State: Your state
- Country: Your country code (e.g., US)

**Output:** `my-release-key.keystore` file

### 4. Update gradle.properties with Keystore Info
Edit `D:\Projects\DrillDown\gradle.properties`:

```properties
maximum.resulting.code.length = 8000
org.gradle.configureondemand = false
org.gradle.daemon            = true
org.gradle.jvmargs           = -Xms128m -Dkotlin.daemon.jvm.options\="-Xmx2048M" -Xmx2048M -Dmaximum.inlined.code.length\=32 -Dmaximum.resulting.code.length\=8000
android.useAndroidX          = true

keypass_pwd = your_keystore_password
keypass_alias = my-key-alias
keypass_file = path/to/my-release-key.keystore
```

**Example:**
```properties
keypass_pwd = MySecurePassword123
keypass_alias = my-key-alias
keypass_file = D:\Projects\DrillDown\my-release-key.keystore
```

---

## Building the Android APK

### Option 1: Release Build (for distribution/deployment)
```batch
cd D:\Projects\DrillDown
gradlew android:assembleFullRelease
```

**Output:** `android\build\outputs\apk\full\release\android-full-release.apk`

### Option 2: Debug Build (for testing)
```batch
cd D:\Projects\DrillDown
gradlew android:assembleFullDebug
```

**Output:** `android\build\outputs\apk\full\debug\android-full-debug.apk`

---

## Installing on Your Samsung Device

### Method 1: Via USB Cable (Recommended)

**Step 1: Enable Developer Mode**
1. Go to **Settings** → **About phone**
2. Scroll down to **Build number**
3. Tap **Build number** 7 times
4. You should see "Developer mode enabled" message

**Step 2: Enable USB Debugging**
1. Go to **Settings** → **Developer options** (now visible)
2. Enable **USB Debugging**
3. Accept the security prompt

**Step 3: Connect Phone & Install APK**
```batch
cd D:\Projects\DrillDown\android\build\outputs\apk\full\release

REM Install the APK
adb install -r android-full-release.apk
```

The `-r` flag allows reinstalling/updating the existing app.

### Method 2: Via File Transfer

**Step 1:** Copy APK to your phone
1. Connect phone via USB
2. Copy `android-full-release.apk` to your phone's Downloads folder
3. Disconnect phone

**Step 2:** Install on Phone
1. Open **File Manager** on phone
2. Navigate to **Downloads**
3. Tap the APK file
4. Tap **Install**
5. Allow installation from unknown sources if prompted

### Method 3: Via Android Studio
If Android Studio is installed:
1. Open the project in Android Studio
2. Connect phone via USB
3. Click **Run** → Select your device
4. Android Studio builds and deploys automatically

---

## Version Management

### Incrementing Version for Updates

Edit `android/build.gradle`:

```gradle
defaultConfig {
    applicationId "de.dakror.quarry"
    minSdkVersion 19
    targetSdkVersion 30
    versionCode 123          ← Increment this
    versionName "v123"       ← Update this
    multiDexEnabled false
}
```

**Important:** 
- `versionCode` must be **higher** each update (Android requirement)
- `versionName` is just for display (can be any format)
- Without incrementing `versionCode`, the new build won't install as an update

### Update Workflow

1. Make code changes
2. Increment `versionCode` and `versionName` in `android/build.gradle`
3. Build: `gradlew android:assembleFullRelease`
4. Install: `adb install -r android-full-release.apk`
5. App updates on phone, preserves user data

---

## Troubleshooting

### Build Fails with Keystore Error
```
ERROR: storeFile must be set when not in debug mode
```
**Solution:** Update `gradle.properties` with correct keystore credentials

### APK Won't Install
```
Error: INSTALL_FAILED_INVALID_APK
```
**Solutions:**
- Delete old APK on phone: `adb uninstall de.dakror.quarry`
- Rebuild clean: `gradlew clean android:assembleFullRelease`
- Check phone storage has space

### ADB Not Found
```
'adb' is not recognized
```
**Solution:** Add Android SDK to PATH:
```batch
setx PATH "%PATH%;C:\Users\YourUsername\AppData\Local\Android\Sdk\platform-tools"
```

### Phone Not Detected
```
List of attached devices
```
(No devices listed)

**Solutions:**
- Enable USB Debugging on phone
- Try different USB cable or port
- Install Samsung USB drivers
- Restart ADB: `adb kill-server` then `adb devices`

### Version Number Not Updating on Phone
```
Make sure versionCode is higher than the previous build
```
- Edit `android/build.gradle`
- Increment `versionCode` (must be integer, no decimals)
- Rebuild and reinstall

---

## Keeping User Data

### Default Behavior
Since the package name remains `de.dakror.quarry`:
- ✅ App data is preserved on update
- ✅ Game saves are kept
- ✅ Settings are retained
- ✅ User preferences persist

### App Data Location (on phone)
- **Internal storage:** `/data/data/de.dakror.quarry/`
- **External storage:** `/Android/data/de.dakror.quarry/`

**To clear app data manually on phone:**
1. Go to **Settings** → **Apps** → **Quarry**
2. Tap **Storage** → **Clear Data**
3. (Your game saves will be deleted)

---

## Quick Reference Commands

```batch
REM Build release APK
gradlew android:assembleFullRelease

REM Build debug APK
gradlew android:assembleFullDebug

REM Clean build
gradlew clean

REM List connected devices
adb devices

REM Install/update APK
adb install -r path\to\android-full-release.apk

REM Uninstall app from phone
adb uninstall de.dakror.quarry

REM View logs from app
adb logcat | findstr "quarry"

REM Push file to phone
adb push file.txt /sdcard/

REM Pull file from phone
adb pull /sdcard/file.txt
```

---

## Important Notes

- **Package Name (de.dakror.quarry)** - DO NOT CHANGE unless you want a completely separate app
- **Keep versionCode incrementing** - Required for updates to install
- **Keep keystore safe** - You'll need it for all future releases
- **Test on debug build first** - Before release build on actual device
- **Release APK is smaller** - Debug includes extra symbols

---

## Next Steps

1. ✅ Install Android SDK & NDK
2. ✅ Set ANDROID_HOME environment variable
3. ✅ Create Android keystore (one-time)
4. ✅ Update gradle.properties with keystore info
5. ✅ Enable USB Debugging on Samsung phone
6. ✅ Build: `gradlew android:assembleFullRelease`
7. ✅ Install: `adb install -r android-full-release.apk`
8. ✅ Test app on phone
9. ✅ Make updates, increment versionCode, rebuild, reinstall

---

**Package Name:** `de.dakror.quarry` (stays the same for all updates)  
**Current Version:** v122  
**Date:** October 23, 2025

