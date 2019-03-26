package org.md2k.motionsense2;

import android.content.Context;
import android.util.Log;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleException;

import org.md2k.mcerebrum.core.access.MCerebrum;

import io.flutter.app.FlutterApplication;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

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
public class MyApplication extends FlutterApplication {
    private RxBleClient rxBleClient;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        rxBleClient = RxBleClient.create(this);
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof BleException) {
                Log.v("SampleApplication", "Suppressed UndeliverableException: " + throwable.toString());
                return; // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw new RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable);
        });
//        RxBleClient.setLogLevel(RxBleLog.);
//        MCerebrum.init(getApplicationContext(), MyMCerebrumInit.class);
    }
    public static Context getContext(){
        return context;
    }

    public static RxBleClient getRxBleClient(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.rxBleClient;
    }

}
