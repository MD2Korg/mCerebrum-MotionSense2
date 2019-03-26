import 'dart:async';

import 'package:flutter/material.dart';
import 'package:motionsense2/motionsense.dart';
import 'package:motionsense2/settings_page.dart';
import 'package:motionsense2/summary_table.dart';

import 'data/summary.dart';

class MainPage extends StatefulWidget {
  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  Timer _timer;
  Summary _summary;

  @override
  void initState() {
    super.initState();
    _timer = new Timer.periodic(const Duration(seconds: 1), getSummary);
    _summary = Summary();
  }

  void getSummary(Timer timer) async {
    await _summary.readSummary();
    setState(() {});
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('MotionSense2'),
        ),
        body: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              ListTile(
                  leading: Icon(Icons.play_circle_filled),
                  title: Text(
                    "Data Collection",
                    style: TextStyle(fontSize: 16),
                  ),
                  subtitle: _summary.isRunning()
                      ? Text(_summary.getRunningTime())
                      : Text('stopped'),
                  trailing: !_summary.isRunning()
                      ? new FlatButton(
                          textColor: Colors.white,
                          color: Colors.green,
//                  borderSide: BorderSide(color: Colors.green),
                          onPressed: () {
                            Motionsense.setBackgroundService(true);
                            //setToDefault();
                          },
                          child:
                              new Text("Start", style: TextStyle(fontSize: 16)),
                        )
                      : new FlatButton(
                          textColor: Colors.white,
                          color: Colors.red,
                          //                 borderSide: BorderSide(color: Colors.red),
                          onPressed: () {
                            Motionsense.setBackgroundService(false);
                          },
                          child:
                              new Text("Stop", style: TextStyle(fontSize: 16)),
                        )),
              ListTile(
                leading: Icon(Icons.settings),
                title: Text(
                  "Settings",
                  style: TextStyle(fontSize: 16),
                ),
                trailing: new FlatButton(
                  textColor: Colors.white,
                  color: Colors.green,
                  onPressed: () {
                    Navigator.push(
                        context,
                        new MaterialPageRoute(
                            builder: (_context) => new SettingsPage()));
                  },
                  child: new Text("Open", style: TextStyle(fontSize: 16)),
                ),
              ),
              Expanded(
                child: new DataSourceTable(_summary.getDevices()),
              )
            ]),
      ),
    );
  }
}
