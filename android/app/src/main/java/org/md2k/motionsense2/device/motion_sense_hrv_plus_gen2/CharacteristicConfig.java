package org.md2k.motionsense2.device.motion_sense_hrv_plus_gen2;


import com.polidea.rxandroidble2.RxBleConnection;

import org.md2k.motionsense2.configuration.ConfigDevice;
import org.md2k.motionsense2.device.Sensor;

import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
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
public class CharacteristicConfig {
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("da39d650-1d81-48e2-9c68-d0ae4bbd351f");
    public Observable<Boolean> setConfiguration(RxBleConnection rxBleConnection, ConfigDevice configDevice) {
        return setSensorEnableObservable(rxBleConnection, configDevice)
                .flatMap(new Function<byte[], Single<byte[]>>() {
                    @Override
                    public Single<byte[]> apply(byte[] bytes) throws Exception {
                        if (configDevice.getSensor(Sensor.PPG.getDataSourceType()).isEnable())
                            return setPPGObservable(rxBleConnection, configDevice);
                        else return Single.just(new byte[0]);
                    }
                })
                .flatMap(new Function<byte[], Single<byte[]>>() {
                    @Override
                    public Single<byte[]> apply(byte[] bytes) throws Exception {
                        if (configDevice.getSensor(Sensor.PPG.getDataSourceType()).isEnable())
                            return setSamplingRateObservable(rxBleConnection, configDevice);
                        else return Single.just(new byte[0]);
                    }
                })
                .flatMap(new Function<byte[], Single<byte[]>>() {
                    @Override
                    public Single<byte[]> apply(byte[] bytes) throws Exception {
                        if (configDevice.getSensor(Sensor.GYROSCOPE.getDataSourceType()).isEnable())
                            return setSensitivityObservable(rxBleConnection, configDevice);
                        else return Single.just(new byte[0]);
                    }
                })
                .flatMap(new Function<byte[], Single<byte[]>>() {
                    @Override
                    public Single<byte[]> apply(byte[] bytes) throws Exception {
                        if (configDevice.getSensor(Sensor.PPG.getDataSourceType()).isEnable() && configDevice.getSensor(Sensor.PPG.getDataSourceType()).isPPGFiltered())
                            return setPPGFilterObservable(rxBleConnection, configDevice);
                        else return Single.just(new byte[0]);
                    }
                }).flatMap(new Function<byte[], Single<byte[]>>() {
                    @Override
                    public Single<byte[]> apply(byte[] bytes) throws Exception {
                        return setMinimumConnectionInterval(rxBleConnection, configDevice);
                    }
                }).flatMapObservable(new Function<byte[], ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(byte[] bytes) throws Exception {
                        return Observable.just(true);
                    }
                });
    }

    private Single<byte[]> setSensorEnableObservable(RxBleConnection rxBleConnection, ConfigDevice configDevice) {
        boolean enableAcl = configDevice.getSensor(Sensor.ACCELEROMETER.getDataSourceType()).isEnable();
        boolean enableGyro = configDevice.getSensor(Sensor.GYROSCOPE.getDataSourceType()).isEnable();
        boolean enableMag = configDevice.getSensor(Sensor.MAGNETOMETER.getDataSourceType()).isEnable();
        boolean enablePPG = configDevice.getSensor(Sensor.PPG.getDataSourceType()).isEnable();
        byte[] bytes = setSensors(enableAcl, enableGyro, enableMag, enablePPG);
        return rxBleConnection.writeCharacteristic(CHARACTERISTIC_UUID, bytes);
    }

    private Single<byte[]> setPPGObservable(RxBleConnection rxBleConnection, ConfigDevice configDevice) {
        int ppgRed = configDevice.getSensor(Sensor.PPG.getDataSourceType()).getPPGRed();
        int ppgGreen = configDevice.getSensor(Sensor.PPG.getDataSourceType()).getPPGRed();
        int ppgInfra = configDevice.getSensor(Sensor.PPG.getDataSourceType()).getPPGInfrared();

        byte[] bytes = setPPGConfiguration(ppgRed, ppgGreen, ppgInfra);
        return rxBleConnection.writeCharacteristic(CHARACTERISTIC_UUID, bytes);
    }
    private Single<byte[]> setSamplingRateObservable(RxBleConnection rxBleConnection, ConfigDevice configDevice){

        double freqAclGyro = configDevice.getSensor(Sensor.GYROSCOPE.getDataSourceType()).getFrequency();
        double freqPPG = configDevice.getSensor(Sensor.PPG.getDataSourceType()).getFrequency();
        Enumeration.ACCEL_GYRO_SAMPLING_RATES aclGyro = Enumeration.ACCEL_GYRO_SAMPLING_RATES.getEnum(freqAclGyro);
        Enumeration.PPG_SAMPLING_RATES ppg = Enumeration.PPG_SAMPLING_RATES.getEnum(freqPPG);
        byte[] bytes = setSamplingRate(aclGyro, ppg);
        return rxBleConnection.writeCharacteristic(CHARACTERISTIC_UUID, bytes);
    }
    private Single<byte[]> setSensitivityObservable(RxBleConnection rxBleConnection, ConfigDevice configDevice){
        int aclG = configDevice.getSensor(Sensor.ACCELEROMETER.getDataSourceType()).getSensitivity();
        int gyroSensitivity = configDevice.getSensor(Sensor.GYROSCOPE.getDataSourceType()).getSensitivity();
        Enumeration.ACCEL_SENSITIVITY aclSen = Enumeration.ACCEL_SENSITIVITY.getEnum(aclG);
        Enumeration.GYRO_SENSITIVITY gyroSen = Enumeration.GYRO_SENSITIVITY.getEnum(gyroSensitivity);
        byte[] bytes = setSensitivity(aclSen, gyroSen);
        return rxBleConnection.writeCharacteristic(CHARACTERISTIC_UUID, bytes);
    }

    private Single<byte[]> setPPGFilterObservable(RxBleConnection rxBleConnection, ConfigDevice configDevice){
        boolean enable = configDevice.getSensor(Sensor.PPG.getDataSourceType()).isPPGFiltered();
        byte[] bytes = setPpgFilteredData(enable);
        return rxBleConnection.writeCharacteristic(CHARACTERISTIC_UUID, bytes);
    }

    private Single<byte[]> setMinimumConnectionInterval(RxBleConnection rxBleConnection, ConfigDevice configDevice){
        int interval = configDevice.getMinConnectionInterval();
        byte[] bytes = setMinimumConnectionInterval(interval);
        return rxBleConnection.writeCharacteristic(CHARACTERISTIC_UUID, bytes);
    }

    /**
     * The gyroscope bit in turn determines the accelerometer and magnetometer bit.
     * <p>
     * Note 1: In the current firmware the accelerometer cannot be independently turned on or off.
     * The value in the accelerometer bit field mirrors the value set in Gyroscope bit automatically.
     * <p>
     * Note 2: The magnetometer can be turned on only if the gyroscope bit is enabled. If the
     * gyroscope bit is 0 then both the magnetometer and accelerometer cannot be turned on by setting the following bits.
     */
    private byte[] setSensors(Boolean accel, Boolean gyro, Boolean magneto, Boolean ppg) {
        byte opCode = 0x00; // Select enable/disable configuration

        byte enableSensors = 0x00;
        if (accel) {
            enableSensors = (byte) (enableSensors | 0x01);
        }
        if (ppg) {
            enableSensors = (byte) (enableSensors | 0x02);
        }
        if (magneto) {
            enableSensors = (byte) (enableSensors | 0x04);
        }
        if (gyro) {
            enableSensors = (byte) (enableSensors | 0x08);
        }

        byte[] result = new byte[2];
        result[0] = opCode;
        result[1] = enableSensors;
        return result;
    }

    /**
     * In case of Dual channel PPG with 2 LEDs and 2 separate photo-diodes the 2nd byte,
     * i.e. Green [1], is not used whereas Red [2] and Infrared (LSB) [0] bytes are begin used for
     * configuring the values for the colored LEDs. Currently the two possible choices in LEDs are
     * Red and Infra-red or Green and Infra-red.
     * <p>
     * Note 3: Although the current values can be set in the range from 0 to 255, the recommended
     * maximum values for Red/Green channel is 200 and Infrared is 100 expressed in decimal.
     */
    private byte[] setPPGConfiguration(int red, int green, int infrared) {
        byte opCode = 0x01; // Select ppg configuration

        if (red < 0) red = 0;
        if (red > 200) red = 200;
        if (green < 0) green = 0;
        if (green > 200) green = 200;
        if (infrared < 0) infrared = 0;
        if (infrared > 100) infrared = 100;


//        if (generation == 2) {
        green = 0x00; //Not used and expected to be set to 0x00
//        }

        byte[] result = new byte[4];
        result[0] = opCode;
        result[1] = (byte) red;
        result[2] = (byte) green;
        result[3] = (byte) infrared;
        return result;
    }
    /**
     * Enable = 0, Disables bandpass filter in hardware and transmits raw values.
     * Enable = 1, Enables bandpass filter for the PPG signal. Setting this bit also enables the
     * PPG DC characteristic, which computes the DC value of the PPG signal averaged over a window
     * of 40 seconds.
     */
    private byte[] setPpgFilteredData(Boolean enable) {
        byte opCode = 0x06; // Select PPG filtering

        byte enabled = 0x00;
        if (enable) enabled = 0x01; //Also enable PPG DC Values

        byte[] result = new byte[2];
        result[0] = opCode;
        result[1] = enabled;
        return result;
    }
    /**
     * The magnetometer sampling rate is fixed to 25Hz and the characteristic packet rate for
     * magnetometer is 12.5 Hz such that the 2 magnetometer samples are packed in the same packet.
     * <p>
     * Accelerometer/Gyroscope sampling rate: 25Hz, 50Hz, 62.5Hz, 125Hz, 250Hz
     * <p>
     * PPG sampling rate: 25Hz, 50Hz
     */

    private byte[] setSamplingRate(Enumeration.ACCEL_GYRO_SAMPLING_RATES accelGyro, Enumeration.PPG_SAMPLING_RATES ppg) {
        byte opCode = 0x02; // Select sampling rates for the sensors
        byte accelGyroSamplingRate = accelGyro.getValue();
        byte ppgSamplingRate = ppg.getValue();

        byte[] result = new byte[3];
        result[0] = opCode;
        result[1] = accelGyroSamplingRate;
        result[2] = ppgSamplingRate;
        return result;
    }

