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

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.dakror.common.libgdx.io.NBT.Builder;
import de.dakror.common.libgdx.io.NBT.CompoundTag;
import de.dakror.common.libgdx.io.NBT.NBTException;
import de.dakror.common.libgdx.render.SpriteRenderer;
import de.dakror.quarry.Const;
import de.dakror.quarry.Quarry;
import de.dakror.quarry.structure.base.Direction;
import de.dakror.quarry.structure.base.Dock;
import de.dakror.quarry.structure.base.Dock.DockType;
import de.dakror.quarry.structure.base.Schema;
import de.dakror.quarry.structure.base.StructureType;
import de.dakror.quarry.structure.base.component.CInventory;
import de.dakror.quarry.structure.base.ISpecialRenderer;
import de.dakror.quarry.structure.base.RenderingStyle;

/**
 * Exit elevator on destination floor
 * 
 * @author Maximilian Stark | Dakror
 */
public class ItemElevatorExit extends ItemElevator implements ISpecialRenderer {
    public static final Schema classSchema = new Schema(0, StructureType.ItemElevatorExit, true, 1, 1,
            "itemlift", // Reuse itemlift sprite but with tint
            ItemElevator.classSchema.buildCosts, // Same costs as parent
            null, 
            new Dock(0, 0, Direction.East, DockType.ItemOut))
                    .components(new CInventory(1, 0));

    public ItemElevatorExit(int x, int y) {
        super(x, y, false, classSchema);
    }
    
    /**
     * Constructor with custom dock direction
     */
    public ItemElevatorExit(int x, int y, Direction dockDirection) {
        super(x, y, false, classSchema);
        // Set the custom dock direction through the parent's method
        setDockDirection(dockDirection);
        // Replace the default dock with one in the correct direction
        docks = new Dock[] { new Dock(0, 0, dockDirection, DockType.ItemOut) };
    }
    
    /**
     * Override to set output dock direction for exit elevator
     */
    @Override
    public void setDockDirection(Direction direction) {
        customDockDirection = direction;
        docks = new Dock[] { new Dock(0, 0, direction, DockType.ItemOut) };
    }
    
    @Override
    public void setUpDirection(Direction upDirection) {
        this.upDirection = upDirection;
        setDirty();
        
        // Only reset docks from schema if we don't have a custom dock direction
        if (customDockDirection == null) {
            docks = schema.copyDocks(this);
        }
        // Otherwise keep custom docks - don't reset them
    }
    
    @Override
    protected void loadData(CompoundTag tag) throws NBTException {
        super.loadData(tag);
        // customDockDirection is loaded in parent class (ItemElevator.loadData)
    }
    
    @Override
    public void postLoad() {
        // IMPORTANT: Store customDockDirection before super.postLoad()
        Direction savedCustomDir = customDockDirection;
        
        // Call parent's postLoad which will call ItemElevator.postLoad
        super.postLoad();
        
        // CRITICAL: Directly restore the dock from the loaded customDockDirection
        // Do NOT use docks[0].dir as it may have been reset by super.postLoad()
        if (savedCustomDir != null) {
            customDockDirection = savedCustomDir;
            docks = new Dock[] { new Dock(0, 0, customDockDirection, DockType.ItemOut) };
        }
    }
    
    /**
     * Gets a detailed description including floor information
     */
    public String getDetailedDescription() {
        if (targetLayerIndex >= 0 && targetElevator != null) {
            int sourceFloor = -targetLayerIndex;
            int destFloor = -layer.getIndex();
            return getSchema().description + "\nFloors: " + sourceFloor + " ↔ " + destFloor;
        }
        return getSchema().description;
    }
    
    @Override
    public void draw(de.dakror.common.libgdx.render.SpriteRenderer spriter) {
        // Tinting is handled at the Chunk level in drawFrameStructures for ISpecialRenderer structures
        // Just call super.draw to add sprites to the batch with whatever tint is currently set
        super.draw(spriter);
    }

    @Override
    public RenderingStyle getRenderingStyle() {
        return RenderingStyle.BLUE_TINT;
    }
}

