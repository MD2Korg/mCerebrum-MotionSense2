package org.md2k.motionsense2;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.motionsense2.configuration.Configuration;
import org.md2k.motionsense2.configuration.ConfigurationManager;
import org.md2k.motionsense2.datakit.DataKitManager;
import org.md2k.motionsense2.device.Data;
import org.md2k.motionsense2.device.Device;
import org.md2k.motionsense2.device.DeviceInfo;
import org.md2k.motionsense2.device.DeviceManager;
import org.md2k.motionsense2.device.ReceiveCallback;

import java.util.ArrayList;


/*
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * - Nazir Saleheen <nazir.saleheen@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class ServiceMotionSense extends Service {
    private DataKitManager dataKitManager;
    private Context context;
    DeviceManager deviceManager;
    Configuration configuration;
    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(ServiceMotionSense.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ContextCompat.checkSelfPermission(ServiceMotionSense.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
            return false;
        return ContextCompat.checkSelfPermission(ServiceMotionSense.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean prepare(){
        if(!hasPermission()) return false;
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()) return false;
        configuration = ConfigurationManager.read(context);
        dataKitManager = new DataKitManager(this);
        deviceManager = DeviceManager.getInstance();
        if(configuration==null || configuration.getDevices().size()==0){
            return false;
        }
        for(int i=0;i<configuration.getDevices().size();i++){
            deviceManager.addDevice(configuration.getDevices().get(i));
        }
        return true;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        if(!prepare()) {
            Log.e("abc","prepare failed");
            stopSelf();
            return;
        }
        try {
            dataKitManager.connect(context, new OnConnectionListener() {
                @Override
                public void onConnected() {
                    ArrayList<Device> devices = deviceManager.getDevices();
                    for(int i =0;i<devices.size();i++){
                        Device device = devices.get(i);
                        device.addListener(receiveCallback);
                        device.start();
                    }
                }
            });
        } catch (DataKitException e) {
            Log.e("abc","connect.. DataKitException e = "+e.getMessage());
            stopSelf();
            return;
        }
    }
    private ReceiveCallback receiveCallback = new ReceiveCallback() {
        @Override
        public void onReceive(Data d) {
            try {
                Log.d("abc","insert "+d.getSensorId());
                dataKitManager.insert(d);
            } catch (DataKitException e) {
                Log.e("abc","insert .. DataKitException e = "+e.getMessage());
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.d("abc","onDestroy");
        ArrayList<Device> devices = deviceManager.getDevices();
        for(int i =0;i<devices.size();i++){
            Device device = devices.get(i);
            device.removeListener(receiveCallback);
            device.stop();
        }
        if (configuration.isRunAsForegroundService())
            stopForegroundServiceIfRequired();
        dataKitManager.disconnect();
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundServiceIfRequired();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundServiceIfRequired() {
        if(!configuration.isRunAsForegroundService()) return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Wrist app running...");
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private void stopForegroundServiceIfRequired() {
        if(!configuration.isRunAsForegroundService()) return;
        stopForeground(true);
    }
}

