package org.md2k.motionsense2.datakit;
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

import android.content.Context;
import android.util.Log;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.motionsense2.device.Data;
import org.md2k.motionsense2.device.DeviceInfo;
import org.md2k.motionsense2.device.Sensor;

import java.util.ArrayList;
import java.util.HashMap;


public class DataKitManager {
    private DataKitAPI dataKitAPI;
    private HashMap<String, DataSourceClient> dataSourceClients;

    public DataKitManager(Context context) {
        dataKitAPI = DataKitAPI.getInstance(context);
        dataSourceClients = new HashMap<>();
    }

    public void connect(Context context, OnConnectionListener onConnectionListener) throws DataKitException {
        dataKitAPI.connect(onConnectionListener);
    }

    private DataSourceClient register(DeviceInfo deviceInfo, String[] fields, String dataSourceType) throws DataKitException {
        Platform p = new PlatformBuilder()
                .setType(deviceInfo.getPlatformType())
                .setId(deviceInfo.getPlatformId())
                .setMetadata(METADATA.VERSION_FIRMWARE, deviceInfo.getVersion())
                .setMetadata(METADATA.DEVICE_ID, deviceInfo.getDeviceId()).build();
        DataSourceBuilder d = new DataSourceBuilder().setType(dataSourceType).setDataDescriptors(createDataDescriptor(fields)).setPlatform(p);
        return dataKitAPI.register(d);
    }
    private ArrayList<HashMap<String, String>> createDataDescriptor(String[] fields){
        ArrayList<HashMap<String, String>> desc = new ArrayList<>();
        for (String field : fields) {
            HashMap<String, String> h = new HashMap<>();
            h.put("NAME", field);
            desc.add(h);
        }
        return desc;
    }


    public void insert(Data data) throws DataKitException {
        Sensor s = Sensor.getSensor(data.getSensorId());
        DataSourceClient dc = dataSourceClients.get(data.getDeviceInfo().getDeviceId() + " " + s.getDataSourceType());
        if (dc == null) {
            dc = register(data.getDeviceInfo(), s.getElements(), s.getDataSourceType());
            dataSourceClients.put(data.getDeviceInfo().getDeviceId() + " " + s.getDataSourceType(), dc);
        }
        DataTypeDoubleArray d = new DataTypeDoubleArray(data.getTimestamp(), data.getSample());
        dataKitAPI.insertHighFrequency(dc, d);
        Log.d("abc","abc");
    }

    public void disconnect() {
        dataKitAPI.disconnect();
    }

/*
    public void setSummary(DataSourceClient dataSourceClient, DataType dataType) {
        try {
            dataKitAPI.setSummary(dataSourceClient, dataType);
        } catch (DataKitException e) {
            throw new RuntimeException("DataKit setSummary error e="+e.getMessage());
        }
    }
*/

}
