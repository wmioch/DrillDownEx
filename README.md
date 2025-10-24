# Drill Down - Factory Building Game

An open-source factory building game originally released in 2019 on Steam and Google Play. This repository contains the complete source code with modern improvements and enhanced development features.

## 🎮 About the Game

Drill Down is a factory building simulation where you create production chains, mine resources, and expand your operation deeper and deeper underground. Manage complex supply networks, optimize production, and face new challenges at each depth level.

**Original Release**: 2019 (Steam & Google Play)  
**Available at**: [itch.io](https://dakror.itch.io/drill-down)

## ⚙️ Tech Stack

- **Engine**: LibGDX 1.9.9
- **Language**: Java (1.8+ source, Java 11+ runtime)
- **Build System**: Gradle 8.3
- **Platform**: Desktop (Windows, Linux, macOS) & Android
- **License**: Apache License 2.0

## ✨ Recent Enhancements

### 🚀 Speed Controls (New!)
Comprehensive game speed control system with UI buttons for easy gameplay adjustment:

- **Five preset speeds**: 1x, 2x, 4x, 10x, 25x
- **Visual speed buttons** located next to the pause button
- **Keyboard shortcuts**: Press `P` to cycle speeds, `O` to reset to 1x
- **Smart UI integration**:
  - Speed buttons disabled when game is paused
  - Only one speed selected at a time (visual feedback)
  - Smooth transitions between speeds
  - Pause button resets speed to 1x
- **Localization support**: English and German interface
- **No performance impact**: Uses existing speed system

### 🐛 Debug Mode
Comprehensive debug controls for development and testing (pre-existing feature with documentation):

- **Speed controls**: Real-time speed adjustment (1x to 25x) via keyboard
- **Building tools**: God mode for free building and tech unlock
- **Quick testing**: Bulk fill structures with resources
- **Visualization**: Debug overlay showing collision bounds and state
- **Frame stepping**: Single-frame advance for precise analysis
- **Screenshot tools**: High-resolution capture modes
- **20+ debug keybinds**: Comprehensive testing toolkit

See [DEBUG_CONTROLS.md](docs/DEBUG_CONTROLS.md) for the complete debug reference.

### 📱 Modern Build System
- Updated Gradle from 7.4 to 8.3 for Java 21+ compatibility
- Fixed deprecated Gradle syntax for modern versions
- Optimized build process
- Clean, reproducible builds

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher (tested with Java 21)
- Gradle 8.3 (included via wrapper)

### Running the Game

**Windows:**
```bash
double-click RUN_GAME.bat
```

**Other platforms:**
```bash
gradlew.bat desktop:run
```

### Building from Source

**Desktop build:**
```bash
gradlew desktop:dist
```
Output: `desktop/build/libs/desktop-1.0.jar` (19.3 MB)

**Android build:**
```bash
# Requires Android SDK, NDK, and keystore setup
gradlew android:assembleFullRelease
```

See [README_SETUP.md](docs/README_SETUP.md) for detailed setup instructions.

## 🛠️ Development

### Project Structure
```
DrillDown/
├── core/                  # Game logic (Java)
├── desktop/              # Desktop launcher
├── android/              # Android source & assets
│   └── assets/          # Game resources (textures, audio, etc.)
├── commons/             # Utility library (submodule)
├── gdx-sfx/            # Audio library (submodule)
└── docs/               # Documentation
```

### Adding Features

1. **Modify game code**:
   - Core logic: `core/src/de/dakror/quarry/`
   - Desktop launcher: `desktop/src/de/dakror/quarry/desktop/`

2. **Add assets**:
   - Place in: `android/assets/`
   - Update: `android/assets/skin.json` for UI elements
   - Localization: Update `.properties` files

3. **Build and test**:
   ```bash
   gradlew desktop:dist    # Build
   double-click RUN_GAME.bat  # Run
   ```

See [DEVELOPMENT_GUIDE.md](docs/DEVELOPMENT_GUIDE.md) for comprehensive development documentation.

### Debug Mode

Run with debug flag to access developer controls:
```bash
gradle desktop:run --args="debug"
```
Or simply use `RUN_GAME.bat` which automatically enables debug mode.

**Available debug keybinds** (when running in debug mode):
- `G` - God mode (free building, unlock all tech)
- `P` - Cycle game speeds (1x → 2x → 4x → 10x → 25x → 1x)
- `O` - Reset speed to 1x
- `X` - Quick fill structures
- `F` - Quick fluid fill
- `D` - Toggle debug overlay
- `H` - Single frame step
- `F1` - Toggle UI visibility
- And 10+ more! See [DEBUG_CONTROLS.md](docs/DEBUG_CONTROLS.md)

## 📦 Build Information

| Aspect | Details |
|--------|---------|
| **Build Time** | ~18 seconds (clean) |
| **JAR Size** | 19.3 MB |
| **Dependencies** | All included in JAR |
| **Working Directory** | Must be `android/assets/` |

## 📚 Documentation

- **[README_SETUP.md](docs/README_SETUP.md)** - Quick start guide
- **[BUILD_SUMMARY.md](docs/BUILD_SUMMARY.md)** - Build overview and status
- **[DEVELOPMENT_GUIDE.md](docs/DEVELOPMENT_GUIDE.md)** - Developer reference
- **[DEBUG_CONTROLS.md](docs/DEBUG_CONTROLS.md)** - Complete debug keybinds reference
- **[SPEED_CONTROLS_IMPLEMENTATION_SUMMARY.md](docs/SPEED_CONTROLS_IMPLEMENTATION_SUMMARY.md)** - Speed control feature details

## 🔧 Troubleshooting

### Game won't start
- Ensure you're in the correct directory when running the JAR
- The working directory must be `android/assets/`
- Use `RUN_GAME.bat` or set the working directory manually

### Build fails
```bash
gradlew.bat --stop      # Stop Gradle daemon
del .gradle\            # Clear cache
BUILD_GAME.bat          # Retry
```

### Out of memory
Edit `gradle.properties`:
```properties
org.gradle.jvmargs = -Xms512m -Xmx4096m
```

## 🤝 Contributing

This is an open-source project! Feel free to:
- Report bugs and issues
- Suggest new features
- Submit pull requests with improvements
- Improve documentation

## 📄 License

Apache License 2.0 - See [LICENSE](LICENSE) file for details

## 🔗 Resources

- **Original Repository**: https://github.com/Dakror/DrillDown
- **Game Framework**: [LibGDX](https://libgdx.com/)
- **Download Game**: [itch.io](https://dakror.itch.io/drill-down)

## 🎯 What's New in This Fork

This version includes:
- ✅ **Speed Control System** - Visual UI buttons and keyboard shortcuts for game speed adjustment
- ✅ **Modern Gradle** - Updated to Gradle 8.3 with Java 21+ compatibility
- ✅ **Enhanced Debug Tools** - Comprehensive development keybinds
- ✅ **Complete Documentation** - Setup guides and development references
- ✅ **Improved Build System** - Faster, cleaner builds with better error messages

---

**Happy developing!** 🚀

For questions or issues, check the documentation in the `docs/` folder or open an issue on GitHub.
