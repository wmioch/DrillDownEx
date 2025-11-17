# CI Workflow Improvements

This document describes the improvements made to the GitHub Actions CI workflow.

## Changes Made

### 1. Simplified Android SDK Manager Path Detection

**Before:** Complex script to locate sdkmanager with fallback logic
```bash
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-/usr/local/lib/android/sdk}"
if [ -x "${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager" ]; then
    SDKMANAGER="${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager"
else
    SDKMANAGER="$(find "${ANDROID_SDK_ROOT}/cmdline-tools" -maxdepth 1 -mindepth 1 -type d -print | sort | tail -n 1)/bin/sdkmanager"
fi
```

**After:** Direct use of `ANDROID_HOME` environment variable
```bash
${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager
```

**Reason:** GitHub Actions runners pre-configure `ANDROID_HOME`, making the complex logic unnecessary and more reliable.

### 2. Added Gradle Wrapper Validation

A new step ensures the Gradle wrapper is executable and displays its version:
```yaml
- name: Validate Gradle wrapper
  run: |
    chmod +x gradlew
    ./gradlew --version
```

**Reason:** Ensures the build environment is properly configured before attempting builds.

### 3. Enhanced Android SDK Component Installation

**Added components:**
- `platforms;android-30` - Required by android/build.gradle (compileSdkVersion 30)
- `platforms;android-34` - Latest platform for compatibility
- `build-tools;34.0.0` - Latest build tools
- `platform-tools` - Essential Android platform tools
- `ndk;21.4.7075529` - Exact NDK version required by build.gradle
- `cmake;3.22.1` - Required for native builds (lz4-jni)

**Reason:** The Android module uses native code (CMake) and requires specific NDK version. Missing these would cause build failures.

### 4. Added SDK Installation Verification

A new verification step checks that the SDK components were installed correctly:
```yaml
- name: Verify Android SDK installation
  run: |
    echo "Installed Android platforms:"
    ls -la ${ANDROID_HOME}/platforms/ || echo "No platforms directory"
    echo "Installed build-tools:"
    ls -la ${ANDROID_HOME}/build-tools/ || echo "No build-tools directory"
```

**Reason:** Provides visibility into what's installed, making troubleshooting easier.

### 5. Added Build Reliability Flags

Both build steps now use:
- `--no-daemon` - Prevents Gradle daemon issues in CI
- `--stacktrace` - Provides detailed error information on failure

```yaml
- name: Build desktop distribution
  run: ./gradlew desktop:dist --no-daemon --stacktrace

- name: Assemble Android debug build
  run: ./gradlew android:assembleDebug --no-daemon --stacktrace
```

**Reason:** CI environments benefit from single-use Gradle processes, and detailed stack traces help diagnose build failures.

### 6. Added Debug Output

The SDK installation step now displays environment information:
```bash
echo "ANDROID_HOME: ${ANDROID_HOME}"
echo "ANDROID_SDK_ROOT: ${ANDROID_SDK_ROOT:-not set}"
ls -la ${ANDROID_HOME}/cmdline-tools/ || true
```

**Reason:** Helps troubleshoot environment-specific issues.

## Key Dependencies

The CI workflow relies on:

1. **Submodules:** The `commons` submodule from https://github.com/Dakror/gdx-commons.git must be initialized
   - Contains core utilities and the lz4-jni native library
   - Already handled by `submodules: recursive` in checkout step

2. **Java 21:** Required by Gradle 8.3 and the project

3. **Android SDK Components:**
   - Platform API 30 (compileSdkVersion)
   - Build Tools 34.0.0
   - NDK 21.4.7075529 (for native builds)
   - CMake 3.22.1 (for lz4-jni compilation)

4. **Gradle 8.3:** Specified in gradle-wrapper.properties

## Testing in Local Environment

**Note:** Local testing in sandboxed environments may fail due to network restrictions, as documented in README.md:

> When running the Gradle wrapper inside this sandboxed environment we cannot download the Gradle 8.3 distribution because the proxy blocks outbound HTTPS requests.

The CI workflow is designed to run on GitHub Actions runners, which have proper internet access.

## Expected Workflow Execution

1. ✓ Checkout repository with submodules
2. ✓ Set up JDK 21
3. ✓ Set up Gradle
4. ✓ Validate Gradle wrapper
5. ✓ Install Android SDK components (platforms, build-tools, NDK, CMake)
6. ✓ Verify installations
7. ✓ Build desktop distribution (`./gradlew desktop:dist`)
8. ✓ Assemble Android debug build (`./gradlew android:assembleDebug`)
9. ✓ Upload Android APK as artifact

## Troubleshooting

If the CI fails:

1. Check the "Install Android SDK components" step for component installation errors
2. Check the "Verify Android SDK installation" step to confirm components were installed
3. Check build steps for `--stacktrace` output showing detailed error information
4. Ensure submodules are properly initialized (should be automatic)

## Related Files

- `.github/workflows/ci.yml` - The CI workflow configuration
- `gradle/wrapper/gradle-wrapper.properties` - Gradle version specification
- `android/build.gradle` - Android module configuration (compileSdk, NDK version)
- `.gitmodules` - Submodule configuration
- `settings.gradle` - Conditional Android module inclusion
