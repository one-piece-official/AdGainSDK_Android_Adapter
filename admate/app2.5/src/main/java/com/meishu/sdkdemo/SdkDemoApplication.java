package com.meishu.sdkdemo;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;


public class SdkDemoApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        SdkDemoApplication.context=this;

    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Context getAppContext() {
        return SdkDemoApplication.context;
    }


}
