import 'package:esp_touch_flutter_plugin/esp_touch_flutter_plugin.dart';
import 'package:esp_touch_flutter_plugin/esp_touch_result.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _state = "stoped";
  int _count = 0;
  List<EspTouchResult> _results = [];

  @override
  void initState() {
    super.initState();
    EspTouchFlutterPlugin.onSending.listen((EspTouchResult espTouchResult) {
      _results.add(espTouchResult);
      setState(() {
        _count = _count + 1;
        _results = _results;
      });
    });
    EspTouchFlutterPlugin.onSendFinished
        .listen((List<EspTouchResult> espTouchResults) {
      setState(() {
        _count = espTouchResults.length;
        _results = espTouchResults;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    List<Row> listText = _results
        .map((e) => Row(
              children: [Text("address: ${e.address} bssid: ${e.bssid}")],
            ))
        .toList();
    return MaterialApp(
      home: Scaffold(
        floatingActionButton: FloatingActionButton(
            child: const Icon(Icons.send),
            onPressed: () async {
              // send password to esp
              String? message =
                  await EspTouchFlutterPlugin.send("928078ok", count: 1);
              setState(() {
                _state = message;
              });
            }),
        appBar: AppBar(
          title: const Text('Plugin example app'),
          actions: [
            IconButton(
                onPressed: () async {
                  // stop send password
                  bool isStoped = await EspTouchFlutterPlugin.cancel();
                  setState(() {
                    _state = isStoped ? "stoped" : '';
                  });
                },
                icon: const Icon(Icons.stop))
          ],
        ),
        body: Column(
          children: [
            Row(
              children: [
                Text("state: $_state"),
              ],
            ),
            Row(
              children: [
                Text("ESP Devices connected count: $_count"),
              ],
            ),
            Row(
              children: [
                Column(
                  children: listText,
                )
              ],
            )
            // ListView(
            //   children: listTitle,
            // )
          ],
        ),
      ),
    );
  }
}
