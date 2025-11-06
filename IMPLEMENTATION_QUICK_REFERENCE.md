# Debug Liquid Fill - Quick Reference Card

## TL;DR - What to Implement
Add number key (0-5) debug shortcuts to fill tanks/pipes with different liquids.

## Three Simple Changes to Game.java

### Change #1: Add Variables (After line 1554)
```java
public static int LIQUID_FILL_TYPE = -1;
public static final ItemType[] DEBUG_LIQUIDS = {
    ItemType.Water,
    ItemType.RefinedOil,
    ItemType.CrudeOil,
    ItemType.IntermediateOilToColumn,
    ItemType.IntermediateOilToRefinery,
    ItemType.MoltenCopper
};
```

### Change #2: Add Key Handlers (After line 4043, in debug switch)
```java
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

### Change #3: Add Click Handler (After FILLMODE block, line 421)
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

## Key Mappings
- **0** = Water (1k to pipes, 100M to tanks)
- **1** = Refined Oil (1k to pipes, 100M to tanks)
- **2** = Crude Oil (1k to pipes, 100M to tanks)
- **3** = Intermediate Oil → Column (1k to pipes, 100M to tanks)
- **4** = Intermediate Oil → Refinery (1k to pipes, 100M to tanks)
- **5** = Molten Copper (1k to pipes, 100M to tanks)

## How It Works
1. Press number key (0-5) in debug mode
2. Toast shows selected liquid
3. Click on any tank or pipe to fill it
4. Toast confirms fill
5. System auto-resets for next selection

## Testing
```bash
gradle desktop:run --args="debug"
```
Then press 0-5 and click structures.

## No New Imports Needed
All classes already imported in Game.java

## See Also
- `DEBUG_LIQUID_FILL_IMPLEMENTATION_GUIDE.md` - Full detailed guide
- Related: FILLMODE (X key), FLUIDMODE (F key)



