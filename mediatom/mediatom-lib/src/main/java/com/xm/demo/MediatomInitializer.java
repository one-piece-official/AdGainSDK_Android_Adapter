package com.xm.demo;

import android.content.Context;

import com.yd.saas.ydsdk.manager.YdConfig;

public final class MediatomInitializer {
    private static boolean initialized;

    private MediatomInitializer() {
    }

    public static synchronized void init(Context context) {
        if (initialized || context == null) {
            return;
        }
        YdConfig.getInstance().init(context.getApplicationContext(), "a3fdd30b422c028a", "", true);
        initialized = true;
    }
}
