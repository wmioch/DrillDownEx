# Speed Controls Implementation Plan

## Overview
Add visible speed control buttons next to the pause button to allow players to control game simulation speed with options: 1x (normal), 2x, 4x, 10x, and 25x.

## Current State Analysis

### Existing Infrastructure
- **Speed System**: Already implemented in `Game.java`
  - `gameSpeed` field (int, default = 1)
  - `increaseSpeed()` method (doubles speed, max 100x)
  - `resetSpeed()` method (resets to 1x)
  - Hidden keyboard shortcuts: P (increase), O (reset)
  
- **Integration**: Speed multiplier is already fully integrated into all game systems
  - Structures update at `deltaTime * gameSpeed`
  - Conveyors, power, fluids, production all respect speed
  - Pause button currently resets speed when clicked

### Current Location
- **Pause Button**: Located at `(24 + Const.BUILD_RING_ITEM_SIZE, Const.UI_H - (Const.BUILD_RING_ITEM_SIZE + 12))`
- **File**: `core/src/de/dakror/quarry/scenes/GameUi.java` (lines 617-631)

## Design Decisions

### UI Layout Options

#### Option A: Horizontal Button Group (RECOMMENDED)
```
[Menu] [Pause/Play] [1x] [2x] [4x] [10x] [25x]
```
- **Pros**: Clean, intuitive, similar to video player controls
- **Cons**: Takes horizontal space
- **Position**: Right of pause button

#### Option B: Dropdown/Popup Menu
```
[Menu] [Pause/Play] [Speed ▼]
                    └─ 1x
                    └─ 2x
                    └─ 4x
                    └─ 10x
                    └─ 25x
```
- **Pros**: Minimal space usage
- **Cons**: Extra click required, less discoverable
- **Position**: Right of pause button

#### Option C: Compact Toggle Buttons
```
[Menu] [Pause/Play] [Speed: 4x ◄►]
```
- **Pros**: Very compact
- **Cons**: Less clear what options are available
- **Position**: Right of pause button

**RECOMMENDATION: Option A - Horizontal Button Group**
- Most intuitive for players
- Clear visual feedback of current speed
- Similar to common video player controls

### Visual Design

#### Button Style
1. **Radio Button Group**: Use LibGDX ButtonGroup for mutual exclusivity
2. **Visual States**:
   - **Unchecked**: Default button appearance, gray/neutral
   - **Checked**: Highlighted/colored (use theme color)
   - **Disabled**: When game is paused (optional)
3. **Size**: Match pause button size (36px icon, similar padding)
4. **Spacing**: 8-12px between buttons

#### Labels
- Display speed multiplier: "1x", "2x", "4x", "10x", "25x"
- Font: Match existing UI font (Roboto-Medium)
- Size: ~18-20px for readability

#### Colors
- **Normal (1x)**: Default/white (this is standard speed)
- **Active (selected)**: Theme green/blue to indicate selection
- **Fast speeds (10x, 25x)**: Consider orange/yellow tint to indicate "turbo mode"

### Tooltip Text (i18n)
Add to `android/assets/i18n/TheQuarry_en.properties`:
```properties
button.speed.1x = Normal speed
button.speed.2x = 2x speed - Double simulation speed
button.speed.4x = 4x speed - Quadruple simulation speed
button.speed.10x = 10x speed - Very fast simulation
button.speed.25x = 25x speed - Maximum recommended speed
button.speed.warning = High speeds may affect performance on older devices
```

Add to `android/assets/i18n/TheQuarry_de.properties`:
```properties
button.speed.1x = Normale Geschwindigkeit
button.speed.2x = 2x Geschwindigkeit - Doppelte Simulationsgeschwindigkeit
button.speed.4x = 4x Geschwindigkeit - Vierfache Simulationsgeschwindigkeit
button.speed.10x = 10x Geschwindigkeit - Sehr schnelle Simulation
button.speed.25x = 25x Geschwindigkeit - Maximal empfohlene Geschwindigkeit
button.speed.warning = Hohe Geschwindigkeiten können die Leistung auf älteren Geräten beeinträchtigen
```

