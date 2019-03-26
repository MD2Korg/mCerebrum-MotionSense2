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

import org.md2k.motionsense2.configuration.ConfigDevice;

import java.util.ArrayList;

public class DeviceManager {
    private static DeviceManager instance;
    private ArrayList<Device> devices;

    public static DeviceManager getInstance() {
        if (instance == null) instance = new DeviceManager();
        return instance;
    }

    private DeviceManager() {
        devices=new ArrayList<>();
    }


    public void addDevice(ConfigDevice cDevice) {
        for (int i = 0; i < devices.size(); i++) {
            if (cDevice.getDeviceId().equals(devices.get(i).getConfigDevice().getDeviceId()))
                return;
        }
        Device d = Device.create(cDevice);
        if (d == null) return;
        devices.add(d);
    }
    public int getDeviceNo(){
        return devices.size();
    }

    public void removeDevice(String deviceId) {
        int index = -1;
        for (int i = 0; i < devices.size(); i++) {
            if (deviceId.equals(devices.get(i).getConfigDevice().getDeviceId())) {
                index = i;
            }
        }
        if (index != -1) {
            devices.remove(index);
        }
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }
}
