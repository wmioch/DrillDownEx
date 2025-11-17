# Texture Implementation Patterns & Advanced Tips

**Real-world patterns used in Drill Down and advanced techniques for texture management.**

---

## Common Implementation Patterns

### Pattern 1: Adding a New Item Type

This is the most straightforward pattern.

**Files to modify:**
1. `Development/Textures/item_[name].png` ← Create texture
2. `core/src/de/dakror/quarry/game/Item.java` ← Add to ItemType enum
3. `android/assets/i18n/TheQuarry_en.properties` ← Add localization (optional)

**Example: Petroleum Coke Fuel**

Step 1: Create PNG
```
Development/Textures/item_petroleum_coke.png (64×64)
└─ Art style: Small square icon, similar to other fuel items
```

Step 2: Add to ItemType
```java
// File: core/src/de/dakror/quarry/game/Item.java
public enum ItemType {
    // ... existing items ...
    
    PetroleumCoke(72, "item_petroleum_coke", 500, ItemCategory.CoalFuel),
    
    // ... rest of items ...
}
```

Step 3: Run TexturePacker
```bash
cd D:\Projects\DrillDown
./gradlew.bat desktop:run -Pargs=textures
```

Step 4: Localization (optional)
```properties
# File: android/assets/i18n/TheQuarry_en.properties
item.PetroleumCoke = Petroleum Coke (Fuel)
```

**Result:** Item appears in inventory with correct texture and name

---

### Pattern 2: Adding a New Building/Structure

More complex because it requires multiple files.

**Files to modify:**
1. `Development/Textures/structure_[name].png` ← Create texture
2. `core/src/de/dakror/quarry/structure/base/StructureType.java` ← Add enum
3. `core/src/de/dakror/quarry/structure/producer/[YourStructure].java` ← Create class
4. `core/src/de/dakror/quarry/scenes/GameUi.java` ← Add to build menu
5. `android/assets/i18n/TheQuarry_en.properties` ← Add localization

**Example: Coker Building**

Step 1: Create PNG
```
Development/Textures/structure_coker.png (128×128)
└─ Art style: Top-down isometric, matches other buildings
```

Step 2: Add to StructureType enum
```java
// File: core/src/de/dakror/quarry/structure/base/StructureType.java
public enum StructureType {
    // ... existing structures ...
    
    Coker(214, Coker.class),  // ID 214 (next available)
    
    // ... rest of structures ...
}
```

Step 3: Create Structure Class
```java
// File: core/src/de/dakror/quarry/structure/producer/Coker.java
public class Coker extends ProducerStructure {
    public Coker(int x, int y) {
        super(x, y);
    }

    @Override
    protected ProducerSchema createSchema() {
        return new ProducerSchema()
            .setSize(4, 4)
            .setTexture("structure_coker")
            .setCost(new Items(
                ItemType.SteelPlate, 50,
                ItemType.CopperPlate, 30,
                ItemType.IronPlate, 20
            ))
            .setRecipe(
                "PetroleumCoke",
                new Recipe(
                    new Items(ItemType.RefinedOil, 1),
                    new Items(ItemType.PetroleumCoke, 1),
                    ScienceType.AdvancedFuelProcessing
                )
            )
            .setBuildTime(60);
    }
}
```

Step 4: Add to Build Menu
```java
// File: core/src/de/dakror/quarry/scenes/GameUi.java
// In the buildMenuItem method for water/liquids tab:
buildMenuItem(wate, new Coker(-1, 0));
```

Step 5: Run TexturePacker
```bash
./gradlew.bat desktop:run -Pargs=textures
```

Step 6: Localization
```properties
# File: android/assets/i18n/TheQuarry_en.properties
structure.coker = Coker
structure.coker.desc = Processes refined oil through delayed coking to produce dense petroleum coke fuel pellets.
structure.coker.recipe.petroleumcoke = Converts refined oil into high-density petroleum coke through thermal cracking and coking processes.
```

**Result:** New building appears in build menu with correct texture, cost, and recipes

---

### Pattern 3: Adding a New Science/Research Tech

**Files to modify:**
1. `Development/Textures/icon_[name].png` ← Create icon texture
2. `core/src/de/dakror/quarry/game/Science.java` ← Add to ScienceType enum
3. `android/assets/i18n/TheQuarry_en.properties` ← Add localization

**Example: Advanced Fuel Processing**

