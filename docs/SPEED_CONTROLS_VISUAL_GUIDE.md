# Speed Controls Visual Guide

## UI Layout

### Top-Left Corner of Game Screen
```
┌─────────────────────────────────────────────────────────────┐
│                                                              │
│ [≡] [⏸ ] [1x] [2x] [4x] [10x] [25x]                        │
│ Menu Pause  Speed Control Buttons                           │
│                                                              │
│                                                              │
│                                                              │
│  Game View (Map, Structures, Items)                         │
│                                                              │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## Button States

### Normal State (1x Speed Selected)
```
[1x]        [2x]        [4x]        [10x]       [25x]
█████       ▯▯▯▯▯       ▯▯▯▯▯       ▯▯▯▯▯       ▯▯▯▯▯
█████       ▯▯▯▯▯       ▯▯▯▯▯       ▯▯▯▯▯       ▯▯▯▯▯
█████       ▯▯▯▯▯       ▯▯▯▯▯       ▯▯▯▯▯       ▯▯▯▯▯
█ Active    □ Inactive  □ Inactive  □ Inactive  □ Inactive
```

### 4x Speed Selected
```
[1x]        [2x]        [4x]        [10x]       [25x]
▯▯▯▯▯       ▯▯▯▯▯       █████       ▯▯▯▯▯       ▯▯▯▯▯
▯▯▯▯▯       ▯▯▯▯▯       █████       ▯▯▯▯▯       ▯▯▯▯▯
▯▯▯▯▯       ▯▯▯▯▯       █████       ▯▯▯▯▯       ▯▯▯▯▯
□ Inactive  □ Inactive  █ Active    □ Inactive  □ Inactive
```

### When Paused (All Disabled)
```
[1x]        [2x]        [4x]        [10x]       [25x]
░░░░░       ░░░░░       █████       ░░░░░       ░░░░░
░░░░░       ░░░░░       █████       ░░░░░       ░░░░░
░░░░░       ░░░░░       █████       ░░░░░       ░░░░░
░ Disabled  ░ Disabled  █ Active    ░ Disabled  ░ Disabled
   (but were selected last)
```

## Tooltip Examples

When hovering over buttons:
```
┌────────────────────────┐
│  2x speed               │  (English)
│  (button tooltip)       │
└────────────────────────┘

┌────────────────────────┐
│  2x Geschwindigkeit     │  (German)
│  (button tooltip)       │
└────────────────────────┘
```

## Speed Multiplier Effects

### 1x Speed (Normal)
- Production: Normal speed
- Items move at normal rate
- Conveyors operate normally
- Power generation normal
- FPS: Whatever your device supports

### 2x Speed
- Production: 2× faster
- Items move twice as fast
- Conveyors work twice as fast
- Power generation doubled
- UPS display: 60 (2x speed)

### 4x Speed
- Production: 4× faster
- Suitable for watching factory run
- Still maintains good responsiveness

### 10x Speed
- Production: 10× faster
- Designed for fast progression
- May impact performance on older devices
- UPS may show 60 (10x speed)

### 25x Speed
- Production: 25× faster
- Maximum recommended speed
- Designed for power users
- Will impact performance
- Not recommended for older devices

## User Interaction Flows

### Using Speed Buttons

#### Scenario 1: Increase Speed
```
1. Player clicks [2x] button
2. Button highlights in blue/green
3. Game speed immediately changes to 2x
4. All structures update production rates
5. Items move 2× faster
6. UPS display updates: "UPS: 60 (2x speed)"
```

#### Scenario 2: Change Speed While Playing
```
1. Player at 2x speed, watching factory
2. Clicks [4x] button
3. [2x] becomes inactive, [4x] becomes active
4. Smooth speed transition
5. Production instantly doubles
```

#### Scenario 3: Pause While at High Speed
```
1. Player at 10x speed
2. Clicks pause button
3. Game pauses
4. Speed resets to 1x
5. All speed buttons disabled (grayed out)
6. Only [1x] shows as selected (but disabled)
7. When unpausing, speed stays at 1x
```

## Keyboard Shortcut Reference

| Key | Action | Speed Sequence |
|-----|--------|-----------------|
| P   | Cycle Speed | 1x → 2x → 4x → 10x → 25x → 1x |
| O   | Reset Speed | → 1x |

## Mobile Layout Considerations

### Portrait Mode (Narrow Screen)
```
┌──────────────────────────┐
│ [≡] [⏸ ] [1x] [2x] [4x] │
│     [10x] [25x]          │
│                          │
│    Game View             │
│                          │
└──────────────────────────┘
```
(Buttons may wrap if screen is too narrow - positioned at same Y coordinate)

### Landscape Mode (Wide Screen)
```
┌─────────────────────────────────────────┐
│ [≡] [⏸ ] [1x] [2x] [4x] [10x] [25x]   │
│                                         │
│            Game View                    │
│                                         │
└─────────────────────────────────────────┘
```

## Integration with Other UI Elements

### With Menu Open
```
Game Menu (left panel)
├─ Save Game
├─ Load Game
├─ Settings
│  ├─ Sound: 75%
│  └─ Music: 50%
└─ Back

                    [⏸ ] [1x] [2x] [4x] [10x] [25x]
                    Game still shows with speed controls
