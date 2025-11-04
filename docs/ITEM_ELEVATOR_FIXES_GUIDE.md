# Item Elevator Critical Fixes Guide

This guide documents two critical fixes for the Item Elevator system that took extensive debugging to resolve. Follow this guide carefully to reimplement these fixes.

---

## Fix #1: Item Elevator Direction Bug on Save/Load

### The Problem
When Item Elevators were saved and loaded, the output elevator (ItemElevatorExit) would change direction incorrectly. Specifically:
- On placement: All outputs pointed correctly inward toward the map
- On loading: All outputs changed direction to North (or some other incorrect direction)

### Root Cause Analysis
The issue occurred because:
1. `ItemElevator` and `ItemElevatorExit` store dock directions in a `customDockDirection` field
2. During save/load, `super.postLoad()` calls `setUpDirection()` 
3. `setUpDirection()` was resetting docks from the schema, overwriting the saved `customDockDirection`
4. The `customDockDirection` needed to be restored AFTER `super.postLoad()` completed

### The Solution

#### File: `core/src/de/dakror/quarry/structure/logistics/ItemElevator.java`

**Add a field to store custom dock direction:**
```java
// Store the dock direction so rotation doesn't reset it
protected Direction customDockDirection = null;
```

**Override setUpDirection to preserve custom direction:**
```java
@Override
public void setUpDirection(Direction upDirection) {
    this.upDirection = upDirection;
    setDirty();
    
    // Only reset docks from schema if we don't have a custom dock direction
    if (customDockDirection == null) {
        docks = schema.copyDocks(this);
    } else {
        // Otherwise keep custom docks - don't reset them
    }
}
```

**Set custom direction during placement:**
```java
@Override
public void onPlacement(boolean fromLoading) {
    super.onPlacement(fromLoading);

    if (!fromLoading && layer != null && isInput && targetLayerIndex >= 0) {
        // Ensure customDockDirection is set from current position
        // (in case it was lost during structure cloning)
        if (customDockDirection == null) {
            customDockDirection = getDockDirectionForEdge(x, y, layer.width, layer.height);
            docks = new Dock[] { new Dock(0, 0, customDockDirection, DockType.ItemIn) };
        }
        
        // Create exit and passthrough structures now that we're placed
        createElevatorStructures();
    }
}
```

**Save and load the custom direction:**
```java
@Override
public void saveData(CompoundTag tag) {
    super.saveData(tag);
    // ... other save code ...
    if (customDockDirection != null) {
        tag.putByte("customDockDir", (byte) customDockDirection.ordinal());
    }
}

@Override
public void loadData(CompoundTag tag) {
    super.loadData(tag);
    // ... other load code ...
    if (tag.containsKey("customDockDir")) {
        customDockDirection = Direction.values[tag.getByte("customDockDir")];
    }
}
```

**CRITICAL: Restore direction after postLoad:**
```java
@Override
public void postLoad(int loadX, int loadY) {
    // IMPORTANT: Store customDockDirection before super.postLoad()
    Direction savedCustomDir = customDockDirection;
    
    super.postLoad(loadX, loadY);
    
    // CRITICAL: Restore customDockDirection after super.postLoad()
    // because super.postLoad() may call setUpDirection() which could reset docks
    if (savedCustomDir != null) {
        customDockDirection = savedCustomDir;
        // Reapply the dock based on the loaded custom direction
        if (isInput) {
            docks = new Dock[] { new Dock(0, 0, customDockDirection, DockType.ItemIn) };
        }
    }
    // ... rest of postLoad logic ...
}
```

#### File: `core/src/de/dakror/quarry/structure/logistics/ItemElevatorExit.java`

**The MOST CRITICAL fix - in postLoad, restore docks directly from loaded customDockDirection:**

```java
@Override
public void postLoad(int loadX, int loadY) {
    // IMPORTANT: Store customDockDirection before super.postLoad()
    Direction savedCustomDir = customDockDirection;
    
    super.postLoad(loadX, loadY);
    
    // CRITICAL: Directly restore the dock from the loaded customDockDirection
    // Do NOT use docks[0].dir as it may have been reset by super.postLoad()
    if (savedCustomDir != null) {
        customDockDirection = savedCustomDir;
        docks = new Dock[] { new Dock(0, 0, customDockDirection, DockType.ItemOut) };
    }
    
    // ... rest of postLoad logic ...
}
```

**Key Insight:** The bug was subtle - we were trying to restore `customDockDirection` from `docks[0].dir`, but `docks[0].dir` had already been reset by `super.postLoad()`. We needed to save the value BEFORE calling super, then restore it AFTER.

