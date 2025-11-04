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

import de.dakror.common.libgdx.io.NBT.Builder;
import de.dakror.common.libgdx.io.NBT.CompoundTag;
import de.dakror.common.libgdx.io.NBT.NBTException;
import de.dakror.quarry.game.Item.Items;
import de.dakror.quarry.structure.base.Schema;
import de.dakror.quarry.structure.base.Structure;
import de.dakror.quarry.structure.base.StructureType;
import de.dakror.common.libgdx.render.SpriteRenderer;
import de.dakror.quarry.structure.base.ISpecialRenderer;
import de.dakror.quarry.structure.base.RenderingStyle;

/**
 * Passthrough structure on intervening floors between source and destination
 * Cannot be manually placed - only created by ItemElevator
 * 
 * @author Maximilian Stark | Dakror
 */
public class ItemElevatorPassthrough extends Structure<Schema> implements ISpecialRenderer {
    public static final Schema classSchema = new Schema(0, StructureType.ItemElevatorPassthrough, false, 1, 1,
            "itemlift", // Reuse itemlift sprite but with tint
            new Items(), // No build costs - can't be manually placed
            null); // No docks

    // Blue tint for visual distinction (same as ItemElevator)
    protected static final Color ELEVATOR_TINT = new Color(0.7f, 0.7f, 1.0f, 1.0f);

    int sourceLayerIndex = -1;
    int targetLayerIndex = -1;

    public ItemElevatorPassthrough(int x, int y) {
        super(x, y, classSchema);
    }

    @Override
    protected void saveData(Builder b) {
        super.saveData(b);
        b.Int("sourceLayer", sourceLayerIndex)
         .Int("targetLayer", targetLayerIndex);
    }

    @Override
    protected void loadData(CompoundTag tag) throws NBTException {
        super.loadData(tag);
        sourceLayerIndex = tag.Int("sourceLayer", -1);
        targetLayerIndex = tag.Int("targetLayer", -1);
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

