# Speed Controls Testing Guide

## Pre-Testing Setup

### Build Project
```bash
cd D:\Projects\DrillDown
gradlew clean build
```

### Run Game
- **Desktop**: `gradlew desktop:run`
- **Android**: Deploy to device/emulator

## Quick Test (5 minutes)

### Visual Inspection
1. [ ] Start game
2. [ ] Look for five speed buttons next to pause button
3. [ ] Buttons should show "1x", "2x", "4x", "10x", "25x"
4. [ ] "1x" button should be highlighted (active) at start
5. [ ] All buttons visible and readable

### Basic Functionality
1. [ ] Click "2x" button
   - Should highlight and show as active
   - Game speed should increase
2. [ ] Click "4x" button
   - Previous button (2x) should become inactive
   - 4x should become active
   - Game should run at 4x speed
3. [ ] Verify game is faster (items moving quicker, structures producing faster)

### Pause Integration
1. [ ] While at 4x speed, click pause button
2. [ ] All speed buttons should become disabled (grayed out)
3. [ ] Speed should reset to 1x
4. [ ] Unpause the game
5. [ ] Speed buttons should remain disabled

---

## Comprehensive Testing

### Test 1: Button Click Functionality

**Objective**: Verify each button works correctly

#### Steps
1. [ ] Start new game
2. [ ] Click "1x" button
   - Verify: Button highlights
   - Verify: Game at normal speed
   - Verify: Only 1x button is selected

3. [ ] Click "2x" button
   - Verify: Button highlights, 1x unhighlights
   - Verify: Game runs 2× faster
   - Verify: Items move faster
   - Verify: Production happens faster
   - Verify: Sound effect plays

4. [ ] Click "4x" button
   - Verify: 4x button highlighted
   - Verify: Game is 4× normal speed
   - Verify: Visual feedback clear

5. [ ] Click "10x" button
   - Verify: Button highlighted
   - Verify: Game is 10× faster
   - Verify: Still playable without freezing

6. [ ] Click "25x" button
   - Verify: Button highlighted
   - Verify: Game is 25× faster
   - Verify: No crash
   - Verify: Acceptable performance

#### Expected Results
- Only one button should be highlighted at any time
- Game speed should change immediately
- No lag or stutter during transition
- Sound effect plays for each click

---

### Test 2: Button States

**Objective**: Verify button visual states are correct

#### Steps
1. [ ] Start game (1x speed, normal state)
   - Verify: 1x button is highlighted
   - Verify: All other buttons appear inactive
   - Verify: Buttons are not disabled (full brightness)

2. [ ] Select 10x speed
   - Verify: 10x button becomes highlighted
   - Verify: All other buttons become inactive
   - Verify: Transition is smooth

3. [ ] Pause game
   - Verify: All buttons become grayed out/disabled
   - Verify: 10x button remains highlighted (but disabled)
   - Verify: Buttons cannot be clicked

4. [ ] Unpause game
   - Verify: Speed stays at 10x
   - Verify: All buttons become enabled again
   - Verify: 10x button is still highlighted

5. [ ] Reset to 1x and pause again
   - Verify: Speed resets to 1x when pausing
   - Verify: 1x button is highlighted when paused
   - Verify: All buttons disabled

#### Expected Results
- Active button always clearly highlighted
- Paused buttons are visibly disabled
- Cannot interact with disabled buttons
- Button states update correctly

---

### Test 3: Game Speed Behavior

**Objective**: Verify game actually runs at different speeds

#### Setup
- Build a simple factory: Mine → Furnace → Storage
- Ensure coal is available

#### Steps
1. [ ] Set speed to 1x
   - Time production with stopwatch
   - Example: Furnace takes 5 seconds to smelt ore

2. [ ] Set speed to 2x
   - Same production should take ~2.5 seconds
   - Count visual confirmations

3. [ ] Set speed to 4x
   - Production should take ~1.25 seconds
   - Items should visibly move faster on conveyors

4. [ ] Set speed to 10x
   - Production should be nearly instant
   - Verify conveyor items move very fast

5. [ ] Set speed to 25x
   - Everything should happen very quickly
   - Verify no crashes or extreme stuttering

#### Expected Results
- Each doubling of speed roughly halves production time
- Visual speed increase is noticeable
- No unexpected behaviors at any speed
- Game remains responsive

