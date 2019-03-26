package org.md2k.motionsense2.device;

import android.content.Context;
import android.util.Log;

import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;

import org.md2k.motionsense2.MyApplication;
import org.md2k.motionsense2.configuration.ConfigDevice;
import org.md2k.motionsense2.device.motion_sense_hrv_plus_gen2.MotionSenseHrvPlusGen2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
public abstract class Device {
    protected ConfigDevice configDevice;
    protected HashMap<ReceiveCallback, Integer> receiveCallbacks;
    protected Disposable connectionDisposable;
    private Disposable connectionStatusDisposable;
    protected DeviceInfo deviceInfo;

    static Device create(ConfigDevice cDevice) {
        return new MotionSenseHrvPlusGen2(cDevice);
    }

    public DeviceInfo getDeviceInfo() {
        return configDevice.getDeviceInfo();
    }

    public abstract ArrayList<SensorInfo> getSensorInfo();

    public void addListener(String sensor, ReceiveCallback receiveCallback) {
        if (receiveCallbacks.containsKey(receiveCallback)) return;
        receiveCallbacks.put(receiveCallback, Sensor.valueOf(sensor).id);
    }

    public void removeListener(ReceiveCallback receiveCallback) {
        receiveCallbacks.remove(receiveCallback);
    }

    public void addListener(ReceiveCallback receiveCallback) {
        if (receiveCallbacks.containsKey(receiveCallback)) return;
        receiveCallbacks.put(receiveCallback, -1);
    }

    protected ConfigDevice getConfigDevice() {
        return configDevice;
    }

    protected Device(ConfigDevice configDevice) {
        this.configDevice = configDevice;
        receiveCallbacks = new HashMap<>();
    }

    abstract protected Observable startSensing(RxBleConnection rxBleConnection);

    private void connect(){
        RxBleDevice bleDevice = MyApplication.getRxBleClient(MyApplication.getContext()).getBleDevice(configDevice.getDeviceId());
        connectionDisposable = bleDevice.establishConnection(false)
                .flatMap(new Function<RxBleConnection, Observable<Data>>() {
                    @Override
                    public Observable<Data> apply(RxBleConnection rxBleConnection) throws Exception {
                        return startSensing(rxBleConnection);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::disconnect)
                .subscribe(this::onConnectionReceived, this::onConnectionFailure);


    }

    public void start() {
        RxBleDevice bleDevice = MyApplication.getRxBleClient(MyApplication.getContext()).getBleDevice(configDevice.getDeviceId());
        connectionStatusDisposable = Observable.merge(Observable.just(bleDevice.getConnectionState()), bleDevice.observeConnectionStateChanges()).observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::stop)
                .subscribe(new Consumer<RxBleConnection.RxBleConnectionState>() {
                    @Override
                    public void accept(RxBleConnection.RxBleConnectionState rxBleConnectionState) throws Exception {
                        if(rxBleConnectionState== RxBleConnection.RxBleConnectionState.DISCONNECTED){
                            reconnect();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void onConnectionReceived(Data data) {
        Log.d("abc","onConnectionReceived: size = "+receiveCallbacks.size()+" datatype="+data.getSensorId());
        data.setDeviceInfo(deviceInfo);
        for (Map.Entry<ReceiveCallback, Integer> entry : receiveCallbacks.entrySet()) {
            if (entry.getValue() == -1 || entry.getValue() == data.getSensorId()) {
                try {
                    Log.d("abc","onConnectionReceived: value = "+entry.getValue());
                    entry.getKey().onReceive(data);
                }catch (Exception e){
                    Log.d("abc","abc");
                }
            }
        }
    }
    private void reconnect(){
        disconnect();
        connect();
    }

    private void onConnectionFailure(Throwable throwable) {
        Log.d("abc", "onFailure");
        reconnect();
        //noinspection ConstantConditions
//        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void disconnect() {
        if (connectionDisposable != null && !connectionDisposable.isDisposed()) {
            connectionDisposable.dispose();
        }
        connectionDisposable = null;
    }
    public void stop() {
        disconnect();
        if(connectionStatusDisposable!=null && !connectionStatusDisposable.isDisposed()){
            connectionStatusDisposable.dispose();
        }
        connectionStatusDisposable = null;
    }
}
