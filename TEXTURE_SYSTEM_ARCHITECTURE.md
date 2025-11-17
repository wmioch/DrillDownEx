# Drill Down Texture System - Architecture & Data Flow

**Visual guide to how textures are processed, packed, loaded, and rendered in the Drill Down game.**

---

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                     TEXTURE PACKING PIPELINE                        │
└─────────────────────────────────────────────────────────────────────┘

                             DEVELOPMENT PHASE

    Development/Textures/           android/assets/            Game Code
    ─────────────────────           ───────────────            ────────
    
    structure_*.png                   atlas-settings.json       ItemType.java
    tile_*.png          ────────────> maxWidth: 4096            StructureType.java
    item_*.png          │             maxHeight: 4096           Science.java
    icon_*.png      TexturePacker     filterMin: Linear         TileType.java
    fluid_*.png         │             filterMag: Nearest        Fluid.java
                        │
                        ↓
    
                                      tex.png (4096×2048)
                                      (All textures packed)
                                      
                                      tex.atlas
                                      (Coordinate mappings)
                                      
                    ↓ (Runtime)
                    
└─────────────────────────────────────────────────────────────────────┘

                         RUNTIME LOADING PHASE

    Game Startup
        ↓
    LoadingScreen.java (line 75)
        ↓
    assets.load("tex.atlas", TextureAtlas.class)
        ↓
    LibGDX AssetManager
        ↓
    Reads tex.atlas → Loads tex.png
        ↓
    Quarry.Q.atlas = TextureAtlas object
        ↓
    Ready for texture lookups

└─────────────────────────────────────────────────────────────────────┘

                         RUNTIME RENDERING PHASE

    Code needs texture
        ↓
    Quarry.Q.atlas.findRegion("structure_name")
        ↓
    tex.atlas lookup: xy: 100, 200; size: 128, 128
        ↓
    TextureRegion created (points to pixels in tex.png)
        ↓
    DepthSpriter.add(TextureRegion)
        ↓
    Rendered to screen
```

---

## Detailed Component Relationships

### 1. Source Files → TexturePacker

```
Development/Textures/
│
├── item_steel_plate.png ──┐
├── item_petroleum_coke.png│
├── structure_furnace.png  │
├── structure_coker.png    │───> TexturePacker ──> Bin Packing
├── tile_dirt.png          │     Algorithm
├── tile_stone.png         │     (Optimizes placement)
├── icon_crude_oil.png     │
├── icon_advanced_fuel...png
├── fluid_crude_oil.png    └────────┐
└── fluid_refined_oil.png         │
                                   ↓
            Arranged efficiently in 4096×2048 canvas
```

### 2. TexturePacker Output

```
┌─ BEFORE PACKING ──────────────────────┐
│ Hundreds of individual PNG files      │
│ (Scattered throughout filesystem)     │
│ Inefficient for game loading          │
└────────────────────────────────────────┘
                    ↓
         [TexturePacker Processing]
                    ↓
┌─ AFTER PACKING ───────────────────────┐
│ tex.png (4096×2048)                    │
│ └── Binary image containing ALL        │
│     textures packed efficiently        │
│     (Single GPU texture upload)        │
│                                        │
│ tex.atlas (Text descriptor)            │
│ └── Maps names to coordinates:         │
│     structure_coker                    │
│       xy: 100, 200                     │
│       size: 128, 128                   │
│       ...                              │
└────────────────────────────────────────┘
```

### 3. Code ↔ Texture Lookup

```
ITEM EXAMPLE:

Code Definition:
    ItemType.PetroleumCoke(72, "item_petroleum_coke", 500, CoalFuel)
                                ↑
                        Texture name
                        
PNG File:
    Development/Textures/item_petroleum_coke.png
                          └─ Must match exactly!
                          
Atlas Entry (auto-generated):
    item_petroleum_coke
      rotate: false
      xy: 3500, 500
      size: 64, 64
      orig: 64, 64
      offset: 0, 0
      index: -1
      
Runtime Lookup:
    ItemType.PetroleumCoke.icon = Quarry.Q.atlas.findRegion("item_petroleum_coke")
                                     ↑
                            Looks up in tex.atlas
                            Finds (3500, 500) in tex.png
                            Returns TextureRegion
                            
Game Rendering:
    batch.draw(itemIcon, x, y)  ← TextureRegion automatically points to
                                   correct pixel region in tex.png
```

### 4. Structure Example - Full Data Flow

```
COKER BUILDING IMPLEMENTATION:

1. CREATE TEXTURE
   └─ Development/Textures/structure_coker.png (128×128)

2. ADD TO ENUMERATION
   └─ StructureType.java
      Coker(214, Coker.class)

3. CREATE STRUCTURE CLASS
   └─ Coker.java
      ProducerSchema → setTexture("structure_coker")

4. RUN TEXTUREPACKER
   └─ TexturePacker reads Development/Textures/structure_coker.png
      └─ Finds optimal location in canvas (e.g., 2000, 500)
      └─ Writes to tex.png
      └─ Creates entry in tex.atlas:
         structure_coker
           rotate: false
           xy: 2000, 500
           size: 128, 128
           orig: 128, 128

