# Item Elevator Complete Reimplementation Guide

This is the master guide for reimplementing all Item Elevator fixes. Follow this guide in order.

---

## Overview

The Item Elevator system required five major fixes:

1. **Direction Bug on Save/Load** - Most complex, took many iterations
2. **Blue Tinting System** - Architectural challenge, extensive debugging
3. **Floor Selection Dialog Integration** - Required callback handling
4. **ArrayIndexOutOfBoundsException** - Critical crash fix
5. **Rotation Prevention** - Verification only

---

## Implementation Order

### Phase 1: Core Architecture (Do First)
These are the foundation that other fixes depend on:

1. **Blue Tinting System** (2-3 hours)
   - Creates `ISpecialRenderer` interface
   - Creates `RenderingStyle` class
   - Modifies `Chunk` rendering pipeline
   - Updates shader with overlay blend
   - **Guide:** `ITEM_ELEVATOR_FIXES_GUIDE.md` - Fix #2
   - **Difficulty:** High - requires understanding rendering pipeline

2. **Crash Fix** (30 minutes)
   - Adds bounds checking to `Layer.setCollision()`
   - Prevents crashes when loading saves
   - **Guide:** `ITEM_ELEVATOR_PLACEMENT_FIXES.md` - Fix #4
   - **Difficulty:** Low - straightforward bounds checking

### Phase 2: Functional Fixes (Do Second)
These implement the core Item Elevator functionality:

3. **Direction Bug Fix** (1-2 hours)
   - Adds `customDockDirection` field
   - Overrides `setUpDirection()` and `postLoad()`
   - Handles save/load correctly
   - **Guide:** `ITEM_ELEVATOR_FIXES_GUIDE.md` - Fix #1
   - **Difficulty:** Medium - subtle save/load timing issues

4. **Floor Selection Dialog** (1 hour)
   - Integrates dialog with placement system
   - Adds callback handling
   - **Guide:** `ITEM_ELEVATOR_PLACEMENT_FIXES.md` - Fix #3
   - **Difficulty:** Medium - requires callback pattern understanding

### Phase 3: Verification (Do Last)
5. **Rotation Check** (5 minutes)
   - Verify ItemElevator doesn't implement IRotatable
   - **Guide:** `ITEM_ELEVATOR_PLACEMENT_FIXES.md` - Fix #5
   - **Difficulty:** Trivial - verification only

---

## Detailed Guides

### Primary Guide: ITEM_ELEVATOR_FIXES_GUIDE.md
Contains the two most complex fixes:
- **Fix #1:** Direction bug (save/load)
- **Fix #2:** Blue tinting system

These were the hardest to debug and took the most iterations to get right.

### Secondary Guide: ITEM_ELEVATOR_PLACEMENT_FIXES.md
Contains the supporting fixes:
- **Fix #3:** Floor selection dialog integration
- **Fix #4:** ArrayIndexOutOfBoundsException crash fix
- **Fix #5:** Rotation prevention verification

---

## Quick Start Checklist

### Before You Begin
- [ ] Read both guide documents completely
- [ ] Understand the rendering pipeline (Chunk, static vs dynamic)
- [ ] Understand the save/load lifecycle (`postLoad()` timing)
- [ ] Familiarize yourself with shader syntax (GLSL)
- [ ] Set up a test save with Item Elevators on all 4 edges

### Implementation Checklist

#### Tinting System (2-3 hours)
- [ ] Create `ISpecialRenderer.java` interface with documentation
- [ ] Create `RenderingStyle.java` class with BLUE_TINT constant
- [ ] Modify `Chunk.drawStructures()` to exclude ISpecialRenderer from static mesh
- [ ] Modify `Chunk.drawFrameStructures()` to render special structures with isolation
- [ ] Update `base.fs` shader with overlay blend and conditional check
- [ ] Implement `ISpecialRenderer` on ItemElevator, ItemElevatorExit, ItemElevatorPassthrough
- [ ] Remove any `setTintColor()` calls from structure `draw()` methods
- [ ] Test: Place elevator, verify blue tint, verify icons are NOT blue
- [ ] Test: Save and load, verify tint persists

#### Direction Fix (1-2 hours)
- [ ] Add `customDockDirection` field to ItemElevator
- [ ] Override `setUpDirection()` to preserve custom direction
- [ ] Update `onPlacement()` to set custom direction
- [ ] Add save/load for `customDockDirection`
- [ ] Fix `postLoad()` to save BEFORE and restore AFTER super.postLoad()
- [ ] Apply same fixes to ItemElevatorExit (critical!)
- [ ] Test: Place elevators on all 4 edges, save, load, verify directions