/**
 * Gyroscope sensitivity (dps): ±250, ±500, ±1000, ±2000
 * Accelerometer sensitivity (g): ±2, ±4, ±8, ±16
 */

    private byte[] setSensitivity(Enumeration.ACCEL_SENSITIVITY accel, Enumeration.GYRO_SENSITIVITY gyro) {
        byte opCode = 0x03; // Select sampling rates for the sensors
        byte accelSensitivity = accel.getValue();
        byte gyroSensitivity = gyro.getValue();

        byte[] result = new byte[3];
        result[0] = opCode;
        result[1] = gyroSensitivity;
        result[2] = accelSensitivity;
        return result;
    }

    private byte[] setMinimumConnectionInterval(int interval) {
        byte opCode = 0x05; // Select connection interval

        if (interval < 10) interval = 10;
        if (interval > 120) interval = 120;

        byte[] result = new byte[2];
        result[0] = opCode;
        result[1] = (byte) interval;
        return result;
    }
    /**
     * Expected return format byte array
     * byte[9] Enable/Disable Configuration (0x00-0x0f)
     * byte[8] PPG Red LED (0-255)
     * byte[7] PPG Green LED (0-255)
     * byte[6] PPG Infrared LED (0-255)
     * byte[5] Gyro Sampling Rate (1-5)
     * byte[4] PPG Sampling Rate (0x0A,0x14,0x28)
     * byte[3] Gyroscope Sensitivity (0-3)
     * byte[2] Accelerometer Sensitivity (0-3)
     * byte[1] Min connection interval (10-120ms)
     * byte[0] PPG Filter Enabled (0-1)
     *
     * If configType == 0x01:
     * byte[9] Hz Sensitivity (0-255)
     * byte[8] Hy Sensitivity (0-255)
     * byte[7] Hx Sensitivity (0-255)
     * byte[6] = 0
     * byte[5] = 0
     * byte[4] = 0
     * byte[3] = 0
     * byte[2] = 0
     * byte[1] = 0
     * byte[0] = 0
     */
    private byte[] currentConfiguration(int type) {
        byte opCode = 0x04; // Select current configuration

        byte configType = 0x00;
        if (type == 0) configType = 0x00; //Return current running configuration
        if (type == 1) configType = 0x01; //Return magnetometer sensitivity

        byte[] result;
        if (configType == 0x01) {
            result = new byte[1];
            result[0] = opCode;
        } else {
            result = new byte[2];
            result[0] = opCode;
            result[1] = configType;
        }
        return result;
    }

}
