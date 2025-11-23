import 'dart:math';

import 'package:drill_down_flame/src/tile_component.dart';
import 'package:drill_down_flame/src/world.dart';
import 'package:flame/camera.dart';
import 'package:flame/components.dart';
import 'package:flame/game.dart';
import 'package:flutter/material.dart';

class DrillDownGame extends FlameGame {
  DrillDownGame()
      : worldState = WorldState.generated(width: 12, height: 8, floors: 3),
        uiState = ValueNotifier(GameSnapshot(
          activeFloor: 0,
          speed: 1,
          selectedTool: BuildTool.drill,
          totals: const {},
          floorPurified: false,
          structureCounts: const {},
        ));

  static const hudOverlay = 'hud';
  final WorldState worldState;
  final ValueNotifier<GameSnapshot> uiState;
  BuildTool selectedTool = BuildTool.drill;
  final double tileSize = 64;

  @override
  Future<void> onLoad() async {
    await super.onLoad();
    camera.viewport = FixedResolutionViewport(
      resolution: Vector2(
        tileSize * worldState.currentFloor.width,
        tileSize * worldState.currentFloor.height,
      ),
    );
    _buildFloor();
    _refreshUi();
  }

  void _buildFloor() {
    for (final component in children.whereType<TileComponent>().toList()) {
      remove(component);
    }
    for (final tile in worldState.currentFloor.allTiles) {
      add(TileComponent(
        tile: tile,
        onTap: _handleTileTap,
        tileSize: tileSize,
      ));
    }
  }

  void _handleTileTap(TileState tile) {
    if (selectedTool == BuildTool.erase) {
      worldState.removeStructure(tile.position);
    } else {
      worldState.placeStructure(selectedTool, tile.position);
    }
    _refreshUi();
  }

  void _refreshUi() {
    uiState.value = worldState.snapshot(selectedTool);
  }

  void setSpeed(double speed) {
    worldState.speed = speed;
    _refreshUi();
  }

  void setTool(BuildTool tool) {
    selectedTool = tool;
    _refreshUi();
  }

  void changeFloor(int delta) {
    worldState.activeFloor = max(0, min(worldState.floors.length - 1, worldState.activeFloor + delta));
    _buildFloor();
    _refreshUi();
  }

  @override
  void update(double dt) {
    super.update(dt);
    worldState.update(dt);
    _refreshUi();
  }
}

class DrillHud extends StatelessWidget {
  const DrillHud({super.key, required this.game});

  final DrillDownGame game;

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.topCenter,
      child: ValueListenableBuilder<GameSnapshot>(
        valueListenable: game.uiState,
        builder: (context, snapshot, _) {
          return SingleChildScrollView(
            padding: const EdgeInsets.only(bottom: 16),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                _buildTopBar(snapshot),
                const SizedBox(height: 8),
                _buildPalette(snapshot),
                const SizedBox(height: 8),
                _buildStats(snapshot),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildTopBar(GameSnapshot snapshot) {
    return Card(
      margin: const EdgeInsets.all(8),
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  const Text('Speed'),
                  const SizedBox(width: 8),
                  for (final entry in [1.0, 2.0, 4.0, 10.0])
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 4),
                      child: ChoiceChip(
                        label: Text('${entry.toStringAsFixed(entry >= 10 ? 0 : 1)}x'),
                        selected: snapshot.speed == entry,
                        onSelected: (_) => game.setSpeed(entry),
                      ),
                    ),
                ],
              ),
              Row(
                children: [
                  IconButton(
                    onPressed: () => game.changeFloor(-1),
                    icon: const Icon(Icons.arrow_upward),
                    tooltip: 'Go up a floor',
                  ),
                  Text('Floor ${snapshot.activeFloor} ${snapshot.floorPurified ? 'Purified' : 'Smoggy'}'),
                  IconButton(
                    onPressed: () => game.changeFloor(1),
                    icon: const Icon(Icons.arrow_downward),
                    tooltip: 'Go down a floor',
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildPalette(GameSnapshot snapshot) {
    final buttons = <_ToolButtonInfo>[
      _ToolButtonInfo(tool: BuildTool.drill, label: 'Drill', icon: Icons.downhill_skiing),
      _ToolButtonInfo(tool: BuildTool.smelter, label: 'Smelter', icon: Icons.local_fire_department),
      _ToolButtonInfo(tool: BuildTool.pump, label: 'Pump', icon: Icons.water_drop),
      _ToolButtonInfo(tool: BuildTool.purifier, label: 'Air Purifier', icon: Icons.air),
      _ToolButtonInfo(tool: BuildTool.elevatorUp, label: 'Elevator Up', icon: Icons.keyboard_double_arrow_up),
      _ToolButtonInfo(tool: BuildTool.elevatorDown, label: 'Elevator Down', icon: Icons.keyboard_double_arrow_down),
      _ToolButtonInfo(tool: BuildTool.reliefValve, label: 'Relief Valve', icon: Icons.outbound),
      _ToolButtonInfo(tool: BuildTool.coker, label: 'Coker', icon: Icons.science),
      _ToolButtonInfo(tool: BuildTool.storage, label: 'Storage', icon: Icons.inventory_2),
      _ToolButtonInfo(tool: BuildTool.erase, label: 'Remove', icon: Icons.delete_outline),
    ];

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8),
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            for (final button in buttons)
              ChoiceChip(
                label: Text(button.label),
                avatar: Icon(button.icon, size: 18),
                selected: snapshot.selectedTool == button.tool,
                onSelected: (_) => game.setTool(button.tool),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildStats(GameSnapshot snapshot) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Production Overview', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Wrap(
              spacing: 12,
              children: [
                for (final resource in ResourceType.values)
                  Chip(
                    label: Text('${resource.label}: ${snapshot.totals[resource]?.toStringAsFixed(1) ?? '0'}'),
                  ),
              ],
            ),
            const SizedBox(height: 12),
            Wrap(
              spacing: 12,
              runSpacing: 8,
              children: [
                for (final entry in snapshot.structureCounts.entries)
                  Chip(label: Text('${_structureLabel(entry.key)}: ${entry.value}')),
              ],
            ),
            const SizedBox(height: 8),
            const Text('Tap tiles to place the selected structure. Adjacent machines share items automatically; elevators move stacks between matching columns.'),
          ],
        ),
      ),
    );
  }

  String _structureLabel(StructureKind kind) {
    switch (kind) {
      case StructureKind.drill:
        return 'Drills';
      case StructureKind.smelter:
        return 'Smelters';
      case StructureKind.pump:
        return 'Pumps';
      case StructureKind.purifier:
        return 'Purifiers';
      case StructureKind.elevatorUp:
        return 'Elevators (Up)';
      case StructureKind.elevatorDown:
        return 'Elevators (Down)';
      case StructureKind.reliefValve:
        return 'Relief Valves';
      case StructureKind.coker:
        return 'Cokers';
      case StructureKind.storage:
        return 'Storage';
    }
  }
}

class _ToolButtonInfo {
  _ToolButtonInfo({required this.tool, required this.label, required this.icon});

  final BuildTool tool;
  final String label;
  final IconData icon;
}
