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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.dakror.common.libgdx.PlatformInterface;
import de.dakror.common.libgdx.io.NBT.Builder;
import de.dakror.common.libgdx.io.NBT.CompoundTag;
import de.dakror.common.libgdx.io.NBT.NBTException;
import de.dakror.common.libgdx.render.SpriteRenderer;
import de.dakror.quarry.Const;
import de.dakror.quarry.Quarry;
import de.dakror.quarry.game.Item;
import de.dakror.quarry.game.Item.ItemCategory;
import de.dakror.quarry.game.Item.ItemType;
import de.dakror.quarry.game.Item.Items;
import de.dakror.quarry.game.Layer;
import de.dakror.quarry.game.Science.ScienceType;
import de.dakror.quarry.scenes.Game;
import de.dakror.quarry.structure.base.Direction;
import de.dakror.quarry.structure.base.Dock;
import de.dakror.quarry.structure.base.Dock.DockType;
import de.dakror.quarry.structure.base.FluidTubeStructure;
import de.dakror.quarry.structure.base.IRotatable;
import de.dakror.quarry.structure.base.ISpecialRenderer;
import de.dakror.quarry.structure.base.RenderingStyle;
import de.dakror.quarry.structure.base.Schema.Flags;
import de.dakror.quarry.structure.base.Structure;
import de.dakror.quarry.structure.base.StructureType;
import de.dakror.quarry.util.Bounds;

/**
 * Relief Valve - allows fluid to flow only when the connected output pipe is full.
 * Useful for pressure control in fluid systems.
 * 
 * Uses the same graphics as Valve but with an orange tint to distinguish it.
 * 
 * @author Maximilian Stark | Dakror
 */
public class ReliefValve extends FluidTubeStructure implements IRotatable, ISpecialRenderer {
    public static final FluidTubeSchema classSchema = new FluidTubeSchema(0, StructureType.ReliefValve, 10000, 1, 1,
            "valve", new Items(ItemType.SteelTube, 6, ItemType.SteelIngot, 12), null)
                    .sciences(ScienceType.PlasticMolding)
                    .removeFlags(Flags.Draggable);

    // Use the same textures as Valve - tinting is applied via ISpecialRenderer
    private static TextureRegion topTexture;
    
    private static TextureRegion getTopTexture() {
        if (topTexture == null) {
            topTexture = Quarry.Q.atlas.findRegion("structure_valve_top");
        }
        return topTexture;
    }

    // direction of middle knob
    Direction dir;
    Direction flowDir;

    ImageButton ui;

    public ReliefValve(int x, int y) {
        super(x, y, classSchema);
        dir = Direction.South;
        flowDir = Direction.North;
    }

    @Override
    public void rotate() {
        dir = dir.next();
        flowDir = flowDir.next();
        if (ui != null) ui.getImage().setRotation(flowDir.rot - Direction.South.rot);
        updateFlowDir();
        setDirty();
    }

    private void updateFlowDir() {
        if (ui != null) {
            ui.getImage().setOrigin(ui.getImage().getPrefWidth() / 2, ui.getImage().getPrefHeight() / 2);
            ui.getImage().setRotation(flowDir.rot - Direction.South.rot);
            ui.setChecked(flowDir == dir.inv());
        }
        updateStructures();
    }

    /**
     * Relief Valve accepts fluid from any direction except opposite the arrow
     */
    @Override
    public boolean canAccept(ItemType item, int x, int y, Direction dir) {
        if (!item.categories.contains(ItemCategory.Fluid)
                || Item.base(item) == ItemType._MoltenMetal) {
            return false;
        }

        // Block input from opposite of arrow (no backflow)
        // Knob direction is visual only, doesn't affect connections
        if (dir == flowDir.inv()) {
            return false;
        }

        // Otherwise, use parent's normal capacity check
        return super.canAccept(item, x, y, dir);
    }

    private boolean wasBlocked = false;
    
    @Override
    public void update(double deltaTime, int gameSpeed, Bounds dirtyBounds) {
        // Check if we're below threshold
        int myMaxCapacity = getSchema().maxFluid;
        int myCapacityThreshold = (myMaxCapacity * 99) / 100;
        boolean shouldBlock = (fluidLevel < myCapacityThreshold);
        
        // If threshold state changed, refresh connections
        if (shouldBlock != wasBlocked) {
            updateStructures();
            wasBlocked = shouldBlock;
        }
        
        if (shouldBlock) {
            // Temporarily clear output so parent's update() won't distribute there
            structures[flowDir.ordinal()] = null;
            types[flowDir.ordinal()] = null;
        }
        
        // Call parent update (handles dirty bounds check and distributes fluid)
        super.update(deltaTime, gameSpeed, dirtyBounds);
    }