```

### With Structure UI Panel
```
┌──────────────────────┐         [⏸ ] [1x] [2x] [4x] [10x] [25x]
│ Structure Name       │
│ ────────────────────│
│ Input: Coal (50)    │
│ Output: Steel (100) │
│ Power: 500/1000     │
└──────────────────────┘
         (Bottom right)      (Top left - speed controls)
```

## Color Coding

### Default Color Scheme
- **Normal buttons**: Light gray / white text on darker background
- **Active button**: Blue/Green highlight with white text
- **Disabled buttons**: Light gray / faded
- **Hover effect**: Slightly brighter highlight

### Text Display
- **Font**: Roboto-Medium (matches game UI)
- **Size**: ~18-20px for readability
- **Labels**: "1x", "2x", "4x", "10x", "25x"
- **Alignment**: Centered on each button

## Animation/Transitions

### Button Click Animation
```
User clicks [2x]
  ↓
Brief visual feedback (button press down)
  ↓
Sound plays (click SFX)
  ↓
Button highlights
  ↓
Game speed changes instantly
  ↓
Game continues at new speed
(No lag or stutter)
```

### Speed Change Visual
No visual change to game world, but:
- Items move visibly faster/slower
- Production rates change immediately
- FPS may vary depending on device

## Accessibility Features

### For Vision Impaired
- Text labels on all buttons ("1x", "2x", etc.)
- High contrast active state
- Tooltips available on hover

### For Motor Impaired
- Buttons are appropriately sized (60px)
- Clear spacing prevents accidental clicks
- Clear visual feedback on button press

## Performance Metrics Display

After speed change, FPS display updates:

```
Before:     After (2x speed):
FPS: 120    FPS: 120
UPS: 60     UPS: 60 (2x speed)
```

## Speed Control Disable Scenarios

Speed buttons are disabled (grayed out) when:
1. ✓ Game is paused
2. (Could add) Tutorial is active
3. (Could add) Dialog/modal open (optional)

Speed buttons remain enabled when:
1. ✓ Menu is open (foreground)
2. ✓ Structure UI is open (bottom right)
3. ✓ Game is running normally
4. ✓ Camera is moving
5. ✓ Building mode is active

## Testing Visuals

### Button Layout Verification
```
Measure gaps:
[Pause] ←80px→ [1x]
              ↑
              Button edge

Buttons should align horizontally at:
Y = Const.UI_H - (Const.BUILD_RING_ITEM_SIZE + 12)

X positions:
1x:  baseX = 80 + (24 + Const.BUILD_RING_ITEM_SIZE)
2x:  baseX + 68 (60 width + 8 spacing)
4x:  baseX + 136
10x: baseX + 204
25x: baseX + 272
```

## Screenshot Expectations

When viewing the game with speed controls:
1. Pause button is visible (top-left)
2. Five speed buttons appear immediately right of pause
3. Buttons are same height as pause button
4. Current speed button is highlighted
5. All buttons have clearly readable "Xx" text
6. Tooltips appear on hover
