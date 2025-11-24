import 'dart:math';

import 'package:drill_down_flame/src/world.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  test('drill extracts ore from veins', () {
    final world = WorldState.generated(width: 2, height: 2, floors: 1, seed: 1);
    world.currentFloor.tileAt(const Point(0, 0)).surface = TileSurface.oreVein;
    world.placeStructure(BuildTool.drill, const Point(0, 0));

    world.update(1.5); // three ticks

    final ore = world.currentFloor.tileAt(const Point(0, 0)).structure!.inventory.amount(ResourceType.ore);
    expect(ore, greaterThan(0));
  });

  test('pump feeds a coker into coke output', () {
    final world = WorldState.generated(width: 3, height: 2, floors: 1, seed: 2);
    world.currentFloor.tileAt(const Point(0, 0)).surface = TileSurface.oilPocket;
    world.placeStructure(BuildTool.pump, const Point(0, 0));
    world.placeStructure(BuildTool.coker, const Point(1, 0));

    world.update(4.0);

    final coke = world.currentFloor.tileAt(const Point(1, 0)).structure!.inventory.amount(ResourceType.petroleumCoke);
    expect(coke, greaterThan(0));
  });
}