Step 1: Create Icon PNG
```
Development/Textures/icon_advanced_fuel_processing.png (64×64)
└─ Art style: Tech tree icon, represents fuel/chemistry
```

Step 2: Add to ScienceType
```java
// File: core/src/de/dakror/quarry/game/Science.java
public enum ScienceType {
    // ... existing sciences ...
    
    AdvancedFuelProcessing(25, "icon_advanced_fuel_processing",
        new Items(
            ItemType.RefinedOilBarrel, 50,
            ItemType.AdvancedMachineFrame, 10,
            ItemType.Chip, 5,
            ItemType.SteelCable, 100
        ),
        95,  // Research time
        OilProcessing,  // Prerequisites...
        ComponentAssembly
    ),
    
    // ... rest of sciences ...
}
```

Step 3: Run TexturePacker
```bash
./gradlew.bat desktop:run -Pargs=textures
```

Step 4: Localization
```properties
# File: android/assets/i18n/TheQuarry_en.properties
science.advancedfuelprocessing = Advanced Fuel Processing
science.advancedfuelprocessing.desc = Refine petroleum into high-energy solid fuel through advanced coking processes.
```

**Result:** New tech appears in science tree with correct icon and requirements

---

### Pattern 4: Adding Multiple Related Textures (Variants)

Some items/structures have multiple texture variants (rotations, alternatives).

**Example: Building with 4 rotation variants**

```
Development/Textures/
├── structure_furnace_n.png  (North-facing)
├── structure_furnace_e.png  (East-facing)
├── structure_furnace_s.png  (South-facing)
└── structure_furnace_w.png  (West-facing)
```

In code:
```java
// The rendering code handles rotation
TextureRegion north = Quarry.Q.atlas.findRegion("structure_furnace_n");
TextureRegion east = Quarry.Q.atlas.findRegion("structure_furnace_e");
// ... use appropriate texture based on rotation
```

**Or for animated sprites (consecutive frames):**

```
Development/Textures/
├── sprite_conveyor_1.png
├── sprite_conveyor_2.png
└── sprite_conveyor_3.png
```

The `.atlas` file automatically handles this:
```
sprite_conveyor
  rotate: false
  xy: 100, 200
  size: 64, 64
  orig: 64, 64
  offset: 0, 0
  index: 0    ← Frame 0

sprite_conveyor
  rotate: false
  xy: 200, 200
  size: 64, 64
  orig: 64, 64
  offset: 0, 0
  index: 1    ← Frame 1

sprite_conveyor
  rotate: false
  xy: 300, 200
  size: 64, 64
  orig: 64, 64
  offset: 0, 0
  index: 2    ← Frame 2
```

---

## Advanced Techniques

### Technique 1: Managing Large Atlases

When the atlas grows very large (approaching 4096×4096 limits):

**Option A: Increase Canvas Size**
```json
{
  "maxWidth": 8192,   // Double the width
  "maxHeight": 8192,  // Double the height
  // ... rest of settings ...
}
```

**Option B: Split Into Categories**
Create separate atlases for different asset categories:
- `tex_structures.atlas` ← All building textures
- `tex_items.atlas` ← All item icons
- `tex_ui.atlas` ← UI elements

Then load multiple atlases at startup:
```java
assets.load("tex_structures.atlas", TextureAtlas.class);
assets.load("tex_items.atlas", TextureAtlas.class);

// Create a combined lookup wrapper
TextureAtlas structures = assets.get("tex_structures.atlas");
TextureAtlas items = assets.get("tex_items.atlas");
```

### Technique 2: Texture Compression & Optimization

For very large textures or performance-critical situations:

**Use Compression Settings**
```json
{
  "maxWidth": 4096,
  "maxHeight": 4096,
  "filterMin": "Linear",
  "filterMag": "Nearest",
  "format": "RGBA8888",  // or "RGB565" for smaller files
  "stripWhitespaceX": true,   // Remove empty columns
  "stripWhitespaceY": true,   // Remove empty rows
  "duplicatePadding": true,
  "trim": true  // Remove transparent borders
}
```

**Impact:**
- Reduces file size
- Slightly slower packing time
- May cause edge artifacts if not careful with `duplicatePadding`

### Technique 3: Dynamic Texture Loading

For mods or dynamic content:

```java
// Load a custom atlas at runtime
AssetManager assets = Quarry.Q.assets;
assets.load("custom/mod_textures.atlas", TextureAtlas.class);
assets.finishLoading();

TextureAtlas customAtlas = assets.get("custom/mod_textures.atlas");
TextureRegion modTexture = customAtlas.findRegion("custom_item_name");
```

