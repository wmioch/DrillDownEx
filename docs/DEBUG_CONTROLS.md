# Debug Controls Reference

This document lists all available debug keybinds for testing and development in Drill Down.

## Enabling Debug Mode

To enable debug mode and access all debug controls:

1. **Desktop**: Run the game with the debug flag:
   ```bash
   gradle desktop:run --args="debug"
   ```
   Or use the provided `RUN_GAME.bat` file which automatically passes the debug flag.

2. **Requirements**: 
   - The game version must be set to `"debug"` for debug controls to activate
   - Debug keybinds are only available when `Quarry.Q.version.equals("debug")`

---

## Debug Keybinds

### Building & Testing

#### **G - GOD_MODE** (Toggle)
- **Free Building**: Structures cost nothing to place
- **Unlock All Technologies**: Build any structure regardless of tech tree progression
- **Affects Build Menu**: Updates UI to show all available buildings
- **Use Case**: Test complete building chains without resource constraints
- **Implementation**: Skips resource deduction in `placeStructure()` when enabled

---

#### **X - FILLMODE** (Single Press)
- **One-Time Bulk Fill**: Click on structures to fill them instantly
- **Fills Different Types**:
  - **Storage**: 100 units of all non-fluid, non-abstract items
  - **Substation** (Power): 100 million power units
  - **Tank** (Fluids): 100 million units of water
- **Auto-Disable**: Automatically turns off after one use
- **Use Case**: Populate production chains for testing without manual resource management

---

#### **F - FLUIDMODE** (Toggle)
- **Quick Fluid Testing**: Click on fluid structures to fill them instantly
- **Fills Different Types**:
  - **BrickChannel**: 500 units of Molten Copper
  - **FluidTubeStructure**: 1000 units of Water
- **Toggle**: Press again to disable
- **Use Case**: Test fluid systems and pipe networks quickly

---

### Rendering & Visualization

#### **D - DRAW_DEBUG** (Toggle)
- **Visual Debug Overlay**: Displays debug information on screen
- **Shows**: 
  - Collision bounds
  - Dirty region rectangles
  - Vein debug information
  - Internal state visualization
- **Affects UI**: Also toggles UI debug mode via `ui.toggleDebug()`
- **Use Case**: Visualize game structure, collision detection, and rendering state

---

#### **F1 - Toggle UI Visibility** (Toggle)
- **Hide/Show UI**: Toggles all on-screen interface elements
- **Affects**: All UI panels, build menus, status displays
- **Use Case**: Clean screenshots or unobstructed view of structures

---

### Chunk & Layer Management

#### **R - Mark Layer Dirty** (Execute Once)
- **Force Redraw**: Marks the entire current layer as needing re-rendering
- **Technical**: Sets layer `dirtyBounds` to full layer size with max priority
- **Scope**: Only affects current layer
- **Use Case**: Fix visual glitches or force graphics refresh for current layer

---

#### **T - Mark All Chunks Dirty** (Execute Once)
- **Force All Chunks to Redraw**: Marks every chunk in current layer as dirty
- **More Thorough**: Iterates through all chunks and sets dirty flag
- **Scope**: Entire current layer
- **Use Case**: Comprehensive graphics refresh for debugging rendering issues

---

#### **L - Add Layer** (Execute Once)
- **Create New Depth Level**: Adds a new layer below the current one
- **Multi-Layer Support**: Enables vertical expansion of the map
- **Use Case**: Test multi-layer mechanics and drilling deeper gameplay

---

### Game Speed & Timing

#### **P - Cycle Game Speed** (Press Multiple Times)
- **Speed Progression**: Cycles through preset speeds
- **Sequence**: 1x → 2x → 4x → 10x → 25x → 1x (loops)
- **Each Press**: Advances to next speed in cycle
- **Use Case**: Speed up testing or slow down to observe specific behavior in detail

---

#### **O - Reset to 1x Speed** (Execute Once)
- **Reset Speed**: Immediately sets game speed back to 1x (normal)
- **Opposite of P**: Direct reset instead of cycling
- **Use Case**: Quick return to normal speed from fast-forward

---

#### **H - SINGLE_FRAME** (Execute Once)
- **Single Frame Step**: Advances game logic by exactly one frame
- **Pause State**: Game remains paused, only one frame advances
- **Use Case**: Frame-by-frame debugging for analyzing specific behavior step-by-step

