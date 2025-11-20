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

package de.dakror.quarry.structure.logistics;

import de.dakror.quarry.game.Item.ItemType;
import de.dakror.quarry.game.Item.Items;
import de.dakror.quarry.structure.base.Direction;
import de.dakror.quarry.structure.base.Schema.Flags;
import de.dakror.quarry.structure.base.StructureType;

/**
 * @author Maximilian Stark | Dakror
 */
public class GreenConveyor extends Conveyor {
    public static final ConveyorSchema classSchema = new ConveyorSchema(0, StructureType.GreenConveyor, true, 1, 1,
            "conveyor_green", new Items(ItemType.Stone, 1), null) {
        {
            if (texWE == null) texWE = tex;
            if (texNE == null) texNE = tex;
            if (texNSE == null) texNSE = tex;
            if (texWNE == null) texWNE = tex;
            if (texWNSE == null) texWNSE = tex;
        }
    }
            .flags(Flags.Draggable, Flags.NoDustEffect)
            .loudness(0.5f);

    public GreenConveyor(int x, int y) {
        super(x, y, classSchema);
        dir = Direction.East;
    }

    public GreenConveyor(int x, int y, Direction direction) {
        super(x, y, classSchema);
        dir = direction;
    }

    @Override
    public Object clone() {
        return new GreenConveyor(x, y, dir);
    }
}
