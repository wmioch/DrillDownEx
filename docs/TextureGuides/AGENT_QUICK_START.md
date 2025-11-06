# Texture Packing Quick Start for Coding Agents

**Read this FIRST if you're adding graphics to Drill Down.**

---

## The Critical Fact

**Building the game does NOT pack textures.**

If you add a texture and wonder why it's not appearing, you forgot this step:

```bash
.\gradlew.bat desktop:run --args="textures"
```

**That's the most common mistake.** Everything else is secondary.

---

## 30-Second Summary

1. **Create PNG**: Put in `Development/Textures/item_name.png` (or `structure_name.png`, etc.)
2. **Add Code**: Reference texture name in `ItemType`/`StructureType`/etc enum
3. **Pack Textures**: Run `.\gradlew.bat desktop:run --args="textures"`
4. **Rebuild**: `.\Build-Game.ps1` (this is just the normal build, separate from packing)
5. **Test**: Launch game and verify texture appears

That's it. See detailed guides for specifics.

---

## The Three Things That Must Match

**These must be IDENTICAL or the game crashes:**

1. **PNG Filename**: `Development/Textures/item_sword.png`
2. **Code Reference**: `"item_sword"` (in ItemType enum or ProducerSchema)
3. **Atlas Entry**: Automatically created by TexturePacker

If any one is wrong, you get: `"could not find texture" error`

---

## The Process in Flow

```
You add PNG file
        ↓
You update code to reference it
        ↓
YOU RUN: .\gradlew.bat desktop:run --args="textures"
        ↓
TexturePacker creates/updates tex.atlas and tex.png
        ↓
YOU RUN: .\Build-Game.ps1 (normal build)
        ↓
Game launches
        ↓
Game reads tex.atlas and finds your texture ✓
```

**Key:** Steps 3 and 5 are DIFFERENT things. Both are necessary.

---

## Troubleshooting

### "could not find texture" crash
- [ ] PNG filename = code reference (check spelling/underscores)
- [ ] PNG is in `Development/Textures/`
- [ ] You ran `.\gradlew.bat desktop:run --args="textures"`
- [ ] You waited for it to finish
- [ ] You ran the build AFTER packing

### Texture doesn't show up
- [ ] Did you pack textures? Run: `.\gradlew.bat desktop:run --args="textures"`
- [ ] Did you rebuild after packing?
- [ ] Is the texture name in `tex.atlas`? (open it and search)

### "Multiple atlas pages created" warning
- [ ] There are `tex2.png` and `tex2.atlas` files
- [ ] Edit `android/assets/atlas-settings.json`: increase `maxWidth` and `maxHeight` to 4096
- [ ] Delete the extra `tex2.*` files
- [ ] Run texture packing again

---

## Command Reference

| What | Command |
|------|---------|
| Pack textures | `.\gradlew.bat desktop:run --args="textures"` |
| Build game | `.\Build-Game.ps1` |
| Build + Run | `.\gradlew.bat desktop:run` |
| Clean build | `.\gradlew.bat clean desktop:dist` |

---

## File Locations

| File/Folder | Purpose |
|-------------|---------|
| `Development/Textures/` | Where you PUT texture PNGs |
| `android/assets/tex.png` | Generated packed texture (DO NOT edit) |
| `android/assets/tex.atlas` | Generated descriptor (DO NOT edit) |
| `core/src/de/dakror/quarry/game/Item.java` | Define new items |
| `core/src/de/dakror/quarry/structure/base/StructureType.java` | Define new structures |

---

## Common Naming Patterns

```
Items:      item_[name].png      → ItemType enum
Structures: structure_[name].png  → StructureType enum
Icons:      icon_[name].png       → ScienceType enum
Tiles:      tile_[name].png       → TileType enum
Fluids:     fluid_[name].png      → FluidType enum
```

**Rule:** Filename (minus `.png`) = what you write in code

---

## Next Steps

- [ ] Done with quick start? Read `TEXTURE_IMPLEMENTATION_CHECKLIST.md` for detailed steps
- [ ] Need deep understanding? Read `TEXTURE_SYSTEM_ARCHITECTURE.md`
- [ ] Have a problem? Read `TEXTURE_PACKING_GUIDE.md` troubleshooting section
- [ ] Want best practices? Read `TEXTURE_PATTERNS_AND_TIPS.md`

---

## Golden Rule

**If the game crashes with "could not find texture", 90% of the time you forgot to run:**

```bash
.\gradlew.bat desktop:run --args="textures"
```

Seriously. It's always this.

---

*This guide is specifically for AI agents to prevent the common mistakes that happen during texture implementation.*

