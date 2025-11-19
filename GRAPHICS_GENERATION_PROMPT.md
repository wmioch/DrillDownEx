# DrillDown Graphics Generation Prompt

## Game Overview
DrillDown is a **factory-building and resource management game** set in an **underground mining operation**. Players build production chains, mine resources, refine materials, and expand deeper underground through multiple layers. The game features complex industrial machinery, conveyor systems, fluid processing, power generation, and a technology research tree.

---

## Visual Style & Aesthetic

### Core Art Direction
- **Style**: **Minimalist flat design** with clean geometric shapes
- **Perspective**: **Top-down orthographic view** (NOT isometric - purely flat from above)
- **Complexity**: Simple, readable silhouettes with subtle depth through shading
- **Transparency**: All assets use transparent backgrounds (PNG with alpha channel)
- **Detail Level**: Low-to-medium detail - functional and readable over decorative

### Color Palette

#### Primary Industrial Colors
- **Grays & Metals**: `#5A6B7A`, `#4A5560`, `#6B7A8A` (machinery, structures, metal components)
- **Dark Backgrounds**: `#2A2D32`, `#3A3D42` (darker structural elements, shadows)
- **Light Metals**: `#8A9BA8`, `#9AAAB8` (highlights, lighter metal surfaces)

#### Accent Colors (Use Sparingly)
- **Orange/Amber**: `#E8704D`, `#FF8855` (heat, molten materials, outputs, warning indicators)
- **Blue**: `#4A8FC4`, `#5A9FD4` (water, cooling, electrical systems when tinted)
- **Green**: `#6B9A5A`, `#7AAA6A` (organic materials, positive indicators)
- **Red**: `#D45A4A`, `#E46A5A` (danger, extreme heat, errors)
- **Yellow/Gold**: `#D4A84A`, `#E4B85A` (precious materials, energy)

#### Material-Specific Colors
- **Copper**: Warm orange-brown `#C87A4A`
- **Steel**: Cool blue-gray `#6A7A8A`
- **Iron**: Medium gray `#7A7A7A`
- **Coal/Carbon**: Deep black with subtle shine `#1A1A1A` - `#2A2A2A`
- **Oil/Petroleum**: Dark brown-black `#2A1A0A`, glossy appearance
- **Water**: Light blue-gray `#6A8A9A`

### Lighting & Shading
- **Shading Type**: Flat shading with **2-3 shade levels maximum**
- **Light Source**: Assume top-left diffuse lighting
- **Highlights**: Minimal, only on metallic/reflective surfaces
- **Shadows**: Subtle, mostly implied through darker base colors rather than drop shadows
- **Depth**: Created through overlapping shapes and slight gradient shifts, not through 3D rendering

---

## Asset Categories & Specifications

### 1. **Structure/Building Textures**
**Size**: 128×128 pixels  
**Naming**: `structure_[name].png`

**Visual Requirements:**
- Top-down flat view showing the **entire building footprint**
- Represent industrial machinery: pipes, chambers, tanks, gears, control panels
- Include subtle indicators of function (pipes for input/output, vents for heat, etc.)
- Use geometric shapes: rectangles, circles, rounded rectangles
- Keep edges clean and readable at small sizes
- Add **1 accent color maximum** for key functional elements (e.g., output chimney, control panel)

**Examples of Building Types:**
- **Processing**: Smelters, refineries, crushers, mixers → show chambers, hoppers, grinding elements
- **Storage**: Tanks, silos, warehouses → show containment vessels, access hatches
- **Power**: Turbines, boilers, generators → show rotating elements, exhaust stacks
- **Transport**: Conveyors, elevators, distributors → show directional flow, mechanical arms

**Reference Style**: Think **circuit board components** or **architectural floor plan symbols** rendered with industrial materials.

---

### 2. **Item Icons**
**Size**: 64×64 pixels  
**Naming**: `item_[name].png`

**Visual Requirements:**
- Small, **immediately recognizable** at inventory size (32×32 displayed)
- Simple silhouette - avoid fine details
- Centered in canvas with some padding
- Clear material indication through color and texture
- Slight 3D form through gradient shading (2-3 tones)

**Item Categories:**

#### Raw Materials
- **Ores**: Chunky, irregular shapes with crystal facets (copper = orange-brown, iron = rust-red, tin = silver-gray)
- **Coal/Carbon**: Matte black chunks with angular breaks
- **Stone**: Gray, rough-textured irregular blocks
- **Clay**: Smooth brown-orange lumps
- **Wood**: Light brown with visible grain lines

#### Refined Materials
- **Ingots**: Clean rectangular bars with beveled edges, metallic shine
- **Plates**: Flat rectangular sheets with slight curl at edges
- **Wires**: Coiled or bundled thin strands
- **Tubes**: Cylindrical with hollow ends visible
- **Dust/Powder**: Small pile with soft edges, matte texture