5. GAME STARTUP
   └─ LoadingScreen.java loads tex.atlas
      └─ LibGDX parses all coordinates
      └─ Quarry.Q.atlas ready for lookups

6. RUNTIME
   └─ Building.java or rendering code:
      region = Quarry.Q.atlas.findRegion("structure_coker")
      └─ Returns TextureRegion pointing to (2000, 500) in tex.png
      └─ Rendered on screen
```

---

## Key Files and Their Roles

### Texture Sources
```
Development/Textures/
├── *.png files (individual textures)
│   └─ Raw input for TexturePacker
│   └─ Naming must be correct for identification
│   └─ PNG format with transparency required
└─ (All textures here will be packed)
```

### Configuration
```
android/assets/atlas-settings.json
{
    "maxWidth": 4096,        ← Size of output tex.png (width)
    "maxHeight": 4096,       ← Size of output tex.png (height)
    "duplicatePadding": true ← Prevent edge artifacts
    "filterMin": "Linear",   ← Smoothing filter
    "filterMag": "Nearest"   ← Magnification filter
}
```

### Generated Output
```
android/assets/
├── tex.png              ← The actual texture image (binary)
│   └─ 4096×2048 pixels
│   └─ RGBA format
│   └─ Contains ALL game textures packed
├── tex.atlas            ← Descriptor file (text)
│   └─ Human-readable coordinate mappings
│   └─ Tells game where each texture is in tex.png
└─ (Optional: tex2.png, tex3.png if overflow)
   └─ Only created if textures don't fit in single page
   └─ Current config prevents this
```

### Code Integration
```
Game Loading Phase:
  LoadingScreen.java (line 75)
    └─ assets.load("tex.atlas", TextureAtlas.class)
    └─ Reads tex.atlas descriptor
    └─ Loads tex.png into GPU memory
    └─ Creates Quarry.Q.atlas object

Game Rendering Phase:
  Anywhere in game code:
    └─ Quarry.Q.atlas.findRegion("texture_name")
    └─ Returns TextureRegion (pointers to tex.png location)
    └─ Draw TextureRegion to screen
```

### TexturePacker Invocation
```
DesktopLauncher.java (line 116)
    if (arg[0].equals("textures")) {
        TexturePacker.main(new String[] {
            "./Development/Textures/",      ← Input directory
            "./android/assets/",             ← Output directory
            "tex.atlas",                     ← Output file prefix
            "./android/assets/atlas-settings.json" ← Config file
        });
    }
```

---

## Naming Convention System

The naming convention ensures the game can find textures by name. The system works like this:

```
┌──────────────────────────────────────────────────────────────┐
│         TEXTURE NAMING & LOOKUP SYSTEM                       │
└──────────────────────────────────────────────────────────────┘

ITEMS
─────
PNG File: Development/Textures/item_steel_plate.png
         └─ Filename = item_steel_plate

Code Reference: ItemType enum
         enum ItemType {
             SteelPlate(42, "item_steel_plate", 100, Category.Metal),
         }
         └─ Must match filename exactly

Runtime Lookup:
         ItemType.SteelPlate.icon = Quarry.Q.atlas.findRegion("item_steel_plate")
                                    └─ Finds in tex.atlas
                                    └─ Returns pixel region from tex.png

STRUCTURES
──────────
PNG File: Development/Textures/structure_furnace.png
         └─ Filename = structure_furnace

Code Reference: StructureType enum
         enum StructureType {
             Furnace(10, Furnace.class)
         }
         
         + ProducerSchema in Furnace class:
           setTexture("structure_furnace")  ← Must match filename

Runtime Lookup:
         Quarry.Q.atlas.findRegion("structure_furnace")

SCIENCE/TECH
────────────
PNG File: Development/Textures/icon_crude_oil.png
         └─ Filename = icon_crude_oil

Code Reference: ScienceType enum
         enum ScienceType {
             OilProcessing(1, "icon_crude_oil", ...)
         }
         └─ Must match filename exactly

TILES
─────
PNG File: Development/Textures/tile_dirt.png
         └─ Filename = tile_dirt

Code Reference: TileType enum
         enum TileType {
             Dirt(1, "dirt", ...)  ← "tile_" prefix added automatically
         }

Runtime Lookup:
         Quarry.Q.atlas.findRegion("tile_" + "dirt")
         = Quarry.Q.atlas.findRegion("tile_dirt")
