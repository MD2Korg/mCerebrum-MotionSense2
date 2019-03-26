package org.md2k.motionsense2.configuration;

import android.content.Context;
import android.os.Environment;

import java.util.ArrayList;

/*
 * Copyright (c) 2015, The University of Memphis, MD2K Center
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
public class ConfigurationManager {
    private static final String DEFAULT_CONFIG_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.motionsense2/default_config.json";
    private static final String CONFIG_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.motionsense2/config.json";
    private static final String CONFIG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.motionsense2";
    private static final String DEFAULT_CONFIG_ASSET = "default_config.json";

    public static Configuration  read(Context context){
        Configuration c = Storage.readJson(CONFIG_FILEPATH, Configuration.class);
        Configuration cDefault = Storage.readJson(DEFAULT_CONFIG_FILEPATH, Configuration.class);
        Configuration cAsset = Storage.readJsonFromAsset(context, DEFAULT_CONFIG_ASSET, Configuration.class);
        if(c==null) c=new Configuration();
        if(c.requiredDeviceNo==null && cDefault!=null) c.requiredDeviceNo=cDefault.requiredDeviceNo;if(c.requiredDeviceNo==null) c.requiredDeviceNo=cAsset.requiredDeviceNo;
        if(c.runAsForegroundService==null && cDefault!=null) c.runAsForegroundService=cDefault.runAsForegroundService;if(c.runAsForegroundService==null) c.runAsForegroundService=cAsset.runAsForegroundService;
        if(c.devices==null) c.devices=new ArrayList<>();
        for(int i =0;i<c.devices.size();i++){
            String platformType = c.devices.get(i).platformType;
            String platformId = c.devices.get(i).platformId;
            ConfigDevice deviceDefault = null;
            if(cDefault!=null) deviceDefault = cDefault.getDevice(platformType, platformId);
            ConfigDevice deviceAsset = cAsset.getDevice(platformType, platformId);
            setDefaultDevice(c.devices.get(i), deviceDefault, deviceAsset);
        }
        return c;
    }
    private static void setDefaultDevice(ConfigDevice c, ConfigDevice d, ConfigDevice a){
        if(c.required==null && d!=null) c.required=d.required;if(c.required==null) c.required=a.required;
        if(c.enable==null && d!=null) c.enable=d.enable;if(c.enable==null) c.enable=a.enable;
        if(c.deviceName==null && d!=null) c.deviceName=d.deviceName;if(c.deviceName==null) c.deviceName=a.deviceName;
        if(c.deviceId==null && d!=null) c.deviceId=d.deviceId;if(c.deviceId==null) c.deviceId=a.deviceId;
        if(c.platformType==null && d!=null) c.platformType=d.platformType;if(c.platformType==null) c.platformType=a.platformType;
        if(c.platformId==null && d!=null) c.platformId=d.platformId;if(c.platformId==null) c.platformId=a.platformId;
        if(c.version==null && d!=null) c.version=d.version;if(c.version==null) c.version=a.version;
        if(c.minConnectionInterval==null && d!=null) c.minConnectionInterval = d.minConnectionInterval; if(c.minConnectionInterval==null) c.minConnectionInterval = a.minConnectionInterval;
        if(c.saveRaw==null && d!=null) c.saveRaw = d.saveRaw; if(c.saveRaw==null) c.saveRaw = a.saveRaw;
        if(c.sensors==null) c.sensors= new ArrayList<>();
        for(int i=0;i<a.sensors.size();i++){
            String id = a.sensors.get(i).id;
            ConfigSensor cs = c.getSensor(id);
            if(cs==null) cs = new ConfigSensor();cs.id = id;
            c.sensors.add(cs);
            ConfigSensor cd = null;
            if(d!=null) cd = d.getSensor(id);
            setDefaultSensor(cs, cd, a.sensors.get(i));
        }
    }
    private static void setDefaultSensor(ConfigSensor c, ConfigSensor d, ConfigSensor a){
        if(c.id==null && d!=null) c.id = d.id; if(c.id==null) c.id = a.id;
        if(c.enable==null && d!=null) c.enable = d.enable; if(c.enable==null) c.enable = a.enable;
        if(c.ppgRed==null && d!=null) c.ppgRed = d.ppgRed; if(c.ppgRed==null) c.ppgRed = a.ppgRed;
        if(c.ppgGreen==null && d!=null) c.ppgGreen = d.ppgGreen; if(c.ppgGreen==null) c.ppgGreen = a.ppgGreen;
        if(c.ppgInfrared==null && d!=null) c.ppgInfrared = d.ppgInfrared; if(c.ppgInfrared==null) c.ppgInfrared = a.ppgInfrared;
        if(c.frequency==null && d!=null) c.frequency = d.frequency; if(c.frequency==null) c.frequency = a.frequency;
        if(c.sensitivity==null && d!=null) c.sensitivity = d.sensitivity; if(c.sensitivity==null) c.sensitivity = a.sensitivity;
        if(c.ppgFiltered==null && d!=null) c.ppgFiltered = d.ppgFiltered; if(c.ppgFiltered==null) c.ppgFiltered = a.ppgFiltered;
    }

    public static void write(Context context, Configuration c) {
        Storage.writeJson(CONFIG_DIRECTORY, CONFIG_FILEPATH, c);
    }
}
