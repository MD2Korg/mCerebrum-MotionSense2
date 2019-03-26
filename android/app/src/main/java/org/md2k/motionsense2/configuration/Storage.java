package org.md2k.motionsense2.configuration;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

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
public class Storage {
    public static <T> T readJson(String filePath, Class<T> classType) {
        T data = null;
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            data = gson.fromJson(reader, classType);
        } catch (FileNotFoundException ignored) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return data;
    }
    public static Map<String, String> readJsonAsMap(String filePath){
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> data=new HashMap<>();

        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            data = gson.fromJson(reader, type);
        } catch (FileNotFoundException ignored) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return data;
    }

    public static <T> T readJsonFromAsset(Context context, String assetFilePath, Class<T> classType) {
        T data = null;
        BufferedReader reader = null;
        try {
            InputStream in = context.getAssets().open(assetFilePath);
            reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            data = gson.fromJson(reader, classType);
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return data;
    }

    static <T> void writeJson(String configDirectory, String filePath, T data) {
        File f = new File(configDirectory);
        f.mkdirs();
        Gson gson = new Gson();
        FileWriter writer = null;
        try {
            String json = gson.toJson(data);
            writer = new FileWriter(filePath);
            writer.write(json);
        } catch (IOException e) {

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
