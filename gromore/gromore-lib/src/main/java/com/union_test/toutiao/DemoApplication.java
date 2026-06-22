package com.union_test.toutiao;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


public class DemoApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    public static String PROCESS_NAME_XXXX = "process_name_xxxx";
    private static Context context;
    private String tag = "----DemoApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        DemoApplication.context = this;
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getAppContext() {
        return DemoApplication.context;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(tag, "onActivityCreated " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(tag, "onActivityResumed " + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