```

---

## Atlas File Format Explained

```
┌─ tex.atlas file structure ────────────────────┐
│                                               │
│ tex.png                  ← Which PNG file?    │
│ size: 4096,2048          ← Canvas dimensions  │
│ format: RGBA8888         ← Pixel format       │
│ filter: Linear,Nearest   ← Texture filtering  │
│ repeat: none             ← Texture wrapping   │
│                                               │
│ structure_coker          ← Texture name       │
│   rotate: false          ← Rotated 90°?       │
│   xy: 100, 200           ← Top-left in png    │
│   size: 128, 128         ← Width × Height     │
│   orig: 128, 128         ← Original size      │
│   offset: 0, 0           ← Offset (unused)    │
│   index: -1              ← Not animated       │
│                                               │
│ structure_refinery                            │
│   rotate: false                               │
│   xy: 300, 200                                │
│   ... (more entries)                          │
│                                               │
└───────────────────────────────────────────────┘
```

**When game looks up "structure_coker":**
1. Opens tex.atlas
2. Finds entry for "structure_coker"
3. Reads: xy: 100, 200; size: 128, 128
4. Creates TextureRegion pointing to pixel (100,200) in tex.png
5. Region is 128×128 pixels
6. Returns region to game
7. Game renders region at desired screen position

---

## Common Issues & System Breakdown Points

```
Issue 1: Texture Missing After Packing
┌───────────────────────────────────────────────┐
│ Cause:                                        │
│ ├─ PNG file in Development/Textures/         │
│ ├─ Code refers to texture by name            │
│ └─ TexturePacker not run                      │
│                                               │
│ Symptom:                                      │
│ └─ "could not find texture" error at runtime  │
│                                               │
│ Fix:                                          │
│ └─ Run TexturePacker to regenerate atlas      │
└───────────────────────────────────────────────┘

Issue 2: Multiple Atlas Pages
┌───────────────────────────────────────────────┐
│ Cause:                                        │
│ ├─ atlas-settings.json maxWidth too small     │
│ ├─ Too many textures in Development/Textures/│
│ └─ TexturePacker creates tex2.png, tex3.png   │
│                                               │
│ Symptom:                                      │
│ ├─ tex2.png and tex2.atlas exist              │
│ ├─ Textures on page 2 not visible             │
│ └─ Game only loads first page                 │
│                                               │
│ Fix:                                          │
│ ├─ Increase maxWidth to 4096                  │
│ ├─ Increase maxHeight to 4096                 │
│ └─ Re-run TexturePacker                       │
└───────────────────────────────────────────────┘

Issue 3: Name Mismatch
┌───────────────────────────────────────────────┐
│ Cause:                                        │
│ ├─ PNG: structure_mybuild.png                 │
│ ├─ Code: findRegion("structure_my_build")     │
│ └─ Names don't match exactly                  │
│                                               │
│ Symptom:                                      │
│ └─ "could not find texture" crash             │
│                                               │
│ Fix:                                          │
│ └─ Ensure filename = code reference           │
└───────────────────────────────────────────────┘

Issue 4: Old Texture After Edit
┌───────────────────────────────────────────────┐
│ Cause:                                        │
│ ├─ PNG file modified in Development/Textures/│
│ ├─ TexturePacker not run                      │
│ └─ tex.png still contains old texture         │
│                                               │
│ Symptom:                                      │
│ └─ Changes don't appear in game               │
│                                               │
│ Fix:                                          │
│ └─ Run TexturePacker again                    │
└───────────────────────────────────────────────┘
```

---

## Performance Characteristics

### Why Texture Atlasing?

```
WITHOUT ATLAS (inefficient)
├─ Hundreds of individual texture files
├─ Each loads separately to GPU
├─ Multiple GPU memory uploads
├─ Multiple texture bindings per frame
└─ Performance: POOR ❌

WITH ATLAS (optimized)
├─ Single 4096×2048 texture file
├─ Single GPU memory upload
├─ Single texture binding per frame (mostly)
└─ Performance: EXCELLENT ✅
```

### Current Configuration Performance

```
atlas-settings.json Configuration:
┌──────────────────────────────────────────┐
│ maxWidth: 4096         │ Large canvas    │
│ maxHeight: 4096        │ Fits all textures
│ duplicatePadding: true │ Higher quality  │
│ filterMin: Linear      │ Smooth scaling  │
│ filterMag: Nearest     │ Pixel-perfect   │
└──────────────────────────────────────────┘

Result:
├─ Single 4096×2048 page (efficient)
├─ All textures fit
├─ Minimal runtime overhead
├─ No texture switching during rendering
└─ Optimal for game performance
```

---

## Checklist: Complete Data Flow Verification

When implementing new textures, verify each step:

```
□ PNG file created
  └─ Location: Development/Textures/[name].png
  └─ Format: PNG with transparency
  └─ Naming: Matches code reference

□ Code reference added
  └─ ItemType / StructureType / ScienceType enum
  └─ Name matches PNG filename exactly

□ TexturePacker run
  └─ Command: ./gradlew.bat desktop:run -Pargs=textures
  └─ Wait for completion

□ Atlas regenerated
  └─ Check: android/assets/tex.png (file newer)
  └─ Check: android/assets/tex.atlas (contains new name)
  └─ Verify: No tex2.png created

□ Game loads successfully
  └─ No "texture not found" errors
  └─ No crash on startup

□ Texture visible
  └─ Appears correctly in game
  └─ Right position and appearance

□ Files committed
  └─ git add Development/Textures/[name].png
  └─ git add android/assets/tex.png
  └─ git add android/assets/tex.atlas
```

---

*This architecture has been battle-tested through multiple texture additions in the Drill Down development cycle.*




