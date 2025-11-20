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

package de.dakror.quarry.structure.producer;

import de.dakror.quarry.Const;
import de.dakror.quarry.game.Item.ItemType;
import de.dakror.quarry.game.Item.Items;
import de.dakror.quarry.game.Item.Items.Amount;
import de.dakror.quarry.game.Science.ScienceType;
import de.dakror.quarry.structure.base.Direction;
import de.dakror.quarry.structure.base.Dock;
import de.dakror.quarry.structure.base.Dock.DockFilter;
import de.dakror.quarry.structure.base.Dock.DockType;
import de.dakror.quarry.structure.base.ProducerStructure;
import de.dakror.quarry.structure.base.RecipeList;
import de.dakror.quarry.structure.base.Schema.Flags;
import de.dakror.quarry.structure.base.StructureType;
import de.dakror.quarry.util.Sfx;

/**
 * Dedicated assembler schema for the advanced servo chain.
 */
public class AdvancedServoAssembler extends ProducerStructure {
    public static final ProducerSchema classSchema = new ProducerSchema(0, StructureType.AdvancedServoAssembler, 6, 6,
            "advanced_servo_assembler", new Items(ItemType.StoneBrick, 45, ItemType.SteelIngot, 70, ItemType.WoodPlank, 85, ItemType.Scaffolding, 60, ItemType.CopperIngot, 80),
            new RecipeList() {
                @Override
                protected void init() {
                    add(new Recipe(70f, "advanced_servo", 2600)
                            .input(
                                    new Amount(ItemType.Lubricant, 12_500),
                                    new Amount(ItemType.SulfurDust, 90),
                                    new Amount(ItemType.PlasticCasing, 3),
                                    new Amount(ItemType.SteelTube, 16),
                                    new Amount(ItemType.Chip, 2))
                            .output(new Amount(ItemType.AdvancedServo, 1))
                            .science(ScienceType.OilProcessing, ScienceType.Blueprints, ScienceType.AdvancedTransport));
                }
            }, new Sfx("assembler" + Const.SFX_FORMAT),
            false,
            new Dock(5, 0, Direction.South, DockType.ItemOut),
            new Dock(0, 5, Direction.West, DockType.ItemIn, new DockFilter(ItemType.CopperWire, ItemType.BronzePlate, ItemType.Rotor, ItemType.Paper, ItemType.WoodPlank)),
            new Dock(5, 5, Direction.East, DockType.ItemIn, new DockFilter(ItemType.Magnet, ItemType.IronPlate, ItemType.Dynamo, ItemType.IronPlate, ItemType.Gunpowder, ItemType.SteelWire, ItemType.Chip)),
            new Dock(5, 3, Direction.East, DockType.ItemIn, new DockFilter(ItemType.SteelIngot, ItemType.SulfurDust, ItemType.SteelTube, ItemType.Sand)),
            new Dock(0, 3, Direction.West, DockType.ItemIn, new DockFilter(ItemType.CarbonBlock, ItemType.PlasticCasing, ItemType.SteelPlate, ItemType.Clay)),
            new Dock(1, 5, Direction.North, DockType.Power), new Dock(4, 5, Direction.North, DockType.FluidIn, new DockFilter(ItemType.Lubricant)))
                    .sciences(ScienceType.Electricity)
                    .flags(Flags.TextureAlwaysUpright);

    public AdvancedServoAssembler(int x, int y) {
        super(x, y, classSchema);
    }
}
