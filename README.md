# Drill Down - Enhanced Edition

A factory building game with modern improvements and enhanced features. Build production chains, mine resources, and expand deeper underground.

**Original**: [Dakror/DrillDown](https://github.com/Dakror/DrillDown)

## ✨ New Features

### 🚀 Speed Controls
Visual UI buttons for game speed adjustment (1x, 2x, 4x, 10x, 25x) with keyboard shortcuts (`P` to cycle, `O` to reset).

### 🌬️ Air Purifier Balance
Air Purifiers now cover an entire layer - only **one needed per depth level** instead of multiple units.

### 🚡 Enhanced Item Elevators
Complete visual overhaul for vertical transport between floors:
- **Directional Sprites**: White graphics for upward travel, black for downward
- **Floor Labels**: Input/exit elevators show destination/source floor numbers
- **Passthrough Labels**: Intermediate floors display both source and target floors with a diagonal dividing line
- **Color-Coded Text**: Black text on white backgrounds (upward), white text on dark backgrounds (downward)
- **Blue Tinting**: All elevator structures feature distinctive blue-tinted graphics
- **Fixed Bugs**: Resolved rotation, placement, direction persistence, and 1-floor elevator labeling issues

### 🔧 Relief Valve System
- Unique structure that prevents overpressure in fluid systems
- Fluid only flows when input exceeds 99% capacity threshold
- Directional output control with orange-tinted graphics
- Automatically detects connected pipes when threshold is reached

### ⚙️ Coker Building
- Advanced fuel processing structure for producing Petroleum Coke
- Converts Refined Oil into high-density Petroleum Coke through thermal cracking
- Minimalist flat top-down design aesthetic
- Requires Advanced Fuel Processing technology
- Complete texture implementation with building sprite, UI icon, and dark fuel item graphic

### 🛠️ Modern Build System
- Gradle 8.3 (Java 21+ compatible)
- Updated dependencies and syntax
- Clean, reproducible builds

### 📚 Complete Documentation
- Full debug keybinds reference (20+ commands)
- Setup and development guides
- Helper scripts for building and running
- **Agent-Focused Texture Guides** (crucial for adding new graphics)
  - [Quick Start for AI Agents](docs/TextureGuides/AGENT_QUICK_START.md)
  - [Pre-Commit Verification](docs/TextureGuides/AGENT_BEFORE_COMMIT.md)

## 🚀 Quick Start

**Windows**: Double-click `RUN_GAME.bat`

**Other platforms**:
```bash
gradlew desktop:run
```

**Flutter + Flame sandbox (no native build required)**:

```bash
cd flutter_game
source ../scripts/flutter_env.sh  # Exposes the preinstalled Flutter/Android/Chrome toolchains
flutter pub get
flutter run -d chrome
```

The sandbox recreates the factory loop (drills, smelters, pumps, purifiers, elevators, relief valves, and cokers) with procedural graphics for easy agent-driven testing.

**Build from source**:
```bash
gradlew desktop:dist
```

See [docs/README_SETUP.md](docs/README_SETUP.md) for detailed instructions.

## 📋 Requirements

- Java 11+ (tested with Java 21)
- Gradle 8.3 (included)

## 🐛 Debug Mode

Run with `RUN_GAME.bat` or pass `debug` argument for developer controls:
- `G` - God mode, `P` - Cycle speeds, `O` - Reset speed
- `X` - Fill structures, `F` - Fill fluids, `D` - Debug overlay
- `0-5` - Select liquid type (Water, Oils, Molten Copper) for filling tanks/pipes
- `H` - Frame step, `F1` - Toggle UI

[Full debug reference →](docs/DEBUG_CONTROLS.md)

## 📚 Documentation

- [Setup Guide](docs/README_SETUP.md)
- [Development Guide](docs/DEVELOPMENT_GUIDE.md)
- [Debug Controls](docs/DEBUG_CONTROLS.md)
- [Build Summary](docs/BUILD_SUMMARY.md)
- [Speed Controls Details](docs/SPEED_CONTROLS_IMPLEMENTATION_SUMMARY.md)

## ✅ Continuous Integration & Automated Builds (Flutter sandbox)

GitHub Actions builds and tests the Flutter + Flame sandbox on pushes/PRs that touch `flutter_game/**`.

**Workflow Config**: [`.github/workflows/flutter-ci.yml`](.github/workflows/flutter-ci.yml)
**View Latest Runs**: [Actions Tab](https://github.com/wmioch/DrillDownEx/actions/workflows/flutter-ci.yml)

### Each Flutter CI run:
1. ✅ Runs unit tests (`flutter test --exclude-tags integration`)
2. ✅ Runs integration tests (`flutter test --tags integration`)
3. ✅ Builds Android release APK (`flutter build apk --release`)
4. ✅ Builds Linux desktop bundle (`flutter build linux --release`)
5. ✅ Uploads both artifacts for download

### 📱 Download the APK or desktop bundle from GitHub

1. **Trigger a build**: Push a change under `flutter_game/**` (or [re-run the workflow](https://github.com/wmioch/DrillDownEx/actions/workflows/flutter-ci.yml)).
2. **Download artifacts**:
   - Open [Actions](https://github.com/wmioch/DrillDownEx/actions/workflows/flutter-ci.yml) → latest successful run
   - Grab **`flutter-android-apk`** (unzips to `app-release.apk`)
   - Grab **`flutter-linux-desktop`** (tarball of the release bundle)
3. **Install/run**:
   - Android: copy `app-release.apk` to a device and install (enable "unknown sources" when prompted)
   - Linux: extract the tarball and run `./flutter_game`

> **Notes**
> - Artifacts expire after 90 days—rerun the workflow to regenerate
> - Workflow installs Flutter SDK 3.24.3 and required Linux deps automatically

### Texture & Graphics Development

**⚠️ Adding New Graphics? Start here:**
- [Agent Quick Start](docs/TextureGuides/AGENT_QUICK_START.md) - 2-3 min overview
- [Agent Before Commit](docs/TextureGuides/AGENT_BEFORE_COMMIT.md) - Pre-commit checklist
- [Complete Implementation Guide](docs/TextureGuides/TEXTURE_IMPLEMENTATION_CHECKLIST.md)
- [System Architecture](docs/TextureGuides/TEXTURE_SYSTEM_ARCHITECTURE.md)
- [Comprehensive Reference](docs/TextureGuides/TEXTURE_PACKING_GUIDE.md)

## 🤝 Contributing

Issues and pull requests welcome!

## 📄 License

Apache License 2.0 - See [LICENSE](LICENSE)

## 🔗 Links

**Tech**: LibGDX 1.9.9 | Java | Gradle 8.3  
**Original**: https://github.com/Dakror/DrillDown  
**Framework**: [LibGDX](https://libgdx.com/)

Clouds are Beautiful!
