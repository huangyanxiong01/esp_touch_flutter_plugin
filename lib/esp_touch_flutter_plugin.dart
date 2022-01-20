import 'dart:async';
import 'dart:convert';

import 'package:esp_touch_flutter_plugin/esp_touch_result.dart';
import 'package:flutter/services.dart';

class EspTouchFlutterPlugin {
  static const MethodChannel _channel = MethodChannel('esp_touch');

  static const EventChannel _espTouchingEventChannel =
      EventChannel('esp_touch_sending');

  static const EventChannel _espTouchFinishedEventChannel =
      EventChannel('esp_touch_finished');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// The EsptouchTask automatic termination when connected esp devices total equal [count] value.
  /// The [count] default value is -1, This will have no quantity limit to connect wifi esp devices.
  /// You can send password by specify EsptouchTask with Broadcast or MulitCast.
  /// Broadcast when [isBroadcast] value is true
  /// MulitCast when [isBroadcast] value is false
  static Future<String> send(String password,
      {int? count = -1, bool isBroadcast = true}) async {
    final String message = await _channel.invokeMethod('send',
        {"password": password, "isBroadcast": isBroadcast, "count": count});
    return message;
  }

  static Future<bool> cancel() async {
    final bool message = await _channel.invokeMethod('cancel');
    return message;
  }

  static Stream<EspTouchResult> get onSending {
    return _espTouchingEventChannel.receiveBroadcastStream().map((e) {
      return EspTouchResult.fromJson(jsonDecode(e));
    });
  }

  static Stream<List<EspTouchResult>> get onSendFinished {
    return _espTouchFinishedEventChannel.receiveBroadcastStream().map((e) {
      return (e as List<Object?>).map((e) {
        return EspTouchResult.fromJson(jsonDecode(e as String));
      }).toList();
    });
  }
}
