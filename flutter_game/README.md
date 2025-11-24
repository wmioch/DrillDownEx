# Drill Down (Flame Edition)

A lightweight Flutter + Flame recreation of the factory-building gameplay from **Drill Down**. The port focuses on agent-friendly
simulation so you can rapidly validate mechanics without native desktop or Android builds.

## Running

1. If you're using the pre-provisioned container, surface the bundled toolchains:
   ```bash
   source ../scripts/flutter_env.sh
   ```
   This exposes Flutter (3.38.x), the Android SDK (platform 36 + build-tools 34.0.0/28.0.3), and Chrome for web runs.
2. From this directory, run:
   ```bash
   flutter pub get
   flutter run -d chrome
   ```
   The project is a pure Flutter package, so running on web avoids platform-specific setup and keeps assets text-only.

## Features

- Multi-floor grid (12x8 by default) with ore, oil, and water surface types.
- Structures mirrored from the Java edition:
  - **Drills** and **smelters** for ore processing
  - **Pumps** for liquids feeding into **relief valves** and **cokers**
  - **Air purifiers** that buff production on the entire floor
  - **Item elevators** that move stacks vertically between matching columns
  - **Storage bins** for overflow
- Speed controls (1x/2x/4x/10x), floor navigation, and a build palette overlay.
- Simplified logistics: adjacent machines share inventory automatically, and elevators link matching tiles across floors.

## Testing

Unit tests cover core simulation loops. Execute them with:
```bash
flutter test --exclude-tags integration
```

An integration harness validates the full game shell and HUD wiring:
```bash
flutter test --tags integration
```

The CI workflow mirrors these commands and also produces downloadable Android and Linux desktop builds (APK + Linux bundle artifacts).

> Note: No binary assets are checked in; everything renders procedurally with colored rectangles and text labels.
