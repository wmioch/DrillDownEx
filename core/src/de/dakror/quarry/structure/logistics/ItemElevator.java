/*******************************************************************************
 * Copyright 2018 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.dakror.quarry.structure.logistics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.dakror.common.libgdx.io.NBT.Builder;
import de.dakror.common.libgdx.io.NBT.CompoundTag;
import de.dakror.common.libgdx.io.NBT.NBTException;
import de.dakror.quarry.game.Item.ItemCategory;
import de.dakror.quarry.game.Item.ItemType;
import de.dakror.quarry.game.Item.Items;
import de.dakror.quarry.game.Layer;
import de.dakror.quarry.game.Science.ScienceType;
import de.dakror.quarry.scenes.Game;
import de.dakror.quarry.structure.base.Direction;
import de.dakror.quarry.structure.base.Dock;
import de.dakror.quarry.structure.base.Dock.DockType;
import de.dakror.quarry.structure.base.Schema;
import de.dakror.quarry.structure.base.Structure;
import de.dakror.quarry.structure.base.StructureType;
import de.dakror.quarry.util.Bounds;
import de.dakror.common.libgdx.render.SpriteRenderer;
import de.dakror.quarry.structure.base.ISpecialRenderer;
import de.dakror.quarry.structure.base.RenderingStyle;

/**
 * @author Maximilian Stark | Dakror
 */
public class ItemElevator extends Structure<Schema> implements ISpecialRenderer {
    public static final Schema classSchema = new Schema(0, StructureType.ItemElevator, true, 1, 1,
            "itemlift",
            new Items(ItemType.SteelIngot, 10, ItemType.StoneBrick, 4, ItemType.MachineFrame, 2),
            null,
            new Dock(0, 0, Direction.West, DockType.ItemIn))
                    .sciences(ScienceType.MineExpansion, ScienceType.Routers, ScienceType.AdvancedTransport);

    // Blue tint for visual distinction
    protected static final Color ELEVATOR_TINT = new Color(0.7f, 0.7f, 1.0f, 1.0f);

    ItemElevator targetElevator;
    boolean hasOutput;
    boolean isInput; // true for input elevator, false for exit or passthrough
    int targetLayerIndex = -1; // -1 means not configured
    
    ItemType currentItem;
    Structure<?> currentSource;

    public ItemElevator(int x, int y) {
        this(x, y, true, classSchema);
    }

    protected ItemElevator(int x, int y, boolean isInput, Schema schema) {
        super(x, y, schema);
        this.isInput = isInput;
    }
    
    // Store the dock direction so rotation doesn't reset it
    protected Direction customDockDirection = null;
    
    /**
     * Sets the dock direction after construction (called during placement validation)
     */
    public void setDockDirection(Direction direction) {
        customDockDirection = direction;
        if (isInput) {
            docks = new Dock[] { new Dock(0, 0, direction, DockType.ItemIn) };
        }
    }
    
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
    
    /**
     * Creates the exit elevator and passthrough structures after main elevator is placed
     */
    private void createElevatorStructures() {
        int startLayer = Math.min(layer.getIndex(), targetLayerIndex);
        int endLayer = Math.max(layer.getIndex(), targetLayerIndex);
        
        for (int i = startLayer; i <= endLayer; i++) {
            Layer l = Game.G.getLayer(i);
            if (l == null) continue;
            
            // Bounds check: ensure the elevator position is valid on this layer
            if (x < 0 || x >= l.width || y < 0 || y >= l.height) {
                continue; // Skip layers where position is out of bounds
            }
            
            if (i == layer.getIndex()) {
                // Source floor - already placed
                continue;
            } else if (i == targetLayerIndex) {
                // Destination floor - create exit elevator with same dock direction as input
                // Use stored customDockDirection if available, otherwise fall back to current docks
                Direction exitDockDir = customDockDirection != null ? customDockDirection : docks[0].dir;
                ItemElevatorExit exit = new ItemElevatorExit(x, y, exitDockDir);
                exit.setUpDirection(upDirection);
                
                exit.targetLayerIndex = layer.getIndex();
                exit.targetElevator = this;
                this.targetElevator = exit;
                l.addStructure(exit);
            } else {
                // Intervening floor - create passthrough
                ItemElevatorPassthrough passthrough = new ItemElevatorPassthrough(x, y);
                passthrough.setUpDirection(upDirection);
                passthrough.sourceLayerIndex = layer.getIndex();
                passthrough.targetLayerIndex = targetLayerIndex;
                l.addStructure(passthrough);
            }
        }
        
        updateOutput();
        setItemNotifications();
        if (targetElevator != null) {
            targetElevator.updateOutput();
            targetElevator.setItemNotifications();
        }
    }
    
