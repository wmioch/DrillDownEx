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

/**
 * Interface for structures that require special rendering (e.g., tinting, effects)
 * This allows for extensible rendering without hardcoded type checks
 * 
 * TINTING SYSTEM OVERVIEW:
 * ========================
 * The tinting system renders special structures separately from the static mesh,
 * allowing dynamic color effects without affecting frame elements (icons, items).
 * 
 * HOW IT WORKS:
 *   1. Structure implements ISpecialRenderer
 *   2. Structure.getRenderingStyle() returns desired tint/style
 *   3. Chunk.drawStructures() EXCLUDES ISpecialRenderer from static mesh
 *   4. Chunk.drawFrameStructures() renders special structures separately with tints:
 *      a. For each ISpecialRenderer structure:
 *         - Flush batch (clear pending sprites)
 *         - Set tint color
 *         - Draw structure
 *         - Flush immediately (render with tint)
 *         - Reset tint to white
 *      b. Then render frame elements (all with white tint)
 * 
 * ISOLATION & TINT BLEEDING PREVENTION:
 *   The flush() calls before/after tint changes ensure:
 *   - No sprites render with wrong tint
 *   - Each structure's tint is isolated
 *   - Frame elements always render with white (normal colors)
 *   - Tint from one chunk doesn't affect next chunk
 * 
 * HOW TO IMPLEMENT:
 *   1. Make your structure class implement ISpecialRenderer
 *   2. Create a RenderingStyle constant in RenderingStyle.java
 *   3. Implement getRenderingStyle():
 * 
 *      @Override
 *      public RenderingStyle getRenderingStyle() {
 *          // Return your custom style (e.g., BLUE_TINT for Item Elevators)
 *          return RenderingStyle.BLUE_TINT;
 *      }
 * 
 *   4. That's it! The rendering system handles everything else.
 * 
 * TINT COLOR TUNING:
 *   See RenderingStyle.java for detailed tint color recommendations.
 *   Quick guide:
 *   - Current blue: (0.4f, 0.6f, 1.0f, 0.8f)
 *   - More intense: lower red/green, increase alpha
 *   - More subtle: raise red/green, decrease alpha
 * 
 * @author Maximilian Stark | Dakror
 */
public interface ISpecialRenderer {
    /**
     * Returns the rendering style (tint, effects) for this structure
     * 
     * @return RenderingStyle containing tint color and render properties
     *         Use RenderingStyle.BLUE_TINT for Item Elevators
     *         Use RenderingStyle.DEFAULT for no tinting
     */
    RenderingStyle getRenderingStyle();
}

