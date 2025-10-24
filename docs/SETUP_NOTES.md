# DrillDown - Setup & Build Notes

## Project Overview
DrillDown is an open-source factory building game originally released in 2019 on Steam and Google Play. This is a Java-based game using LibGDX framework.

**GitHub Repository:** https://github.com/Dakror/DrillDown

## Environment Setup

### Prerequisites
- **Java:** OpenJDK 21.0.7 LTS (or Java >= 11)
- **Gradle:** 8.3 (automatically downloaded via gradlew)
- **System:** Windows 10/11

### Modifications Made for Desktop Build

#### 1. Updated Gradle Version
- **File:** `gradle/wrapper/gradle-wrapper.properties`
- **Change:** Updated from Gradle 7.4 to Gradle 8.3 for Java 21 compatibility
- **Reason:** Gradle 7.4 doesn't support Java 21 bytecode

#### 2. Excluded Android Build (Desktop-Only)
- **File:** `settings.gradle`
- **Change:** Removed `:android` and `gdx-sfx:android` from includes
- **File:** `build.gradle`
- **Change:** Commented out Android project configuration
- **File:** `desktop/build.gradle`
- **Change:** Updated asset source path from `project(":android").file("assets")` to `"../android/assets"`
- **Reason:** Android build requires Android SDK, NDK, and code signing setup

#### 3. Fixed Legacy Gradle Syntax
- **Files:** 
  - `gdx-sfx/core/build.gradle`
  - `gdx-sfx/desktop/build.gradle`
- **Changes:**
  - `classifier()` → `archiveClassifier =`
  - `classifier =` → `archiveClassifier =`
  - `baseName =` → `archiveBaseName =`
- **Reason:** Gradle 8.3 requires modern syntax

#### 4. Fixed gradle.properties
- **File:** `gradle.properties`
- **Change:** Added placeholder values for keystore configuration
  - `keypass_pwd = placeholder`
  - `keypass_alias = placeholder`
  - `keypass_file = placeholder.keystore`
- **Reason:** Android configuration requires these values to evaluate

## Building the Game

### Desktop Build (Recommended)
```bash
cd D:\Projects\DrillDown
gradlew.bat clean desktop:dist
```

**Output:** `desktop/build/libs/desktop-1.0.jar` (19.3 MB)

### Running the Game
```bash
cd D:\Projects\DrillDown\android\assets
java -jar D:\Projects\DrillDown\desktop\build\libs\desktop-1.0.jar
```

The game window should launch with the factory building game interface.

## Project Structure

```
DrillDown/
├── android/              # Android-specific code (not built in current config)
├── core/                 # Core game logic (shared between platforms)
├── desktop/              # Desktop launcher and build configuration
├── commons/              # Shared utility library (submodule)
│   ├── core/            # Common core utilities
│   └── annotations/     # Annotation processor
├── gdx-sfx/             # Sound effects library (submodule)
│   ├── core/
│   └── desktop/
├── Development/         # Development assets and textures
├── distribute/          # Distribution scripts
└── gradle/              # Gradle wrapper files
```

## Git Submodules

The project uses two git submodules that are automatically initialized:
- **commons:** `https://github.com/Dakror/gdx-commons.git`
- **gdx-sfx:** `https://github.com/Dakror/gdx-sfx.git`

If submodules aren't initialized, run:
```bash
git submodule update --init --recursive
```

## Original Build Instructions

For **Android** builds and creating signed APKs (requires Android SDK/NDK):
1. Create a Java keystore for code signing
2. Enter credentials into `gradle.properties`
3. Run: `gradlew android:assembleFullRelease`

For **PC distributions** (original command):
```bash
gradlew desktop:dist
```

## Game Assets

Game assets are located in `android/assets/` and include:
- Game textures and sprites
- Configuration files
- Resource data

These are included in the desktop JAR as resources.

## Development Notes

- **Source Format:** Java 8 compatible
- **Main Package:** `de.dakror.quarry`
- **Desktop Launcher:** `de.dakror.quarry.desktop.DesktopLauncher`
- **Annotation Processor:** Uses custom LML tag processor for UI generation

## Troubleshooting

### Build Issues
- **Gradle daemon errors:** Run `gradlew.bat --stop` to stop all daemons, then rebuild
- **Out of memory:** Increase Gradle heap in `gradle.properties`:
  ```
  org.gradle.jvmargs = -Xms512m -Xmx4096m
  ```

### Runtime Issues
- **Assets not found:** Ensure you run the JAR from `android/assets` directory
- **Missing libraries:** Run `gradlew clean` to reset build cache

## Next Steps for Customization

To add features to the game:
1. Modify source files in `core/src/` for shared logic
2. Modify `desktop/src/` for desktop-specific changes
3. Add game assets to `android/assets/`
4. Rebuild with `gradlew.bat desktop:dist`

## License

This project is licensed under the Apache License 2.0. See `LICENSE` file for details.

---

**Setup Date:** October 23, 2025
**Build Status:** ✅ Desktop version successfully compiles and runs

