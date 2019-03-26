package org.md2k.motionsense2.device.motion_sense_hrv;

import com.polidea.rxandroidble2.RxBleConnection;

import org.md2k.motionsense2.configuration.ConfigDevice;
import org.md2k.motionsense2.device.Data;
import org.md2k.motionsense2.device.Device;
import org.md2k.motionsense2.device.Sensor;
import org.md2k.motionsense2.device.SensorInfo;

import java.util.ArrayList;

import io.reactivex.Observable;


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
public class MotionSenseHrv extends Device {
    public static final String PLATFORM_TYPE = "MOTION_SENSE_HRV";


    @Override
    public ArrayList<SensorInfo> getSensorInfo() {
        return null;
    }

    protected MotionSenseHrv(ConfigDevice configDevice) {
        super(configDevice);
    }

    @Override
    public Observable<Data[]> startSensing(RxBleConnection rxBleConnection) {
        return null;
    }
}
