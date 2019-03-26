import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'data/configuration.dart';

class MotionSenseGen2Page extends StatefulWidget {
  Configuration configuration;
  String id;

  MotionSenseGen2Page(this.id, this.configuration);

  @override
  _MotionSenseGen2PageState createState() =>
      _MotionSenseGen2PageState(id, configuration);
}

class _MotionSenseGen2PageState extends State<MotionSenseGen2Page> {
  Configuration configuration;
  String id;
  double _minConnectionInterval;
  double _ppgRed;
  double _ppgGreen;
  double _ppgInfrared;

  _MotionSenseGen2PageState(this.id, this.configuration);

  @override
  void initState() {
    super.initState();
    initValue();
  }

  initValue() {
    _minConnectionInterval =
        double.parse(configuration.getDeviceValue(id, "minConnectionInterval"));
    _ppgRed = double.parse(configuration.getSensorValue(id, "PPG", "ppgRed"));
    _ppgGreen =
        double.parse(configuration.getSensorValue(id, "PPG", "ppgGreen"));
    _ppgInfrared =
        double.parse(configuration.getSensorValue(id, "PPG", "ppgInfrared"));
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: AppBar(elevation: 4.0, title: Text("MotionSense")),
      body: bodyData(context),
    );
  }

  void setToDefault() async {
    await configuration.setToDefault(id);
    initValue();
    setState(() {});
  }

  Widget bodyData(BuildContext context) {
    return Container(
      height: double.infinity,
      child: SingleChildScrollView(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            Container(
              color: Theme.of(context).highlightColor,
              child: Padding(
                padding: const EdgeInsets.all(4.0),
                child: Center(
                  child: Text("MotionSense2",
                      style: Theme.of(context).textTheme.subtitle),
                ),
              ),
            ),
            ListTile(
              dense: true,
              leading: Text(
                "Set Configuration",
                style: TextStyle(fontSize: 14),
              ),
              trailing: new OutlineButton(
                color: Colors.green,
                shape: new RoundedRectangleBorder(borderRadius: new BorderRadius.circular(10.0)),
                textColor: Colors.green,
                onPressed: () {
                  setToDefault();
                },
                child: new Text("Default", style: TextStyle(fontSize: 14)),
              ),
            ),
            ListTile(
              dense: true,
              leading: Text(
                "Delete Device",
                style: TextStyle(fontSize: 14),
              ),
              trailing: new OutlineButton(
                color: Colors.red,
                shape: new RoundedRectangleBorder(borderRadius: new BorderRadius.circular(10.0)),
                textColor: Colors.red,
                onPressed: () {
                  configuration.deleteDevice(id);
                  Navigator.pop(context);
                },
                child: Icon(Icons.remove_circle, color: Colors.red,)//new Text("Delete", style: TextStyle(fontSize: 14)),
              ),
            ),
            ListTile(
              dense: true,
              leading: Text(
                "Min. Conn. Interval",
                style: TextStyle(fontSize: 14),
              ),
              title: Slider(
                min: 10,
                max: 120,
                divisions: 22,
                value: _minConnectionInterval,
                label: _minConnectionInterval.toString(),
                onChanged: (newRating) {
                  setState(() => _minConnectionInterval = newRating);
                },
                onChangeEnd: (newRating) {
                  configuration.setDeviceValue(id, "minConnectionInterval",
                      newRating.round().toString());
                },
              ),
              trailing: Text(_minConnectionInterval.round().toString()),
            ),
            ListTile(
              dense: true,
              leading: Text(
                "Save Raw Data",
                style: TextStyle(fontSize: 14),
              ),
              trailing: Checkbox(
                value: this.configuration.getDeviceValue(id, "saveRaw")=="true"?true: false,
                onChanged: (bool newValue) {
                  this.configuration.setDeviceValue(
                      id, "saveRaw", newValue.toString());
                  setState(() {});
                },
              ),
            ),
            Container(
              color: Theme.of(context).highlightColor,
              child: Padding(
                padding: const EdgeInsets.all(4.0),
                child: Center(
                  child: Text('Accel & Gyro Settings',
                      style: Theme.of(context).textTheme.subtitle),
                ),
              ),
            ),
            aclgyro(),
            Container(
              color: Theme.of(context).highlightColor,
              child: Padding(
                padding: const EdgeInsets.all(4.0),
                child: Center(
                  child: Text('Magnetometer Settings',
                      style: Theme.of(context).textTheme.subtitle),
                ),
              ),
            ),
            magnetometer(),
            Container(
              color: Theme.of(context).highlightColor,
              child: Padding(
                padding: const EdgeInsets.all(4.0),
                child: Center(
                  child: Text('PPG Settings',
                      style: Theme.of(context).textTheme.subtitle),
                ),
              ),
            ),
            ppg(),
          ],
        ),
      ),
    );
  }

  Widget aclgyro() {
    return Column(
      children: <Widget>[
        ListTile(
          dense: true,
          leading: Text(
            "Enable",
            style: TextStyle(fontSize: 14),
          ),
          trailing: Switch(
            value:
                this.configuration.getSensorValue(id, "GYROSCOPE", "enable") ==
                        "true"
                    ? true
                    : false,
            onChanged: (bool newValue) {
              this.configuration.setSensorValue(
                  id, "ACCELEROMETER", "enable", newValue.toString());
              this.configuration.setSensorValue(
                  id, "GYROSCOPE", "enable", newValue.toString());
              setState(() {});
            },
          ),
        ),
        ListTile(
          dense: true,
          leading: Text(
            "Sampling Rate",
            style: TextStyle(fontSize: 14),
          ),
          trailing: new DropdownButton<String>(
            style: TextStyle(color: Colors.black, fontSize: 14),
            items:
                <String>['25', '50', '62.5', '125', '250'].map((String value) {
              return new DropdownMenuItem<String>(
                value: value,
                child: new Text('$value Hz'),
              );
            }).toList(),
            value: this
                .configuration
                .getSensorValue(id, "GYROSCOPE", "frequency"),
            onChanged: (String value) {
              this
                  .configuration
                  .setSensorValue(id, "ACCELEROMETER", "frequency", value);
              this
                  .configuration
                  .setSensorValue(id, "GYROSCOPE", "frequency", value);
              setState(() {});
            },
          ),
        ),
        ListTile(
          dense: true,
          leading: Text(
            "Accl Sensitivity",
            style: TextStyle(fontSize: 14),
          ),
          trailing: new DropdownButton<String>(
              style: TextStyle(color: Colors.black, fontSize: 14),
              items: <String>['2', '4', '8', '16'].map((String value) {
                return new DropdownMenuItem<String>(
                  value: value,
                  child: new Text('\u00b1${value}g'),
                );
              }).toList(),
              value: this
                  .configuration
                  .getSensorValue(id, "ACCELEROMETER", "sensitivity"),
              onChanged: (String value) {
                this
                    .configuration
                    .setSensorValue(id, "ACCELEROMETER", "sensitivity", value);
                setState(() {});
              }),
        ),
        ListTile(
          dense: true,
          leading: Text(
            "Gyro Sensitivity",
            style: TextStyle(fontSize: 14),
          ),
          trailing: new DropdownButton<String>(
            style: TextStyle(color: Colors.black, fontSize: 14),
            items: <String>['250', '500', '1000', '2000'].map((String value) {
              return new DropdownMenuItem<String>(
                value: value,
                child: new Text('\u00b1${value} dps'),
              );
            }).toList(),
            value: this
                .configuration
                .getSensorValue(id, "GYROSCOPE", "sensitivity"),
            onChanged: (String value) {
              this
                  .configuration
                  .setSensorValue(id, "GYROSCOPE", "sensitivity", value);
              setState(() {});
            },
          ),
        ),
      ],
    );
  }

  Widget ppg() {
    return Column(
      children: <Widget>[
        ListTile(
          dense: true,
          leading: Text(
            "Enable",
            style: TextStyle(fontSize: 14),
          ),
          trailing: Switch(
            value:
                this.configuration.getSensorValue(id, "PPG", "enable") == "true"
                    ? true
                    : false,
            onChanged: (bool newValue) {
              setState(() {
                this
                    .configuration
                    .setSensorValue(id, "PPG", "enable", newValue.toString());
              });
            },
          ),
        ),
        ListTile(
          dense: true,
          leading: Text(
            "Sampling Rate",
            style: TextStyle(fontSize: 14),
          ),
          trailing: new DropdownButton<String>(
            style: TextStyle(color: Colors.black, fontSize: 14),
            items: <String>['25', '50'].map((String value) {
              return new DropdownMenuItem<String>(
                value: value,
                child: new Text('$value Hz'),
              );
            }).toList(),
            value: this.configuration.getSensorValue(id, "PPG", "frequency"),
            onChanged: (String value) {
              setState(() => this
                  .configuration
                  .setSensorValue(id, "PPG", "frequency", value));
            },
          ),
        ),
        ListTile(
          dense: true,
          leading: Text(
            "Red Brightness",
            style: TextStyle(fontSize: 14),
          ),
          title: Slider(
            min: 0.0,
            max: 255.0,
            divisions: 255,
            value: _ppgRed,
            label: _ppgRed.toString(),
            onChanged: (newRating) {
              setState(() => _ppgRed = newRating.roundToDouble());
            },
            onChangeEnd: (newRating) {
              configuration.setSensorValue(
                  id, "PPG", "ppgRed", newRating.round().toString());
            },
          ),
          trailing: Text(_ppgRed.round().toString()),
        ),
        ListTile(
          dense: true,
          leading: Text(
            "Green Brightness",
            style: TextStyle(fontSize: 14),
          ),
          title: Slider(
            min: 0.0,
            max: 255.0,
            divisions: 255,
            value: _ppgGreen,
            label: _ppgGreen.toString(),
            onChanged: (newRating) {
              setState(() => _ppgGreen = newRating.roundToDouble());
            },
            onChangeEnd: (newRating) {
              configuration.setSensorValue(
                  id, "PPG", "ppgGreen", newRating.round().toString());
            },
          ),
          trailing: Text(_ppgGreen.round().toString()),
        ),
        ListTile(
          dense: true,
          leading: Text(
            "Infrared Brightness",
            style: TextStyle(fontSize: 14),
          ),
          title: Slider(
            min: 0.0,
            max: 255.0,
            divisions: 255,
            value: _ppgInfrared,
            label: _ppgInfrared.toString(),
            onChanged: (newRating) {
              setState(() => _ppgInfrared = newRating.roundToDouble());
            },
            onChangeEnd: (newRating) {
              configuration.setSensorValue(
                  id, "PPG", "ppgInfrared", newRating.round().toString());
            },
          ),
          trailing: Text(_ppgInfrared.round().toString()),
        ),
        ListTile(
            dense: true,
          leading: Text(
            "PPG Filtered",
            style: TextStyle(fontSize: 14),
          ),
          trailing: Checkbox(
            value: this.configuration.getSensorValue(id, "PPG", "ppgFiltered")=="true"?true: false,
            onChanged: (bool newValue) {
              this.configuration.setSensorValue(
                  id, "PPG", "ppgFiltered", newValue.toString());
              setState(() {});
            },
          )
        ),
      ],
    );
  }

  Widget magnetometer() {
    return Column(
      children: <Widget>[
        ListTile(
          dense: true,
          leading: Text(
            "Enable",
            style: TextStyle(fontSize: 14),
          ),
          trailing: Switch(
            value: this
                        .configuration
                        .getSensorValue(id, "MAGNETOMETER", "enable") ==
                    "true"
                ? true
                : false,
            onChanged: (bool newValue) {
              this.configuration.setSensorValue(
                  id, "MAGNETOMETER", "enable", newValue.toString());
              setState(() {});
            },
          ),
        ),
        ListTile(
            dense: true,
          leading: Text(
            "Sampling Rate (Read only)",
            style: TextStyle(fontSize: 14),
          ),
          trailing: Text('25 Hz')
        ),
      ],
    );
  }
}
