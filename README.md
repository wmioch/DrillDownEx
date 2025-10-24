# Drill Down - Enhanced Edition

A factory building game with modern improvements and enhanced features. Build production chains, mine resources, and expand deeper underground.

**Original**: [Dakror/DrillDown](https://github.com/Dakror/DrillDown) | **Play**: [itch.io](https://dakror.itch.io/drill-down)

## ✨ New Features

### 🚀 Speed Controls
Visual UI buttons for game speed adjustment (1x, 2x, 4x, 10x, 25x) with keyboard shortcuts (`P` to cycle, `O` to reset).

### 🌬️ Air Purifier Balance
Air Purifiers now cover an entire layer - only **one needed per depth level** instead of multiple units.

### 🛠️ Modern Build System
- Gradle 8.3 (Java 21+ compatible)
- Updated dependencies and syntax
- Clean, reproducible builds

### 📚 Complete Documentation
- Full debug keybinds reference (20+ commands)
- Setup and development guides
- Helper scripts for building and running

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
- `H` - Frame step, `F1` - Toggle UI

[Full debug reference →](docs/DEBUG_CONTROLS.md)

## 📚 Documentation

- [Setup Guide](docs/README_SETUP.md)
- [Development Guide](docs/DEVELOPMENT_GUIDE.md)
- [Debug Controls](docs/DEBUG_CONTROLS.md)
- [Build Summary](docs/BUILD_SUMMARY.md)
- [Speed Controls Details](docs/SPEED_CONTROLS_IMPLEMENTATION_SUMMARY.md)

## 🤝 Contributing

Issues and pull requests welcome!

## 📄 License

Apache License 2.0 - See [LICENSE](LICENSE)

## 🔗 Links

**Tech**: LibGDX 1.9.9 | Java | Gradle 8.3  
**Original**: https://github.com/Dakror/DrillDown  
**Framework**: [LibGDX](https://libgdx.com/)