---

### Test 4: Pause Button Integration

**Objective**: Verify pause and speed interaction

#### Steps
1. [ ] Set speed to 4x
2. [ ] Verify 4x button is highlighted
3. [ ] Click pause button
   - Verify: Game pauses
   - Verify: Speed buttons disable
   - Verify: Speed resets to 1x
   - Verify: 1x button is highlighted

4. [ ] Click unpause (pause button again)
   - Verify: Game resumes
   - Verify: Speed stays at 1x
   - Verify: Speed buttons enable
   - Verify: 1x button is highlighted

5. [ ] Set speed to 10x
6. [ ] Open menu (pause from menu)
   - Verify: Game pauses
   - Verify: Speed buttons disable
   - Verify: Speed resets to 1x

7. [ ] Close menu (resume)
   - Verify: Speed is 1x
   - Verify: Can adjust speed again

#### Expected Results
- Pausing always resets speed to 1x
- Speed buttons always disabled when paused
- Resuming keeps speed at 1x
- No speed changing while paused

---

### Test 5: Keyboard Shortcuts

**Objective**: Verify keyboard shortcuts work

#### Steps (Debug Mode Only)
1. [ ] Press P key
   - Verify: Speed cycles to next preset
   - Verify: 1x → 2x → 4x → 10x → 25x → 1x

2. [ ] From 10x, press P
   - Verify: Speed goes to 25x

3. [ ] From 25x, press P
   - Verify: Speed goes back to 1x (wraps around)

4. [ ] Press O key from any speed
   - Verify: Speed resets to 1x
   - Verify: 1x button becomes highlighted

5. [ ] Verify buttons update when using keyboard
   - Press P several times
   - Verify: Button highlighting changes each time

#### Expected Results
- P cycles through all preset speeds
- O resets to 1x
- UI buttons sync with keyboard changes
- No conflicts with other hotkeys

---

### Test 6: Tooltips

**Objective**: Verify tooltips display correctly

#### Steps
1. [ ] Hover over "1x" button
   - Tooltip should appear
   - Text should read: "Normal speed" (English) or "Normale Geschwindigkeit" (German)

2. [ ] Hover over "2x" button
   - Tooltip: "2x speed" or "2x Geschwindigkeit"

3. [ ] Hover over "4x" button
   - Tooltip: "4x speed" or "4x Geschwindigkeit"

4. [ ] Hover over "10x" button
   - Tooltip: "10x speed" or "10x Geschwindigkeit"

5. [ ] Hover over "25x" button
   - Tooltip: "25x speed" or "25x Geschwindigkeit"

6. [ ] Test language switching
   - Switch language to German
   - Verify: Tooltips show German text
   - Switch back to English
   - Verify: Tooltips show English text

#### Expected Results
- Tooltips appear on hover
- Correct text for each button
- Tooltips disappear when not hovering
- Language changes affect tooltip text

---

### Test 7: UI Layout and Positioning

**Objective**: Verify buttons are positioned correctly

#### Desktop (1920x1080)
1. [ ] Pause button visible at top-left
2. [ ] Five speed buttons immediately right of pause
3. [ ] All buttons aligned horizontally
4. [ ] Buttons don't overlap
5. [ ] Buttons don't cover other UI elements
6. [ ] All text readable

#### Mobile (Portrait 1080x1920)
1. [ ] Buttons visible at top
2. [ ] May wrap if screen too narrow
3. [ ] All buttons still accessible
4. [ ] No overlap with game view

#### Mobile (Landscape 1920x1080)
1. [ ] Buttons clearly visible
2. [ ] Proper spacing
3. [ ] All readable

#### Expected Results
- Buttons layout correctly on all screen sizes
- No overflow or clipping
- Proper alignment and spacing
- All text readable

---

### Test 8: Performance Testing

**Objective**: Verify no performance degradation

#### With Small Factory
1. [ ] Cycle through speeds 1x → 2x → 4x → 10x → 25x
2. [ ] Monitor FPS (top-right of screen)
3. [ ] Monitor UPS (top-right of screen)

#### With Large Factory (100+ structures)
1. [ ] Set speed to 1x
   - Check FPS and UPS display
2. [ ] Set speed to 2x
   - Verify: FPS should remain similar
   - Verify: UPS should remain ~60