    /**
     * Called when user selects a target floor during placement.
     * Stores the target and validates it. Actual structure creation happens in onPlacement.
     */
    public boolean setTargetFloor(int targetIndex) {
        // This method is called before the structure is placed, so layer might be null
        // We'll use Game.G.layer instead
        Layer sourceLayer = layer != null ? layer : Game.G.layer;
        
        if (targetIndex == sourceLayer.getIndex()) {
            return false; // Can't target same floor
        }
        
        Layer targetLayer = Game.G.getLayer(targetIndex);
        if (targetLayer == null) {
            return false;
        }
        
        // Check if target position is valid
        if (targetLayer.getStructure(x, y) != null) {
            return false; // Position occupied
        }
        
        // Create a temporary structure for fog of war check
        ItemElevator temp = new ItemElevator(x, y);
        if (!targetLayer.isNotInFogOfWar(temp, false)) {
            return false; // In fog of war
        }
        
        this.targetLayerIndex = targetIndex;
        
        // Note: Cost calculation done elsewhere - schema.buildCosts is final
        
        return true;
    }
    
    /**
     * Removes all elevator structures between source and old target
     */
    protected void removeElevatorStructures(int oldTargetIndex) {
        int startLayer = Math.min(layer.getIndex(), oldTargetIndex);
        int endLayer = Math.max(layer.getIndex(), oldTargetIndex);
        
        for (int i = startLayer; i <= endLayer; i++) {
            if (i == layer.getIndex()) continue; // Don't remove self
            
            Layer l = Game.G.getLayer(i);
            if (l == null) continue;
            
            Structure<?> s = l.getStructure(x, y);
            if (s instanceof ItemElevator || s instanceof ItemElevatorPassthrough) {
                l.removeStructure(s);
            }
        }
    }

    protected void updateOutput() {
        if (!isInput) return; // Only input elevator checks output
        
        if (targetElevator == null) {
            hasOutput = false;
            return;
        }
        
        Direction d = targetElevator.getDocks()[0].dir;
        Structure<?> s = targetElevator.layer.getStructure(
            targetElevator.x + d.dx, 
            targetElevator.y + d.dy
        );
        
        hasOutput = s != null
                && ((s.getSchema().type == StructureType.Conveyor && ((Conveyor) s).getDirection() != d.inv())
                        || (s.getSchema().type == StructureType.ElectricConveyor && ((Conveyor) s).getDirection() != d.inv())
                        || (s.getSchema().type == StructureType.ElectricConveyorCore && ((Conveyor) s).getDirection() == d)
                        || (s.getSchema().type == StructureType.ConveyorBridge && (((ConveyorBridge) s).getDirection() != d.inv() || ((ConveyorBridge) s).getDirection2() != d.inv()))
                        || (s.getSchema().type == StructureType.Hopper && ((Conveyor) s).getDirection() == d));
    }

    @Override
    public void update(double deltaTime, int gameSpeed, Bounds dirtyBounds) {
        super.update(deltaTime, gameSpeed, dirtyBounds);

        if (dirtyBounds.touches(this) && targetElevator != null) {
            updateOutput();
            targetElevator.updateOutput();
            setItemNotifications();
            targetElevator.setItemNotifications();
        }

        // Transfer item instantly to target elevator
        if (currentItem != null && targetElevator != null && targetElevator.layer != null) {
            if (targetElevator.layer.addItemEntity(currentItem, targetElevator, 
                    targetElevator.getDocks()[0].dir, currentSource)) {
                currentItem = null;
                currentSource = null;
                setItemNotifications();
                targetElevator.setItemNotifications();
            }
        }
    }

    @Override
    public boolean canAccept(ItemType item, int x, int y, Direction dir) {
        if (!isInput) return false; // Only input elevator accepts items
        
        return !item.categories.contains(ItemCategory.Fluid) 
            && isNextToDock(x, y, dir, getDocks()[0]) 
            && targetElevator != null
            && hasOutput 
            && currentItem == null;
    }