#### Processed Goods
- **Petroleum Coke**: Very dark (almost black) chunky fuel pellets, matte surface
- **Plastic Beads**: Small spherical particles, slight transparency
- **Glass**: Transparent blue-green tint, high shine
- **Cement**: Gray powder, slightly granular texture

#### Fluids (in containers)
- Show liquid color through transparent container
- Add gloss/shine to indicate liquid state
- Slight meniscus curve at top

---

### 3. **Icon Textures (UI/Tech Tree)**
**Size**: 64×64 pixels  
**Naming**: `icon_[name].png`

**Visual Requirements:**
- Symbolic representation of a building or concept
- More stylized than realistic
- Can show simplified side view or characteristic element
- Use 1-2 colors maximum plus base gray
- Clear foreground/background separation

**Tech Tree Icons**: Represent research topics abstractly (e.g., gear for machinery, test tube for chemistry, lightning bolt for electricity)

---

### 4. **Tile Textures (Terrain/Resources)**
**Size**: 32×32 or 64×64 pixels  
**Naming**: `tile_[name].png`

**Visual Requirements:**
- **Tileable** - edges must seamlessly connect
- Represent underground layers: dirt, stone, ore veins, oil deposits
- Subtle texture - avoid high contrast repeating patterns
- Use color shifts to show ore concentration
- Can include small detail elements (rocks, cracks, crystals)

---

### 5. **Fluid Textures**
**Size**: 32×32 pixels  
**Naming**: `fluid_[name].png`

**Visual Requirements:**
- Tileable animated texture or static representation
- Show liquid surface with slight ripples or flow lines
- Color-code by fluid type:
  - Water: `#6A8A9A` (blue-gray)
  - Crude Oil: `#2A1A0A` (dark brown-black)
  - Refined Oil: `#4A3A2A` (lighter brown)
  - Molten Copper: `#FF6A2A` (bright molten orange)
  - Steam: `#EAEAEA` (semi-transparent white-gray)

---

## Specific Design Guidelines

### DO:
✅ Keep designs **simple and geometric**  
✅ Use **flat colors** with 2-3 shades maximum  
✅ Make **function visually obvious** (pipes for fluids, gears for mechanical, etc.)  
✅ Maintain **consistent lighting direction** (top-left)  
✅ Use **transparent backgrounds** (PNG alpha channel)  
✅ Ensure **readability at small sizes** (test at 50% scale)  
✅ Match the **industrial/mechanical theme**  
✅ Use **muted colors** with occasional bright accents  
✅ Design for **top-down flat view only**  
✅ Include **subtle texture** (brushed metal, granular materials)  

### DON'T:
❌ Use isometric or 3D perspective  
❌ Add excessive detail or decoration  
❌ Use gradients for entire shapes (only for subtle depth)  
❌ Include text or numbers on textures  
❌ Use bright saturated colors as base (only as accents)  
❌ Add drop shadows (depth through shading only)  
❌ Make edges or details too thin (will disappear at small scale)  
❌ Use photorealistic textures or effects  
❌ Include animated elements in static textures  
❌ Use more than 5-6 distinct colors per asset  

---

## Special Features & Effects

### Color Tinting System
The game engine supports **dynamic color tinting** for certain structures:

- **Blue Tint** (0.4, 0.6, 1.0, 0.8): Used for Item Elevators to indicate vertical transport
- **Orange Tint** (1.0, 0.65, 0.2, 0.8): Used for Relief Valves to show pressure control

**Note**: When creating textures for tinted structures, design in **neutral gray tones** - the tint will be applied by the engine using overlay blend mode, which preserves brightness and contrast while shifting hue.

### Directional Variants
Some structures need **rotational or directional variants**:
- Conveyors: Show direction with arrow or flow lines (`_n`, `_e`, `_s`, `_w`, `_ne`, `_ew`, etc.)
- Pipes/Tubes: Connection variants (`_ew`, `_nesw`, `_esw`, etc.)
- Machines with output: Indicate which side outputs materials

### Multi-Part Structures
Large structures (4×4 tiles or bigger) may need:
- **Base texture**: Main building structure (128×128+)
- **Top texture**: If building has vertical height (`_top` suffix)
- **Status indicators**: Separate overlay showing fill level, power status

---

## Thematic Context

### Industrial Era
Think **late 19th/early 20th century industrial revolution** meeting **modern automation**:
- Riveted metal construction
- Exposed piping and mechanical elements
- Steam power transitioning to electrical
- Analog gauges and control panels
- Brick channels for fluids
- Heavy machinery with visible function

### Underground Mining Setting
- Dark underground atmosphere reflected in color choices
- Artificial lighting implied
- Resource veins visible in terrain
- Dirt, stone, ore deposits shown in tiles
- Buildings designed for underground construction (compact, modular)

### Progression Theme
As players progress through the tech tree:
- **Early game**: Simple wooden/stone structures, basic metal
- **Mid game**: Steel construction, electrical systems, complex piping
- **Late game**: Advanced materials, solar panels, high-tech electronics

