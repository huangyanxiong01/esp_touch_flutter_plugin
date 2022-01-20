import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:esp_touch_flutter_plugin/esp_touch_flutter_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('esp_touch_flutter_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await EspTouchFlutterPlugin.platformVersion, '42');
  });
}
