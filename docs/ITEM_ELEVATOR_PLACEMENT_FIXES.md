# Item Elevator Placement & Dialog Fixes

This guide covers the additional fixes needed for Item Elevator placement functionality.

---

## Fix #3: Floor Selection Dialog Integration

### The Problem
When clicking to place an Item Elevator, the floor selection dialog never appeared. The structure was attempting to place immediately without letting the user select a destination floor.

### Root Cause
The placement logic in `Game.placeActiveElement()` had no special handling for ItemElevator. It treated it like any other structure and placed it immediately.

### The Solution

**File: `core/src/de/dakror/quarry/scenes/Game.java`**

In the `placeActiveElement()` method, around line 1320, add special handling for ItemElevator:

```java
@Override
protected void placeActiveElement() {
    if (endB.x > -1) {
        // ... draggable structure placement ...
    } else {
        if (pasteMode) {
            // ... paste mode logic ...
        } else {
            // ... conveyor rotation logic ...
            
            // remove conveyor when building conveyor bridge on top
            if (activeStructure instanceof ConveyorBridge) {
                Structure<?> x = layer.getStructure(activeStructure.x, activeStructure.y);
                if (x != null && x.getSchema().type == StructureType.Conveyor) {
                    layer.removeStructure(x);
                }
            }

            // ===== ADD THIS SECTION =====
            // Handle Item Elevator - show floor selection dialog
            if (activeStructure instanceof de.dakror.quarry.structure.logistics.ItemElevator) {
                de.dakror.quarry.structure.logistics.ItemElevator elevator = 
                    (de.dakror.quarry.structure.logistics.ItemElevator) activeStructure;
                // Show floor selection dialog instead of placing directly
                ui.floorSelectionDialog.show(layer.getIndex(), activeStructure.x, activeStructure.y,
                    new de.dakror.common.Callback<Integer>() {
                        @Override
                        public void call(Integer selectedFloor) {
                            // Confirm callback: user selected a floor
                            if (elevator.setTargetFloor(selectedFloor)) {
                                // Floor selection was valid, now place the structure
                                placeStructure(layer, (Structure<?>) activeStructure.clone());
                                if (endA.x > -1) {
                                    endA.set(-1, 0);
                                    endB.set(-1, 0);
                                    activeStructure.x = -1;
                                    activeStructure.y = 0;
                                }
                            }
                        }
                    },
                    new de.dakror.common.Callback<Boolean>() {
                        @Override
                        public void call(Boolean cancelled) {
                            // Cancel callback - do nothing, user cancelled placement
                        }
                    }
                );
            } else {
                // ===== END NEW SECTION =====
                
                placeStructure(layer, (Structure<?>) activeStructure.clone());

                if (endA.x > -1) {
                    endA.set(-1, 0);
                    endB.set(-1, 0);
                    activeStructure.x = -1;
                    activeStructure.y = 0;
                }
            } // <-- Add closing brace for the else
        }
    }
}
```

### How It Works

1. **Intercept placement**: When user clicks to place, check if it's an ItemElevator
2. **Show dialog**: Instead of placing immediately, show the floor selection dialog
3. **Confirm callback**: When user confirms a floor:
   - Call `elevator.setTargetFloor(selectedFloor)` to validate and store the selection
   - If valid, place the structure (which triggers `onPlacement()`)
   - `onPlacement()` creates the exit elevator and passthroughs automatically
4. **Cancel callback**: If user cancels, do nothing (structure remains in placement mode)

### Key Points

- The dialog is modal - user must either confirm or cancel
- `setTargetFloor()` validates the floor (not in fog of war, not occupied, etc.)
- The actual elevator structures are created in `ItemElevator.onPlacement()` after the floor is set
- Use anonymous inner classes for callbacks (Java 7 style) for compatibility

---

## Fix #4: ArrayIndexOutOfBoundsException on Load

### The Problem
Loading a save game with Item Elevators placed near map edges caused:
```
java.lang.ArrayIndexOutOfBoundsException: Index 4123 out of bounds for length 4096
    at de.dakror.quarry.game.Layer.setCollision(Layer.java:578)
```

