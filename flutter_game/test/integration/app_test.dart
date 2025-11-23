@Tags(['integration'])
import 'package:drill_down_flame/main.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('renders HUD and advances simulation', (tester) async {
    await tester.pumpWidget(const DrillDownApp());

    await tester.pump(const Duration(milliseconds: 200));
    await tester.pump();

    expect(find.text('Speed'), findsOneWidget);
    expect(find.textContaining('Floor'), findsOneWidget);

    // Let the game tick and refresh UI values.
    await tester.pump(const Duration(milliseconds: 150));
    await tester.pump();

    // The HUD should keep rendering while the simulation advances.
    expect(find.text('Production Overview'), findsOneWidget);
  });
}