---

### Fog of War

#### **W - Remove Fog of War** (Execute Once)
- **Clear All Fog**: Removes FOG_OF_WAR metadata from every tile in current layer
- **Reveals Map**: Makes entire map visible
- **Scope**: Current layer only
- **Use Case**: Test gameplay with full visibility or debug fog-of-war logic

---

#### **U - FOGMODE** (Execute Once)
- **Enable Fog Rendering**: Activates special fog-of-war visualization mode
- **Status**: Developmental flag (sets to true, no toggle)
- **Use Case**: Test or debug fog-of-war rendering

---

### Resource & State Management

#### **K - Recalculate Resources** (Execute Once)
- **Recompute All Resources**: Forces full recalculation of current inventory
- **Syncs State**: Updates internal resource tracking system
- **Scope**: All resources in game
- **Use Case**: Fix resource count discrepancies or debug resource calculation bugs

---

### Recording & Screenshots

#### **C - RECORDMODE** (Toggle)
- **Video Recording**: Enables automatic screenshot capture for video creation
- **Capture Interval**: Takes screenshots every 5 seconds
- **Output**: High-resolution framebuffer rendering (full layer size)
- **Use Case**: Record gameplay footage for demonstrations, trailers, or documentation

---

#### **V - SCREENSHOT** (Toggle)
- **High-Res Screenshot**: Takes single high-resolution screenshot of current layer
- **Quality**: Uses dedicated framebuffer for clean rendering without artifacts
- **Use Case**: Capture clean screenshots without UI for documentation or sharing

---

## Quick Tips & Combinations

### Testing Full Building Chains
1. Press **G** to enable GOD_MODE
2. Press **X** to fill storages with resources
3. Press **F** multiple times to fill fluid structures
4. Use **P** to speed up testing

### Debugging Visual Issues
1. Press **D** to enable DRAW_DEBUG
2. Press **R** or **T** to force redraw
3. Press **F1** to hide UI for clearer view

### Frame-by-Frame Analysis
1. Press **H** repeatedly to step through frames one at a time
2. Press **D** to visualize debug information
3. Observe specific behavior in detail

### Speed Testing
- **P**: Cycle speeds for fast testing (25x) or slow observation (1x)
- **O**: Quick reset to normal speed
- **H**: Single frame for precise analysis

---

## Flag State Reference

These are the static boolean flags used by debug controls:

| Flag | Default | Purpose |
|------|---------|---------|
| `GOD_MODE` | false | Free building, unlock all techs |
| `DRAW_DEBUG` | false | Visual debug overlay |
| `FLUIDMODE` | false | Quick fluid testing |
| `FILLMODE` | false | Bulk fill structures |
| `RECORDMODE` | false | Video recording mode |
| `SCREENSHOT` | false | Single screenshot mode |
| `FOGMODE` | false | Fog-of-war visualization |
| `SINGLE_FRAME` | false | Step one frame |
| `UI_VISIBLE` | true | UI display state |
| `SMOOTH_CAMERA` | false | Camera smoothing |

---

## Implementation Details

### Version Check
All debug keybinds are gated by:
```java
if (Quarry.Q.version.equals("debug")) {
    // Debug keybinds available
}
```

### Location
- Debug keydown handler: `Game.java` lines 3920-3985
- Debug flags: `Game.java` lines 1486-1495
- Desktop launcher: `DesktopLauncher.java` lines 75-101

### Running with Debug Mode
The `RUN_GAME.bat` file automatically passes the `debug` argument, which enables debug mode through:
1. `RUN_GAME.bat` passes `debug` argument to jar
2. `DesktopLauncher.main()` receives argument
3. `DesktopLauncher` sets `version = "debug"` when detected
4. `Quarry` instance is created with debug version
5. `Game.keyDown()` checks for `"debug"` version and enables keybinds

---

## Troubleshooting

**Debug commands not working?**
- Ensure game is running in debug mode (`RUN_GAME.bat` or `gradle desktop:run --args="debug"`)
- Verify the game version shows as "debug" in the title bar or logs
- Rebuild with `gradle desktop:build` after code changes

**Keybind not responding?**
- Check if another UI element has focus (click on game area first)
- Verify you're using the correct key (not numpad)
- Some keybinds execute once and auto-disable (X, V, H, K, L)

---

## Last Updated
Reference guide created for developer convenience in testing and debugging Drill Down.
