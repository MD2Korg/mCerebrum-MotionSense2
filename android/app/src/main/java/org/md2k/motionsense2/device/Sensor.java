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
public enum  Sensor {
    ACCELEROMETER(0, "ACCELEROMETER", "Accelerometer", new String[]{"X","Y", "Z"}),
    GYROSCOPE(1, "GYROSCOPE", "Gyroscope", new String[]{"X","Y", "Z"}),
    SEQUENCE_NUMBER_MOTION_SENSOR(2, "SEQUENCE_NUMBER_MOTION_SENSOR", "Seq Number(Motion)", new String[]{"SEQ"}),
    RAW_MOTION_SENSOR(3, "RAW_MOTION_SENSOR", "Raw (Motion)",fill(14)),

    MAGNETOMETER(4, "MAGNETOMETER", "Magnetometer", new String[]{"X","Y", "Z"}),
    SEQUENCE_NUMBER_MAGNETOMETER(5, "SEQUENCE_NUMBER_MAGNETOMETER","Seq Number(Mag)", new String[]{"SEQ"}),
    RAW_MAGNETOMETER(6, "RAW_MAGNETOMETER", "Raw(Mag)", fill(14)),

    PPG(7, "PPG", "PPG", new String[]{"Infra-red1","Infra-red2", "red1", "red2"}),
    SEQUENCE_NUMBER_PPG(8, "SEQUENCE_NUMBER_PPG", "Seq Number(PPG)", new String[]{"SEQ"}),
    RAW_PPG(9, "RAW_PPG", "Raw (PPG)",fill(14)),

    PPG_FILTER(10, "PPG_FILTER", "PPG (Filter)", new String[]{"Infra-red1","Infra-red2", "red1", "red2"}),
    SEQUENCE_NUMBER_PPG_FILTER(11, "SEQUENCE_NUMBER_PPG_FILTER", "Seq Number(PPG Filter)", new String[]{"SEQ"}),
    RAW_PPG_FILTER(12, "RAW_PPG_FILTER", "Raw (PPG Filter)",fill(18)),

    PPG_FILTER_DC(13, "PPG_FILTER_DC", "PPG (Filter DC)", new String[]{"Infra-red1","Infra-red2", "red1", "red2"}),
    SEQUENCE_NUMBER_PPG_FILTER_DC(14, "SEQUENCE_NUMBER_PPG_FILTER_DC", "Seq Number(PPG Filter DC)", new String[]{"SEQ"}),
    RAW_PPG_FILTER_DC(15, "RAW_PPG_FILTER_DC", "Raw (PPG Filter DC)",fill(18)),

    BATTERY(16, "BATTERY", "Battery", new String[]{"Battery"});


    int id;
    String dataSourceType;
    String title;
    String[] elements;
    Sensor(int id, String dataSourceType, String title, String[] elements){
        this.dataSourceType = dataSourceType;
        this.id = id;
        this.title = title;
        this.elements = elements;
    }
    public static Sensor getSensor(int id){
        for(Sensor s:Sensor.values()){
            if(s.getId()==id) return s;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public String getTitle() {
        return title;
    }

    public String[] getElements() {
        return elements;
    }
    private static String[] fill(int num){
        String[] filled = new String[num];
        for(int i=0;i<num;i++){
            filled[i]="C"+Integer.toString(i);
        }
        return filled;
    }
}