---

## Fix #2: Blue Tinting System (Architecture & Implementation)

### The Problem
Item Elevators needed a blue tint to distinguish them from Item Lifts, but implementing this was complex because:
1. Initial attempts caused tint to "bleed" to other structures
2. Static mesh caching in `Chunk` didn't support per-structure tinting
3. Shader needed careful design to preserve original colors while adding tint
4. Icons (storage full, sleeping, etc.) were incorrectly receiving the tint

### Why This Was So Difficult

**Challenge #1: The Rendering Pipeline**
The game uses a two-phase rendering system:
- **Static Phase**: Structures are batched into a cached mesh for performance (`MeshBuilderDelegate`)
- **Dynamic Phase**: Icons, items, and effects are rendered each frame (`DepthSpriter`)

The static mesh system does NOT support `setTintColor()` - it's baked into the mesh at build time.

**Challenge #2: Global Shader State**
The `u_tintColor` shader uniform is GLOBAL. Once set, it affects ALL subsequent draw calls until changed. This caused "tint bleeding" where icons and other structures would receive the blue tint.

**Challenge #3: Blend Mode**
The shader blend mode needed to:
- Add blue tint without whitewashing bright colors
- Add blue tint without darkening dark colors
- Preserve original color contrast and brightness

### The Solution Architecture

#### Step 1: Create an Interface-Based System

**File: `core/src/de/dakror/quarry/structure/base/ISpecialRenderer.java`**
```java
/**
 * Interface for structures that require special rendering treatment.
 * 
 * TINTING SYSTEM OVERVIEW:
 * This interface enables structures to specify custom rendering properties, such as tint colors.
 * Structures implementing this interface are rendered separately from the standard batch
 * to ensure proper isolation of rendering effects.
 * 
 * HOW IT WORKS:
 * 1. Structures implementing ISpecialRenderer are EXCLUDED from the static mesh build
 * 2. They are rendered dynamically in Chunk.drawFrameStructures() with their custom tint
 * 3. Each structure is rendered with flush() before and after to isolate the tint effect
 * 4. After all special structures, the tint is reset to white (1,1,1,1) for normal rendering
 * 
 * ISOLATION:
 * The tinting is isolated using spriter.flush() calls:
 * - Flush before setting tint: Ensures previous batch doesn't get the tint
 * - Flush after drawing structure: Ensures tint doesn't bleed to next structure
 * - Reset to white after all special structures: Ensures normal rendering continues
 * 
 * IMPLEMENTATION STEPS:
 * 1. Have your structure implement ISpecialRenderer
 * 2. Implement getRenderingStyle() to return your desired RenderingStyle
 * 3. The Chunk rendering system will automatically handle the rest
 * 4. Do NOT call setTintColor() in your structure's draw() method
 */
public interface ISpecialRenderer {
    /**
     * Returns the rendering style for this structure.
     * This determines how the structure will be rendered (tint color, immediate mode, etc.)
     */
    RenderingStyle getRenderingStyle();
}
```

#### Step 2: Create a Configuration Class

**File: `core/src/de/dakror/quarry/structure/base/RenderingStyle.java`**
```java
/**
 * Encapsulates rendering properties for structures requiring special rendering.
 * 
 * ARCHITECTURE:
 * This class separates rendering configuration from structure logic, making it easy
 * to add new rendering styles without modifying core structure code.
 * 
 * CRITICAL DETAILS:
 * - tintColor: RGBA color where RGB adds tint, A controls blend strength
 * - renderImmediately: If true, structure is excluded from static mesh and rendered per-frame
 * 
 * HOW TO ADD NEW TINTS:
 * 1. Create a new static RenderingStyle constant (e.g., RED_TINT)
 * 2. Set tintColor and renderImmediately appropriately
 * 3. Have your structure's getRenderingStyle() return the new constant
 * 4. No other changes needed - the Chunk rendering system handles it automatically
 * 
 * TINT COLOR TUNING:
 * The shader uses an overlay blend mode: mix(original, original * tint * 2.0, alpha)
 * - For BLUE tint: Use high blue (0.8-1.0), lower red/green (0.4-0.6)
 * - Alpha (0.6-0.8) controls intensity: higher = more tint, lower = more subtle
 * - RGB values should NOT go below 0.4 or above 1.0 for best results
 * - Test with both bright and dark textures to ensure good contrast
 */
public class RenderingStyle {
    public final Color tintColor;
    public final boolean renderImmediately;
    
    public static final RenderingStyle DEFAULT = new RenderingStyle(
        new Color(1.0f, 1.0f, 1.0f, 1.0f), // White = no tint
        false // Render in static mesh
    );
    
    public static final RenderingStyle BLUE_TINT = new RenderingStyle(
        new Color(0.4f, 0.6f, 1.0f, 0.8f), // Blue tint with overlay blend
        true // Render immediately to apply tint
    );
    
    public RenderingStyle(Color tintColor, boolean renderImmediately) {
        this.tintColor = tintColor;
        this.renderImmediately = renderImmediately;
    }
}
```

