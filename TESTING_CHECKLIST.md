# ITEM ELEVATOR COMPREHENSIVE TESTING CHECKLIST

## UI ELEMENT TESTING

### 1. Floor Selection Dialog
- [x]  Dialog title: "Select Destination Floor" displays correctly without overlapping borders
- [X ] Subtitle "Choose target floor:" displays with proper spacing
- [X ] Up button (▲) is clickable and rotates floor selector up
- [X ] Down button (▼) is clickable and rotates floor selector down  
- [X ] Current floor number displays correctly in white text
- [x ] Distance shows in format: "Floor (Distance: X)"
- [x ] Confirm button (→) has visible arrow icon
- [x ] Cancel button (✕) has visible X icon
- [ ] Invalid floor changes text color to red
- [x ] Dialog centers on screen
- [x ] Dialog is modal (blocks interaction behind it)
- [x ] Up/down buttons disable at floor boundaries

### 2. Build Menu Integration
- [x ] Item Elevator appears in Routing & Logistics menu after ItemLift
- [x ] Item Elevator name displays: "Item Elevator"
- [x ] Item Elevator description visible in tooltip
- [ x] Item Elevator can be selected and placed

### 3. Placement Preview
- [ ] Preview shows at edge positions (not corners)
- [x ] Preview only shows at valid edges (not middle of map)
- [ ] Blue tint visible on preview structure
- [ ] Dock direction shown correctly in preview before rotation
- [ ] Preview rotates when rotate button pressed
- [ ] Preview updates when floor selection changes

## PLACEMENT TESTING

### 4. Edge Placement (All 4 Edges)

#### North Edge (Top)
- [ ] Can place at north edge (y = max)
- [ ] Cannot place at north corners (x = 0 or x = max)
- [ ] Floor selection dialog appears on placement
- [ ] Input dock points South (toward center)
- [ ] Output dock points South (same direction)
- [ ] Passthroughs created on intermediate floors

#### South Edge (Bottom)
- [ ] Can place at south edge (y = 0)
- [ ] Cannot place at south corners (x = 0 or x = max)
- [ ] Floor selection dialog appears on placement
- [ ] Input dock points North (toward center)
- [ ] Output dock points North (same direction)
- [ ] Passthroughs created on intermediate floors

#### East Edge (Right)
- [ ] Can place at east edge (x = max)
- [ ] Cannot place at east corners (y = 0 or y = max)
- [ ] Floor selection dialog appears on placement
- [ ] Input dock points West (toward center)
- [ ] Output dock points West (same direction)
- [ ] Passthroughs created on intermediate floors

#### West Edge (Left)
- [ ] Can place at west edge (x = 0)
- [ ] Cannot place at west corners (y = 0 or y = max)
- [ ] Floor selection dialog appears on placement
- [ ] Input dock points East (toward center)
- [ ] Output dock points East (same direction)
- [ ] Passthroughs created on intermediate floors

### 5. Invalid Placements (Should Reject)
- [ ] Corner placement rejected (e.g., x=0, y=0)
- [ ] Middle of map placement rejected
- [ ] Placement in fog of war rejected
- [ ] Placement on occupied space rejected
- [ ] Placement with occupied destination rejected
- [ ] Same floor selection rejected (shows red text)

## DOCK DIRECTION TESTING

### 6. Rotation Behavior
- [ ] Rotate structure BEFORE placement confirmation
- [ ] Dock direction does NOT change when rotating
- [ ] Input dock stays pointing same direction after rotation
- [ ] Output dock stays pointing same direction after rotation
- [ ] Preview shows correct direction after rotation

### 7. Direction Consistency
- [ ] Input dock direction matches edge position
- [ ] Output dock direction matches input dock direction (NOT opposite)
- [ ] All passthroughs between floors have correct direction info

## ITEM TRANSPORT TESTING

### 8. Item Acceptance and Transport
- [ ] Items can be placed on input dock
- [ ] Item appears in elevator (not lost)
- [ ] Item instantly transfers to output floor
- [ ] Item exits from output dock in correct direction
- [ ] Multiple items can be transported sequentially
- [ ] Output conveyor receives items correctly

