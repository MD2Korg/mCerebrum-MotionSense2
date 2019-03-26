import 'package:motionsense2/motionsense.dart';

class Summary{

  Map<String, dynamic> _summary;
  Future<void> readSummary() async {
    _summary = await Motionsense.getSummary();
  }
  bool isRunning(){
    if(_summary==null || !_summary.containsKey("isRunning")) return false;
    return _summary["isRunning"];
  }
  String getRunningTime(){
    if(_summary==null || !_summary.containsKey("runningTime")) return '';
    int runningTime = _summary["runningTime"];
    runningTime = (runningTime/1000).round();
    int sec = runningTime%60;
    runningTime = (runningTime/60).round();
    int min = runningTime%60;
    int hour = (runningTime/60).round();
    String res = (hour.toString().padLeft(2, '0'))+':'+(min.toString().padLeft(2, '0'))+":"+(sec.toString().padLeft(2, '0'));
    return res;
  }
  List getDevices(){
    if(_summary==null || !_summary.containsKey("devices")) return new List();
    return _summary["devices"];
  }
}