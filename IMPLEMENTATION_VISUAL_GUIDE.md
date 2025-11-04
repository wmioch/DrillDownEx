# Debug Liquid Fill - Visual Implementation Guide

## Feature Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│ User presses number key (0-5) in debug mode                │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ keyDown() method called            │
         │ (in Game.java)                    │
         └───────────────┬───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ Check: debug mode?                │
         │ if (Quarry.Q.version.equals...)   │
         └───────────────┬───────────────────┘
                         │ YES
                         ▼
         ┌───────────────────────────────────┐
         │ Check number key case (0-6)       │
         │ case Keys.NUM_0, NUM_1, ...       │
         └───────────────┬───────────────────┘
                         │ MATCH
                         ▼
         ┌───────────────────────────────────┐
         │ Calculate liquid index             │
         │ liquidIndex = keycode - NUM_0     │
         └───────────────┬───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ Bounds check                      │
         │ if (liquidIndex < array.length)   │
         └───────────────┬───────────────────┘
                         │ VALID
                         ▼
         ┌───────────────────────────────────┐
         │ Set LIQUID_FILL_TYPE = liquidIndex│
         └───────────────┬───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ Show toast with liquid name       │
         │ ui.toast.show(...)                │
         └───────────────┬───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ User clicks on tank or pipe       │
         └───────────────┬───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ touchDown() method called         │
         │ Structure s = layer.getStructure()│
         └───────────────┬───────────────────┘
                         │ FOUND
                         ▼
         ┌───────────────────────────────────┐
         │ Check LIQUID_FILL_TYPE >= 0       │
         │ (is a liquid selected?)           │
         └───────────────┬───────────────────┘
                         │ YES
                         ▼
         ┌───────────────────────────────────┐
         │ Get liquid from array             │
         │ ItemType liquid = DEBUG_LIQUIDS[] │
         └───────────────┬───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ Check structure type              │
         ├───────────────┬───────────────────┤
         │               │                   │
      Pipe?          Tank?            Other?
         │               │                   │
         ▼               ▼                   ▼
    Fill 1000       Fill 100M           Nothing
    units          units
         │               │                   │
         └───────────────┴───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ Show confirmation toast           │
         │ "Filled [type] with [liquid]"    │
         └───────────────┬───────────────────┘
                         │
                         ▼
         ┌───────────────────────────────────┐
         │ Reset: LIQUID_FILL_TYPE = -1      │
         │ (ready for next selection)        │
         └───────────────────────────────────┘
