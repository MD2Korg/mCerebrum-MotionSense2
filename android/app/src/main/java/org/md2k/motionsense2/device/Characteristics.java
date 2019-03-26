package org.md2k.motionsense2.device;
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

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


public abstract class Characteristics {
    private long lastReceivedTimestamp = -1;
    private int count;
    private long startTimestamp;
    private ArrayList<SensorInfo> sensorInfos;

    protected Characteristics(Sensor[] sensors){
        sensorInfos = new ArrayList<>();
        for (Sensor sensor : sensors) {
            sensorInfos.add(new SensorInfo(sensor));
        }
    }

    protected Observable<byte[]> getCharacteristicListener(RxBleConnection rxBleConnection, UUID uuid) {
        startTimestamp = DateTime.getDateTime();
        count = 0;
        lastReceivedTimestamp = startTimestamp;
        return rxBleConnection.setupNotification(uuid)
                .flatMap(notificationObservable -> notificationObservable)
                .map(new Function<byte[], byte[]>() {
                    @Override
                    public byte[] apply(byte[] bytes) throws Exception {
                        count++;
                        lastReceivedTimestamp = DateTime.getDateTime();
                        return bytes;
                    }
                });
    }
    public abstract boolean isEnable(ConfigDevice configDevice);
    public abstract ArrayList<SensorInfo> getSensorInfo(ConfigDevice configDevice);
    public abstract Observable<Data> listen(RxBleConnection rxBleConnection, ConfigDevice configDevice);
    protected long correctTimeStamp(int curSequence, long curTimestamp, int lastSequenceNumber, long lastTimestamp, double frequency, int maxLimit) {
        if (lastSequenceNumber == -1)
            return curTimestamp;
        int diff = (curSequence - lastSequenceNumber + maxLimit) % maxLimit;
        long predictedTimestamp = (long) (lastTimestamp + (1000.0 * diff) / frequency);
        if (curTimestamp < predictedTimestamp || curTimestamp - predictedTimestamp > 2000)
            predictedTimestamp = curTimestamp;
        return predictedTimestamp;
    }
    private double getFrequency() {
        if (lastReceivedTimestamp == startTimestamp) return 0;
        return count / ((lastReceivedTimestamp - startTimestamp) / 1000.0);
    }
    protected  void setSensorInfos(){
        for(int i=0;i<sensorInfos.size();i++){
            sensorInfos.get(i).setCount(count);
            sensorInfos.get(i).setFrequency(getFrequency());
        }
    }
    protected SensorInfo getSensorInfo(int id){
        for(int  i=0;i<sensorInfos.size();i++){
            if(sensorInfos.get(i).getId()==id) return sensorInfos.get(i);
        }
        return null;
    }

    public ArrayList<SensorInfo> getSensorInfos() {
        return sensorInfos;
    }
}
