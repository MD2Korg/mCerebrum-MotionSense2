package org.md2k.motionsense2.plot;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.motionsense2.device.Data;
import org.md2k.motionsense2.device.Device;
import org.md2k.motionsense2.device.DeviceManager;
import org.md2k.motionsense2.device.ReceiveCallback;
import org.md2k.motionsense2.device.Sensor;

import java.util.ArrayList;
public class ActivityPlot extends RealtimeLineChartActivity {
    DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            dataSource = getIntent().getExtras().getParcelable(DataSource.class.getSimpleName());
                YAxis leftAxis = mChart.getAxisLeft();
//                leftAxis.setAxisMaximum(5);
//                leftAxis.setAxisMinimum(-5);
        }catch (Exception e){
            finish();
        }
    }

    @Override
    public void onResume() {
        Device device = getDevice();
        if (device != null) {
            device.addListener(dataSource.getType(), receiveCallback);
        }
        super.onResume();
    }
    private Device getDevice(){
        Device device = null;
        ArrayList<Device> d = DeviceManager.getInstance().getDevices();
        for (int i = 0; i < d.size(); i++) {
            if (d.get(i).getDeviceInfo().getPlatformId().equals(dataSource.getPlatform().getId())) {
                device = d.get(i);
                break;
            }
        }
        return device;
    }
    ReceiveCallback receiveCallback = new ReceiveCallback() {
        @Override
        public void onReceive(Data d) {
            Log.d("abc","abc");
            updatePlot(d, Sensor.valueOf(dataSource.getType()).getElements());
//            addEntry(d.getSample(), Sensor.valueOf(dataSource.getType()).getElements(),400);
        }
    };

    @Override
    public void onPause() {
        Device device = getDevice();
        if (device != null)
            device.removeListener(receiveCallback);
        super.onPause();
    }

    void updatePlot(Data data, String[] legends) {
        float[] sample = new float[1];

        getmChart().getDescription().setText(dataSource.getType());
        getmChart().getDescription().setPosition(1f, 1f);
        getmChart().getDescription().setEnabled(true);
        getmChart().getDescription().setTextColor(Color.WHITE);
            addEntry(data.getSample(), legends, 400);
    }

}

/*

public class ActivityPlot extends AppCompatActivity  implements
        OnChartValueSelectedListener {
    DataSource dataSource;
    protected LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_realtime_linechart);
        dataSource = getIntent().getExtras().getParcelable(DataSource.class.getSimpleName());

        initChart();
    }
    private void initChart(){
        Typeface tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        // enable description text
        chart.getDescription().setEnabled(true);
        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color

        chart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chart.setData(data);
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(tfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = chart.getXAxis();
        xl.setTypeface(tfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.resetAxisMaximum();
        leftAxis.resetAxisMinimum();
*/
/*
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
*//*

        //       leftAxis.resetAxisMaximum();
//        leftAxis.resetAxisMinimum();
               leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getDescription().setPosition(1f, 1f);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setTextColor(Color.WHITE);
        chart.getDescription().setText(dataSource.getType());
        String[] elements=Sensor.valueOf(dataSource.getType()).getElements();
        for (int i = 0; i < elements.length; i++) {
            LineDataSet set = createSet(i, elements[i]);
            data.addDataSet(set);
        }
//            data.addEntry(new Entry(set.getEntryCount(),value), 0);
        chart.getData().notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        // limit the number of visible entries
        chart.setVisibleXRangeMaximum(400);
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // move to the latest entry
        chart.moveViewToX(chart.getData().getEntryCount());
*/
/*
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaximum(5);
        leftAxis.setAxisMinimum(-5);
*//*


        // this automatically refreshes the chart (calls invalidate())
        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }
    int count = 0;

    ReceiveCallback receiveCallback = new ReceiveCallback() {
        @Override
        public void onReceive(Data d) {
            Log.d("abc","abc");
*/
/*
            count=(count+1)%3;
            if(count==0)
*//*

            addEntry(d.getSample());
        }
    };
    private Device getDevice(){
        Device device = null;
        ArrayList<Device> d = DeviceManager.getInstance().getDevices();
        for (int i = 0; i < d.size(); i++) {
            if (d.get(i).getDeviceInfo().getPlatformId().equals(dataSource.getPlatform().getId())) {
                device = d.get(i);
                break;
            }
        }
        return device;
    }

    @Override
    public void onResume() {
        Log.d("abc","onResume ..."+dataSource.getType());
        Device device = getDevice();
        if (device != null) {
            device.addListener(dataSource.getType(), receiveCallback);
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        Device device = getDevice();
        if (device != null)
            device.removeListener(receiveCallback);
        super.onPause();
    }
    public void addEntry(double[] value) {

        LineData data = chart.getData();
        if(data==null){
            Log.d("abc","abc");
            return;
        }
        List<ILineDataSet> dataSets = data.getDataSets();
        Log.d("abc","data = "+data+" "+data.getDataSetCount()+" "+data.getEntryCount());
        if (data != null) {
            for(int i=0;i<value.length;i++){
                data.addEntry(new Entry(dataSets.get(i).getEntryCount(), (float) value[i]), i);
            }
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(400);
            chart.moveViewToX(data.getEntryCount());

        }

    }
    int[] colors= new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    protected LineDataSet createSet(int i, String l) {

        LineDataSet set = new LineDataSet(null, l);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(colors[i%colors.length]);
        set.setDrawCircles(false);
//        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
//        set.setCircleRadius(4f);
//        set.setFillAlpha(65);
//        set.setFillColor(ColorTemplate.getHoloBlue());
//        set.setHighLightColor(Color.rgb(244, 117, 117));
//        set.setValueTextColor(Color.WHITE);
//        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }



    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
    @Override
    public void onDestroy(){

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }



}
*/