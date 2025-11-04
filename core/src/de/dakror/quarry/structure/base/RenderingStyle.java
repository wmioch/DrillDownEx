/*******************************************************************************
 * Copyright 2024 Maximilian Stark | Dakror <mail@dakror.de>
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

package de.dakror.quarry.structure.base;

import com.badlogic.gdx.graphics.Color;

/**
 * Encapsulates rendering properties for structures
 * Allows for extensible rendering without hardcoded type checks
 * 
 * TINTING SYSTEM:
 * ===============
 * This class is part of a comprehensive tinting/rendering system that allows structures
 * to apply custom color tints without affecting other rendering passes.
 * 
 * ARCHITECTURE:
 *   1. Structure implements ISpecialRenderer interface
 *   2. Structure.getRenderingStyle() returns a RenderingStyle with custom tint
 *   3. Chunk.drawFrameStructures() detects ISpecialRenderer and renders separately
 *   4. For each special structure:
 *      a. Flush pending sprites (clear batch)
 *      b. Set tint color via DepthSpriter.setTintColor()
 *      c. Draw structure
 *      d. Flush immediately to render with tint
 *      e. Reset tint to white for next structure
 *   5. Frame elements (icons, items) render AFTER tint is reset, so they're unaffected
 * 
 * CRITICAL DETAILS:
 *   - Each structure's tint is isolated via flush() calls before/after
 *   - Shader checks if tint is not white (< 0.9 on any channel) before applying
 *   - Uses overlay blend mode to preserve brightness (see base.fs for details)
 *   - Default tint is WHITE (1,1,1,1) which applies no effect
 * 
 * HOW TO ADD A NEW TINTED STRUCTURE:
 *   1. Make your structure implement ISpecialRenderer
 *   2. Create a RenderingStyle constant in this file:
 *      public static final RenderingStyle YOUR_TINT = 
 *          new RenderingStyle(new Color(0.4f, 0.6f, 1.0f, 0.8f), true);
 *   3. In your structure's getRenderingStyle():
 *      @Override
 *      public RenderingStyle getRenderingStyle() {
 *          return RenderingStyle.YOUR_TINT;
 *      }
 *   4. Done! Chunk.drawFrameStructures() handles everything else
 * 
 * TINT COLOR VALUES:
 *   Format: new Color(red, green, blue, alpha)
 *   - Range: 0.0 to 1.0 per channel
 *   - Current blue tint (Item Elevators): (0.4f, 0.6f, 1.0f, 0.8f)
 *     * Red < Green < Blue = cool blue tone
 *     * Alpha 0.8 = 80% blend intensity
 *   - To make tint more intense: lower red/green, increase alpha
 *   - To make tint more subtle: raise red/green, decrease alpha
 *   - Avoid values < 0.3 (makes colors too dark)
 * 
 * @author Maximilian Stark | Dakror
 */
public class RenderingStyle {
    private final Color tintColor;
    private final boolean renderImmediately;

    /**
     * Create a rendering style with tint color
     * Defaults to immediate rendering (not batched in static mesh)
     */
    public RenderingStyle(Color tintColor) {
        this(tintColor, true);
    }

    /**
     * Create a rendering style with full control
     * 
     * @param tintColor The color to tint the structure with.
     *                  Use (1,1,1,1) for white = no tint (default behavior)
     *                  Use custom colors like (0.4f, 0.6f, 1.0f, 0.8f) for blue tint
     * @param renderImmediately If true, renders immediately each frame with tint instead of 
     *                         being batched in static mesh. Must be true for tinted structures.
     */
    public RenderingStyle(Color tintColor, boolean renderImmediately) {
        this.tintColor = tintColor != null ? tintColor : Color.WHITE;
        this.renderImmediately = renderImmediately;
    }

    public Color getTintColor() {
        return tintColor;
    }

    public boolean shouldRenderImmediately() {
        return renderImmediately;
    }

    /**
     * Standard white tint (no tinting) - used for structures in static mesh
     */
    public static final RenderingStyle DEFAULT = new RenderingStyle(Color.WHITE, false);

    /**
     * Blue tint for Item Elevators
     * Color: (0.4f, 0.6f, 1.0f, 0.8f)
     * - Intense blue overlay that preserves brightness via overlay blend mode
     * - Red 0.4, Green 0.6, Blue 1.0 creates a cool blue tone
     * - Alpha 0.8 = 80% blend intensity
     * 
     * If you need to adjust intensity:
     *   - For MORE blue: lower red/green more (e.g., 0.2/0.5), increase alpha (e.g., 0.9)
     *   - For LESS blue: raise red/green (e.g., 0.6/0.8), decrease alpha (e.g., 0.6)
     */
    public static final RenderingStyle BLUE_TINT = new RenderingStyle(new Color(0.4f, 0.6f, 1.0f, 0.8f), true);
}
