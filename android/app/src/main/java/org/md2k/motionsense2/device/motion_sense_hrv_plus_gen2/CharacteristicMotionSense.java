package org.md2k.motionsense2.device.motion_sense_hrv_plus_gen2;

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
class CharacteristicMotionSense extends Characteristics {
    private static final UUID CHARACTERISTICS = UUID.fromString("DA39C921-1D81-48E2-9C68-D0AE4BBD351F");
    private static final int MAX_SEQUENCE_NUMBER = 65536;
    private int lastSequenceNumber = -1;
    private long lastCorrectedTimestamp = -1;
    private double[] acl = new double[0];
    private double[] gyro = new double[0];
    private double[] seq = new double[0];
    private double[] raw = new double[0];

    private static final int ACCELEROMETER = Sensor.ACCELEROMETER.getId();
    private static final int GYROSCOPE = Sensor.GYROSCOPE.getId();
    private static final int SEQUENCE_NUMBER_MOTION_SENSOR = Sensor.SEQUENCE_NUMBER_MOTION_SENSOR.getId();
    private static final int RAW_MOTION_SENSOR = Sensor.RAW_MOTION_SENSOR.getId();

    protected CharacteristicMotionSense() {
        super(new Sensor[]{Sensor.ACCELEROMETER, Sensor.GYROSCOPE, Sensor.SEQUENCE_NUMBER_MOTION_SENSOR, Sensor.RAW_MOTION_SENSOR});
    }

    @Override
    public boolean isEnable(ConfigDevice configDevice) {
        if (configDevice.getSensor(Sensor.ACCELEROMETER.getDataSourceType()).isEnable()) return true;
        if (configDevice.getSensor(Sensor.GYROSCOPE.getDataSourceType()).isEnable()) return true;
        return false;
    }
    @Override
    public ArrayList<SensorInfo> getSensorInfo(ConfigDevice configDevice) {
        super.setSensorInfos();
        boolean saveRaw = configDevice.isSaveRaw();
        getSensorInfo(ACCELEROMETER).setEnable(configDevice.getSensor(Sensor.ACCELEROMETER.getDataSourceType()).isEnable());
        getSensorInfo(GYROSCOPE).setEnable(configDevice.getSensor(Sensor.GYROSCOPE.getDataSourceType()).isEnable());
        getSensorInfo(SEQUENCE_NUMBER_MOTION_SENSOR).setEnable( isEnable(configDevice) && saveRaw);
        getSensorInfo(RAW_MOTION_SENSOR).setEnable( isEnable(configDevice) && saveRaw);
        return super.getSensorInfos();
    }


    @Override
    public Observable<Data> listen(RxBleConnection rxBleConnection, ConfigDevice configDevice) {
        ConfigSensor cAccelerometer = configDevice.getSensor(Sensor.ACCELEROMETER.getDataSourceType());
        ConfigSensor cGyroscope = configDevice.getSensor(Sensor.GYROSCOPE.getDataSourceType());
        boolean isSaveRaw = configDevice.isSaveRaw();
        lastSequenceNumber = -1;
        lastCorrectedTimestamp = -1;
        double scaleFactorAcl = getScalingFactorAcl(cAccelerometer.getSensitivity());
        double scaleFactorGyro = getScalingFactorGyro(cGyroscope.getSensitivity());
        return getCharacteristicListener(rxBleConnection, CHARACTERISTICS)
                .flatMap(new Function<byte[], Observable<Data>>() {
                    @Override
                    public Observable<Data> apply(byte[] bytes) throws Exception {
                        long curTime = DateTime.getDateTime();
                        ArrayList<Data> data = new ArrayList<>();
                        acl = getAccelerometer(bytes, scaleFactorAcl);
                        gyro = getGyroscope(bytes, scaleFactorGyro);
                        raw = getRaw(bytes);
                        seq = new double[]{getSequenceNumber(bytes)};
                        long newCorrectedTimestamp = correctTimeStamp((int) seq[0], curTime, lastSequenceNumber, lastCorrectedTimestamp, cGyroscope.getFrequency(), MAX_SEQUENCE_NUMBER);
                        if (cAccelerometer.isEnable()) {
                            data.add(new Data(ACCELEROMETER, newCorrectedTimestamp, acl));
                        }
                        if (cGyroscope.isEnable()) {
                            data.add(new Data(GYROSCOPE, newCorrectedTimestamp, gyro));
                        }
                        if (isSaveRaw) {
                            data.add(new Data(SEQUENCE_NUMBER_MOTION_SENSOR, newCorrectedTimestamp, seq));
                            data.add(new Data(RAW_MOTION_SENSOR, curTime, raw));
                        }
                        lastCorrectedTimestamp = newCorrectedTimestamp;
                        lastSequenceNumber = (int) seq[0];
                        Data[] d = new Data[data.size()];
                        data.toArray(d);
                        return Observable.fromArray(d);
                    }
                });
    }

    private double getScalingFactorAcl(int sensitivityAcl) {
        switch (sensitivityAcl) {
            case 2:
                return 16384;
            case 4:
                return 8192;
            case 8:
                return 4096;
            case 16:
                return 2048;
            default:
                return 16384;
        }
    }

    private double getScalingFactorGyro(int sensitivityGyro) {
        switch (sensitivityGyro) {
            case 250:
                return 131;
            case 500:
                return 65.5;
            case 1000:
                return 32.8;
            case 2000:
                return 16.4;
            default:
                return 131;
        }
    }

    private double[] getAccelerometer(byte[] bytes, double scalingFactor) {
        double[] sample = new double[3];
        sample[0] = convertADCtoSI((short) ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff), scalingFactor);
        sample[1] = convertADCtoSI((short) ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff), scalingFactor);
        sample[2] = convertADCtoSI((short) ((bytes[4] & 0xff) << 8) | (bytes[5] & 0xff), scalingFactor);
        return sample;
    }


    private double[] getGyroscope(byte[] bytes, double scalingFactor) {
        double[] sample = new double[3];
        sample[0] = convertADCtoSI((short) ((bytes[6] & 0xff) << 8) | (bytes[7] & 0xff), scalingFactor);
        sample[1] = convertADCtoSI((short) ((bytes[8] & 0xff) << 8) | (bytes[9] & 0xff), scalingFactor);
        sample[2] = convertADCtoSI((short) ((bytes[10] & 0xff) << 8) | (bytes[11] & 0xff), scalingFactor);
        return sample;
    }

    private static double convertADCtoSI(double x, double scalingFactor) {
        return x / scalingFactor;
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