#### Step 3: Modify Chunk Rendering

**File: `core/src/de/dakror/quarry/game/Chunk.java`**

**In drawStructures() - Exclude special structures from static mesh:**
```java
public void drawStructures(SpriteRenderer spriter) {
    synchronized (structures) {
        if (structureMeshNeedsRebuild || structureMesh == null) {
            // Build static mesh
            MeshBuilderDelegate builder = new MeshBuilderDelegate(spriter, 16, x, y);
            for (Structure<?> st : structures) {
                // CRITICAL: Exclude ISpecialRenderer structures from static mesh
                if (st instanceof ISpecialRenderer) {
                    continue; // Skip - will be rendered in drawFrameStructures
                }
                st.draw(builder);
            }
            // ... mesh building code ...
        }
        // Draw static mesh
        // ...
    }
}
```

**In drawFrameStructures() - Render special structures with proper isolation:**
```java
public void drawFrameStructures(SpriteRenderer spriter, boolean showTooltip, Structure<?> s) {
    // CRITICAL: Reset tint to white at start to ensure clean state
    spriter.setTintColor(1.0f, 1.0f, 1.0f, 1.0f);
    
    synchronized (structures) {
        // Pass 1: Render all ISpecialRenderer structures with their custom tints
        for (Structure<?> st : structures) {
            if (st instanceof ISpecialRenderer) {
                RenderingStyle style = ((ISpecialRenderer) st).getRenderingStyle();
                
                // CRITICAL: Flush before applying tint to isolate this structure
                spriter.flush();
                
                // Apply custom tint
                Color tint = style.tintColor;
                spriter.setTintColor(tint.r, tint.g, tint.b, tint.a);
                
                // Draw structure with tint
                st.draw(spriter);
                
                // CRITICAL: Flush after drawing to prevent tint bleeding
                spriter.flush();
                
                // Reset tint to white for next iteration
                spriter.setTintColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        
        // Pass 2: Render all frame elements (icons, items, etc.) with white tint
        // This ensures icons don't get the blue tint
        for (Structure<?> st : structures) {
            // Draw item notifications, state icons, etc.
            st.drawItemNotifications(spriter);
            st.drawStates(spriter);
            // ... other frame rendering ...
        }
    }
}
```

#### Step 4: Modify the Shader

**File: `android/assets/glsl/base.fs`**

**Add comprehensive documentation and overlay blend:**
```glsl
/**
 * TINTING SYSTEM DOCUMENTATION
 * =============================
 * 
 * This shader supports dynamic tinting for structures via the u_tintColor uniform.
 * 
 * BLEND MODE: Overlay Blend
 * - Preserves brightness and contrast of original colors
 * - Formula: mix(original, original * tint * 2.0, tint.alpha)
 * - Bright areas stay bright, dark areas stay dark
 * - The tint color shifts the hue without washing out or darkening
 * 
 * HOW TO ADD NEW TINTS:
 * 1. Define a new RenderingStyle constant in RenderingStyle.java
 * 2. Set appropriate RGBA values for your tint color
 * 3. Implement ISpecialRenderer on your structure
 * 4. Return your new RenderingStyle from getRenderingStyle()
 * 5. No shader changes needed!
 * 
 * TINT COLOR RECOMMENDATIONS:
 * - Blue tint: (0.4, 0.6, 1.0, 0.8) - Good for Item Elevators
 * - Red tint: (1.0, 0.4, 0.4, 0.8) - For danger/warning structures
 * - Green tint: (0.4, 1.0, 0.6, 0.8) - For eco/organic structures
 * - Alpha range: 0.5-0.9 (lower = subtle, higher = strong)
 * 
 * DEFAULT BEHAVIOR:
 * - White tint (1,1,1,1) = no tint applied
 * - The conditional check prevents tinting when tint is white
 */

uniform vec4 u_tintColor;

void main() {
    vec4 col = texture2D(u_texture, v_texCoords) * v_color;
    
    // Apply tint only if it's not the default white
    if(u_tintColor.b < 0.9 || u_tintColor.g < 0.9 || u_tintColor.r < 0.9) {
        // Overlay blend: preserves brightness and contrast
        col.rgb = mix(col.rgb, col.rgb * u_tintColor.rgb * 2.0, u_tintColor.a);
    }
    
    gl_FragColor = col;
}
```