## Implementation Steps

### Phase 1: Core Speed Control Logic (Game.java)

**File**: `core/src/de/dakror/quarry/scenes/Game.java`

#### 1.1 Add setGameSpeed Method
```java
// Around line 4127, after increaseSpeed()
public void setGameSpeed(int speed) {
    gameSpeed = Math.max(1, Math.min(speed, 100)); // Clamp between 1 and 100
}

public int getGameSpeed() {
    return gameSpeed;
}
```

#### 1.2 Update Existing Methods
- Keep `increaseSpeed()` and `resetSpeed()` for backward compatibility
- Ensure pause button interaction remains: `setPaused()` should reset speed to 1x

#### 1.3 Add Speed Change Callback
```java
// Notify UI when speed changes
private void onSpeedChange() {
    if (ui != null) {
        ui.updateSpeedButtons();
    }
}
```

Update `setGameSpeed()` to call `onSpeedChange()`.

### Phase 2: UI Component Creation (GameUi.java)

**File**: `core/src/de/dakror/quarry/scenes/GameUi.java`

#### 2.1 Add Field Declarations (around line 248)
```java
public ImageButton pauseButton;
public ButtonGroup<ImageButton> speedButtonGroup;
public ImageButton speed1xButton, speed2xButton, speed4xButton, speed10xButton, speed25xButton;
```

#### 2.2 Create Speed Button Factory Method (around line 650, after pause button creation)
```java
private ImageButton createSpeedButton(int speed, float xPosition) {
    final int speedValue = speed;
    
    // Create label for speed text
    Label label = new Label(speed + "x", skin, "default");
    label.setAlignment(Align.center);
    
    // Create button with label
    ImageButton button = new ImageButton(skin, "round");
    button.add(label);
    button.setTransform(true);
    
    // Add click listener
    button.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Quarry.Q.sound.play(Quarry.Q.clickSfx);
            Game.G.setGameSpeed(speedValue);
            updateSpeedButtons();
        }
    });
    
    // Add tooltip
    button.addListener(new TextTooltip(
        Quarry.Q.i18n.get("button.speed." + speed + "x"), 
        skin
    ));
    
    button.setPosition(xPosition, Const.UI_H - (Const.BUILD_RING_ITEM_SIZE + 12));
    button.setName("speed" + speed + "xButton");
    
    return button;
}
```

#### 2.3 Initialize Speed Buttons (in init() method, after line 631)
```java
// Speed control buttons
float baseX = 24 + Const.BUILD_RING_ITEM_SIZE + 80; // Start after pause button
float buttonWidth = 60; // Width including spacing
float spacing = 8;

speedButtonGroup = new ButtonGroup<>();
speedButtonGroup.setMaxCheckCount(1);
speedButtonGroup.setMinCheckCount(1);
speedButtonGroup.setUncheckLast(true);

speed1xButton = createSpeedButton(1, baseX);
speedButtonGroup.add(speed1xButton);
stage.addActor(speed1xButton);

speed2xButton = createSpeedButton(2, baseX + buttonWidth + spacing);
speedButtonGroup.add(speed2xButton);
stage.addActor(speed2xButton);

speed4xButton = createSpeedButton(4, baseX + (buttonWidth + spacing) * 2);
speedButtonGroup.add(speed4xButton);
stage.addActor(speed4xButton);

speed10xButton = createSpeedButton(10, baseX + (buttonWidth + spacing) * 3);
speedButtonGroup.add(speed10xButton);
stage.addActor(speed10xButton);

speed25xButton = createSpeedButton(25, baseX + (buttonWidth + spacing) * 4);
speedButtonGroup.add(speed25xButton);
stage.addActor(speed25xButton);

// Set initial state (1x is default)
speed1xButton.setChecked(true);
```

