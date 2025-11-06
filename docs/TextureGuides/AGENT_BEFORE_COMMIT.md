# Agent Checklist: Before You Commit Texture Changes

**Use this checklist before committing ANY texture changes to verify you didn't miss the texture packing step.**

---

## ❌ Common Mistakes That Get Caught Here

These are mistakes every coding agent makes at least once:

1. **"I edited textures but forgot to pack them"** ← Most common
2. **"I packed textures but used the wrong command"** ← Also very common
3. **"The names don't match between file and code"** ← Easy to miss
4. **"I built the game but forgot to pack textures first"** ← Confusing step order

---

## Pre-Commit Verification (5 minutes)

### Step 1: Did I Pack Textures?

**Question:** Did I add OR edit any PNG files in `Development/Textures/`?

- [ ] **YES** → Continue to Step 2
- [ ] **NO** → Skip to Step 5 (normal commit)

### Step 2: Did I Run TexturePacker?

**Question:** Did I run this command?
```bash
.\gradlew.bat desktop:run --args="textures"
```

- [ ] **YES and it completed successfully** → Continue to Step 3
- [ ] **NO** → STOP! Go run it now before committing:
  ```bash
  cd D:\Projects\DrillDown
  .\gradlew.bat desktop:run --args="textures"
  ```
- [ ] **YES but it failed** → Check the error message, fix it, then run again

### Step 3: Do the Files Exist and Are Updated?

**Question:** Check these files have recent timestamps:
- `android/assets/tex.png` ← Should be updated when you ran TexturePacker
- `android/assets/tex.atlas` ← Should be updated when you ran TexturePacker

**From command line, verify files are recent:**
```bash
dir android/assets/tex.*
```

- [ ] Both files show a recent timestamp (within last few minutes)
- [ ] **If not:** Something went wrong with TexturePacker. Run it again.

### Step 4: Verify Texture Names

**For each new texture you added:**

| PNG Filename | Code Reference | Atlas Entry | Match? |
|---|---|---|---|
| `item_sword.png` | `"item_sword"` in ItemType | Should exist in tex.atlas | ✓ |
| `structure_furnace.png` | `"structure_furnace"` in ProducerSchema | Should exist in tex.atlas | ✓ |

**How to verify Atlas entry exists:**
```bash
# Search for your texture in tex.atlas
findstr /i "item_sword" android/assets/tex.atlas
```

- [ ] All texture names match exactly
- [ ] All textures appear in `tex.atlas`
- [ ] **If not:** The names don't match. Fix them and re-pack.

### Step 5: Did You Rebuild?

**After packing, did you rebuild the game?**

```bash
.\Build-Game.ps1
```

Or if that's not working:
```bash
.\gradlew.bat desktop:dist
```

- [ ] **YES and build succeeded** → Continue to Step 6
- [ ] **NO** → Go build it now before committing

### Step 6: Did You Test?

**Did you launch the game and verify new textures appear?**

```bash
.\gradlew.bat desktop:run
```

- [ ] **YES, textures appear correctly** → You're ready to commit
- [ ] **NO or textures don't appear** → DO NOT COMMIT. The texture packing step may have failed.

---

## Files to Stage for Commit

**Only commit these files together:**

```bash
git add Development/Textures/[your_new_textures].png
git add android/assets/tex.png
git add android/assets/tex.atlas
git add [any code changes you made]
```

**If you forgot any of these, the build will break for others.**

---

## One-Command Summary

Before you commit, run this sequence:

```bash
# 1. Pack textures
.\gradlew.bat desktop:run --args="textures"

# 2. Rebuild
.\Build-Game.ps1

# 3. Test
.\gradlew.bat desktop:run

# If all 3 completed successfully, you can commit
```

---

## What to Commit (Don't Miss These)

```bash
git add Development/Textures/item_youritem.png
git add android/assets/tex.png
git add android/assets/tex.atlas
git add core/src/de/dakror/quarry/game/Item.java  # if you added code
git commit -m "feat: add [description of new textures]"
```

**DO NOT forget `tex.png` and `tex.atlas`!** Other developers need these files.

---

## What NOT to Commit

❌ `tex2.png`, `tex2.atlas`, `tex3.png` - These shouldn't exist
❌ Temporary test files in `Development/Textures/`
❌ Backup or `.bak` files anywhere

---

## If You Messed Up (Too Late to Prevent)

**Git hasn't pushed yet? You can fix it:**

```bash
# Undo the commit (keeps your changes)
git reset --soft HEAD~1

# Re-pack if needed
.\gradlew.bat desktop:run --args="textures"

# Re-stage with the fixed files
git add android/assets/tex.*
git add [your code]

# Re-commit
git commit -m "feat: add [description] - fixed texture packing"
```

---

## The Golden Moment Before Commit

**Ask yourself:**

- [ ] Did I add/edit PNGs in `Development/Textures/`? If YES:
  - [ ] Did I run `.\gradlew.bat desktop:run --args="textures"`?
  - [ ] Did I see `tex.png` and `tex.atlas` get updated?
  - [ ] Did I rebuild with `.\Build-Game.ps1`?
  - [ ] Did I test and see the textures in-game?
  - [ ] Am I committing `tex.png` and `tex.atlas`?

**If you answered NO to any of these, STOP and fix it first.**

---

## Why This Matters

When you commit texture changes but forget to pack them:
- The committed `tex.atlas` doesn't have your new textures
- Others pull the code but the textures don't load
- The game crashes for everyone with "could not find texture"
- Everyone blames you (rightfully)

**Just run the steps above. It takes 5 minutes and prevents 2 hours of debugging.**

---

*This checklist exists because even AI agents forget the texture packing step. You will too. Use this list.*