    /**
     * Override to restrict directions like Valve, but only output when 99% full.
     * This ensures fluid only flows out the arrow direction when backpressure reaches threshold.
     */
    @Override
    protected void updateStructures() {
        Layer l = layer == null ? Game.G.layer : layer;

        for (Direction d : Direction.values) {
            // Block connections opposite the arrow (can't flow backwards)
            // Note: knob direction (this.dir) is visual only, doesn't block connections
            if (d == flowDir.inv()) {
                types[d.ordinal()] = null;
                structures[d.ordinal()] = null;
            } else {
                Structure<?> s = Game.G.activeStructureTrail.get((x + d.dx) * l.height + (y + d.dy));
                if (s == null)
                    s = l.getStructure(x + d.dx, y + d.dy);
                
                DockType type = DockType.FluidOut;

                if (s != null && s.getDocks().length > 0) {
                    boolean any = false;
                    for (Dock dock : s.getDocks()) {
                        if (s.isNextToDock(x, y, d, dock)
                                && ((d == flowDir && dock.type == DockType.FluidIn)
                                        || dock.type == DockType.FluidOut)) {
                            type = dock.type;
                            any = true;
                            break;
                        }
                    }
                    if (!any) {
                        type = null;
                        s = null;
                    }
                } else if (s == null || !(s instanceof FluidTubeStructure) 
                        || (s instanceof Valve && ((Valve) s).getDirection() == d)
                        || (s instanceof ReliefValve && ((ReliefValve) s).getDirection() == d)) {
                    s = null;
                    type = null;
                }

                if (d == flowDir) {
                    type = DockType.FluidIn;
                }

                types[d.ordinal()] = type;
                structures[d.ordinal()] = s;
            }
        }
        
        // Note: 99% capacity threshold check is now done in update() every frame,
        // not here in updateStructures() which only runs occasionally
    }

    @Override
    public void postLoad() {
        super.postLoad();
        updateStructures();
    }

    @Override
    public void setRotation(Direction direction) {
        dir = direction;
        updateFlowDir();
        setDirty();
    }

    @Override
    public Direction getDirection() {
        return dir;
    }

    @Override
    public Object clone() {
        ReliefValve valve = (ReliefValve) super.clone();
        valve.dir = dir;
        valve.flowDir = flowDir;
        return valve;
    }

    @Override
    public void onClick(Table content) {
        super.onClick(content);

        if (ui == null) {
            ui = new ImageButton(Quarry.Q.skin, "flow_dir");
            ui.pad(16);
            ui.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    flowDir = flowDir.next();
                    updateFlowDir();
                }
            });
            updateFlowDir();
        }

        content.add(ui).expand();
    }

    @Override
    public RenderingStyle getRenderingStyle() {
        return RenderingStyle.ORANGE_TINT;
    }

    @Override
    public void draw(SpriteRenderer spriter) {
        // Draw base texture (same as Valve, tinting applied via ISpecialRenderer)
        TextureRegion baseTex = getSchema().tex;
        if (baseTex == null) {
            return; // Skip drawing if texture is null
        }
        
        spriter.add(baseTex, x * Const.TILE_SIZE, y * Const.TILE_SIZE, Const.Z_TUBES,
                Const.TILE_SIZE / 2, Const.TILE_SIZE / 2, Const.TILE_SIZE, Const.TILE_SIZE, 1, 1, dir.rot + 90);
        
        // Draw top layer (same as Valve, tinting applied via ISpecialRenderer)
        TextureRegion top = getTopTexture();
        if (top != null) {
            spriter.add(top, x * Const.TILE_SIZE, y * Const.TILE_SIZE, Const.Z_TUBES + 0.1f,
                    Const.TILE_SIZE / 2, Const.TILE_SIZE / 2, Const.TILE_SIZE, Const.TILE_SIZE, 1, 1, dir.rot + 90);
        }
    }

    @Override
    public void loadData(CompoundTag tag) throws NBTException {
        super.loadData(tag);

        try {
            dir = Direction.values[tag.Byte("dir")];
            flowDir = Direction.values[tag.Byte("flow")];
        } catch (NBTException e) {
            Quarry.Q.pi.message(PlatformInterface.MSG_EXCEPTION, e);
        }
    }

    @Override
    public void saveData(Builder b) {
        super.saveData(b);
        b
                .Byte("dir", (byte) dir.ordinal())
                .Byte("flow", (byte) flowDir.ordinal());
    }

    @Override
    protected void copyData(int[] copyRegion, Builder b) {
        super.copyData(copyRegion, b);
        b
                .Byte("dir", (byte) dir.ordinal())
                .Byte("flow", (byte) flowDir.ordinal());
    }

    @Override
    protected void pasteData(int[] pasteRegion, CompoundTag tag) {
        super.pasteData(pasteRegion, tag);
        try {
            dir = Direction.values[tag.Byte("dir")];
            flowDir = Direction.values[tag.Byte("flow")];
        } catch (NBTException e) {
            Quarry.Q.pi.message(PlatformInterface.MSG_EXCEPTION, e);
        }
    }

    @Override
    protected boolean isAllowedFluid(ItemType i) {
        return i.categories.contains(ItemCategory.Fluid) && Item.base(i) != ItemType._MoltenMetal;
    }
}
