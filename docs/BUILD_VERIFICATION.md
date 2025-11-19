# Build Verification (Local Sandbox)

This document captures the exact steps used to prove that the sandbox
environment can successfully build the DrillDown project in parity with
our CI workflow (`.github/workflows/ci.yml`).

## 0. Regenerate the texture atlas (matches CI)

Before triggering any Gradle builds, run the exact TexturePacker command
that CI now executes to refresh `android/assets/tex.png` and
`android/assets/tex.atlas` from `Development/Textures/`:

```
./gradlew desktop:run --args="textures" --no-daemon --stacktrace
```

Seeing `BUILD SUCCESSFUL` here guarantees that the atlas artifacts used
in later build steps match what CI produces.

## 1. Install an Android SDK locally

Because CI runners already expose the Android SDK but the sandbox does
not, the SDK was downloaded directly from Google and unpacked into
`android-sdk/` (ignored in git):

```
mkdir -p android-sdk/cmdline-tools
cd android-sdk
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdline-tools.zip
unzip cmdline-tools.zip -d cmdline-tools-temp
mv cmdline-tools-temp/cmdline-tools cmdline-tools/latest
rm -rf cmdline-tools-temp cmdline-tools.zip
cd ..
```

Licenses were accepted and the required components (matching CI) were
installed:

```
export ANDROID_HOME=$PWD/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
  "platforms;android-30" \
  "platforms;android-34" \
  "build-tools;34.0.0" \
  "platform-tools" \
  "ndk;21.4.7075529" \
  "cmake;3.22.1"
```

Finally, `local.properties` was populated with the local SDK path so that
Gradle includes the Android module.

## 2. Desktop distribution build

Command:

```
./gradlew desktop:dist --no-daemon --stacktrace
```

Result: `BUILD SUCCESSFUL` (see terminal chunk `7c0d82`).

## 3. Android full debug build

Command:

```
./gradlew :android:assembleFullDebug --no-daemon --stacktrace
```

Result: `BUILD SUCCESSFUL` (see terminal chunk `511a13`). The generated
APK is located at `android/build/outputs/apk/full/debug/`.

With these steps the sandbox environment matches the working CI
configuration and is now able to compile both desktop and Android
artifacts on demand.
