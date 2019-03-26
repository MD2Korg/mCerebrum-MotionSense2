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

public class CharacteristicMagnetometer extends Characteristics {
    private static final UUID CHARACTERISTICS = UUID.fromString("DA39C924-1D81-48E2-9C68-D0AE4BBD351F");
    private static final int MAX_SEQUENCE_NUMBER = 65536;
    private int lastSequenceNumber = -1;
    private long lastCorrectedTimestamp = -1;
    private double[] magnetometerLast = new double[0];
    private double[] sequenceNumberLast = new double[0];
    private double[] rawLast = new double[0];
    private static final int MAGNETOMETER = Sensor.MAGNETOMETER.getId();
    private static final int SEQUENCE_NUMBER_MAGNETOMETER = Sensor.SEQUENCE_NUMBER_MAGNETOMETER.getId();
    private static final int RAW_MAGNETOMETER = Sensor.RAW_MAGNETOMETER.getId();

    CharacteristicMagnetometer() {
        super(new Sensor[]{Sensor.MAGNETOMETER, Sensor.SEQUENCE_NUMBER_MAGNETOMETER, Sensor.RAW_MAGNETOMETER});
    }

    @Override
    public boolean isEnable(ConfigDevice configDevice) {
        return configDevice.getSensor(Sensor.MAGNETOMETER.getDataSourceType()).isEnable();
    }

    @Override
    public ArrayList<SensorInfo> getSensorInfo(ConfigDevice configDevice) {
        super.setSensorInfos();
        boolean enable = isEnable(configDevice);
        boolean raw = configDevice.isSaveRaw();
        getSensorInfo(MAGNETOMETER).setEnable(enable);
        getSensorInfo(SEQUENCE_NUMBER_MAGNETOMETER).setEnable(enable && raw);
        getSensorInfo(RAW_MAGNETOMETER).setEnable(enable && raw);
        return super.getSensorInfos();
    }

    @Override
    public Observable<Data> listen(RxBleConnection rxBleConnection, ConfigDevice configDevice) {
        ConfigSensor cMagnetometer;
        cMagnetometer = configDevice.getSensor(Sensor.MAGNETOMETER.getDataSourceType());
        boolean isSaveRaw = configDevice.isSaveRaw();
        lastCorrectedTimestamp = -1;
        lastSequenceNumber = -1;
        return getCharacteristicListener(rxBleConnection, CHARACTERISTICS)
                .flatMap(new Function<byte[], Observable<Data>>() {
                    @Override
                    public Observable<Data> apply(byte[] bytes) throws Exception {
                        long curTime = DateTime.getDateTime();
                        ArrayList<Data> data = new ArrayList<>();
                        double[] mag1 = getMagnetometer1(bytes);
                        double[] mag2 = getMagnetometer2(bytes);
                        rawLast = getRaw(bytes);
                        int sequenceNumber = getSequenceNumber(bytes);
                        sequenceNumberLast = new double[]{sequenceNumber};
                        long correctedTimestamp = correctTimeStamp(sequenceNumber, curTime, lastSequenceNumber, lastCorrectedTimestamp, cMagnetometer.getFrequency(), MAX_SEQUENCE_NUMBER);
                        //TODO:
                        data.add(new Data(MAGNETOMETER, correctedTimestamp, mag1));
                        data.add(new Data(MAGNETOMETER, correctedTimestamp, mag2));
                        magnetometerLast = mag2;
                        if (isSaveRaw) {
                            data.add(new Data(RAW_MAGNETOMETER, correctedTimestamp, rawLast));
                            data.add(new Data(SEQUENCE_NUMBER_MAGNETOMETER, correctedTimestamp, sequenceNumberLast));
                        }
                        lastCorrectedTimestamp = correctedTimestamp;
                        lastSequenceNumber = sequenceNumber;
                        Data[] d = new Data[data.size()];
                        data.toArray(d);
                        return Observable.fromArray(d);
                    }
                });
    }

    private double[] getMagnetometer1(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertADCtoSI((short) ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
        sample[1] = convertADCtoSI((short) ((bytes[4] & 0xff) << 8) | (bytes[5] & 0xff));
        sample[2] = convertADCtoSI((short) ((bytes[8] & 0xff) << 8) | (bytes[9] & 0xff));
        return sample;
    }

    private double[] getMagnetometer2(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertADCtoSI((short) ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
        sample[1] = convertADCtoSI((short) ((bytes[6] & 0xff) << 8) | (bytes[7] & 0xff));
        sample[2] = convertADCtoSI((short) ((bytes[10] & 0xff) << 8) | (bytes[11] & 0xff));
        return sample;
    }

    private double convertADCtoSI(double mag) {
        return mag * 0.15;
    }

    private int getSequenceNumber(byte[] data) {
        return ((data[data.length - 2] & 0xff) << 8) | (data[data.length - 1] & 0xff);
    }

    private double[] getRaw(byte[] bytes) {
        double[] sample = new double[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            sample[i] = bytes[i];
        return sample;
    }
}