### 9. Output Validation
- [ ] Elevator checks output connection before accepting items
- [ ] If output blocked, items backup (normal behavior)
- [ ] When output clears, items resume flow

## SAVE/LOAD TESTING

### 10. Data Persistence
- [ ] Create Item Elevator on a specific edge
- [ ] Save game
- [ ] Load game
- [ ] Elevator exists at same position: **YES**
- [ ] Dock directions preserved: **YES**
- [ ] Input still points correct direction after load
- [ ] Output still points correct direction after load
- [ ] Target floor information preserved
- [ ] Bidirectional links (input ↔ output) restored

### 11. Complex Save/Load
- [ ] Create multiple elevators on different edges
- [ ] Save game
- [ ] Load game
- [ ] All elevators present: **YES**
- [ ] All dock directions correct: **YES**
- [ ] No cross-linking between elevators: **VERIFIED**

## EDGE CASE TESTING

### 12. Corner Rejection
- [ ] Cannot place at (x=0, y=0)
- [ ] Cannot place at (x=0, y=max)
- [ ] Cannot place at (x=max, y=0)
- [ ] Cannot place at (x=max, y=max)
- [ ] Error message shows or placement simply rejects

### 13. Floor Validation
- [ ] Cannot select floor 0 when placing on floor 0
- [ ] Cannot select non-existent floor (below bottom)
- [ ] Can select any valid layer between boundaries
- [ ] Distance calculation correct (abs difference)

### 14. Destination Conflicts
- [ ] Cannot place exit on occupied space
- [ ] Cannot place exit in fog of war
- [ ] Cannot place passthroughs on occupied spaces

## VISUAL TESTING

### 15. Blue Tinting
- [ ] Input elevator has blue tint
- [ ] Output elevator has blue tint
- [ ] Passthrough structures have blue tint
- [ ] Tint is consistent (0.7, 0.7, 1.0 ratio)
- [ ] Tint visible but not obnoxiously bright

### 16. UI Descriptions
- [ ] Input elevator has description in tooltip
- [ ] Output elevator has description in tooltip
- [ ] Passthrough has description in tooltip
- [ ] Descriptions explain purpose clearly
- [ ] Floor information shown in descriptions (e.g., "Floors: 0 ↔ -5")

### 17. Visual Hierarchy
- [ ] Elevators visually distinct from regular ItemLifts
- [ ] Blue tint makes identification easy
- [ ] Docks visible and properly oriented
- [ ] All three elevator types easily distinguishable

## INTEGRATION TESTING

### 18. Complete Workflow
- [ ] Select Item Elevator from menu
- [ ] Navigate to edge position
- [ ] Floor selection dialog appears
- [ ] Select valid target floor
- [ ] Placement cost shows correctly
- [ ] Confirm placement
- [ ] Input, output, and passthroughs all created
- [ ] Connect item source to input dock
- [ ] Connect conveyor to output dock
- [ ] Items flow through elevator successfully

### 19. Multi-Floor Setup
- [ ] Create elevator from floor 0 to floor -3
- [ ] Items visible traveling through all layers
- [ ] No items lost in transit
- [ ] Output correctly positioned on floor -3

### 20. Multiple Elevators
- [ ] Create two elevators on different edges
- [ ] Both function independently
- [ ] No interference between them
- [ ] Items route correctly to each

## TEST RESULTS SUMMARY

Write your test results here:
- UI Elements: PASS / FAIL - Details:
- Placement (All Edges): PASS / FAIL - Details:
- Dock Directions: PASS / FAIL - Details:
- Item Transport: PASS / FAIL - Details:
- Save/Load: PASS / FAIL - Details:
- Edge Cases: PASS / FAIL - Details:
- Visual: PASS / FAIL - Details:
- Integration: PASS / FAIL - Details:

OVERALL STATUS: ___________

Any Bugs Found: ___________
Any Incomplete Features: ___________
Any Untested Elements: ___________

