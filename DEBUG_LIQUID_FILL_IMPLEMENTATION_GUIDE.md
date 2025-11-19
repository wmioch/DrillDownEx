# Debug Liquid Fill Feature - Implementation Guide

## Overview
This guide documents how to implement debug number key bindings (0-5) that allow filling storage tanks and pipes with different liquid types while in debug mode.

## Feature Behavior
- Press number keys **0-5** in debug mode to select a liquid type
- A toast notification appears showing which liquid is selected
- Click on any tank or pipe to fill it with the selected liquid
- The system auto-resets after each fill for convenience
- Each liquid fills different amounts:
  - **Pipes (FluidTubeStructure)**: 1,000 units
  - **Tanks**: 100,000,000 units

## Available Liquids (by key)
| Key | Liquid | ItemType |
|-----|--------|----------|
| 0 | Water | `ItemType.Water` |
| 1 | Refined Oil | `ItemType.RefinedOil` |
| 2 | Crude Oil | `ItemType.CrudeOil` |
| 3 | Intermediate Oil (Column) | `ItemType.IntermediateOilToColumn` |
| 4 | Intermediate Oil (Refinery) | `ItemType.IntermediateOilToRefinery` |
| 5 | Molten Copper | `ItemType.MoltenCopper` |

---

## Implementation Steps

### STEP 1: Add Debug Liquid Variables

**File**: `core/src/de/dakror/quarry/scenes/Game.java`

**Location**: After line 1554 (after `public static boolean SMOOTH_CAMERA = false;`)

**Code to add**:
```java
    // Debug liquid fill mode - for filling tanks with specific liquids via number keys
    public static int LIQUID_FILL_TYPE = -1; // -1 = none, 0+ = index into debugLiquids array
    public static final ItemType[] DEBUG_LIQUIDS = {
            ItemType.Water,
            ItemType.RefinedOil,
            ItemType.CrudeOil,
            ItemType.IntermediateOilToColumn,
            ItemType.IntermediateOilToRefinery,
            ItemType.MoltenCopper
    };
```

**Explanation**:
- `LIQUID_FILL_TYPE`: Stores which liquid is currently selected (-1 means no liquid selected)
- `DEBUG_LIQUIDS[]`: Constant array mapping index to ItemType, indexed by key (0-5)

---

### STEP 2: Add Number Key Handlers in Debug Mode

**File**: `core/src/de/dakror/quarry/scenes/Game.java`

**Location**: In the `keyDown()` method, within the debug mode switch statement (lines 3981-4044)

**Find this block**:
```java
                case Keys.W:
                    for (int i = 0; i < layer.width; i++)
                        for (int j = 0; j < layer.height; j++)
                            layer.removeMeta(i, j, TileMeta.FOG_OF_WAR);
                    break;
            }
        }
```

**Replace with**:
```java
                case Keys.W:
                    for (int i = 0; i < layer.width; i++)
                        for (int j = 0; j < layer.height; j++)
                            layer.removeMeta(i, j, TileMeta.FOG_OF_WAR);
                    break;
                // Number key handlers for liquid fill mode (0-6)
                case Keys.NUM_0:
                case Keys.NUM_1:
                case Keys.NUM_2:
                case Keys.NUM_3:
                case Keys.NUM_4:
                case Keys.NUM_5:
                case Keys.NUM_6:
                    int liquidIndex = keycode - Keys.NUM_0;
                    if (liquidIndex < DEBUG_LIQUIDS.length) {
                        LIQUID_FILL_TYPE = liquidIndex;
                        ui.toast.show("Liquid Fill Mode: " + DEBUG_LIQUIDS[liquidIndex].title + " (click on tank/pipe to fill)");
                    }
                    break;
            }
        }
```

**Explanation**:
- Handles all number key cases (0-6) in one block
- Calculates the liquid index by subtracting `Keys.NUM_0` from the keycode
- Bounds checks to ensure we don't go out of array bounds
- Sets `LIQUID_FILL_TYPE` to the selected index
- Shows a toast message with the selected liquid name and instructions

---

### STEP 3: Add Click Handler for Liquid Fill

**File**: `core/src/de/dakror/quarry/scenes/Game.java`

**Location**: In the `touchDown()` method, around line 407-421

**Find this block**:
```java
                if (FILLMODE) {
                    if (s instanceof Storage) {
                        //((Storage) s).putBack(ItemType.CopperOre, 200);
                        for (ItemType t : ItemType.values) {
                            if (!t.categories.contains(ItemCategory.Fluid)
                                    && !t.categories.contains(ItemCategory.Abstract))
                                ((Storage) s).putBack(t, 100);
                        }
                    } else if (s instanceof Substation) {
                        ((Substation) s).acceptPower(100_000_000, NetworkStrength.PowerPole.maxPowerPerSecond);
                    } else if (s instanceof Tank) {
                        ((Tank) s).acceptFluid(ItemType.Water, 100_000_000, null);
                    }
                    FILLMODE = false;
                }
                if (structureDestroyMode) {
```