### Root Cause
When Item Elevators are placed at map edges, their docks point outward (off the map). The `Layer.setCollision()` method didn't check bounds before accessing the flags array, causing an array index out of bounds.

### The Solution

**File: `core/src/de/dakror/quarry/game/Layer.java`**

In the `setCollision()` method, add comprehensive bounds checking:

```java
protected void setCollision(Structure<?> s, boolean colliding) {
    for (int i = 0; i < s.getWidth(); i++) {
        int j = (s.x + i) * height + s.y;
        if (s instanceof ITube) {
            if (colliding) {
                flags[j] |= FLAG_TUBE_COLLISION;
            } else {
                flags[j] &= ~FLAG_TUBE_COLLISION;
            }
        } else {
            if (colliding) {
                for (int k = 0; k < s.getHeight(); k++)
                    flags[j + k] |= FLAG_STRUCTURE_COLLISION;
            } else {
                for (int k = 0; k < s.getHeight(); k++)
                    flags[j + k] &= ~FLAG_STRUCTURE_COLLISION;
            }
        }
    }
    
    // ===== ADD BOUNDS CHECKING FOR DOCKS =====
    for (Dock d : s.getDocks()) {
        // Calculate absolute dock coordinates
        int dockX = s.x + d.x;
        int dockY = s.y + d.y;
        
        // Check bounds for initial dock position
        if (dockX < 0 || dockX >= width || dockY < 0 || dockY >= height) {
            continue; // Skip if dock position is out of bounds
        }
        
        if (d.type == DockType.Power) {
            if (colliding) {
                flags[dockX * height + dockY] |= FLAG_POWER_DOCK_COLLISION;
            } else {
                flags[dockX * height + dockY] &= ~FLAG_POWER_DOCK_COLLISION;
            }
        } else if (d.type == DockType.BigPower) {
            if (colliding) {
                flags[dockX * height + dockY] |= FLAG_POWER_DOCK_COLLISION;
                // Check bounds for dock direction
                int dockDirX = dockX + d.dir.dx;
                int dockDirY = dockY + d.dir.dy;
                if (dockDirX >= 0 && dockDirX < width && dockDirY >= 0 && dockDirY < height) {
                    flags[dockDirX * height + dockDirY] |= FLAG_POWER_DOCK_COLLISION | FLAG_STRUCTURE_COLLISION;
                }
            } else {
                flags[dockX * height + dockY] &= ~FLAG_POWER_DOCK_COLLISION;
                // Check bounds for dock direction
                int dockDirX = dockX + d.dir.dx;
                int dockDirY = dockY + d.dir.dy;
                if (dockDirX >= 0 && dockDirX < width && dockDirY >= 0 && dockDirY < height) {
                    flags[dockDirX * height + dockDirY] &= ~(FLAG_POWER_DOCK_COLLISION | FLAG_STRUCTURE_COLLISION);
                }
            }
        } else if (d.type == DockType.ItemIn || d.type == DockType.ItemOut) {
            // Check bounds for dock direction
            int dockDirX = dockX + d.dir.dx;
            int dockDirY = dockY + d.dir.dy;
            if (dockDirX >= 0 && dockDirX < width && dockDirY >= 0 && dockDirY < height) {
                if (colliding) {
                    flags[dockDirX * height + dockDirY] |= FLAG_ITEM_DOCK_COLLISION;
                } else {
                    flags[dockDirX * height + dockDirY] &= ~FLAG_ITEM_DOCK_COLLISION;
                }
            }
        } else if (d.type == DockType.FluidIn || d.type == DockType.FluidOut) {
            // Check bounds for dock direction
            int dockDirX = dockX + d.dir.dx;
            int dockDirY = dockY + d.dir.dy;
            if (dockDirX >= 0 && dockDirX < width && dockDirY >= 0 && dockDirY < height) {
                if (colliding) {
                    flags[dockDirX * height + dockDirY] |= FLAG_FLUID_DOCK_COLLISION;
                } else {
                    flags[dockDirX * height + dockDirY] &= ~FLAG_FLUID_DOCK_COLLISION;
                }
            }
        }
    }
}
```

### Why This Matters

- Item Elevators MUST be placed at map edges (requirement)
- Docks naturally point off the map (toward the edge)
- The collision system tried to mark docks that don't exist on the map
- This fix makes the system robust for any edge-placed structure

