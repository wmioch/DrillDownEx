import 'dart:math';

/// Resource types available in the Flame port.
enum ResourceType { ore, ingot, refinedOil, petroleumCoke, water }

extension ResourceTypeX on ResourceType {
  bool get isFluid => this == ResourceType.refinedOil || this == ResourceType.water;

  String get label {
    switch (this) {
      case ResourceType.ore:
        return 'Ore';
      case ResourceType.ingot:
        return 'Ingot';
      case ResourceType.refinedOil:
        return 'Refined Oil';
      case ResourceType.petroleumCoke:
        return 'Petroleum Coke';
      case ResourceType.water:
        return 'Water';
    }
  }
}

/// Surface makeup of a tile, used to determine what can be harvested.
enum TileSurface { rock, oreVein, oilPocket, aquifer }

extension TileSurfaceX on TileSurface {
  ResourceType? get deposit {
    switch (this) {
      case TileSurface.oreVein:
        return ResourceType.ore;
      case TileSurface.oilPocket:
        return ResourceType.refinedOil;
      case TileSurface.aquifer:
        return ResourceType.water;
      case TileSurface.rock:
        return null;
    }
  }
}

/// Build tools exposed to the HUD.
enum BuildTool {
  none,
  drill,
  smelter,
  pump,
  purifier,
  elevatorUp,
  elevatorDown,
  reliefValve,
  coker,
  storage,
  erase
}

/// Represents the operational stats to mirror in the HUD.
class GameSnapshot {
  const GameSnapshot({
    required this.activeFloor,
    required this.speed,
    required this.selectedTool,
    required this.totals,
    required this.floorPurified,
    required this.structureCounts,
  });

  final int activeFloor;
  final double speed;
  final BuildTool selectedTool;
  final Map<ResourceType, double> totals;
  final bool floorPurified;
  final Map<StructureKind, int> structureCounts;
}

/// Inventory helper that caps each resource independently.
class Inventory {
  Inventory({required this.capacity, required this.allowed});

  final double capacity;
  final Set<ResourceType> allowed;
  final Map<ResourceType, double> _stored = {};

  double amount(ResourceType type) => _stored[type] ?? 0;

  double add(ResourceType type, double amount) {
    if (!allowed.contains(type) || amount <= 0) {
      return 0;
    }
    final current = this.amount(type);
    final accepted = min(amount, capacity - current);
    if (accepted <= 0) {
      return 0;
    }
    _stored[type] = current + accepted;
    return accepted;
  }

  double take(ResourceType type, double amount) {
    final current = this.amount(type);
    final removed = min(amount, current);
    if (removed <= 0) {
      return 0;
    }
    _stored[type] = current - removed;
    return removed;
  }

  bool canAccept(ResourceType type, double amount) =>
      allowed.contains(type) && this.amount(type) + amount <= capacity + 1e-6;

  double fillRatio(ResourceType type) => capacity == 0 ? 0 : amount(type) / capacity;

  Map<ResourceType, double> snapshot() => Map.unmodifiable(_stored);
}

/// Types of structures supported by the miniature factory simulation.
enum StructureKind {
  drill,
  smelter,
  pump,
  purifier,
  elevatorUp,
  elevatorDown,
  reliefValve,
  coker,
  storage,
}

BuildTool toolForStructure(StructureKind kind) {
  switch (kind) {
    case StructureKind.drill:
      return BuildTool.drill;
    case StructureKind.smelter:
      return BuildTool.smelter;
    case StructureKind.pump:
      return BuildTool.pump;
    case StructureKind.purifier:
      return BuildTool.purifier;
    case StructureKind.elevatorUp:
      return BuildTool.elevatorUp;
    case StructureKind.elevatorDown:
      return BuildTool.elevatorDown;
    case StructureKind.reliefValve:
      return BuildTool.reliefValve;
    case StructureKind.coker:
      return BuildTool.coker;
    case StructureKind.storage:
      return BuildTool.storage;
  }
}