#### 2.4 Add Update Method (after speed button creation)
```java
public void updateSpeedButtons() {
    int currentSpeed = Game.G.getGameSpeed();
    
    // Update checked state based on current speed
    speed1xButton.setChecked(currentSpeed == 1);
    speed2xButton.setChecked(currentSpeed == 2);
    speed4xButton.setChecked(currentSpeed == 4);
    speed10xButton.setChecked(currentSpeed == 10);
    speed25xButton.setChecked(currentSpeed == 25);
    
    // If speed is not one of the presets, select closest
    if (currentSpeed != 1 && currentSpeed != 2 && currentSpeed != 4 && 
        currentSpeed != 10 && currentSpeed != 25) {
        if (currentSpeed < 2) speed1xButton.setChecked(true);
        else if (currentSpeed < 4) speed2xButton.setChecked(true);
        else if (currentSpeed < 10) speed4xButton.setChecked(true);
        else if (currentSpeed < 25) speed10xButton.setChecked(true);
        else speed25xButton.setChecked(true);
    }
}
```

### Phase 3: Pause Button Integration

**File**: `core/src/de/dakror/quarry/scenes/GameUi.java` (lines 617-623)

#### 3.1 Update Pause Button Click Handler
```java
pauseButton = roundButton(skin, skin.getDrawable("symb_pause"), new ClickListener() {
    @Override
    public void clicked(InputEvent event, float x, float y) {
        Quarry.Q.sound.play(Quarry.Q.clickSfx);
        
        boolean isPaused = pauseButton.isChecked();
        
        if (isPaused) {
            // When pausing, remember current speed
            // Reset to 1x to avoid confusion
            Game.G.setGameSpeed(1);
        }
        // When unpausing, speed stays at 1x (user can change it)
        
        Game.G.setPaused(isPaused);
        updateSpeedButtons();
    }
}, "button.pause");
```

#### 3.2 Optional: Disable Speed Buttons When Paused
```java
public void updateSpeedButtons() {
    boolean isPaused = Game.G.isPaused();
    
    // Disable speed controls when paused (optional)
    speed1xButton.setDisabled(isPaused);
    speed2xButton.setDisabled(isPaused);
    speed4xButton.setDisabled(isPaused);
    speed10xButton.setDisabled(isPaused);
    speed25xButton.setDisabled(isPaused);
    
    // ... rest of update logic
}
```

### Phase 4: Internationalization

**File**: `android/assets/i18n/TheQuarry_en.properties`

Add after line 15 (button.pause):
```properties
button.speed.1x = Normal speed
button.speed.2x = 2x speed
button.speed.4x = 4x speed
button.speed.10x = 10x speed
button.speed.25x = 25x speed
```

**File**: `android/assets/i18n/TheQuarry_de.properties`

Add after line 15:
```properties
button.speed.1x = Normale Geschwindigkeit
button.speed.2x = 2x Geschwindigkeit
button.speed.4x = 4x Geschwindigkeit
button.speed.10x = 10x Geschwindigkeit
button.speed.25x = 25x Geschwindigkeit
```

### Phase 5: Keyboard Shortcut Updates

**File**: `core/src/de/dakror/quarry/scenes/Game.java` (around lines 3961-3965)

#### 5.1 Update Debug Key Handlers
```java
case Keys.P:
    // Cycle through preset speeds: 1x -> 2x -> 4x -> 10x -> 25x -> 1x
    int[] speeds = {1, 2, 4, 10, 25};
    int currentIndex = -1;
    for (int i = 0; i < speeds.length; i++) {
        if (gameSpeed == speeds[i]) {
            currentIndex = i;
            break;
        }
    }
    int nextSpeed = speeds[(currentIndex + 1) % speeds.length];
    setGameSpeed(nextSpeed);
    onSpeedChange();
    break;
case Keys.O:
    setGameSpeed(1);
    onSpeedChange();
    break;
```

