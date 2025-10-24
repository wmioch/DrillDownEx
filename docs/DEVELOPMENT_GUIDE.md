# DrillDown Development Guide

## Quick Start

### Building from Source
```bash
cd D:\Projects\DrillDown
gradlew.bat clean desktop:dist
```

### Running the Game
```bash
cd D:\Projects\DrillDown\android\assets
java -jar ..\desktop\build\libs\desktop-1.0.jar
```

## Project Architecture

### Core Modules

#### `core/` - Game Logic
- **Location:** `D:\Projects\DrillDown\core\src\de\dakror\quarry`
- **Purpose:** Platform-independent game logic
- **Key Classes:**
  - Main game engine and entities
  - Game rules and mechanics
  - Common utilities

#### `desktop/` - Desktop Entry Point
- **Location:** `D:\Projects\DrillDown\desktop\src\de\dakror\quarry\desktop`
- **Main Class:** `DesktopLauncher.java`
- **Purpose:** Desktop-specific configuration and rendering

#### `commons/` - Shared Utilities (Submodule)
- **Purpose:** Reusable components for LibGDX projects
- **Features:**
  - Debug utilities (TreeDebugger, Delta)
  - Audio management (SoundManager, AmbientSound)
  - Scene and UI management
  - Math utilities (AStar, ConcaveHull, etc.)
  - I/O utilities (Pack, NBT, etc.)

#### `gdx-sfx/` - Sound Effects Library (Submodule)
- **Purpose:** Enhanced sound effects management for LibGDX
- **Usage:** Audio playback and management

### Game Assets
- **Location:** `D:\Projects\DrillDown\android\assets\`
- **Contents:**
  - Textures and sprites
  - Game configuration files
  - Resource data
  - UI layouts

## Adding Features

### 1. Simple Feature (UI/Gameplay Modification)

**Example: Add a new game mode**

1. **Create a new class in `core/src`:**
   ```java
   package de.dakror.quarry.game;
   
   public class NewGameMode {
       // Your implementation
   }
   ```

2. **Integrate with main game logic:**
   - Modify `DesktopLauncher.java` or game state manager
   - Add initialization and update logic

3. **Rebuild:**
   ```bash
   gradlew.bat desktop:dist
   ```

### 2. Adding Graphics/Animations

1. **Add textures to `android/assets/`:**
   - Create subdirectories for organization
   - Recommended: PNG format for transparency

2. **Load in game code:**
   ```java
   Texture myTexture = new Texture("assets/myimage.png");
   ```

3. **Rebuild to include assets:**
   ```bash
   gradlew.bat desktop:dist
   ```

### 3. Adding Audio

1. **Add audio files to `android/assets/`:**
   - Supported formats: WAV, MP3, OGG
   - Recommended: OGG for file size

2. **Use SoundManager (from commons):**
   ```java
   SoundManager soundManager = new SoundManager();
   soundManager.play("assets/sound.wav");
   ```

## File Structure Tips

```
D:\Projects\DrillDown\
├── core\src\de\dakror\quarry\
│   ├── game\              # Game logic
│   ├── ui\                # User interface
│   ├── entity\            # Game entities
│   ├── system\            # Game systems
│   └── util\              # Utilities
├── desktop\src\de\dakror\quarry\
│   └── desktop\
│       └── DesktopLauncher.java
├── android\assets\
│   ├── textures\          # Sprite images
│   ├── audio\             # Sound files
│   ├── config\            # Configuration files
│   └── ui\                # UI layouts
└── core\gen\              # Generated code (annotation processor)
```

## Important Classes

### LibGDX Foundation
- `com.badlogic.gdx.Game` - Base game class
- `com.badlogic.gdx.Screen` - Screen/scene base
- `com.badlogic.gdx.graphics.Texture` - Image loading
- `com.badlogic.gdx.audio.Sound` - Audio effects

### Commons Library
- `de.dakror.common.libgdx.GameBase` - Enhanced game base class
- `de.dakror.common.libgdx.GameScene` - Scene management
- `de.dakror.common.libgdx.audio.SoundManager` - Audio management
- `de.dakror.common.debug.TreeDebugger` - Debug utilities

### GDX-SFX Library
- Sound effects management
- Background music support

## Debugging

### Enable Console Output
The game may output to console. Check for:
- LibGDX framework messages
- Custom debug output
- Errors and warnings

### IntelliJ IDEA Setup (Optional)
1. Open the project folder
2. Let IntelliJ recognize it as a Gradle project
3. Use Gradle tool window for tasks
4. Set run configuration to desktop main class

### Visual Debugging
1. Add `System.out.println()` debug statements
2. Use LibGDX's built-in debug renderer (if available)
3. Check game state through visual inspection

## Building for Distribution

### Clean Build
```bash
gradlew.bat clean desktop:dist
```

### Optimized Build
For release builds, ensure optimization flags are set:
- Check `desktop/build.gradle` dist task
- Consider adding ProGuard/R8 obfuscation

### Output
- **Location:** `desktop/build/libs/desktop-1.0.jar`
- **Size:** ~19.3 MB (includes all dependencies)
- **Run:** `java -jar desktop-1.0.jar` from assets directory

## Gradle Commands

```bash
# Clean build
gradlew.bat clean

# Build desktop only
gradlew.bat desktop:dist

# Build without packaging
gradlew.bat desktop:classes

# Run desktop version (requires assets)
gradlew.bat desktop:run

# View all available tasks
gradlew.bat tasks

# Build with info logging
gradlew.bat desktop:dist --info

# Stop Gradle daemon
gradlew.bat --stop
```

## Performance Optimization Tips

1. **Use texture atlases** instead of individual textures
2. **Pool object instances** for frequently created entities
3. **Batch render calls** with SpriteBatch
4. **Optimize asset sizes** (compress images/audio)
5. **Profile with LibGDX profiler** if needed

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Assets not found | Run JAR from `android/assets` directory |
| Out of memory | Increase Gradle heap: `-Xmx4096m` |
| Stale classes | Run `gradlew.bat clean` |
| Slow build | Delete `.gradle` directory, rebuild |
| Won't run | Ensure Java 8+ is on PATH |

## Version Information

- **LibGDX:** 1.9.9
- **Java:** 1.8 (target), 21 (runtime)
- **Android API Level:** 19-30
- **Build System:** Gradle 8.3

## Resources

- **LibGDX Documentation:** https://libgdx.com/wiki/
- **Original Project:** https://github.com/Dakror/DrillDown
- **Commons Library:** https://github.com/Dakror/gdx-commons
- **GDX-SFX Library:** https://github.com/Dakror/gdx-sfx

---

**Last Updated:** October 23, 2025