**CRITICAL: The conditional check prevents tint bleeding!** Without this check, even the "reset to white" operation would apply a slight tint. This check ensures only intentional tints are applied.

#### Step 5: Implement Interface on Structures

**File: `core/src/de/dakror/quarry/structure/logistics/ItemElevator.java`**
```java
public class ItemElevator extends Structure<Schema> implements ISpecialRenderer {
    // ... existing code ...
    
    @Override
    public RenderingStyle getRenderingStyle() {
        return RenderingStyle.BLUE_TINT;
    }
    
    @Override
    public void draw(SpriteRenderer spriter) {
        // DO NOT call setTintColor here!
        // The tinting is handled by the Chunk rendering system
        
        // Just draw normally
        if (docks.length > 0)
            drawDocks(spriter);
        
        // ... rest of normal draw code ...
    }
}
```

**Apply to all elevator structures:**
- `ItemElevator.java`
- `ItemElevatorExit.java`
- `ItemElevatorPassthrough.java`

All three must implement `ISpecialRenderer` and return `RenderingStyle.BLUE_TINT`.

---

## Key Lessons Learned

### For Direction Bug:
1. **Order matters**: Save custom state BEFORE `super.postLoad()`, restore AFTER
2. **Don't rely on derived state**: `docks[0].dir` was being reset, so we saved the original `customDockDirection`
3. **Test loading thoroughly**: Placement worked fine, but loading revealed the bug

### For Tinting:
1. **Static meshes can't tint**: Structures with special rendering must be excluded from static mesh
2. **Global state is dangerous**: Always flush() before and after custom shader state changes
3. **Blend mode is crucial**: Overlay blend preserves colors better than multiply or add
4. **Isolate effects**: The conditional check in the shader prevents unwanted tinting
5. **Architecture over hacks**: Interface-based design makes the system extensible and maintainable

---

## Testing Checklist

### Direction Bug Fix:
- [ ] Place Item Elevators on all 4 map edges (North, South, East, West)
- [ ] Verify outputs point inward on placement
- [ ] Save the game
- [ ] Load the saved game
- [ ] Verify all outputs STILL point inward (not all North or East)

### Tinting Fix:
- [ ] Place Item Elevator - should show blue tint
- [ ] Verify Item Lift does NOT show blue tint
- [ ] Check that storage full icons are NOT blue
- [ ] Check that sleeping/blocked icons are NOT blue
- [ ] Check that items on conveyors are NOT blue
- [ ] Verify tint works on bright textures (steel, white)
- [ ] Verify tint works on dark textures (coal, stone)
- [ ] Save and load - tint should persist correctly

---

## Common Pitfalls to Avoid

1. **Don't call `setTintColor()` in structure `draw()` methods** - This interferes with the Chunk-level tinting system
2. **Don't forget to flush()** - Always flush before and after tint changes
3. **Don't skip the shader conditional** - Without it, resetting to white still applies a subtle tint
4. **Don't forget postLoad fixes** - Save state BEFORE super.postLoad(), restore AFTER
5. **Test with saves** - Many bugs only appear on save/load, not on fresh placement

---

## File Change Summary

### Direction Bug Fix:
- `core/src/de/dakror/quarry/structure/logistics/ItemElevator.java` - Add customDockDirection handling
- `core/src/de/dakror/quarry/structure/logistics/ItemElevatorExit.java` - Fix postLoad to restore direction

### Tinting System:
- `core/src/de/dakror/quarry/structure/base/ISpecialRenderer.java` - NEW FILE
- `core/src/de/dakror/quarry/structure/base/RenderingStyle.java` - NEW FILE
- `core/src/de/dakror/quarry/game/Chunk.java` - Modify drawStructures and drawFrameStructures
- `android/assets/glsl/base.fs` - Add overlay blend and conditional check
- `core/src/de/dakror/quarry/structure/logistics/ItemElevator.java` - Implement ISpecialRenderer
- `core/src/de/dakror/quarry/structure/logistics/ItemElevatorExit.java` - Implement ISpecialRenderer
- `core/src/de/dakror/quarry/structure/logistics/ItemElevatorPassthrough.java` - Implement ISpecialRenderer

---

Good luck with the reimplementation! Follow this guide carefully and you should be able to restore these fixes without the extensive debugging that was originally required.



