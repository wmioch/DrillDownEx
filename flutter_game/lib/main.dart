import 'package:drill_down_flame/src/drill_down_game.dart';
import 'package:flutter/material.dart';
import 'package:flame/game.dart';

/// Entry point for the Flame-powered recreation of Drill Down.
void main() {
  runApp(const DrillDownApp());
}

class DrillDownApp extends StatelessWidget {
  const DrillDownApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Drill Down (Flame Edition)',
      theme: ThemeData.dark(useMaterial3: true),
      home: const Scaffold(
        body: SafeArea(
          child: _GameShell(),
        ),
      ),
    );
  }
}

class _GameShell extends StatefulWidget {
  const _GameShell();

  @override
  State<_GameShell> createState() => _GameShellState();
}

class _GameShellState extends State<_GameShell> {
  late final DrillDownGame _game;

  @override
  void initState() {
    super.initState();
    _game = DrillDownGame();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        GameWidget(
          game: _game,
          overlayBuilderMap: {
            DrillDownGame.hudOverlay: (context, game) => DrillHud(game: game as DrillDownGame),
          },
          initialActiveOverlays: const [DrillDownGame.hudOverlay],
        ),
      ],
    );
  }
}
