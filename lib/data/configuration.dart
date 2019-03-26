/*
import 'package:phonesensor/phonesensor.dart';
*/

import '../motionsense.dart';

class Configuration{
  Map _configuration=new Map();
  Future<void> getSettings() async {

    _configuration = await Motionsense.getSettings;

  }
  Future<void> save() async {
    Motionsense.saveSettings(_configuration);
    _configuration = await Motionsense.getSettings;
  }
  int getDeviceNo(){
    if(_configuration["devices"]==null) return 0;
      return _configuration["devices"].length;
  }


  Future<void> addDevice(String platformType, String platformId, String deviceId, String deviceName, String version) async {
    List l = _configuration["devices"];
    Map device = new Map();
    device["platformType"]=platformType;
    device["platformId"]=platformId;
    device["deviceId"] = deviceId;
    device["deviceName"] = deviceName;
    device["version"] = version;
    l.add(device);
    await save();
  }
  List getDevices(){
    return _configuration["devices"];
  }

  Map getDevice(String id) {
    int no = getDeviceNo();
    if(no==0) return null;
    for(int i=0;i<no;i++){
      Map m = _configuration["devices"][i];
      if(m["deviceId"]==id)
        return m;
    }
    return null;
  }

  Map getDeviceByIndex(int i) {
    int no = getDeviceNo();
    if(i>=no) return null;
      return _configuration["devices"][i];
  }

  String getSensorValue(String deviceId, String sensor, String varName){
    Map d = getDevice(deviceId);
    int no = d["sensors"].length;
    for(int i=0;i<no;i++){
      if(d["sensors"][i]["id"]==sensor)
        return d["sensors"][i][varName];
    }
    return '';
  }

  Future<void> setSensorValue(String deviceId, String sensor, String varName, String value) async{
    Map d = getDevice(deviceId);
    int no = d["sensors"].length;
    for(int i=0;i<no;i++){
      if(d["sensors"][i]["id"]==sensor) {
        d["sensors"][i][varName] = value;
        break;
      }
    }
    await save();
  }

  Future<void> setToDefault(String id) async {
    Map device = getDevice(id);
    String platformType = device["platformType"];
    String platformId = device["platformId"];
    String deviceId = device["deviceId"];
    String deviceName = device["deviceName"];
    String version = device["version"];
    await deleteDevice(id);
    await addDevice(platformType, platformId, deviceId, deviceName, version);
  }

  Future<void> deleteDevice(String id) async{
    int no = getDeviceNo();
    if(no==0) return;
    List l = _configuration["devices"];
    for(int i=0;i<no;i++){
      Map m = l[i];
      if(m["deviceId"]==id) {
        l.removeAt(i);
        await save();
        return ;
      }
    }
  }

  Future<void> setDeviceValue(String id, String varName, String value) async{
    Map m = getDevice(id);
    m[varName]=value;
    await save();
  }

  String getDeviceValue(String id, String varName) {
    Map m = getDevice(id);
    if(m==null) return '';
    else return m[varName];
  }

}
