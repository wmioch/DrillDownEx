# Item Elevator Critical Fix - Icons Tinting Bug

## 🐛 Critical Bug Found and Fixed

**Issue:** Icons (storage full, sleeping, blocked) were receiving the blue tint because `drawFrame()` was being called WITH the tint applied in Pass 1.

**Impact:** HIGH - Icons should NEVER be tinted, only structure sprites should be tinted.

---

## ❌ What Was Wrong (Before Fix)

In `Chunk.drawFrameStructures()`:

```java
// Pass 1: ISpecialRenderer structures
for (Structure<?> st : structures.items) {
    if (st instanceof ISpecialRenderer) {
        // Apply tint
        spriter.setTintColor(tint.r, tint.g, tint.b, tint.a);
        
        st.draw(spriter);           // ✅ Correct - structure with tint
        st.drawFrame(spriter, ...); // ❌ WRONG - icons with tint!
        
        spriter.flush();
    }
}

// Pass 2: Only non-ISpecialRenderer structures
for (Structure<?> st : structures.items) {
    if (!(st instanceof ISpecialRenderer)) {
        st.drawFrame(spriter, ...); // Only non-tinted structures
    }
}
```

**Problem:** Icons for ISpecialRenderer structures were rendered WITH the blue tint in Pass 1.

---

## ✅ What Is Correct (After Fix)

In `Chunk.drawFrameStructures()`:

```java
// Pass 1: Render ONLY structure sprites with tint for ISpecialRenderer
for (Structure<?> st : structures.items) {
    if (st instanceof ISpecialRenderer) {
        // Apply tint
        spriter.setTintColor(tint.r, tint.g, tint.b, tint.a);
        
        st.draw(spriter); // ✅ ONLY structure sprite with tint
        // NO drawFrame() call here!
        
        spriter.flush();
        spriter.setTintColor(1.0f, 1.0f, 1.0f, 1.0f); // Reset to white
    }
}

// Pass 2: Render ALL frame elements with white tint
for (Structure<?> st : structures.items) {
    st.drawFrame(spriter, ...); // ✅ ALL structures, no tint check
}
```

**Solution:** 
1. Pass 1 draws ONLY structure sprites (`st.draw()`) with tint
2. Pass 2 draws ALL frame elements (`st.drawFrame()`) with white tint
3. Icons for ALL structures (including ISpecialRenderer) are rendered in Pass 2 with NO tint

---

## 📋 Key Insight from Documentation

From `ITEM_ELEVATOR_FIXES_GUIDE.md` lines 299-316:

> **Pass 1:** Render all ISpecialRenderer structures with their custom tints
> ```java
> st.draw(spriter);  // Draw structure with tint
> ```
> 
> **Pass 2:** Render all frame elements (icons, items, etc.) with white tint
> ```java
> st.drawItemNotifications(spriter);
> st.drawStates(spriter);
> ```

The documentation makes it VERY clear:
- **Pass 1:** Structure sprites ONLY
- **Pass 2:** Frame elements (icons) for ALL structures

---

## 🎯 What Each Pass Does

### Pass 1: Structure Sprites with Tint
**Purpose:** Render the actual structure graphics (the Item Elevator sprite itself) with blue tint

**What gets rendered:**
- ✅ Item Elevator base sprite (blue)
- ✅ Item Elevator Exit sprite (blue)
- ✅ Item Elevator Passthrough sprite (blue)

**What does NOT get rendered:**
- ❌ Storage full icons
- ❌ Sleeping icons
- ❌ Blocked icons
- ❌ Item notifications
- ❌ Any overlay graphics

### Pass 2: Frame Elements WITHOUT Tint
**Purpose:** Render icons, notifications, and overlay elements for ALL structures with normal colors

**What gets rendered:**
- ✅ Storage full icons (white/red/green - normal colors)
- ✅ Sleeping icons (normal colors)
- ✅ Blocked icons (normal colors)
- ✅ Item notifications (normal colors)
- ✅ All overlay graphics (normal colors)

**Critical:** This pass renders frame elements for BOTH tinted AND non-tinted structures.

---

## 🔍 Why This Bug Was Subtle

The bug was subtle because:

1. **Misleading naming:** `drawFrame()` sounds like it should be called once per structure, but actually it should be called AFTER tint is reset
2. **Pass separation:** The two-pass system isn't obvious - you might think "render everything for structure A, then structure B"
3. **Tint reset timing:** The tint must be reset to white BEFORE rendering any frame elements

---

## ✅ Verification

After this fix:
- ✅ Item Elevator sprite: Blue tinted
- ✅ Item Elevator Exit sprite: Blue tinted
- ✅ Item Elevator Passthrough sprite: Blue tinted
- ✅ Storage full icons: NOT tinted (normal colors)
- ✅ Sleeping icons: NOT tinted (normal colors)
- ✅ Blocked icons: NOT tinted (normal colors)
- ✅ Items on conveyors: NOT tinted (normal colors)

---

## 📝 Summary of Fix

**File:** `core/src/de/dakror/quarry/game/Chunk.java`  
**Method:** `drawFrameStructures()`  
**Lines Changed:** ~15 lines

**Change:**
1. Removed `st.drawFrame()` call from Pass 1 (line 525)
2. Removed instanceof check from Pass 2 (line 539)
3. Now Pass 2 renders frame elements for ALL structures

**Result:** Icons are never tinted, only structure sprites are tinted.

---

## 🎓 Lesson Learned

When implementing a two-pass rendering system:
1. **Be explicit** about what each pass renders
2. **Document clearly** which methods are called in which pass
3. **Test boundaries** - check that tint doesn't bleed to unintended elements
4. **Follow the guide exactly** - the separation of `draw()` and `drawFrame()` is critical

---

**Status:** ✅ FIXED  
**Date:** Current Session  
**Severity:** HIGH (Icons would have been blue tinted)