```

---

## Code Location Map

```
Game.java File Structure
│
├─ Imports
├─ Class declaration
├─ Public methods
│
├─ Line ~1554 ─────────────────────────── [LOCATION 1]
│  │ public static boolean SMOOTH_CAMERA = false;
│  │ 
│  │ [INSERT 9 LINES HERE]  ← Add LIQUID_FILL_TYPE and DEBUG_LIQUIDS
│  │
│  └─ private static final Pattern fileRegex = ...
│
├─ Line ~260-3850 (touchDown method)
│  │
│  ├─ Line ~402-406: FLUIDMODE check
│  │
│  ├─ Line ~407-421: FILLMODE check
│  │
│  ├─ Line ~421 ─────────────────────── [LOCATION 3]
│  │  │
│  │  │ [INSERT 10 LINES HERE]  ← Add liquid fill click handler
│  │  │
│  │  └─ if (structureDestroyMode) { ...
│  │
│  └─ ... rest of method
│
└─ Line ~3979-4045 (keyDown method)
   │
   ├─ Line ~3982-3983: if (debug mode) { switch(keycode)
   │
   ├─ Line ~3983-4000: F, H, R, D, G keys
   │
   ├─ Line ~4000-4004: T, L, X, C, V keys
   │
   ├─ Line ~4004-4008: K, P, O keys
   │
   ├─ Line ~4008-4010: U, W keys
   │
   ├─ Line ~4043 ─────────────────────── [LOCATION 2]
   │  │                    break; (from W case)
   │  │
   │  │ [INSERT 13 LINES HERE]  ← Add number key handlers (0-6)
   │  │
   │  └─ } (end debug switch)
   │
   ├─ Line ~4047+: switch (keycode) { (NORMAL MODE)
   │
   └─ End of method
```

---

## Data Structure Visualization

### LIQUID_FILL_TYPE Variable
```
LIQUID_FILL_TYPE: int
┌──────────────────────┐
│ Value   | Meaning    │
├──────────────────────┤
│  -1     | No liquid  │ (default state)
│   0     | Water      │ (after pressing 0)
│   1     | Refined Oil│ (after pressing 1)
│   2     | Crude Oil  │ (after pressing 2)
│   3     | Oil→Col    │ (after pressing 3)
│   4     | Oil→Ref    │ (after pressing 4)
│   5     | Mol.Copper │ (after pressing 5)
└──────────────────────┘
```

### DEBUG_LIQUIDS Array
```
DEBUG_LIQUIDS: ItemType[] (CONSTANT)
┌──────────────────────────────────────┐
│ Index │ Value                        │
├──────────────────────────────────────┤
│   0   │ ItemType.Water               │
│   1   │ ItemType.RefinedOil          │
│   2   │ ItemType.CrudeOil            │
│   3   │ ItemType.IntermediateOilToColumn    │
│   4   │ ItemType.IntermediateOilToRefinery  │
│   5   │ ItemType.MoltenCopper        │
├──────────────────────────────────────┤
│        (Only indices 0-5 used)       │
└──────────────────────────────────────┘

Accessed by: DEBUG_LIQUIDS[LIQUID_FILL_TYPE]
```

---

## Method Interaction Diagram

```
keyDown(int keycode)
  │
  ├─ Check: debug mode?
  │
  ├─ [NEW] Number key (0-6) case
  │   └─ Calculate liquidIndex
  │   └─ Validate liquidIndex
  │   └─ Set LIQUID_FILL_TYPE = liquidIndex
  │   └─ Show toast
  │
  └─ (other key cases...)


touchDown(int x, int y, ...)
  │
  ├─ Convert coordinates to tile
  │
  ├─ Get structure at tile
  │
  ├─ [EXISTING] Check FLUIDMODE
  │
  ├─ [EXISTING] Check FILLMODE
  │
  ├─ [NEW] Check LIQUID_FILL_TYPE >= 0
  │   ├─ Get liquid from DEBUG_LIQUIDS[LIQUID_FILL_TYPE]
  │   ├─ If FluidTubeStructure: acceptFluid(liquid, 1000)
  │   ├─ If Tank: acceptFluid(liquid, 100_000_000)
  │   ├─ Show confirmation toast
  │   └─ Reset LIQUID_FILL_TYPE = -1
  │
  ├─ [EXISTING] Check structureDestroyMode
  │
  └─ (other checks...)
```

---

## Execution Timeline Example

### Example: User presses 1, then clicks a tank

```
INITIAL STATE
  LIQUID_FILL_TYPE = -1    (no liquid selected)

┌─────────────────────────────────────────────────────────────┐
│ T=0ms: User presses "1" key                                 │
│ 
│ Event: KeyDown(NUM_1)
│   ├─ keyDown(Keys.NUM_1) called
│   ├─ liquidIndex = Keys.NUM_1 - Keys.NUM_0 = 1
│   ├─ LIQUID_FILL_TYPE = 1
│   ├─ Toast: "Liquid Fill Mode: Refined Oil (click on tank/pipe to fill)"
│   │
│   └─ STATE AFTER: LIQUID_FILL_TYPE = 1
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ T=50ms: User clicks on tank at (5, 7)                       │
│
│ Event: TouchDown(x, y)
│   ├─ Convert to tile: tileX=5, tileY=7
│   ├─ Get structure: Tank found at (5, 7)
│   ├─ Check: LIQUID_FILL_TYPE >= 0?     YES (value is 1)
│   ├─ Get liquid: DEBUG_LIQUIDS[1] = RefinedOil
│   ├─ Is Tank?                           YES
│   ├─ Call: s.acceptFluid(RefinedOil, 100_000_000, null)
│   ├─ Toast: "Filled Tank with Refined Oil"
│   ├─ Reset: LIQUID_FILL_TYPE = -1
│   │
│   └─ STATE AFTER: LIQUID_FILL_TYPE = -1
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ T=55ms: User clicks on pipe at (6, 7)  (without pressing 1 again)
│
│ Event: TouchDown(x, y)
│   ├─ Convert to tile: tileX=6, tileY=7
│   ├─ Get structure: Pipe found at (6, 7)
│   ├─ Check: LIQUID_FILL_TYPE >= 0?     NO (value is -1)
│   ├─ Skip liquid fill handler
│   │
│   └─ STATE AFTER: LIQUID_FILL_TYPE = -1 (unchanged)
└─────────────────────────────────────────────────────────────┘

CONCLUSION: Must press 1 again to fill another structure
```

---

## Class Dependencies

```
Game.java
  │
  ├─ Uses ItemType (enum)
  │   └─ DEBUG_LIQUIDS array stores ItemType values
  │
  ├─ Uses FluidTubeStructure (interface/class)
  │   └─ Checked with instanceof
  │   └─ acceptFluid(itemType, amount, null) called
  │
  ├─ Uses Tank (class extends Structure)
  │   └─ Checked with instanceof
  │   └─ acceptFluid(itemType, amount, null) called
  │
  ├─ Uses Keys (libGDX)
  │   └─ Keys.NUM_0 through Keys.NUM_6 constants
  │
  └─ Uses GameUi
      └─ ui.toast.show(message) for notifications
```

---

## Testing Checklist Flowchart

```
START TEST
  │
  ├─► Compile code → OK?
  │      NO → Fix errors, goto START
  │      YES ↓
  │
  ├─► Run: gradle desktop:run --args="debug" → Started?
  │      NO → Check gradle setup
  │      YES ↓
  │
  ├─► Press 0 → Toast shows "Water"?
  │      NO → Location 2 issue
  │      YES ↓
  │
  ├─► Click tank → Tank fills with water, toast shows?
  │      NO → Location 3 issue
  │      YES ↓
  │
  ├─► Press 1 → Toast shows "Refined Oil"?
  │      NO → Location 2 issue
  │      YES ↓
  │
  ├─► Click pipe → Pipe fills with oil, toast shows?
  │      NO → Location 3 issue
  │      YES ↓
  │
  ├─► Press 2-5 → Each shows correct liquid?
  │      NO → Location 1 or 2 issue
  │      YES ↓
  │
  ├─► Click structure without pressing key → Nothing happens?
  │      NO → Logic issue in Location 3
  │      YES ↓
  │
  └─► Press X (FILLMODE) → Still works?
         NO → Compatibility issue
         YES ↓
         
       ✓ ALL TESTS PASS
```

---

## File Size Impact

```
Original Game.java
├─ Current size: ~4200 lines
│
New changes
├─ Location 1: +9 lines  (variables)
├─ Location 2: +13 lines (key handlers)
├─ Location 3: +10 lines (click handler)
│
Result
└─ New size: ~4232 lines (+32 total, +0.76%)
```

The additions are minimal and focused!