3. [ ] Set speed to 4x
   - Game might slow down, but should not crash
4. [ ] Set speed to 25x
   - Acceptable to have lower FPS
   - Should not crash

#### Expected Results
- FPS relatively stable at different speeds
- UPS stays near 60 at all speeds
- No crashes even at 25x
- Performance degrades gracefully

---

### Test 9: Edge Cases

**Objective**: Test unusual scenarios

#### Edge Case 1: Rapid Clicking
1. [ ] Rapidly click different speed buttons
2. [ ] Verify: No crash
3. [ ] Verify: Speed updates correctly
4. [ ] Verify: Button states correct

#### Edge Case 2: Change Speed While Paused
1. [ ] Set speed to 4x
2. [ ] Pause (speed resets to 1x, buttons disable)
3. [ ] Try clicking 2x (should not work)
4. [ ] Verify: Button doesn't respond
5. [ ] Unpause
6. [ ] Set speed to 2x
7. [ ] Verify: Works correctly

#### Edge Case 3: Multiple Speed Changes
1. [ ] Set to 2x, wait 5 seconds
2. [ ] Set to 10x, wait 5 seconds
3. [ ] Set to 1x, wait 5 seconds
4. [ ] Verify: No freezing or lag
5. [ ] Verify: All speeds applied correctly

#### Edge Case 4: Speed Change During Building
1. [ ] Build some structures at 1x
2. [ ] Change to 4x mid-construction
3. [ ] Verify: Building continues at 4x
4. [ ] Verify: No issues

#### Edge Case 5: Speed Change During Layer Switch
1. [ ] Set speed to 10x
2. [ ] Switch to another layer
3. [ ] Verify: Speed remains 10x
4. [ ] Verify: Buttons still correct

#### Expected Results
- All edge cases handled gracefully
- No crashes or errors
- Correct behavior maintained

---

### Test 10: Internationalization

**Objective**: Verify i18n strings work correctly

#### English Testing
1. [ ] Set game language to English
2. [ ] Hover over each button
3. [ ] Verify tooltips show English text

#### German Testing
1. [ ] Set game language to German
2. [ ] Hover over each button
3. [ ] Verify tooltips show German text

#### Mixed Testing
1. [ ] Change language during gameplay
2. [ ] Speed controls should not break
3. [ ] Tooltips should update

#### Missing String Testing
1. [ ] Intentionally remove a i18n key
2. [ ] Verify: Default text shown (key name)
3. [ ] Verify: Game doesn't crash
4. [ ] Add key back and reload

#### Expected Results
- All buttons display text correctly
- Tooltips show correct language
- Language switching works
- No crashes with missing keys

---

## Bug Report Template

If you find issues, use this template:

```
## Bug Report

**Title**: [Brief description]

**Severity**: [Critical/High/Medium/Low]

**Steps to Reproduce**:
1. [Step 1]
2. [Step 2]
3. [Step 3]

**Expected Behavior**:
[What should happen]

**Actual Behavior**:
[What actually happened]

**Screenshots/Video**:
[If available]

**System Info**:
- Device: [Phone/Desktop/Emulator]
- OS: [Android/Windows/etc]
- Screen Size: [1920x1080/etc]
- Speed Setting: [1x/2x/etc]
```

---

## Regression Testing Checklist

Before releasing, ensure:

- [ ] All existing UI elements work (menu, structures, etc.)
- [ ] Save/load still works
- [ ] No new crashes or errors
- [ ] Performance not degraded
- [ ] No memory leaks
- [ ] Pause button works as before
- [ ] Keyboard shortcuts not conflicting
- [ ] No visual glitches

---

## Performance Benchmarks (Optional)

Document baseline performance:

```
Configuration: [Device/Resolution]
Testing: Large factory with 150 structures

Speed | FPS | UPS | Notes
------|-----|-----|-------
1x    | 120 | 60  | Baseline
2x    | 120 | 60  | (2x speed)
4x    | 118 | 60  | (4x speed)
10x   | 110 | 60  | (10x speed)
25x   | 85  | 60  | (25x speed)
```

---

## Sign-Off

Testing Completed By: _______________
Date: _______________
Platform(s) Tested: _______________
Issues Found: _______________
Status: ☐ PASS ☐ FAIL ☐ PASS WITH NOTES
