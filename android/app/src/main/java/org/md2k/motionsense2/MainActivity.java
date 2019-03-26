package org.md2k.motionsense2;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.motionsense2.configuration.Configuration;
import org.md2k.motionsense2.configuration.ConfigurationManager;
import org.md2k.motionsense2.plot.ActivityPlot;
import org.md2k.motionsense2.summary.Summary;

import java.util.List;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
    public static final String CHANNEL = "org.md2k.motionsense2";
    private static final String GET_SETTINGS = "GET_SETTINGS";
    private static final String SAVE_SETTINGS = "SAVE_SETTINGS";
    private static final String GET_SUMMARY = "GET_SUMMARY";
    private static final String BACKGROUND_SERVICE = "BACKGROUND_SERVICE";
    private static final String PLOT = "PLOT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.BLUETOOTH
                , Manifest.permission.BLUETOOTH_ADMIN
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.INTERNET).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    GeneratedPluginRegistrant.registerWith(MainActivity.this);
                    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(h);
                } else {
                    finish();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    private MethodChannel.MethodCallHandler h = new MethodChannel.MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall call, MethodChannel.Result result) {
            Gson gson = new Gson();
            Configuration c;
            switch (call.method) {
                case GET_SETTINGS:
                    c = ConfigurationManager.read(MainActivity.this);
                    result.success(gson.toJson(c));
                    break;

                case SAVE_SETTINGS:
                    String x = call.argument("config");
                    c = gson.fromJson(x, Configuration.class);
                    ConfigurationManager.write(MainActivity.this, c);
                    result.success(true);
                    break;
                case GET_SUMMARY:
                    Summary summary = Summary.getSummary(MainActivity.this);
                    result.success(gson.toJson(summary));
                    break;
                case BACKGROUND_SERVICE:
                    boolean run = call.argument("run");
                    Intent intent = new Intent(MainActivity.this, ServiceMotionSense.class);
                    if(run)
                        startService(intent);
                    else stopService(intent);
                    result.success(true);
                    break;
                case PLOT:
                    String platformType = call.argument("platformType");
                    String platformId = call.argument("platformId");
                    String dataSourceType = call.argument("dataSourceType");
                    Platform p = new PlatformBuilder().setType(platformType).setId(platformId).build();
                    DataSource d = new DataSourceBuilder().setType(dataSourceType).setPlatform(p).build();
                    Intent intent1 = new Intent(MainActivity.this, ActivityPlot.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DataSource.class.getSimpleName(), d);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    break;
                default:
                    result.notImplemented();
                    break;

            }

        }
    };

}
