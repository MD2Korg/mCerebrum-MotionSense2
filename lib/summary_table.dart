// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:flutter/material.dart';
import 'package:motionsense2/motionsense.dart';

class DataSourceInfo {
  DataSourceInfo(this.deviceName, this.platformType, this.platformId, this.sensorType, this.sensorTitle, this.dataCount,
      this.frequency, this.lastSample);

  final String deviceName;
  final String sensorType;
  final String sensorTitle;
  final String platformType;
  final String platformId;
  final int dataCount;
  final double frequency;
  final List<double> lastSample;
}

class DataSourceInfos extends DataTableSource {
  List<DataSourceInfo> _dataSourceInfos = new List();

  DataSourceInfos(List devices) {
    _dataSourceInfos.clear();
    for (int i = 0; i < devices.length; i++) {
      String platformId = devices[i]["deviceInfo"]["platformId"];
      String platformType = devices[i]["deviceInfo"]["platformType"];
      String deviceName = "MS_Gen2(" + platformId.substring(0, 1) + ")";
      List sensors = devices[i]["sensorInfo"];
      for (int j = 0; j < sensors.length; j++) {
        String sensorName = sensors[j]["title"];
        String sensorType = sensors[j]["dataSourceType"];
        double freq = sensors[j]["frequency"];
        int count = sensors[j]["count"];
//        List<double> sample = sensors[j]["lastSample"];
        DataSourceInfo a =
            new DataSourceInfo(deviceName, platformType, platformId,sensorType, sensorName, count, freq, List());
        _dataSourceInfos.add(a);
      }
    }
  }


  @override
  DataRow getRow(int index) {
    assert(index >= 0);
    if (index >= _dataSourceInfos.length) return null;
    final DataSourceInfo sensor = _dataSourceInfos[index];
    return DataRow.byIndex(
        index: index,
/*
        onSelectChanged: (bool selected){
          Motionsense.plot(sensor.platformType, sensor.platformId, sensor.sensorType);
          print("abc");
        },
*/
        cells: <DataCell>[
          DataCell(Text('${sensor.deviceName}')),
          DataCell(Text('${sensor.sensorTitle}')),
          DataCell(Text('${sensor.dataCount}')),
          DataCell(Text('${sensor.frequency.toStringAsFixed(2)}')),
          DataCell(sensor.sensorType.startsWith("RAW")?SizedBox():Icon(Icons.multiline_chart, color: Colors.green,), onTap:(){
            if(sensor.sensorType.startsWith("RAW")) return;
            Motionsense.plot(sensor.platformType, sensor.platformId, sensor.sensorType);
          }),
        ]);
  }


  @override
  int get rowCount => _dataSourceInfos.length;

  @override
  bool get isRowCountApproximate => false;

  @override
  int get selectedRowCount => 0;
}

class DataSourceTable extends StatelessWidget {
  DataSourceInfos _dataSourceInfos;

  DataSourceTable(List devices) {
    _dataSourceInfos = new DataSourceInfos(devices);
  }

  @override
  Widget build(BuildContext context) {
    return _dataSourceInfos == null
        ? SizedBox()
        : ListView(children: <Widget>[
            PaginatedDataTable(
                header: SizedBox(height: 0,),
                rowsPerPage: 20,
                onRowsPerPageChanged: (int value) {},
                columns: <DataColumn>[
                  DataColumn(label: const Text('Device')),
                  DataColumn(
                    label: const Text('Sensor'),
                  ),
                  DataColumn(
                    label: const Text('Count'),
                  ),
                  DataColumn(
                    label: const Text('Frequency'),
                  ),
                  DataColumn(label: const Text('Plot')),
                ],
                source: _dataSourceInfos)
          ]);
  }
}