**Replace with**:
```java
                if (FILLMODE) {
                    if (s instanceof Storage) {
                        //((Storage) s).putBack(ItemType.CopperOre, 200);
                        for (ItemType t : ItemType.values) {
                            if (!t.categories.contains(ItemCategory.Fluid)
                                    && !t.categories.contains(ItemCategory.Abstract))
                                ((Storage) s).putBack(t, 100);
                        }
                    } else if (s instanceof Substation) {
                        ((Substation) s).acceptPower(100_000_000, NetworkStrength.PowerPole.maxPowerPerSecond);
                    } else if (s instanceof Tank) {
                        ((Tank) s).acceptFluid(ItemType.Water, 100_000_000, null);
                    }
                    FILLMODE = false;
                }
                // Debug liquid fill mode (number key activated)
                if (LIQUID_FILL_TYPE >= 0 && LIQUID_FILL_TYPE < DEBUG_LIQUIDS.length) {
                    ItemType liquid = DEBUG_LIQUIDS[LIQUID_FILL_TYPE];
                    if (s instanceof FluidTubeStructure) {
                        s.acceptFluid(liquid, 1000, null);
                    } else if (s instanceof Tank) {
                        ((Tank) s).acceptFluid(liquid, 100_000_000, null);
                    }
                    ui.toast.show("Filled " + s.getSchema().type.name() + " with " + liquid.title);
                    LIQUID_FILL_TYPE = -1; // Reset after one use
                }
                if (structureDestroyMode) {
```

**Explanation**:
- Checks if a liquid fill type has been selected (`LIQUID_FILL_TYPE >= 0`)
- Bounds checks to ensure the index is valid
- Gets the selected liquid from the `DEBUG_LIQUIDS` array
- Fills `FluidTubeStructure` instances (pipes) with 1,000 units
- Fills `Tank` instances with 100,000,000 units
- Shows a confirmation toast with the structure type and liquid name
- **Important**: Resets `LIQUID_FILL_TYPE = -1` after each fill so the user must press the number key again for the next fill

---

## Key Implementation Details

### Design Decisions

1. **Auto-reset after use**: Setting `LIQUID_FILL_TYPE = -1` after each fill prevents accidental fills. Users must explicitly select a liquid for each action.

2. **Toast notifications**: Provides clear feedback about:
   - Which liquid was selected (on key press)
   - What was filled and with what liquid (on click)

3. **Separate from existing FLUIDMODE**: This is completely independent from the existing `FLUIDMODE` (F key) which always uses Water or Molten Copper. This new system allows specific liquid selection.

4. **Bounds checking**: Always verify `liquidIndex < DEBUG_LIQUIDS.length` to prevent array index out of bounds.

5. **Works only in debug mode**: Number key handlers are inside the `if (Quarry.Q.version.equals("debug"))` block.

---

## Testing Checklist

- [ ] Run game with: `gradle desktop:run --args="debug"`
- [ ] Press **0** - verify "Water" toast appears
- [ ] Press **1** - verify "Refined Oil" toast appears
- [ ] Press **2-5** - verify each liquid shows correct toast
- [ ] Click a pipe with liquid selected - verify it fills with 1,000 units
- [ ] Click a tank with liquid selected - verify it fills with 100,000,000 units
- [ ] Press a number key twice - verify first fill happens, second click doesn't fill (auto-reset works)
- [ ] Press **6** or **7** - verify no error (bounds checking works)
- [ ] Verify existing FILLMODE (X key) still works normally
- [ ] Verify existing FLUIDMODE (F key) still works normally

---

## Related Features (Already Exist)

These features are already in the codebase and work independently:

- **FILLMODE (X key)**: Fills all non-fluid items in storage, power in substations, and water in tanks
- **FLUIDMODE (F key)**: Fills BrickChannels with Molten Copper, pipes with Water

The new number key system complements these by allowing specific liquid selection.

---

## Integration Notes

### Import Requirements
No new imports needed - all classes used are already imported:
- `ItemType` - already imported
- `FluidTubeStructure` - already imported
- `Tank` - already imported
- `Keys` - already imported from libGDX

### Compatibility
- Works alongside existing debug features without conflicts
- Doesn't affect normal gameplay (only active in debug mode)
- Doesn't affect other keybindings

---

## Reverting This Feature

If you need to remove this feature:
1. Delete the `LIQUID_FILL_TYPE` and `DEBUG_LIQUIDS` variables from Step 1
2. Delete the number key case handlers from Step 2
3. Delete the liquid fill click handler from Step 3
4. The codebase returns to the previous state with no side effects

---

## Future Enhancements

Possible improvements:
- Add visual indicator showing currently selected liquid (UI overlay)
- Allow toggling "sticky mode" where liquid selection persists across multiple clicks
- Add key to disable liquid fill mode (press ESC or press number key again)
- Add toast showing current liquid selection when hovering over tanks
- Store liquid preference in debug session cache