### Technique 4: Texture Replacement System

For modding or themes:

```java
public class TextureRegistry {
    private static Map<String, TextureRegion> overrides = new HashMap<>();
    
    public static void registerOverride(String name, TextureRegion region) {
        overrides.put(name, region);
    }
    
    public static TextureRegion getTexture(String name) {
        if (overrides.containsKey(name)) {
            return overrides.get(name);
        }
        return Quarry.Q.atlas.findRegion(name);
    }
}

// Usage:
TextureRegion customItem = TextureRegistry.getTexture("item_petroleum_coke");
```

### Technique 5: Batch Texture Updates

When adding many textures at once:

1. **Create all PNGs first**
   ```
   Development/Textures/
   ├── item_new_1.png
   ├── item_new_2.png
   ├── item_new_3.png
   ├── structure_new_1.png
   └── structure_new_2.png
   ```

2. **Update all code references**
   ```java
   ItemType.New1(80, "item_new_1", 100, Category.Metal),
   ItemType.New2(81, "item_new_2", 100, Category.Metal),
   ItemType.New3(82, "item_new_3", 100, Category.Metal),
   
   StructureType.New1(215, NewStructure1.class),
   StructureType.New2(216, NewStructure2.class),
   ```

3. **Run TexturePacker once**
   ```bash
   ./gradlew.bat desktop:run -Pargs=textures
   ```

4. **Test all at once**
   - Faster workflow
   - Fewer atlas regenerations
   - Reduced risk of forgetting a step

---

## Performance Tips

### 1. Monitor Atlas Size

Check the actual used space:

```bash
python mark_used_textures.py  # Utility in project root
```

This shows:
- Total atlas size: 4096×2048 = 8,388,608 pixels
- Used pixels: 6,250,000 (74%)
- Unused pixels: 2,138,608 (26%)

**When to optimize:**
- Usage > 90%: Consider increasing canvas or removing unused textures
- Usage < 50%: Consider decreasing canvas size to save memory

### 2. Texture Size Guidelines

**Recommended sizes by type:**

| Type | Size | Notes |
|------|------|-------|
| Items | 64×64 | Small icons in inventory |
| Small Items | 32×32 | Currency, components |
| Large Items | 128×128 | Heavy machinery icons |
| Structures | 128×128 | Buildings |
| Tiles | 32×32 or 64×64 | Ground/terrain |
| UI Elements | Variable | Buttons, icons, borders |
| Science Icons | 64×64 | Tech tree |
| Fluids | 32×32 | Usually tileable |

**Impact of size:**
- Larger textures = More atlas space = More GPU memory
- Smaller textures = More efficient but less detail

### 3. Texture Format Best Practices

**Use PNG for:**
- Textures with varying transparency
- Complex colors
- High detail

**Avoid for:**
- Photographs (use JPEG where applicable)
- Simple shapes (consider using code generation)

**Ensure:**
- All PNGs use RGBA format (transparency required)
- No embedded color profiles
- Optimized PNG files (run through optimizer)

### 4. Build Time Optimization

TexturePacker gets slower with more textures:

- **Current setup:** ~50-100 textures = fast (~1-2 seconds)
- **With 500+ textures:** Can take 10-30 seconds
- **Workaround:** Use `.desktop` runs for development, optimize settings for release builds

---

## Debugging Texture Issues

### Debug Technique 1: Print Atlas Contents

```java
// Temporary debug code to list all textures in atlas
TextureAtlas atlas = Quarry.Q.atlas;
for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
    System.out.println("Texture: " + region.name + " @ " + region.getRegionX() + "," + region.getRegionY());
}
```

**Output:**
```
Texture: structure_coker @ 100,200
Texture: item_petroleum_coke @ 3500,500
... (hundreds more)
```

### Debug Technique 2: Visualize Atlas Pages

Check if multiple pages were created:

```bash
# List all tex files in assets
cd android/assets
ls -la tex*.png
# Should only see: tex.png
# If you see tex2.png, tex3.png → increase canvas size
```

### Debug Technique 3: Texture Lookup Logging

Wrap texture lookups with logging:

```java
public static TextureRegion findRegion(String name) {
    TextureRegion region = Quarry.Q.atlas.findRegion(name);
    if (region == null) {
        System.err.println("ERROR: Texture not found: " + name);
    } else {
        System.out.println("OK: Found texture " + name);
    }
    return region;
}
```

