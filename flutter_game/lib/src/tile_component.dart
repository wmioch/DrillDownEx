import 'package:drill_down_flame/src/world.dart';
import 'package:flame/components.dart';
import 'package:flame/events.dart';
import 'package:flutter/material.dart';

typedef TileTapCallback = void Function(TileState tile);

class TileComponent extends PositionComponent with TapCallbacks {
  TileComponent({
    required this.tile,
    required this.onTap,
    required double tileSize,
  }) {
    size = Vector2.all(tileSize);
    anchor = Anchor.topLeft;
    position = Vector2(tile.position.x * tileSize, tile.position.y * tileSize);
  }

  final TileState tile;
  final TileTapCallback onTap;

  static final _textPaint = TextPaint(
    style: TextStyle(
      color: Color(0xFFFAFAFA),
      fontSize: 12,
      fontWeight: FontWeight.w600,
    ),
  );

  @override
  void onTapDown(TapDownEvent event) {
    onTap(tile);
  }

  @override
  void render(Canvas canvas) {
    super.render(canvas);
    canvas.drawRect(size.toRect(), Paint()..color = _surfaceColor(tile.surface));
    if (tile.structure != null) {
      final structure = tile.structure!;
      final rect = Rect.fromLTWH(4, 4, size.x - 8, size.y - 8);
      canvas.drawRect(rect, Paint()..color = _structureColor(structure.kind));
      final text = _textForStructure(structure.kind);
      _textPaint.render(canvas, text, Vector2(rect.left + 4, rect.top + 8));
    } else if (tile.surface.deposit != null) {
      final text = tile.surface.deposit!.label;
      _textPaint.render(canvas, text, Vector2(4, size.y / 2 - 6));
    }
  }

  Color _surfaceColor(TileSurface surface) {
    switch (surface) {
      case TileSurface.rock:
        return const Color(0xFF3C3C3C);
      case TileSurface.oreVein:
        return const Color(0xFF73522D);
      case TileSurface.oilPocket:
        return const Color(0xFF42302E);
      case TileSurface.aquifer:
        return const Color(0xFF244A6E);
    }
  }

  Color _structureColor(StructureKind kind) {
    switch (kind) {
      case StructureKind.drill:
        return const Color(0xFF4CAF50);
      case StructureKind.smelter:
        return const Color(0xFFEF6C00);
      case StructureKind.pump:
        return const Color(0xFF81D4FA);
      case StructureKind.purifier:
        return const Color(0xFF00BCD4);
      case StructureKind.elevatorUp:
      case StructureKind.elevatorDown:
        return const Color(0xFF64B5F6);
      case StructureKind.reliefValve:
        return const Color(0xFFFF9800);
      case StructureKind.coker:
        return const Color(0xFFB39DDB);
      case StructureKind.storage:
        return const Color(0xFF9E9E9E);
    }
  }

  String _textForStructure(StructureKind kind) {
    switch (kind) {
      case StructureKind.drill:
        return 'Drill';
      case StructureKind.smelter:
        return 'Smelt';
      case StructureKind.pump:
        return 'Pump';
      case StructureKind.purifier:
        return 'Purify';
      case StructureKind.elevatorUp:
        return 'Up';
      case StructureKind.elevatorDown:
        return 'Down';
      case StructureKind.reliefValve:
        return 'Valve';
      case StructureKind.coker:
        return 'Coker';
      case StructureKind.storage:
        return 'Store';
    }
  }
}