/// Base class representing a placed structure.
abstract class Structure {
  Structure({required this.kind, required Set<ResourceType> allowed, required double capacity})
      : inventory = Inventory(capacity: capacity, allowed: allowed);

  final StructureKind kind;
  final Inventory inventory;

  bool canAccept(ResourceType type, double amount) => inventory.canAccept(type, amount);

  void produce(WorldState world, FloorState floor, TileState tile) {}

  void distribute(WorldState world, FloorState floor, TileState tile) {}
}

class DrillStructure extends Structure {
  DrillStructure() : super(kind: StructureKind.drill, allowed: {ResourceType.ore}, capacity: 20);

  @override
  void produce(WorldState world, FloorState floor, TileState tile) {
    final deposit = tile.surface.deposit;
    if (deposit == ResourceType.ore) {
      inventory.add(ResourceType.ore, 1.2 * floor.productionModifier);
    }
  }
}

class SmelterStructure extends Structure {
  SmelterStructure()
      : super(kind: StructureKind.smelter, allowed: {ResourceType.ore, ResourceType.ingot}, capacity: 16);

  @override
  void produce(WorldState world, FloorState floor, TileState tile) {
    final oreRemoved = inventory.take(ResourceType.ore, 2 * floor.productionModifier);
    if (oreRemoved >= 2 * floor.productionModifier) {
      inventory.add(ResourceType.ingot, 1 * floor.productionModifier);
    } else {
      // Return partial ore if not enough to smelt a bar.
      if (oreRemoved > 0) {
        inventory.add(ResourceType.ore, oreRemoved);
      }
    }
  }
}

class PumpStructure extends Structure {
  PumpStructure()
      : super(
          kind: StructureKind.pump,
          allowed: {ResourceType.refinedOil, ResourceType.water},
          capacity: 20,
        );

  @override
  void produce(WorldState world, FloorState floor, TileState tile) {
    final deposit = tile.surface.deposit;
    if (deposit == ResourceType.refinedOil || deposit == ResourceType.water) {
      inventory.add(deposit!, 1.0 * floor.productionModifier);
    }
  }
}

class AirPurifierStructure extends Structure {
  AirPurifierStructure() : super(kind: StructureKind.purifier, allowed: const {}, capacity: 0);

  @override
  void produce(WorldState world, FloorState floor, TileState tile) {
    floor.registerPurifier();
  }
}

class ItemElevatorStructure extends Structure {
  ItemElevatorStructure({required this.targetFloor, required this.upward})
      : super(
          kind: upward ? StructureKind.elevatorUp : StructureKind.elevatorDown,
          allowed: {ResourceType.ore, ResourceType.ingot, ResourceType.petroleumCoke},
          capacity: 18,
        );

  final int targetFloor;
  final bool upward;

  @override
  void distribute(WorldState world, FloorState floor, TileState tile) {
    final destination = world.tileAt(targetFloor, tile.position);
    if (destination?.structure is ItemElevatorStructure) {
      final partner = destination!.structure! as ItemElevatorStructure;
      if (partner.targetFloor == floor.depth && partner.upward != upward) {
        for (final resource in inventory.snapshot().entries) {
          final moved = inventory.take(resource.key, 2);
          if (moved > 0) {
            partner.inventory.add(resource.key, moved);
          }
        }
      }
    }
  }
}

class ReliefValveStructure extends Structure {
  ReliefValveStructure()
      : super(
          kind: StructureKind.reliefValve,
          allowed: {ResourceType.water, ResourceType.refinedOil},
          capacity: 24,
        );

  @override
  void distribute(WorldState world, FloorState floor, TileState tile) {
    for (final resource in inventory.snapshot().entries) {
      if (inventory.fillRatio(resource.key) >= 0.99) {
        for (final neighbor in world.neighbors(tile, floorDepth: floor.depth)) {
          final target = neighbor.structure;
          if (target != null && target.canAccept(resource.key, 2)) {
            final moved = inventory.take(resource.key, 2);
            target.inventory.add(resource.key, moved);
            if (moved > 0) {
              break;
            }
          }
        }
      }
    }
  }
}

