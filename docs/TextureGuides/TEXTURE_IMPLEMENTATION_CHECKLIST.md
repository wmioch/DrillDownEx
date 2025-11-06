# Quick Reference: Adding New Textures to Drill Down

**A step-by-step checklist for implementing new textures in the game.**

---

## ⚠️ AGENT ALERT: Remember the Texture Packing Step!

**YOU WILL FORGET THIS STEP.** Every agent does. It's separate from building.

After editing texture files, you MUST run:
```bash
.\gradlew.bat desktop:run --args="textures"
```

See **Step 3** below. Don't skip it. The game will crash if you do.

---

## 📋 Pre-Implementation Checklist

Before you start, ensure you have:
- [ ] New texture PNG file(s) ready
- [ ] Texture names decided (following naming convention)
- [ ] Game code locations identified (ItemType, StructureType, etc.)
- [ ] Git branch or working directory ready
- [ ] Current code compiles and runs
- [ ] **You understand texture packing is a SEPARATE step** (read TEXTURE_PACKING_GUIDE.md first)

---

## 🎨 Step 1: Create Texture Files

**Location:** `Development/Textures/`

### For Each Texture:
1. [ ] Create PNG file with correct name format:
   - `item_[name].png` for items
   - `structure_[name].png` for buildings
   - `icon_[name].png` for UI icons
   - `tile_[name].png` for ground tiles
   - `fluid_[name].png` for liquids

2. [ ] Verify specifications:
   - [ ] Format: PNG with transparency (RGBA)
   - [ ] Style: Matches existing textures
   - [ ] Size: Appropriate for use (typically 64×64 or 128×128)
   - [ ] No errors or corruption in file

**Naming Rule:** Filename (without .png) = texture name used in code

---

## 💻 Step 2: Update Game Code

### For New Items:
**File:** `core/src/de/dakror/quarry/game/Item.java`

```java
public enum ItemType {
    // ... existing items ...
    YourItemName(ID, "item_your_item_name", stackSize, ItemCategory.CategoryHere),
    // ...
}
```

[ ] Add item to `ItemType` enum with:
- [ ] Unique ID number
- [ ] Exact texture name match (must match PNG filename)
- [ ] Stack size
- [ ] Category

**Example:**
```java
PetroleumCoke(72, "item_petroleum_coke", 500, ItemCategory.CoalFuel),
```

### For New Structures/Buildings:
**File:** `core/src/de/dakror/quarry/structure/base/StructureType.java`

```java
public enum StructureType {
    // ... existing structures ...
    YourStructure(ID, YourStructure.class),
    // ...
}
```

[ ] Add structure to `StructureType` enum with:
- [ ] Unique ID number
- [ ] Structure class name

**File:** `core/src/de/dakror/quarry/structure/producer/YourStructure.java` (or appropriate package)

[ ] Ensure the structure class references the texture in `ProducerSchema`:
```java
ProducerSchema schema = new ProducerSchema()
    .setSize(4, 4)
    .setTexture("structure_your_structure")  // Must match PNG name
    // ... rest of schema ...
```

### For New Science Technologies:
**File:** `core/src/de/dakror/quarry/game/Science.java`

```java
public enum ScienceType {
    // ... existing sciences ...
    YourScience(ID, "icon_your_science", costs, researchTime, prerequisites...),
    // ...
}
```

[ ] Add science to `ScienceType` enum with:
- [ ] Unique ID number
- [ ] Icon texture name (must match PNG)
- [ ] Items cost (if any)
- [ ] Research time
- [ ] Prerequisites (if any)

### For New Tiles:
**File:** `core/src/de/dakror/quarry/game/Tile.java`

```java
public enum TileType {
    // ... existing tiles ...
    YourTile(ID, "tile_your_tile", itemDrop, base, metadata),
    // ...
}
```

[ ] Add tile to `TileType` enum with:
- [ ] Unique ID
- [ ] Texture name prefix (game adds "tile_" automatically)
- [ ] Item drop (or null)
- [ ] Base tile
- [ ] Metadata flags

---

## 🔄 Step 3: Run TexturePacker

**THIS IS CRITICAL - DO NOT SKIP!**

**⚠️ THIS IS SEPARATE FROM BUILDING THE GAME**
- `Build-Game.ps1` does NOT do this
- `gradlew desktop:dist` does NOT do this
- You MUST run this explicitly after adding/editing textures

### Option A: Using Gradle (Recommended - Only Way That Works)
```bash
cd D:\Projects\DrillDown
.\gradlew.bat desktop:run --args="textures"
```

[ ] Command executed without errors
[ ] Waited for TexturePacker to complete
[ ] No error messages in console
[ ] Console output shows "Packing..." messages from TexturePacker

**Why `--args` and not `-Pargs`?** Because that's how DesktopLauncher.java checks for it (line 114)

### Option B: Manual TexturePacker Invocation (Not Recommended)
```bash
java -jar gdx-tools.jar com.badlogic.gdx.tools.texturepacker.TexturePacker \
  ./Development/Textures/ \
  ./android/assets/ \
  tex.atlas \
  ./android/assets/atlas-settings.json
```

⚠️ Only use this if Gradle fails for some reason

---

## ✅ Step 4: Verify Atlas Regeneration

### Check Output Files:
1. [ ] `android/assets/tex.png` exists and is updated
   - Timestamp should be recent
   - Size should be reasonable