### Debug Technique 4: Visual Debugging

Export a marked version of the atlas:

```bash
python mark_used_textures.py  # Creates marked version showing used regions
```

Then open in image editor to see:
- Which parts of atlas are used
- Which parts are wasted
- Where optimization is possible

---

## Common Pitfalls & How to Avoid Them

### Pitfall 1: Forgetting Transparency

**Problem:** PNG created with white background
**Result:** White background visible in game (ugly)
**Solution:** Use transparent backgrounds (RGBA with alpha channel)

**Check:**
```bash
# Use ImageMagick to verify transparency
identify -verbose structure_coker.png | grep -i alpha
# Should output: alpha: on
```

### Pitfall 2: Wrong Texture Size

**Problem:** Created 256×256 texture for item icon
**Result:** Takes up 4x the atlas space unnecessarily
**Solution:** Use appropriate sizes (64×64 for items)

### Pitfall 3: Inconsistent Art Style

**Problem:** One texture is 3D-rendered, others are pixel art
**Result:** Visually jarring, looks out of place
**Solution:** Keep consistent style with existing textures

**Before adding a new texture:**
1. Study existing textures in your category
2. Match the art style
3. Use similar color palette
4. Similar level of detail

### Pitfall 4: Including Editor Files in Atlas

**Problem:** PSD, XCF, or temporary files left in Development/Textures/
**Result:** Bloats atlas unnecessarily
**Solution:** Store source files elsewhere, only final PNGs in Development/Textures/

**Recommended structure:**
```
Development/
├── Textures/          ← Only final .png files
│   ├── structure_*.png
│   ├── item_*.png
│   └── ...
├── Textures_Source/   ← Source files (not packed)
│   ├── *.psd
│   ├── *.xcf
│   └── *.ai
└── Textures_Backups/  ← Old versions (not packed)
    └── *.png
```

### Pitfall 5: Texture Name Inconsistency

**Problem:** PNG named `structure_my_build.png` but code references `"structure_my-build"`
**Result:** Texture not found error
**Solution:** Double-check names match exactly (especially hyphens vs underscores)

---

## Workflow Optimization

### Daily Development Workflow

```bash
# 1. Make changes to textures
# Edit Development/Textures/*.png

# 2. Update code
# Edit Java files to reference new textures

# 3. Regenerate atlas (quick)
./gradlew.bat desktop:run -Pargs=textures

# 4. Test
./gradlew.bat desktop:run

# 5. Iterate
# If textures don't appear:
#   - Check name matches PNG
#   - Check atlas regenerated correctly
#   - Look for error messages

# 6. Commit when satisfied
git add Development/Textures/
git add android/assets/tex.png
git add android/assets/tex.atlas
git add core/src/...  # Code changes
git commit -m "feat: add [feature] textures"
```

### Batch Implementation Workflow

For adding multiple related textures:

```bash
# 1. Create all PNG files
# Development/Textures/item_*.png
# Development/Textures/structure_*.png

# 2. Update all code at once
# Edit ItemType enum (all items)
# Edit StructureType enum (all structures)
# Edit Science enum (if applicable)

# 3. Single regeneration
./gradlew.bat desktop:run -Pargs=textures

# 4. Comprehensive test
./gradlew.bat desktop:run
# Check all new items/structures/science

# 5. Single commit
git add .
git commit -m "feat: add multiple [feature] items and structures"
```

---

## Summary: Best Practices Checklist

Before committing texture changes:

- [ ] All PNG files are in `Development/Textures/`
- [ ] PNG files use RGBA format with transparency
- [ ] All PNG files follow naming convention (type_name.png)
- [ ] All code references match PNG filenames exactly
- [ ] TexturePacker was run after all changes
- [ ] Only `tex.png` and `tex.atlas` in `android/assets/` (no tex2.png, etc.)
- [ ] Game compiles without errors
- [ ] Game runs without "texture not found" errors
- [ ] All new textures visible in-game and display correctly
- [ ] No regression: existing textures still work
- [ ] Localization strings added (if applicable)
- [ ] Source files (PSD, XCF, etc.) are NOT in Development/Textures/
- [ ] No temporary or backup files in Development/Textures/

---

*These patterns have been successfully used throughout Drill Down development. Follow them to ensure smooth texture integration.*




