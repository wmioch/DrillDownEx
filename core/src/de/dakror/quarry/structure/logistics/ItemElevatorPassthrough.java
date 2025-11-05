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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import de.dakror.common.libgdx.io.NBT.Builder;
import de.dakror.common.libgdx.io.NBT.CompoundTag;
import de.dakror.common.libgdx.io.NBT.NBTException;
import de.dakror.common.libgdx.render.SpriteRenderer;
import de.dakror.quarry.Const;
import de.dakror.quarry.Quarry;
import de.dakror.quarry.game.Item.Items;
import de.dakror.quarry.game.Layer;
import de.dakror.quarry.scenes.Game;
import de.dakror.quarry.structure.base.Schema;
import de.dakror.quarry.structure.base.Structure;
import de.dakror.quarry.structure.base.StructureType;
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

    private static BitmapFont passthroughLabelFont;
    private static GlyphLayout passthroughLabelLayout;

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
    public void postLoad() {
        super.postLoad();
    }

    @Override
    public void draw(SpriteRenderer spriter) {
        TextureRegion tex = resolveTexture();
        spriter.add(tex,
                x * Const.TILE_SIZE,
                y * Const.TILE_SIZE,
                Const.Z_STRUCTURES,
                Const.TILE_SIZE,
                Const.TILE_SIZE);
    }

    @Override
    public RenderingStyle getRenderingStyle() {
        return RenderingStyle.BLUE_TINT;
    }

    private TextureRegion resolveTexture() {
        TextureRegion upward = ItemElevator.UPWARD_TEXTURE != null ? ItemElevator.UPWARD_TEXTURE : ItemElevator.DEFAULT_TEXTURE;
        return isUpward() ? upward : ItemElevator.DEFAULT_TEXTURE;
    }

    private boolean isUpward() {
        if (sourceLayerIndex >= 0 && targetLayerIndex >= 0) {
            return targetLayerIndex < sourceLayerIndex;
        }

        Layer currentLayer = layer != null ? layer : Game.G.layer;
        if (currentLayer == null) {
            return false;
        }

        int current = currentLayer.getIndex();
        if (sourceLayerIndex >= 0) {
            return current < sourceLayerIndex;
        }
        if (targetLayerIndex >= 0) {
            return targetLayerIndex < current;
        }
        return false;
    }
    
    /**
     * Renders the text labels for this passthrough: source floor (top-left) and target floor (bottom-right).
     * Called from Layer.drawElevatorText().
     */
    public void drawPassthroughText(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (sourceLayerIndex < 0 || targetLayerIndex < 0) {
            return;
        }
        
        ensureLabelResources();
        
        String sourceLabel = Integer.toString(-sourceLayerIndex);
        String targetLabel = Integer.toString(-targetLayerIndex);
        
        float tileSize = Const.TILE_SIZE;
        float padding = 12f; // Padding from edges (moved towards center)
        
        // Top-left: source floor
        passthroughLabelLayout.setText(passthroughLabelFont, sourceLabel);
        float sourceX = x * tileSize + padding;
        float sourceY = (y + 1) * tileSize - padding;
        
        // Bottom-right: target floor
        passthroughLabelLayout.setText(passthroughLabelFont, targetLabel);
        float targetX = (x + 1) * tileSize - padding - passthroughLabelLayout.width;
        float targetY = y * tileSize + padding + passthroughLabelLayout.height;
        
        // Determine text color based on background (upward = white background, use black text)
        if (isUpward()) {
            passthroughLabelFont.setColor(0f, 0f, 0f, 1f); // Black
        } else {
            passthroughLabelFont.setColor(1f, 1f, 1f, 1f); // White
        }
        
        // Draw text labels
        passthroughLabelFont.draw(batch, sourceLabel, sourceX, sourceY);
        passthroughLabelFont.draw(batch, targetLabel, targetX, targetY);
    }
    
    /**
     * Renders the diagonal line for this passthrough.
     * Called from Layer.drawElevatorLines().
     */
    public void drawPassthroughLine(ShapeRenderer shaper) {
        if (sourceLayerIndex < 0 || targetLayerIndex < 0) {
            return;
        }
        
        float tileSize = Const.TILE_SIZE;
        float padding = 12f; // Match text padding
        
        // Set line color to match text
        Color lineColor = isUpward() ? Color.BLACK : Color.WHITE;
        
        // Draw diagonal line from top-right to bottom-left (dividing the numbers)
        float lineX1 = (x + 1) * tileSize - padding;
        float lineY1 = (y + 1) * tileSize - padding;
        float lineX2 = x * tileSize + padding;
        float lineY2 = y * tileSize + padding;
        
        shaper.setColor(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
        shaper.line(lineX1, lineY1, lineX2, lineY2);
    }
    
    private static void ensureLabelResources() {
        if (passthroughLabelFont != null) return;
        passthroughLabelFont = Quarry.Q.skin.getFont("small-font");
        passthroughLabelLayout = new GlyphLayout();
    }
}