Graphics should subtly reflect this progression through:
- Material complexity (wood → iron → steel → advanced alloys)
- Size and detail (small simple machines → large complex factories)
- Color sophistication (browns/grays → metallics → colored accents)

---

## Technical Specifications

### File Format
- **Format**: PNG with alpha transparency
- **Color Mode**: RGBA (8-bit per channel)
- **Compression**: Optimized PNG (lossless)
- **No embedded profiles**: Remove color profiles

### Canvas Sizes (Common)
- Small items/icons: **64×64px**
- Terrain tiles: **32×32px** or **64×64px**
- Standard structures: **128×128px**
- Large structures: **192×192px** or **256×256px**
- UI elements: Variable (keep power-of-2 dimensions when possible)

### Texture Packing
All individual textures are packed into a single texture atlas (`tex.atlas` / `tex.png`) at 4096×4096px maximum.

**Best Practices:**
- Keep individual textures reasonably sized
- Trim excessive transparency (auto-handled by TexturePacker)
- Avoid huge empty areas in textures

---

## Example Generation Prompts

### For a New Processing Building
```
Create a top-down flat industrial building texture (128×128px) for a "Petroleum Cracker" 
structure. Show a rectangular industrial processing chamber with rounded corners, exposed 
piping on two sides (left input, right output), a small control panel (orange accent) on 
the front, and circular access hatches. Use muted industrial grays (#5A6B7A base, #4A5560 
shadows, #8A9BA8 highlights) with a single orange accent (#FF8855) for the control panel. 
Style: minimalist flat geometric, no isometric perspective, transparent background. 
Think circuit board components rendered in industrial materials.
```

### For a New Item
```
Create a 64×64px item icon for "Titanium Ingot" - a refined metal bar. Show a rectangular 
metallic ingot with beveled edges, centered in frame. Use light silver-gray colors with 
subtle metallic sheen (#9AAAB8 highlights, #6B7A8A base, #4A5560 shadows). Flat design 
with 2-3 shade levels, slight gradient for depth. No drop shadow. Transparent background. 
Instantly recognizable at 32px display size.
```

### For a Tech Tree Icon
```
Create a 64×64px tech tree icon for "Advanced Metallurgy" research. Show a simplified 
symbolic representation: overlapping metallic gear and test tube, or abstract molecular 
structure. Use 1-2 colors (gray base + one accent). Stylized and simplified, clear 
silhouette, transparent background. More iconic than realistic.
```

---

## Quality Checklist

Before submitting a new texture:

- [ ] **Correct size** for asset category
- [ ] **Transparent background** (PNG alpha)
- [ ] **Top-down flat view** (not isometric/3D)
- [ ] **Readable at 50% scale** (for gameplay zoom levels)
- [ ] **Matches color palette** (muted industrials with subtle accents)
- [ ] **Consistent shading** (2-3 levels, top-left light source)
- [ ] **Function is visually clear** (can identify purpose from appearance)
- [ ] **Fits thematic style** (industrial/mechanical/underground mining)
- [ ] **No text or fine details** that disappear at small scale
- [ ] **Clean edges** (not jagged or overly complex)
- [ ] **Naming convention** followed (`structure_`, `item_`, etc.)

---

## Integration Workflow

Once graphics are generated:

1. **Save as PNG** with alpha transparency
2. **Name appropriately**: `item_[name].png`, `structure_[name].png`, etc.
3. **Place in**: `Development/Textures/` directory
4. **Update code**: Add reference in appropriate enum (ItemType, StructureType, etc.)
5. **Pack textures**: Run `.\gradlew.bat desktop:run --args="textures"`
6. **Build game**: Run `.\Build-Game.ps1`
7. **Test in-game**: Verify appearance at gameplay scale

---

## References & Inspiration

### Visual Reference Games
- Factorio (industrial theme, top-down view)
- Shapez.io (minimalist geometric aesthetic)
- Mindustry (clean industrial sprites)

### Real-World References
- Industrial machinery schematics
- Circuit board components
- Factory floor plan symbols
- Mining equipment catalogs
- Victorian-era industrial diagrams

### Existing Assets
Refer to current textures in `Development/Textures/`:
- `structure_coker.png` - Exemplifies flat minimalist industrial design
- `structure_smelter.png` - Shows clean geometric approach
- `item_petroleum_coke.png` - Demonstrates simple, readable item icons
- `item_steel_ingot.png` - Shows metal material representation

---

## Summary

**DrillDown's visual identity is: Minimalist, flat, industrial, geometric, and functional.**

Graphics should look like **industrial engineering diagrams** brought to life with **subtle material textures** and **muted colors**, viewed from **directly above** in a **clean, readable style** that prioritizes **gameplay clarity** over decorative detail.

When in doubt: **Simpler is better.** Keep it flat, keep it geometric, keep it industrial.

---

*Generated for DrillDown - Enhanced Edition*  
*Texture System Architecture Documentation*

