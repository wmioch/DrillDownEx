/*******************************************************************************
 * Copyright 2017 Maximilian Stark | Dakror <mail@dakror.de>
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
 * A green-skinned conveyor belt variant that behaves like the default conveyor.
 */
public class GreenConveyor extends Conveyor {
    public static final ConveyorSchema classSchema = new ConveyorSchema(0, StructureType.GreenConveyor, true, 1, 1, "green_conveyor", new Items(ItemType.Stone, 1), null)
            .flags(Flags.Draggable, Flags.NoDustEffect)
            .loudness(0.5f);

    public GreenConveyor(int x, int y) {
        super(x, y, classSchema);
    }

    public GreenConveyor(int x, int y, Direction direction) {
        super(x, y, classSchema);
        dir = direction;
    }
}