#### 5.2 Add Number Key Shortcuts (Optional)
```java
// Around line 3978, in the main switch statement
case Keys.NUM_1:
    if (Quarry.Q.version.equals("debug")) {
        setGameSpeed(1);
        onSpeedChange();
    }
    break;
case Keys.NUM_2:
    if (Quarry.Q.version.equals("debug")) {
        setGameSpeed(2);
        onSpeedChange();
    }
    break;
case Keys.NUM_4:
    if (Quarry.Q.version.equals("debug")) {
        setGameSpeed(4);
        onSpeedChange();
    }
    break;
```

### Phase 6: Save/Load Speed State (Optional)

**File**: `core/src/de/dakror/quarry/scenes/Game.java`

#### 6.1 Save Speed to Preferences
```java
// In save game logic (if desired)
data.put("gameSpeed", gameSpeed);
```

#### 6.2 Load Speed from Save
```java
// In load game logic
if (data.has("gameSpeed")) {
    setGameSpeed(data.getInt("gameSpeed", 1));
    onSpeedChange();
}
```

**Note**: Consider whether speed should persist across saves or always reset to 1x.

## Visual Assets Needed

### Option 1: Text-based Buttons (SIMPLEST)
- No new assets needed
- Use existing button styles with text labels
- **Recommended for initial implementation**

### Option 2: Icon-based Buttons (FUTURE ENHANCEMENT)
Create new texture atlas entries:
- `speed_1x.png` - Normal speed icon (single arrow?)
- `speed_2x.png` - 2x icon (double arrow?)
- `speed_4x.png` - 4x icon (quad arrow?)
- `speed_10x.png` - 10x icon (fast forward icon?)
- `speed_25x.png` - 25x icon (ultra fast icon?)

Size: 32x32 or 36x36 to match pause button

Add to `android/assets/tex.atlas` and update skin.json accordingly.

## Testing Checklist

### Functional Testing
- [ ] Click each speed button (1x, 2x, 4x, 10x, 25x)
- [ ] Verify game speed changes correctly
- [ ] Verify only one button is selected at a time
- [ ] Test pause button resets speed to 1x
- [ ] Test keyboard shortcuts (P, O) still work
- [ ] Verify speed persists when switching layers
- [ ] Test with complex factories (many structures)

### Performance Testing
- [ ] Test 25x speed with large factory (100+ structures)
- [ ] Monitor FPS and UPS at different speeds
- [ ] Verify no crashes at high speeds
- [ ] Test on low-end devices (Android)
- [ ] Check for sound desync issues

### UI Testing
- [ ] Buttons visible on all screen sizes
- [ ] Buttons don't overlap with other UI elements
- [ ] Tooltips display correctly
- [ ] Button states clear (selected vs unselected)
- [ ] Text readable on all backgrounds
- [ ] Proper alignment in portrait/landscape (mobile)