### Benefits

- Fixes crash on loading saves with Item Elevators
- Prevents similar crashes for any future edge-placed structures
- Maintains correct collision detection for in-bounds docks

---

## Fix #5: Rotation Prevention

### The Problem
Item Elevators were allowing rotation during placement, which doesn't make sense since their direction is determined by which map edge they're placed on.

### The Solution
**No code change needed!** Item Elevator does NOT implement the `IRotatable` interface, so rotation was never actually allowed. The rotation button shouldn't appear.

If rotation is somehow enabled, verify that:

1. `ItemElevator` does NOT implement `IRotatable`
2. `ItemElevatorExit` does NOT implement `IRotatable`
3. `ItemElevatorPassthrough` does NOT implement `IRotatable`

The `showOrHideRotateButton()` method in `GameUi.java` checks for `IRotatable` before showing the button.

---

## Integration Notes

### Callback Pattern
The `de.dakror.common.Callback<T>` interface is used throughout the codebase:
```java
public interface Callback<T> {
    void call(T value);
}
```

Use anonymous inner classes (Java 7 style) for callbacks:
```java
new Callback<Integer>() {
    @Override
    public void call(Integer value) {
        // Handle callback
    }
}
```

### FloorSelectionDialog API
```java
public void show(
    int sourceFloor,        // Current layer index
    int x, int y,           // Structure position
    Callback<Integer> confirm,  // Called with selected floor
    Callback<Boolean> cancel    // Called if user cancels
)
```

The dialog:
- Shows current floor
- Lets user navigate floors with +1/-1 and +10/-10 buttons
- Validates floor selection (not same as source, not in fog, not occupied)
- Calls confirm callback with selected floor index
- Calls cancel callback if user clicks X or outside dialog

---

## Testing Checklist

After implementing these fixes:

### Placement Flow:
- [ ] Select Item Elevator from build menu
- [ ] Move cursor to map edge - should show green preview
- [ ] Click to place - floor selection dialog should appear
- [ ] Dialog shows current floor and allows navigation
- [ ] Clicking confirm places the elevator
- [ ] Exit elevator appears on destination floor
- [ ] Passthroughs appear on floors in between

### Edge Placement:
- [ ] Place on North edge - dock should point South
- [ ] Place on South edge - dock should point North
- [ ] Place on East edge - dock should point West
- [ ] Place on West edge - dock should point East

### Save/Load:
- [ ] Place elevators on all 4 edges
- [ ] Save the game
- [ ] Load the game - should not crash
- [ ] All elevators should maintain correct direction
- [ ] Items should flow correctly after loading

### Cancel:
- [ ] Click to place Item Elevator
- [ ] Dialog appears
- [ ] Click X or outside dialog to cancel
- [ ] Structure should still be in placement mode
- [ ] Can place again or cancel with Esc

---

## Common Issues

### Issue: Dialog doesn't appear
**Check:** Is the code in the right place in `placeActiveElement()`? It should be BEFORE the default `placeStructure()` call.

### Issue: Elevator places without exit
**Check:** Is `setTargetFloor()` being called before `placeStructure()`? The exit is created in `onPlacement()` which requires `targetLayerIndex` to be set.

### Issue: Crash on loading edge elevators
**Check:** Did you add bounds checking to ALL dock types in `Layer.setCollision()`? Missing even one can cause crashes.

### Issue: Dialog appears but confirm doesn't work
**Check:** Is the callback actually calling `placeStructure()`? Verify the callback implementation matches the example.

---

## File Summary

Files modified for these fixes:
- `core/src/de/dakror/quarry/scenes/Game.java` - Add floor dialog logic
- `core/src/de/dakror/quarry/game/Layer.java` - Add bounds checking

Files used (already exist):
- `core/src/de/dakror/quarry/ui/FloorSelectionDialog.java` - The dialog itself
- `core/src/de/dakror/quarry/structure/logistics/ItemElevator.java` - The structure
- `commons/core/src/de/dakror/common/Callback.java` - The callback interface

---

These fixes work together with the Direction and Tinting fixes from the main guide. All four systems must be implemented for full Item Elevator functionality.