class CokerStructure extends Structure {
  CokerStructure()
      : super(
          kind: StructureKind.coker,
          allowed: {ResourceType.refinedOil, ResourceType.petroleumCoke},
          capacity: 20,
        );

  @override
  void produce(WorldState world, FloorState floor, TileState tile) {
    final removed = inventory.take(ResourceType.refinedOil, 3 * floor.productionModifier);
    if (removed >= 3 * floor.productionModifier) {
      inventory.add(ResourceType.petroleumCoke, 1 * floor.productionModifier);
    } else if (removed > 0) {
      inventory.add(ResourceType.refinedOil, removed);
    }
  }
}

class StorageStructure extends Structure {
  StorageStructure()
      : super(
          kind: StructureKind.storage,
          allowed: ResourceType.values.toSet(),
          capacity: 64,
        );
}

class TileState {
  TileState({required this.position, required this.surface});

  final Point<int> position;
  TileSurface surface;
  Structure? structure;
}

class FloorState {
  FloorState({required this.depth, required this.tiles});

  final int depth;
  final List<List<TileState>> tiles;
  int _purifiersDetected = 0;

  Iterable<TileState> get allTiles sync* {
    for (final column in tiles) {
      for (final tile in column) {
        yield tile;
      }
    }
  }

  int get width => tiles.length;

  int get height => tiles.first.length;

  bool get purified => _purifiersDetected > 0;

  double get productionModifier => purified ? 1.0 : 0.6;

  void clearPurifiers() => _purifiersDetected = 0;

  void registerPurifier() => _purifiersDetected++;

  TileState tileAt(Point<int> position) => tiles[position.x][position.y];
}

class WorldState {
  WorldState({required this.floors, this.activeFloor = 0});

  final List<FloorState> floors;
  int activeFloor;
  double speed = 1.0;
  double _tickAccumulator = 0;

  factory WorldState.generated({int width = 10, int height = 8, int floors = 3, int seed = 7}) {
    final random = Random(seed);
    final generatedFloors = <FloorState>[];
    for (var depth = 0; depth < floors; depth++) {
      final columns = <List<TileState>>[];
      for (var x = 0; x < width; x++) {
        final column = <TileState>[];
        for (var y = 0; y < height; y++) {
          column.add(TileState(position: Point(x, y), surface: _pickSurface(random, depth, y)));
        }
        columns.add(column);
      }
      generatedFloors.add(FloorState(depth: depth, tiles: columns));
    }
    return WorldState(floors: generatedFloors);
  }

  static TileSurface _pickSurface(Random random, int depth, int row) {
    final roll = random.nextDouble();
    if (roll > 0.8 && depth == 0) return TileSurface.oreVein;
    if (roll > 0.75 && depth == 1) return TileSurface.oilPocket;
    if (roll > 0.7 && depth == 2) return TileSurface.aquifer;
    if (row == 0 || row == 1) return TileSurface.rock;
    return roll > 0.9 ? TileSurface.oreVein : TileSurface.rock;
  }

  FloorState get currentFloor => floors[activeFloor];

  TileState? tileAt(int floor, Point<int> position) {
    if (floor < 0 || floor >= floors.length) return null;
    final targetFloor = floors[floor];
    if (position.x < 0 || position.x >= targetFloor.width) return null;
    if (position.y < 0 || position.y >= targetFloor.height) return null;
    return targetFloor.tileAt(position);
  }

  Iterable<TileState> neighbors(TileState tile, {int? floorDepth}) sync* {
    final depth = floorDepth ?? activeFloor;
    const offsets = [Point<int>(1, 0), Point<int>(-1, 0), Point<int>(0, 1), Point<int>(0, -1)];
    for (final offset in offsets) {
      final neighbor = tileAt(depth, Point(tile.position.x + offset.x, tile.position.y + offset.y));
      if (neighbor != null) {
        yield neighbor;
      }
    }
  }