### Integration Testing
- [ ] Test with tutorial (ensure it doesn't confuse new players)
- [ ] Test with save/load
- [ ] Test with game menu open
- [ ] Test with other modals (confirm dialogs, etc.)
- [ ] Verify i18n strings work (English and German)

## Edge Cases & Considerations

### 1. Speed and Pause Interaction
**Decision**: When pausing, reset speed to 1x to avoid confusion when unpausing.
**Rationale**: Players might forget they were at high speed and be surprised when unpausing.

### 2. High Speed Performance Warning
Consider adding a one-time warning dialog when first selecting 10x or 25x:
```
"High speeds may impact performance on older devices. 
Continue with [speed]x speed?"
[Don't show again] [Cancel] [OK]
```

### 3. Speed Limits Based on Device
Optionally detect device performance and limit max speed:
- Desktop: Allow up to 100x
- High-end mobile: Allow up to 25x
- Low-end mobile: Limit to 10x or warn user

### 4. Tutorial Integration
**Question**: Should speed controls be available during tutorial?
**Recommendation**: Hide speed controls during tutorial, show after completion to avoid confusion.

### 5. Demo Version
**Question**: Should demo version have speed controls?
**Recommendation**: Yes, but limit to 4x max to differentiate from full version.

### 6. Multiplayer Considerations
**Note**: If game ever supports multiplayer/sharing, speed control would need to be synchronized.
Current implementation is single-player only, so no concerns.

### 7. UPS Display Update
Consider updating the UPS calculation tooltip to indicate when game is running at non-1x speed:
```
FPS: 120
UPS: 60 (2x speed)
```

## Alternative Implementations

### Minimal Implementation (Quick Win)
If timeline is tight, implement just:
1. Single speed toggle button: [1x] ↔ [2x]
2. Use existing keyboard shortcuts for other speeds
3. Add full UI later as enhancement

### Advanced Implementation (Future Enhancement)
- Slider control for continuous speed adjustment (1x to 25x)
- Speed presets as shortcuts within slider
- Visual indicator in game world (speed lines, motion blur)
- Sound pitch adjustment based on speed
- "Catch up" mode that auto-adjusts speed based on buffer states

## Implementation Timeline Estimate

### Phase 1: Core Logic (2-4 hours)
- Add `setGameSpeed()` and `getGameSpeed()` methods
- Update speed change callbacks
- Test with keyboard shortcuts

### Phase 2: UI Components (4-6 hours)
- Create button factory method
- Initialize speed buttons
- Add button group logic
- Position buttons correctly

### Phase 3: Integration (2-3 hours)
- Update pause button interaction
- Test speed transitions
- Handle edge cases

### Phase 4: Localization (1 hour)
- Add i18n strings for English
- Add i18n strings for German
- Test language switching

### Phase 5: Testing & Polish (3-4 hours)
- Functional testing
- Performance testing
- UI polish and adjustments
- Bug fixes

**Total Estimated Time**: 12-18 hours

## Success Criteria

1. ✅ Speed controls are visible and accessible next to pause button
2. ✅ Players can easily switch between 1x, 2x, 4x, 10x, and 25x speeds
3. ✅ Current speed is clearly indicated
4. ✅ Speed controls work smoothly without performance issues
5. ✅ Integration with pause button is intuitive
6. ✅ Tooltips explain each speed option
7. ✅ Feature works on desktop and mobile platforms
8. ✅ Localization works for English and German

## Post-Implementation Enhancements

### Future Improvements
1. **Speed Indicator in Game World**: Show "2x" overlay when not at normal speed
2. **Custom Speed Presets**: Allow players to define their own speed presets in settings
3. **Auto-speed**: Automatically adjust speed based on game state (idle factory = faster)
4. **Speed Profiles**: Save different speed profiles for different game phases
5. **Achievement**: "Speed Demon" - Complete game using only 10x+ speeds
6. **Speed Limiter**: Automatically slow down when UPS drops below target

### Analytics to Track
- Which speeds are most popular?
- Do players use speed controls frequently?
- Does high-speed cause more crashes/bugs?
- Performance impact of different speeds

## Risk Assessment

### Low Risk
- Core speed system already exists and works
- Simple UI addition with existing patterns
- No save game format changes needed

### Medium Risk
- UI layout might need adjustment for mobile screens
- High speeds might expose hidden performance issues
- Player confusion if not explained well

### Mitigation Strategies
- Thorough testing on various screen sizes
- Performance profiling at high speeds
- Clear tooltips and optional tutorial hint
- Start with conservative speed limits, increase later if stable

## Notes

- Current hidden keyboard shortcuts (P/O) should remain functional
- Consider adding speed controls to tutorial as advanced tip
- May want to add setting to hide speed controls for purists
- Sound effect playback might need pitch adjustment at high speeds
- Particle effects might look odd at very high speeds
