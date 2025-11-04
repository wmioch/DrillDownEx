# Build Error Fixed ✅

## Issue
Compilation error in `FloorSelectionDialog.java` and `Game.java`:
```
error: cannot find symbol
import de.dakror.quarry.util.Callback;
```

## Root Cause
Incorrect import path. The `Callback` class is located in `de.dakror.common.Callback`, not `de.dakror.quarry.util.Callback`.

## Fix Applied

### FloorSelectionDialog.java
**Changed:**
```java
import de.dakror.quarry.util.Callback;
```

**To:**
```java
import de.dakror.common.Callback;
```

### Game.java
**Changed:**
```java
new de.dakror.quarry.util.Callback<Integer>() { ... }
new de.dakror.quarry.util.Callback<Boolean>() { ... }
```

**To:**
```java
new Callback<Integer>() { ... }
new Callback<Boolean>() { ... }
```

(The correct import `import de.dakror.common.Callback;` was already present in Game.java)

## Verification
✅ No linter errors detected
✅ Correct import path now used in all files
✅ Ready to compile

## Status
**Build should now succeed!** Please try building again.

