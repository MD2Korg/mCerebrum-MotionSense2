package org.md2k.motionsense2.device.motion_sense_hrv_plus_gen2;

/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center
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
 */public class Enumeration {
    public enum GYRO_SENSITIVITY {
        SEN_250_DPS((byte) 0x00,250),
        SEN_500_DPS((byte) 0x01, 500),
        SEN_1000_DPS((byte) 0x02,1000),
        SEN_2000_DPS((byte) 0x03,2000);

        private byte value;
        int sensitivity;

        GYRO_SENSITIVITY(byte value, int sensitivity) {
            this.value = value;this.sensitivity = sensitivity;
        }

        public static GYRO_SENSITIVITY getEnum(int sensitivity) {
            for(GYRO_SENSITIVITY a: GYRO_SENSITIVITY.values()){
                if(a.sensitivity==sensitivity)
                    return a;
            }
            return SEN_250_DPS;
        }

        public byte getValue() {
            return this.value;
        }

        public double getScalingFactor() {
            if (this.value == SEN_250_DPS.value) {
                return (2 << 16) / 500.0;
            }
            if (this.value == SEN_500_DPS.value) {
                return (2 << 16) / 1000.0;
            }
            if (this.value == SEN_1000_DPS.value) {
                return (2 << 16) / 2000.0;
            }
            if (this.value == SEN_2000_DPS.value) {
                return (2 << 16) / 4000.0;
            }
            return 1;
        }
    }

    public enum PPG_SAMPLING_RATES {
        TWENTYFIVE_HZ((byte) 0x28,25.0),
        FIFTY_HZ((byte) 0x14, 50.0);

        private byte value;
        private double frequency;

        PPG_SAMPLING_RATES(byte value, double frequency) {
            this.value = value;
            this.frequency = frequency;
        }

        public static PPG_SAMPLING_RATES getEnum(double frequency) {
            for(PPG_SAMPLING_RATES a: PPG_SAMPLING_RATES.values()){
                if(a.frequency==frequency)
                    return a;
            }
            return TWENTYFIVE_HZ;
        }

        public byte getValue() {
            return this.value;
        }
    }

    public enum ACCEL_GYRO_SAMPLING_RATES {
        TWENTYFIVE_HZ((byte) 0x05, 25.0),
        FIFTY_HZ((byte) 0x04, 50.0),
        SIXTYTWO_POINT_FIVE_HZ((byte) 0x03, 62.5),
        ONE_HUNDRED_TWENTYFIVE_HZ((byte) 0x02, 125.0),
        TWO_HUNDRED_FIFTY_HZ((byte) 0x01, 250.0);

        private byte value;
        double frequency;

        ACCEL_GYRO_SAMPLING_RATES(byte value, double frequency) {
            this.value = value;
            this.frequency = frequency;
        }

        public byte getValue() {
            return this.value;
        }
        public static ACCEL_GYRO_SAMPLING_RATES getEnum(double frequency){
            for(ACCEL_GYRO_SAMPLING_RATES a: ACCEL_GYRO_SAMPLING_RATES.values()){
                if(a.frequency==frequency)
                    return a;
            }
            return TWENTYFIVE_HZ;
        }
    }

    public enum ACCEL_SENSITIVITY {
        SEN_2_G((byte) 0x00, 2),
        SEN_4_G((byte) 0x01, 4),
        SEN_8_G((byte) 0x02, 8),
        SEN_16_G((byte) 0x03, 16);

        private byte value;
        private int g;

        ACCEL_SENSITIVITY(byte value, int g) {
            this.value = value; this.g = g;
        }

        public byte getValue() {
            return this.value;
        }
        public static ACCEL_SENSITIVITY getEnum(int g) {
            for(ACCEL_SENSITIVITY a: ACCEL_SENSITIVITY.values()){
                if(a.g==g)
                    return a;
            }
            return SEN_2_G;
        }

        public double getScalingFactor() {
            if (this.value == SEN_2_G.value) {
                return (2 << 16) / 4.0;
            }
            if (this.value == SEN_4_G.value) {
                return (2 << 16) / 8.0;
            }
            if (this.value == SEN_8_G.value) {
                return (2 << 16) / 16.0;
            }
            if (this.value == SEN_16_G.value) {
                return (2 << 16) / 32.0;
            }
            return 1;
        }
    }
}