2. [ ] `android/assets/tex.atlas` exists and is updated
   - Timestamp should be recent
   - Contains your new texture names

3. [ ] No extra files created:
   - [ ] `tex2.png` should NOT exist
   - [ ] `tex2.atlas` should NOT exist
   - [ ] `tex3.png` should NOT exist

**If multiple pages were created:**
- [ ] Check `android/assets/atlas-settings.json`
- [ ] Increase `maxWidth` and `maxHeight` to 4096
- [ ] Delete extra atlas files
- [ ] Re-run TexturePacker

### Verify Texture Names in Atlas:
1. [ ] Open `android/assets/tex.atlas` in text editor
2. [ ] Search for your texture names
3. [ ] Example entry should exist:
   ```
   item_petroleum_coke
     rotate: false
     xy: XXXX, XXXX
     size: XX, XX
     orig: XX, XX
     offset: 0, 0
     index: -1
   ```

---

## 🎮 Step 5: Test the Game

### Compilation:
[ ] Run `./gradlew.bat build` or rebuild in IDE
[ ] No compilation errors
[ ] No texture-loading errors

### Game Launch:
[ ] Start the game: `./gradlew.bat desktop:run`
[ ] Watch the loading screen
[ ] No "could not find texture" errors
[ ] No crashes on startup

### Visual Verification:
[ ] Navigate to where texture should appear
  - [ ] Check inventory for new items
  - [ ] Check build menu for new structures
  - [ ] Check science tree for new technologies
[ ] Texture displays correctly
  - [ ] Right color and appearance
  - [ ] No graphics glitches
  - [ ] Positioned correctly

---

## 📝 Step 6: Update Localization (Optional but Recommended)

**File:** `android/assets/i18n/TheQuarry_en.properties`

Add descriptions for your new content:

```properties
# Items
item.YourItemName = Your Item Name (Description)

# Structures
structure.yourstructure = Structure Name
structure.yourstructure.desc = Description of what it does.
structure.yourstructure.recipe.recipename = What the recipe does.

# Sciences
science.yourscience = Technology Name
science.yourscience.desc = What it unlocks or improves.
```

[ ] Localization strings added
[ ] Game displays proper names in menus

---

## 🔍 Troubleshooting During Implementation

### Problem: "could not find texture for [item/structure] ..."

**Debug Steps:**
1. [ ] PNG filename matches exactly: `item_name.png` vs code `"item_name"`
2. [ ] PNG exists in `Development/Textures/`
3. [ ] Run TexturePacker again
4. [ ] Check `tex.atlas` contains texture name
5. [ ] Restart game after atlas regeneration

### Problem: Texture Missing After Regeneration

**Debug Steps:**
1. [ ] Check for multiple atlas files (tex2.png, tex3.png)
   - If yes, increase `maxWidth`/`maxHeight` in `atlas-settings.json`
2. [ ] Delete extra atlas files
3. [ ] Remove temporary files from `Development/Textures/`
4. [ ] Re-run TexturePacker
5. [ ] Restart game

### Problem: Old Texture Still Appears

**Debug Steps:**
1. [ ] Ensure PNG file was actually modified
2. [ ] Run TexturePacker again
3. [ ] Delete old `tex.png` and `tex.atlas` manually
4. [ ] Run TexturePacker once more
5. [ ] Clean build (delete build directory)
6. [ ] Rebuild and restart game

### Problem: Texture Looks Wrong/Pixelated

**Debug Steps:**
1. [ ] Check texture file isn't corrupted
2. [ ] Verify size is appropriate for use
3. [ ] Check `filterMin`/`filterMag` in `atlas-settings.json`
4. [ ] Regenerate atlas

---

## 📋 Final Verification Checklist

Before committing your changes:

- [ ] All PNG files created and placed in `Development/Textures/`
- [ ] All code references added (ItemType, StructureType, etc.)
- [ ] TexturePacker run successfully
- [ ] `tex.png` and `tex.atlas` updated in `android/assets/`
- [ ] No multiple atlas pages (tex2.png, etc.)
- [ ] Game compiles without errors
- [ ] Game runs without "texture not found" errors
- [ ] New textures visible and display correctly
- [ ] No regression: existing textures still work
- [ ] Localization strings added (if applicable)

---

## 💾 Version Control

### Files to Commit:
```bash
git add Development/Textures/[your_new_textures]
git add android/assets/tex.png
git add android/assets/tex.atlas
git add core/src/de/dakror/quarry/game/Item.java          # if modified
git add core/src/de/dakror/quarry/structure/base/StructureType.java  # if modified
git add core/src/de/dakror/quarry/game/Science.java       # if modified
git add android/assets/i18n/TheQuarry_en.properties       # if modified
git commit -m "feat: add [your feature] with new textures"
```

### Keep in Sync:
- Always commit `tex.png` and `tex.atlas` together
- They are interdependent and must match

---

## 🚀 Quick Summary

1. **Create PNG** → `Development/Textures/[name].png`
2. **Add Code** → ItemType/StructureType/etc enum
3. **Run TexturePacker** → Regenerates atlas
4. **Verify Atlas** → Check `tex.atlas` has your texture
5. **Test Game** → Launch and verify texture appears
6. **Commit** → Include both texture files and code changes

**That's it!** The game will automatically find and use your texture from the regenerated atlas.

---

*For detailed information, see TEXTURE_PACKING_GUIDE.md*
