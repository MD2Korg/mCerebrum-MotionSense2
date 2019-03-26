package org.md2k.motionsense2.device.motion_sense_hrv_plus_gen2;
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

import com.polidea.rxandroidble2.RxBleConnection;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.motionsense2.configuration.ConfigDevice;
import org.md2k.motionsense2.configuration.ConfigSensor;
import org.md2k.motionsense2.device.Characteristics;
import org.md2k.motionsense2.device.Data;
import org.md2k.motionsense2.device.Sensor;
import org.md2k.motionsense2.device.SensorInfo;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class CharacteristicBattery extends Characteristics {
    private static final UUID CHARACTERISTICS = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    private static final int  BATTERY = Sensor.BATTERY.getId();
    private double[] battery=new double[0];

    protected CharacteristicBattery() {
        super(new Sensor[]{Sensor.BATTERY});
    }

    @Override
    public boolean isEnable(ConfigDevice configDevice){
        return configDevice.getSensor(Sensor.BATTERY.getDataSourceType()).isEnable();
    }

    @Override
    public ArrayList<SensorInfo> getSensorInfo(ConfigDevice configDevice) {
        super.setSensorInfos();
        boolean enable = isEnable(configDevice);
        boolean raw = configDevice.isSaveRaw();
        getSensorInfo(BATTERY).setEnable(enable);
        return super.getSensorInfos();
    }

    @Override
    public Observable<Data> listen(RxBleConnection rxBleConnection, ConfigDevice configDevice) {
        ConfigSensor cBattery = configDevice.getSensor(Sensor.BATTERY.getDataSourceType());
        return getCharacteristicListener(rxBleConnection, CHARACTERISTICS)
                .flatMap(new Function<byte[], Observable<Data>>() {
                    @Override
                    public Observable<Data> apply(byte[] bytes) throws Exception {
                        long curTime = DateTime.getDateTime();
                        Data data=null;
                        battery = getBattery(bytes);
                        if(cBattery.isEnable())
                            data = new Data(BATTERY, DateTime.getDateTime(), battery);
                        return Observable.just(data);
                    }
                });
    }
    private double[] getBattery(byte[] bytes) {
        double[] sample = new double[1];
        sample[0] = bytes[0];
        return sample;
    }

}
