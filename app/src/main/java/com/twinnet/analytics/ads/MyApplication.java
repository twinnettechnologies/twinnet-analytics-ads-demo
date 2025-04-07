package com.twinnet.analytics.ads;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.lib.adssdk.MyApp;

public class MyApplication extends MultiDexApplication {

    public void InitSdk() {
        MyApp.init(getInstance(), "vIBHzZuKkplC4yPAxTLyBDCJHdxTDUU","twinnet_analytics_demo",false);
    }

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MyApp.init(this);
    }

}
