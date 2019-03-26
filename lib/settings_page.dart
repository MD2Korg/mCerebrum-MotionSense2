import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_blue/flutter_blue.dart';

import 'data/configuration.dart';
import 'motionsensegen2_page.dart';
import 'scan_result_tile.dart';

class SettingsPage extends StatefulWidget {
  @override
  _SettingsPageState createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  Configuration configuration = new Configuration();
  FlutterBlue _flutterBlue = FlutterBlue.instance;
  StreamSubscription deviceConnection;

  /// Scanning
  StreamSubscription _scanSubscription;
  Map<DeviceIdentifier, ScanResult> scanResults = new Map();
  bool isScanning = false;

  /// State
  StreamSubscription _stateSubscription;
  BluetoothState state = BluetoothState.unknown;

  Future<void> getSettings() async {
    await configuration.getSettings();
    setState(() {
    });
  }

  @override
  void initState() {
    super.initState();
    getSettings();
    // Immediately get the state of FlutterBlue
    _flutterBlue.state.then((s) {
      setState(() {
        state = s;
        if(state == BluetoothState.on){
          _startScan();
        }
        else {
          _stopScan();
        }
      });
    });
    // Subscribe to state changes
    _stateSubscription = _flutterBlue.onStateChanged().listen((s) {
      setState(() {
        state = s;
        if(state == BluetoothState.on){
          _startScan();
        }
        else {
          _stopScan();
        }
      });
    });
  }

  @override
  void dispose() {
    _stateSubscription?.cancel();
    _stateSubscription = null;
    _scanSubscription?.cancel();
    _scanSubscription = null;
    deviceConnection?.cancel();
    deviceConnection = null;
    super.dispose();
  }

  _startScan() {
    if(isScanning) return;
    _scanSubscription = _flutterBlue
        .scan(
      timeout: const Duration(seconds: 10),
      /*withServices: [
          new Guid('0000180F-0000-1000-8000-00805F9B34FB')
        ]*/
    )
        .listen((scanResult) {
      if (scanResult.advertisementData.serviceUuids == null ||
          scanResult.advertisementData.serviceUuids.length != 1) return;
      if (scanResult.advertisementData.serviceUuids[0] !=
          "0000180f-0000-1000-8000-00805f9b34fb") return;
      if(scanResult.advertisementData.localName!='MotionSense2') return;
      if (configuration.getDevice(scanResult.device.id.toString()) != null)
        return;
      setState(() {
        scanResults[scanResult.device.id] = scanResult;
      });
    }, onDone: _stopScan);

    setState(() {
      isScanning = true;
    });
  }

  _stopScan() {
    if(!isScanning) return;
    _scanSubscription?.cancel();
    _scanSubscription = null;
    setState(() {
      isScanning = false;
    });
  }

  _buildScanningButton() {
    if (state != BluetoothState.on) {
      return null;
    }
    if (isScanning) {
      return new FloatingActionButton(
        child: new Icon(Icons.stop),
        onPressed: _stopScan,
        backgroundColor: Colors.red,
      );
    } else {
      return new FloatingActionButton(
          child: new Icon(Icons.search), onPressed: _startScan);
    }
  }

  _buildScanResultTiles() {
    return scanResults.values
        .map((r) => ScanResultTile(
            result: r,
            myCallback: (BluetoothDevice device, String platformId) =>
                _addDevice(r.device, platformId)))
        .toList();
  }


   _addDevice(BluetoothDevice device, String platformId) async{
    deviceConnection = await _flutterBlue.connect(device).listen((s) {
      if (s == BluetoothDeviceState.connected) {
        device.discoverServices().then((onValue){
          BluetoothCharacteristic ch = new BluetoothCharacteristic(uuid: Guid("da39d600-1d81-48e2-9c68-d0ae4bbd351f"), serviceUuid: Guid("da395d22-1d81-48e2-9c68-d0ae4bbd351f"), descriptors: null, properties: null);
          device.readCharacteristic(ch).then((version) {
            String versionStr = version[0].toString();
            for(int i =1;i<version.length;i++){
              versionStr+="."+version[i].toString();
            }
            configuration.addDevice("MOTION_SENSE_HRV_PLUS_GEN2", platformId, device.id.toString(), device.name, versionStr).then((onValue){
              scanResults.remove(DeviceIdentifier(device.id.toString()));
/*
              deviceConnection?.cancel();
              deviceConnection = null;
*/
              setState(() {});

            });
          });

        });
      } else {
//        print("abc");
      }
    }, onDone: (){
      deviceConnection?.cancel();
      deviceConnection = null;

    });
  }

  _buildAlertTile() {
    return new Container(
      color: Colors.redAccent,
      child: new ListTile(
        title: new Text(
          'Bluetooth adapter is ${state.toString().substring(15)}',
          style: Theme.of(context).primaryTextTheme.subhead,
        ),
        trailing: new Icon(
          Icons.error,
          color: Theme.of(context).primaryTextTheme.subhead.color,
        ),
      ),
    );
  }

  _buildProgressBarTile() {
    return new LinearProgressIndicator();
  }

  @override
  Widget build(BuildContext context) {
    var tiles = new List<Widget>();
    if (state != BluetoothState.on) {
      tiles.add(_buildAlertTile());
    }
    tiles.addAll(_buildScanResultTiles());
    return new Scaffold(
      appBar: AppBar(elevation: 4.0, title: Text("MotionSense Devices")),
      floatingActionButton: _buildScanningButton(),
      body: new Column(
        children: <Widget>[
          (isScanning) ? _buildProgressBarTile() : new Container(),
          Container(
            color: Theme.of(context).highlightColor,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: Text("Configured Devices",
                    style: Theme.of(context).textTheme.subtitle),
              ),
            ),
          ),
          configuration.getDeviceNo() == 0
              ? Container(
                  padding: const EdgeInsets.symmetric(vertical: 20.0),
                  child: Text("Not configured yet",
                      style: TextStyle(fontSize: 14)),
                )
              : Column(children: createList()),
          Container(
            color: Theme.of(context).highlightColor,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: Text("Available Devices",
                    style: Theme.of(context).textTheme.subtitle),
              ),
            ),
          ),
          Expanded(
              child: new ListView(
            children: tiles,
          )),
        ],
      ),
    );
  }

  List<Widget> createList() {
    List<Widget> list = new List();
    int no = configuration.getDeviceNo();
    for (int i = 0; i < no; i++) {
      Map m = configuration.getDeviceByIndex(i);
      list.add(GestureDetector(
          onTap: () {
            Navigator.push(
                context,
                new MaterialPageRoute(
                    builder: (_context) =>
                        MotionSenseGen2Page(m["deviceId"], configuration)));
          },
          child: ListTile(
              title: Text(m["deviceName"], style: TextStyle(fontSize: 14),),
              subtitle: Text(m["deviceId"], style: TextStyle(fontSize: 12),),
              trailing: Text(m["platformId"], style: TextStyle(fontSize: 14),))));
    }
    return list;
  }
}
