package org.md2k.motionsense2.device.motion_sense_hrv_plus_gen2;

import com.polidea.rxandroidble2.RxBleConnection;

import org.md2k.motionsense2.configuration.ConfigDevice;
import org.md2k.motionsense2.device.Characteristics;
import org.md2k.motionsense2.device.Data;
import org.md2k.motionsense2.device.Device;
import org.md2k.motionsense2.device.Sensor;
import org.md2k.motionsense2.device.SensorInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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
public class MotionSenseHrvPlusGen2 extends Device {
    private ArrayList<Characteristics> characteristics;
    private CharacteristicConfig characteristicConfig;

    public MotionSenseHrvPlusGen2(ConfigDevice cDevice) {
        super(cDevice);
        this.deviceInfo = cDevice.getDeviceInfo();
        characteristics = new ArrayList<>();
        characteristics.add(new CharacteristicMotionSense());
        characteristics.add(new CharacteristicMagnetometer());
        characteristics.add(new CharacteristicPPG());
//        characteristics.add(new CharacteristicBattery());
        characteristicConfig = new CharacteristicConfig();
    }
    @Override
    public ArrayList<SensorInfo> getSensorInfo() {
        ArrayList<SensorInfo> sensorInfos = new ArrayList<>();
        for(int i=0;i<characteristics.size();i++){
            sensorInfos.addAll(characteristics.get(i).getSensorInfo(configDevice));
        }
        return sensorInfos;
    }


    private Observable<Boolean> setConfiguration(RxBleConnection rxBleConnection){
        return characteristicConfig.setConfiguration(rxBleConnection, configDevice);
    }
    private Observable<Data> listenSensing(RxBleConnection rxBleConnection){
        ArrayList<Observable> observables=new ArrayList<>();
        for(int i =0;i<characteristics.size();i++){
            if(characteristics.get(i).isEnable(configDevice)){
                observables.add(characteristics.get(i).listen(rxBleConnection, configDevice));
            }
        }
        Observable<Data>[] a = new Observable[observables.size()];
        observables.toArray(a);
        return Observable.mergeArray(a);

    }

    @Override
    public Observable<Data> startSensing(RxBleConnection rxBleConnection) {
        return setConfiguration(rxBleConnection).flatMap(new Function<Boolean, Observable<Data>>() {
            @Override
            public Observable<Data> apply(Boolean aBoolean) throws Exception {
                return listenSensing(rxBleConnection);
            }
        });
    }

//        return readMotionSense(rxBleConnection);
/*
        return rxBleConnection.readCharacteristic(CHARACTERISTIC_CONFIG).flatMapObservable(new Function<byte[], ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(byte[] bytes) throws Exception {
                Log.d("abc","abc");
                return Observable.just(true);
            }
        });
*/
/*
        return rxBleConnection.discoverServices().flatMapObservable(new Function<RxBleDeviceServices, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(RxBleDeviceServices rxBleDeviceServices) throws Exception {
                Log.d("abc","abc");
                rxBleConnection.readCharac
                return Observable.just(true);
            }
        });
    }
*/

}