#### Crash Fix (30 minutes)
- [ ] Open `Layer.java`, find `setCollision()` method
- [ ] Add `dockX` and `dockY` calculations
- [ ] Add bounds check for initial dock position
- [ ] Add bounds checks for all dock direction accesses
- [ ] Apply to all dock types (Power, BigPower, ItemIn/Out, FluidIn/Out)
- [ ] Test: Load save with edge elevators, should not crash

#### Dialog Integration (1 hour)
- [ ] Open `Game.java`, find `placeActiveElement()` method
- [ ] Add instanceof check for ItemElevator
- [ ] Show floor selection dialog instead of placing immediately
- [ ] Implement confirm callback to call `setTargetFloor()` then `placeStructure()`
- [ ] Implement cancel callback (empty)
- [ ] Test: Place elevator, dialog appears, confirm works, cancel works

#### Rotation Check (5 minutes)
- [ ] Verify ItemElevator does NOT implement IRotatable
- [ ] Verify ItemElevatorExit does NOT implement IRotatable
- [ ] Verify ItemElevatorPassthrough does NOT implement IRotatable
- [ ] Test: Select elevator, rotation button should NOT appear

---

## File Change Summary

### New Files (2)
```
core/src/de/dakror/quarry/structure/base/ISpecialRenderer.java
core/src/de/dakror/quarry/structure/base/RenderingStyle.java
```

### Modified Files (9)
```
android/assets/glsl/base.fs
core/src/de/dakror/quarry/game/Chunk.java
core/src/de/dakror/quarry/game/Layer.java
core/src/de/dakror/quarry/scenes/Game.java
core/src/de/dakror/quarry/structure/logistics/ItemElevator.java
core/src/de/dakror/quarry/structure/logistics/ItemElevatorExit.java
core/src/de/dakror/quarry/structure/logistics/ItemElevatorPassthrough.java
```

---

## Testing Protocol

### Quick Smoke Test (5 minutes)
1. Launch game
2. Select Item Elevator from build menu
3. Place at North edge
4. Floor dialog should appear
5. Select destination floor
6. Elevator should place with blue tint
7. Exit should appear on destination floor
8. Save game
9. Load game
10. No crash, elevator direction correct, tint still blue

### Comprehensive Test (30 minutes)

#### Tinting Tests
- [ ] Place elevator - has blue tint
- [ ] Place item lift - NO blue tint (white)
- [ ] Check storage full icon - NO blue tint
- [ ] Check sleeping icon - NO blue tint
- [ ] Check blocked icon - NO blue tint
- [ ] Check items on conveyors - NO blue tint
- [ ] Test with bright textures (steel, white)
- [ ] Test with dark textures (coal, stone)
- [ ] Save and load - tint persists

#### Direction Tests
- [ ] Place on North edge - dock points South (inward)
- [ ] Place on South edge - dock points North (inward)
- [ ] Place on East edge - dock points West (inward)
- [ ] Place on West edge - dock points East (inward)
- [ ] Save game with all 4 elevators
- [ ] Load game
- [ ] All 4 elevators still point inward
- [ ] Feed items - they transport correctly

#### Dialog Tests
- [ ] Click to place - dialog appears
- [ ] Shows current floor correctly
- [ ] +1 button works
- [ ] -1 button works
- [ ] +10 button works
- [ ] -10 button works
- [ ] Can't select same floor (red text)
- [ ] Can't select invalid floor (red text)
- [ ] Confirm button works
- [ ] Cancel (X) works
- [ ] Cancel (click outside) works

#### Crash Tests
- [ ] Place elevator at corner (x=0, y=0)
- [ ] Place elevator at corner (x=max, y=0)
- [ ] Place elevator at corner (x=0, y=max)
- [ ] Place elevator at corner (x=max, y=max)
- [ ] Save game
- [ ] Load game - NO CRASH
- [ ] All elevators work correctly

#### Rotation Tests
- [ ] Select elevator for placement
- [ ] Rotation button should NOT appear
- [ ] Press R key - should NOT rotate
- [ ] Confirm direction determined by edge

---

## Common Pitfalls & Solutions

### Tinting Issues

**Problem:** Icons are blue  
**Solution:** Ensure flush() is called before and after each tinted structure in `drawFrameStructures()`

**Problem:** Tint bleeds to other structures  
**Solution:** Add the conditional check in base.fs shader to skip white tint

**Problem:** Tint makes colors too dark  
**Solution:** Use overlay blend (mix with multiply by 2.0), not simple multiply

**Problem:** Static mesh structures don't tint  
**Solution:** Exclude ISpecialRenderer from static mesh in `drawStructures()`

### Direction Issues

**Problem:** Directions wrong after loading  
**Solution:** Save customDockDirection BEFORE super.postLoad(), restore AFTER

**Problem:** Exit elevator direction wrong  
**Solution:** Restore docks directly from customDockDirection, NOT from docks[0].dir

**Problem:** Directions change on rotation  
**Solution:** Don't call super.setUpDirection() if customDockDirection is set

### Dialog Issues