    @Override
    public boolean acceptItem(ItemType item, Structure<?> source, Direction dir) {
        if (!isInput) return false;
        
        if (item.categories.contains(ItemCategory.Fluid) 
            || targetElevator == null 
            || !hasOutput 
            || currentItem != null) {
            return false;
        }
        
        currentItem = item;
        currentSource = source;
        setItemNotifications();
        targetElevator.setItemNotifications();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Remove all related structures (exit and passthroughs)
        if (isInput && targetLayerIndex >= 0) {
            removeElevatorStructures(targetLayerIndex);
        }
    }
    
    @Override
    public void postLoad() {
        // IMPORTANT: Store customDockDirection before super.postLoad()
        // because super.postLoad() may call setUpDirection() which could reset docks
        Direction savedCustomDir = customDockDirection;
        
        super.postLoad();
        
        // CRITICAL: Restore customDockDirection after super.postLoad()
        // The savedCustomDir is what was loaded from the save file
        if (savedCustomDir != null) {
            customDockDirection = savedCustomDir;
            // Reapply the dock based on the loaded custom direction
            if (isInput) {
                docks = new Dock[] { new Dock(0, 0, customDockDirection, DockType.ItemIn) };
            }
        }
        
        // Reconnect to target elevator if this is an input elevator
        if (isInput && targetLayerIndex >= 0) {
            Layer targetLayer = Game.G.getLayer(targetLayerIndex);
            if (targetLayer != null) {
                targetElevator = (ItemElevator) targetLayer.getStructure(x, y);
                if (targetElevator != null) {
                    targetElevator.targetElevator = this;
                    updateOutput();
                    setItemNotifications();
                    targetElevator.setItemNotifications();
                }
            }
        }
    }
    
    @Override
    protected void saveData(Builder b) {
        super.saveData(b);
        b.Int("targetLayer", targetLayerIndex)
         .Byte("isInput", (byte) (isInput ? 1 : 0))
         .Short("item", currentItem != null ? currentItem.value : 0)
         .Byte("customDockDir", customDockDirection != null ? (byte) customDockDirection.ordinal() : (byte) -1);
    }
    
    @Override
    protected void loadData(CompoundTag tag) throws NBTException {
        super.loadData(tag);
        targetLayerIndex = tag.Int("targetLayer", -1);
        isInput = tag.Byte("isInput", (byte) 1) == 1;
        currentItem = de.dakror.quarry.game.Item.get(tag.Short("item", (short) 0));
        
        byte dockDir = tag.Byte("customDockDir", (byte) -1);
        if (dockDir >= 0 && dockDir < Direction.values.length) {
            customDockDirection = Direction.values[dockDir];
            if (isInput) {
                docks = new Dock[] { new Dock(0, 0, customDockDirection, DockType.ItemIn) };
            }
        }
    }
    
    /**
     * Gets a detailed description including floor information
     */
    public String getDetailedDescription() {
        if (isInput && targetLayerIndex >= 0) {
            int sourceFloor = -layer.getIndex();
            int destFloor = -targetLayerIndex;
            return getSchema().description + "\nFloors: " + sourceFloor + " ↔ " + destFloor;
        }
        return getSchema().description;
    }
    
    @Override
    public RenderingStyle getRenderingStyle() {
        return RenderingStyle.BLUE_TINT;
    }

    @Override
    public void draw(de.dakror.common.libgdx.render.SpriteRenderer spriter) {
        // Tinting is handled at the Chunk level in drawFrameStructures for ISpecialRenderer structures
        // Just call super.draw to add sprites to the batch with whatever tint is currently set
        super.draw(spriter);
    }
    
    /**
     * Determines the dock direction based on which edge this elevator is placed on
     */
    public static Direction getDockDirectionForEdge(int x, int y, int layerWidth, int layerHeight) {
        Direction result;
        if (x == 0) {
            result = Direction.East; // West edge, face center (east)
        } else if (x == layerWidth - 1) {
            result = Direction.West; // East edge, face center (west)
        } else if (y == 0) {
            result = Direction.North; // South edge, face center (north)
        } else if (y == layerHeight - 1) {
            result = Direction.South; // North edge, face center (south)
        } else {
            result = Direction.East; // Default
        }
        return result;
    }
}

