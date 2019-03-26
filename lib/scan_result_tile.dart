// Copyright 2017, Paul DeMarco.
// All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

import 'package:flutter/material.dart';
import 'package:flutter_blue/flutter_blue.dart';
import 'my_callback.dart';
class ScanResultTile extends StatefulWidget {
  final ScanResult result;
  final MyCallback myCallback;

  const ScanResultTile({Key key, this.result, this.myCallback}) : super(key: key);

  @override
  _ScanResultTileState createState() => _ScanResultTileState(result, myCallback);
}

class _ScanResultTileState extends State<ScanResultTile> {
  final ScanResult result;
  final MyCallback myCallback;
  int _radioValue;
  String _result;

  _ScanResultTileState(this.result, this.myCallback);

  Widget _buildTitle(BuildContext context) {
    if (result.device.name.length > 0) {
      return Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(result.device.name),
          Text(
            result.device.id.toString(),
            style: Theme
                .of(context)
                .textTheme
                .caption,
          )
        ],
      );
    } else {
      return Text(result.device.id.toString());
    }
  }

  String getNiceHexArray(List<int> bytes) {
    return '[${bytes.map((i) => i.toRadixString(16).padLeft(2, '0')).join(
        ', ')}]'
        .toUpperCase();
  }

  @override
  Widget build(BuildContext context) {
    return ExpansionTile(
//      leading: Text(result.rssi.toString()),
      title: _buildTitle(context),
      children: <Widget>[
        Text("Position",
            style: TextStyle(
                fontWeight: FontWeight.bold,
                decoration: TextDecoration.underline)),
        new Row(mainAxisAlignment: MainAxisAlignment.center, children: <Widget>[
          new Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              new Radio(
                value: 0,
                groupValue: _radioValue,
                onChanged: _handleRadioValueChange,
              ),
              new Text(
                'Left Wrist',
                style: new TextStyle(fontSize: 14.0),
              ),
              new Radio(
                value: 1,
                groupValue: _radioValue,
                onChanged: _handleRadioValueChange,
              ),
              new Text(
                'Right Wrist',
                style: new TextStyle(
                  fontSize: 14.0,
                ),
              ),
              new Radio(
                value: 2,
                groupValue: _radioValue,
                onChanged: _handleRadioValueChange,
              ),
              new Text(
                'Other',
                style: new TextStyle(fontSize: 14.0),
              ),
            ],
          )
        ]),
        _radioValue==2?
        Padding(padding: EdgeInsets.fromLTRB(100, 0, 100, 0), child:
        TextField(style: TextStyle(fontSize: 12.0),
          decoration: InputDecoration(labelText: "Position (Other):", labelStyle: TextStyle(fontSize: 14.0)),
          textCapitalization: TextCapitalization.characters,
            onChanged: (text) {
              _result = text;
            }
        ),
        ): SizedBox(),
        SizedBox(height: 10,),
        new OutlineButton(
            color: Colors.green,
            shape: new RoundedRectangleBorder(borderRadius: new BorderRadius.circular(10.0)),
            textColor: Colors.green,
            onPressed: () {
              save();
            },

            child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
              Icon(Icons.add_circle_outline, color: Colors.green,), //new Text("Delete", style: TextStyle(fontSize: 14)),
              Text("Add")//new Text("Delete", style: TextStyle(fontSize: 14)),
          ])
        ),
      ],
    );
  }

  _handleRadioValueChange(int value) {
    setState(() {
      _radioValue = value;
      switch (_radioValue) {
        case 0:
          _result = "LEFT_WRIST";
          break;
        case 1:
          _result = "RIGHT_WRIST";
          break;
        case 2:
          _result = "OTHER";
          break;
      }
    });
  }
  void save(){
    myCallback(result.device, _result);
  }
}