**Problem:** Dialog doesn't appear  
**Solution:** Check instanceof ItemElevator is in correct place in placeActiveElement()

**Problem:** Confirm doesn't place  
**Solution:** Ensure setTargetFloor() is called before placeStructure()

**Problem:** Exit elevator doesn't appear  
**Solution:** Verify targetLayerIndex is set before onPlacement() is called

### Crash Issues

**Problem:** Crash on loading saves  
**Solution:** Add bounds checking to ALL dock types in setCollision()

**Problem:** Crash on edge placement  
**Solution:** Check both dock position AND dock direction position

---

## Performance Notes

### Tinting Performance Impact
- **Static mesh:** Very fast (cached, no per-frame overhead)
- **ISpecialRenderer:** Slightly slower (rendered per frame with flush calls)
- **Impact:** Negligible - Item Elevators are rare, few per map
- **Optimization:** Already optimal - uses immediate mode only when needed

### When to Use ISpecialRenderer
Use for structures that need:
- Custom tinting/coloring
- Per-frame shader effects
- Special rendering state

Don't use for:
- Regular structures (use static mesh)
- Structures with many instances (performance impact)

---

## Future Extensibility

### Adding New Tints

To add a red tint for a different structure:

1. Add to RenderingStyle.java:
```java
public static final RenderingStyle RED_TINT = new RenderingStyle(
    new Color(1.0f, 0.4f, 0.4f, 0.8f), // Red tint
    true
);
```

2. Implement ISpecialRenderer on your structure:
```java
@Override
public RenderingStyle getRenderingStyle() {
    return RenderingStyle.RED_TINT;
}
```

3. Done! No shader changes needed, Chunk handles it automatically.

### Adding New Rendering Modes

To add more complex rendering (not just tinting):

1. Add fields to RenderingStyle:
```java
public final boolean useCustomShader;
public final String customShaderName;
```

2. Update Chunk.drawFrameStructures() to check and apply:
```java
if (style.useCustomShader) {
    spriter.setShader(loadShader(style.customShaderName));
}
```

3. Document in RenderingStyle.java for future developers

---

## Debugging Tips

### Tinting Debug
Add to `Chunk.drawFrameStructures()`:
```java
System.out.println("[TINT] Rendering " + st.getClass().getSimpleName() + 
    " with tint: " + tint);
```

### Direction Debug
Add to `ItemElevator.postLoad()`:
```java
System.out.println("[DIR] Loaded customDockDirection: " + customDockDirection);
System.out.println("[DIR] Dock after restore: " + docks[0].dir);
```

### Placement Debug
Add to `Game.placeActiveElement()`:
```java
System.out.println("[PLACE] Placing ItemElevator at (" + 
    activeStructure.x + ", " + activeStructure.y + ")");
System.out.println("[PLACE] Target floor: " + 
    ((ItemElevator)activeStructure).targetLayerIndex);
```

---

## Success Criteria

You'll know the implementation is complete when:

✅ Item Elevators place with blue tint  
✅ Floor selection dialog appears on placement  
✅ Exit elevators appear on destination floor  
✅ Directions are correct on all 4 map edges  
✅ Saving and loading preserves directions  
✅ No crash when loading edge elevators  
✅ Icons and conveyors are NOT blue  
✅ Tint works on all texture types  
✅ No tint bleeding to other structures  
✅ Rotation is NOT available during placement  

---

## Time Estimate

**Total Time:** 5-7 hours

Breakdown:
- Reading guides: 1 hour
- Tinting system: 2-3 hours
- Direction fix: 1-2 hours
- Dialog integration: 1 hour
- Crash fix: 30 minutes
- Testing: 1 hour
- Debugging issues: 30-60 minutes buffer

**Important:** Don't rush the tinting system. It's the foundation for the visual distinction and requires careful attention to the rendering pipeline.

---

## Support Resources

### Key Concepts to Understand
1. **LibGDX Rendering:** Batching, flushing, shader state
2. **GLSL Shaders:** Uniforms, fragment shaders, blend modes
3. **Java Save/Load:** Lifecycle, timing, super.method() order
4. **Callback Pattern:** Interface implementation, anonymous classes

### Related Game Systems
- **Chunk:** Map rendering, static mesh caching
- **Layer:** Collision detection, structure management
- **Structure:** Base class, lifecycle methods
- **SpriteRenderer:** Batching, shader management

---

## Final Notes

These fixes represent significant debugging effort - over 200 tool calls and many iterations. The guides document not just what to do, but WHY it works and what doesn't work.

Follow the guides carefully, test thoroughly, and don't skip the flush() calls or bounds checking. These small details are critical to system stability.

Good luck with the reimplementation!

---

*Last Updated: Current Session*  
*Status: Fully tested and working*  
*Difficulty: High (Tinting), Medium (Direction, Dialog), Low (Crash)*




