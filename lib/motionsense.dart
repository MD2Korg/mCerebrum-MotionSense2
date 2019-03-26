import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

class Motionsense {
  static const _GET_SETTINGS = 'GET_SETTINGS';
  static const _SAVE_SETTINGS = 'SAVE_SETTINGS';
  static const _GET_SUMMARY = 'GET_SUMMARY';
  static const _BACKGROUND_SERVICE = 'BACKGROUND_SERVICE';
  static const _PLOT = 'PLOT';

  static const MethodChannel _channel =
      const MethodChannel('org.md2k.motionsense2');
  static Future<Map<String, dynamic>> get getSettings async {
    final res = await _channel.invokeMethod(_GET_SETTINGS);
    Map<String, dynamic> x = jsonDecode(res);
    return x;
  }
  static Future<bool> saveSettings(Map _configuration) async {
    String x = jsonEncode(_configuration);
    final res = await _channel.invokeMethod(_SAVE_SETTINGS, {"config":x});
    return res;
  }
  static Future<Map<String, dynamic>> getSummary() async {
    final res = await _channel.invokeMethod(_GET_SUMMARY);
    Map<String, dynamic> x = jsonDecode(res);
    return x;
  }
  static Future<bool> setBackgroundService(bool run) async {
    final res = await _channel.invokeMethod(_BACKGROUND_SERVICE, {"run":run});
    return res;
  }
  static Future<bool> plot(String platformType, String platformId, String sensorName) async {
    final res = await _channel.invokeMethod(_PLOT, {"platformType":platformType, "platformId": platformId, "dataSourceType": sensorName});
    return res;
  }
}
