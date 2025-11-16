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

## ✅ Continuous Integration

- GitHub Actions workflow: [`.github/workflows/ci.yml`](.github/workflows/ci.yml)
- Triggers on pushes and pull requests targeting `main`
- Steps:
  1. Check out the repository (including submodules)
  2. Install JDK 21 and the Android SDK platform/Build Tools 34
  3. Run `./gradlew desktop:dist` and `./gradlew android:assembleDebug`

## 📱 Deploy an Android build directly from GitHub

You no longer need a PC to sideload new versions—each CI run now publishes the latest debug APK so you can install it straight from your phone:

1. Push/merge your changes so the **CI** workflow runs.
2. On your Android device, open the GitHub app or https://github.com, navigate to this repository, and tap the **Actions** tab.
3. Select the most recent successful run of the "CI" workflow.
4. Scroll to the **Artifacts** section and download `android-debug-apk`. GitHub delivers it as a `.zip`; tap it in your Downloads app to extract the contained `.apk`.
5. If prompted, allow installs from unknown sources, then open the extracted `.apk` to update Drill Down on the device.

The artifact is rebuilt on every push to `main`, so the download is always up to date with the latest commit.

> **Mobile install tips**
> - You must push (or merge) your commits so that the CI workflow runs—only successful runs produce the downloadable artifact.
> - GitHub zips artifacts; any Files app that can extract ZIP archives works (no desktop required).
> - Artifacts expire automatically after 90 days. If the archive is gone, re-run the workflow (or push again) to generate a new APK.

### 🔁 Trigger a new Android artifact from your phone

Already merged but need to rebuild without a PC handy? You can kick off a new CI run directly from the GitHub app or mobile site:

1. Navigate to **Actions → CI** and open the most recent workflow run.
2. Tap the **⋯** menu in the upper-right corner and choose **Re-run jobs** (GitHub will queue a fresh run on hosted runners).
3. Wait for the run to complete, then grab the regenerated `android-debug-apk` artifact using the steps above.

This is handy when an old artifact has expired or when you just merged changes from another device and want the latest APK right away.

> **Note:** When running the Gradle wrapper inside this sandboxed environment we cannot download the Gradle 8.3 distribution because the proxy blocks outbound HTTPS requests ("Unable to tunnel through proxy"), so local builds here fail before Gradle runs. GitHub Actions has normal internet access, so the workflow succeeds there—any failure you may see locally only indicates the sandbox restriction, not a CI issue.

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