  bool placeStructure(BuildTool tool, Point<int> position) {
    final tile = tileAt(activeFloor, position);
    if (tile == null || tile.structure != null) {
      return false;
    }
    switch (tool) {
      case BuildTool.drill:
        if (tile.surface.deposit != ResourceType.ore) return false;
        tile.structure = DrillStructure();
        return true;
      case BuildTool.smelter:
        tile.structure = SmelterStructure();
        return true;
      case BuildTool.pump:
        if (tile.surface.deposit == null || !tile.surface.deposit!.isFluid) return false;
        tile.structure = PumpStructure();
        return true;
      case BuildTool.purifier:
        tile.structure = AirPurifierStructure();
        return true;
      case BuildTool.elevatorUp:
        if (activeFloor == 0) return false;
        tile.structure = ItemElevatorStructure(targetFloor: activeFloor - 1, upward: true);
        return true;
      case BuildTool.elevatorDown:
        if (activeFloor >= floors.length - 1) return false;
        tile.structure = ItemElevatorStructure(targetFloor: activeFloor + 1, upward: false);
        return true;
      case BuildTool.reliefValve:
        tile.structure = ReliefValveStructure();
        return true;
      case BuildTool.coker:
        tile.structure = CokerStructure();
        return true;
      case BuildTool.storage:
        tile.structure = StorageStructure();
        return true;
      case BuildTool.none:
      case BuildTool.erase:
        return false;
    }
  }

  bool removeStructure(Point<int> position) {
    final tile = tileAt(activeFloor, position);
    if (tile?.structure == null) {
      return false;
    }
    tile!.structure = null;
    return true;
  }

  void update(double dt) {
    _tickAccumulator += dt * speed;
    const tickLength = 0.5;
    while (_tickAccumulator >= tickLength) {
      _tickAccumulator -= tickLength;
      _tick();
    }
  }

  void _tick() {
    for (final floor in floors) {
      floor.clearPurifiers();
    }
    for (final floor in floors) {
      for (final tile in floor.allTiles) {
        tile.structure?.produce(this, floor, tile);
      }
    }
    for (final floor in floors) {
      for (final tile in floor.allTiles) {
        tile.structure?.distribute(this, floor, tile);
        _shareWithNeighbors(floor, tile);
      }
    }
  }

  void _shareWithNeighbors(FloorState floor, TileState tile) {
    final structure = tile.structure;
    if (structure == null) {
      return;
    }
    for (final entry in structure.inventory.snapshot().entries) {
      if (structure is CokerStructure && entry.key == ResourceType.refinedOil) {
        // Keep feedstock internal so it can cook into coke rather than bouncing back to pumps.
        continue;
      }
      for (final neighbor in neighbors(tile, floorDepth: floor.depth)) {
        final target = neighbor.structure;
        if (target != null && target.canAccept(entry.key, 1)) {
          final moved = structure.inventory.take(entry.key, 1);
          if (moved > 0) {
            target.inventory.add(entry.key, moved);
          }
        }
      }
    }
  }

  GameSnapshot snapshot(BuildTool selectedTool) {
    final totals = <ResourceType, double>{for (final r in ResourceType.values) r: 0};
    final counts = <StructureKind, int>{}..addEntries(StructureKind.values.map((kind) => MapEntry(kind, 0)));
    for (final floor in floors) {
      for (final tile in floor.allTiles) {
        final structure = tile.structure;
        if (structure != null) {
          counts[structure.kind] = (counts[structure.kind] ?? 0) + 1;
          for (final entry in structure.inventory.snapshot().entries) {
            totals[entry.key] = (totals[entry.key] ?? 0) + entry.value;
          }
        }
      }
    }
    return GameSnapshot(
      activeFloor: activeFloor,
      speed: speed,
      selectedTool: selectedTool,
      totals: Map.unmodifiable(totals),
      floorPurified: currentFloor.purified,
      structureCounts: Map.unmodifiable(counts),
    );
  }
}
