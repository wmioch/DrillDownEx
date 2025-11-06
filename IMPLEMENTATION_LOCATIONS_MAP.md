# Debug Liquid Fill - Implementation Locations Map

## File: core/src/de/dakror/quarry/scenes/Game.java

### Location 1: Debug Variables Section

**Where**: After line 1554  
**Context**: After `public static boolean SMOOTH_CAMERA = false;`

```
Line 1553: public static boolean SMOOTH_CAMERA = false;
Line 1554: [INSERT HERE - Add 6 new lines]
Line 1555: private static final Pattern fileRegex = ...
```

**What to insert**:
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

**Result**: 
- Line 1555 becomes blank
- Lines 1556-1562: New debug variables
- Line 1563 onwards: Rest of file shifts down

---

### Location 2: Number Key Handlers in Debug Mode

**Where**: Inside the `keyDown()` method, in the debug keybinds switch statement  
**Before**: After the `Keys.W` case (around line 4043)  
**Context**: The method starts around line 3979, debug switch around line 3982

**Find this pattern**:
```java
Line 4039:                case Keys.W:
Line 4040:                    for (int i = 0; i < layer.width; i++)
Line 4041:                        for (int j = 0; j < layer.height; j++)
Line 4042:                            layer.removeMeta(i, j, TileMeta.FOG_OF_WAR);
Line 4043:                    break;
Line 4044:            }  // <- End of debug switch
Line 4045:        }      // <- End of debug if
Line 4046:
Line 4047:        switch (keycode) {  // <- Start of normal mode switch
```

**What to insert** (Between lines 4043 and 4044):
```java
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
```

**Result**: Debug keybinds now handle number keys, normal mode switch unaffected

---

### Location 3: Click Handler for Liquid Fill

**Where**: Inside the `touchDown()` method, in structure click handling  
**Before**: After FILLMODE block, before structureDestroyMode check (around line 421)  
**Context**: Method starts around line 260

**Find this pattern**:
```java
Line 407:            if (FILLMODE) {
Line 408:                if (s instanceof Storage) {
Line 409:                    //((Storage) s).putBack(ItemType.CopperOre, 200);
Line 410:                    for (ItemType t : ItemType.values) {
Line 411:                        if (!t.categories.contains(ItemCategory.Fluid)
Line 412:                                && !t.categories.contains(ItemCategory.Abstract))
Line 413:                            ((Storage) s).putBack(t, 100);
Line 414:                    }
Line 415:                } else if (s instanceof Substation) {
Line 416:                    ((Substation) s).acceptPower(100_000_000, NetworkStrength.PowerPole.maxPowerPerSecond);
Line 417:                } else if (s instanceof Tank) {
Line 418:                    ((Tank) s).acceptFluid(ItemType.Water, 100_000_000, null);
Line 419:                }
Line 420:                FILLMODE = false;
Line 421:            }
Line 422:            if (structureDestroyMode) {  // <- Next check
```

**What to insert** (Between lines 421 and 422):
```java
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
```

**Result**: Structure clicks now trigger liquid fill if a liquid type is selected

---

## Summary of Changes

| Change | File | Approx. Line | Type | Size |
|--------|------|-------------|------|------|
| 1 | Game.java | 1554 | New variables | 9 lines |
| 2 | Game.java | 4043 | New key handlers | 13 lines |
| 3 | Game.java | 421 | New click handler | 10 lines |
| **Total** | **1 file** | **Various** | **32 lines** |

---

## Verification Steps

1. **After adding variables**:
   - Line should compile with no errors
   - `LIQUID_FILL_TYPE` and `DEBUG_LIQUIDS` should be recognized

2. **After adding key handlers**:
   - Should still compile
   - Debug keybinds should include number keys 0-6
   - Normal mode keybinds unaffected

3. **After adding click handler**:
   - Should compile with no errors
   - touchDown method should handle liquid fills
   - Other click handlers still work normally

---

## Rollback Instructions

If you need to remove these changes:

1. **Delete Location 1** (lines 1555-1562, or however many lines were added)
   - Remove all 9 lines added
   
2. **Delete Location 2** (the number key cases)
   - Remove all 13 lines with number key handlers
   - Keep the closing `}` and `}`
   
3. **Delete Location 3** (the liquid fill click handler)
   - Remove all 10 lines of the liquid fill block

---

## Common Issues & Fixes

| Issue | Cause | Fix |
|-------|-------|-----|
| Compilation error: "LIQUID_FILL_TYPE not found" | Location 1 not added | Add variables from Location 1 |
| Number keys don't work | Location 2 not added | Add key handlers from Location 2 |
| Clicking doesn't fill | Location 3 not added | Add click handler from Location 3 |
| "cannot find symbol: ItemType" | Import issue | Already imported in Game.java |
| Array index out of bounds | Missing bounds check | Verify `liquidIndex < DEBUG_LIQUIDS.length` in Location 2 |
| Fills only one structure per key press | Working as designed | User must press key again for next structure |

---

## Related Existing Code Patterns

These patterns should be familiar if reading Game.java:

- **FILLMODE implementation** (lines 407-421) - Similar structure-checking pattern
- **FLUIDMODE implementation** (lines 402-406) - Similar fluid acceptance pattern  
- **Debug key handlers** (lines 3983-4043) - Similar switch statement pattern
- **Toast notifications** - Used throughout UI feedback

The new code follows these same patterns for consistency.



