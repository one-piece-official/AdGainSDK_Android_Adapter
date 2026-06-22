package com.amps.demo;

import android.content.Context;
import android.util.Log;

import xyz.adscope.amps.AMPSSDK;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSPrivacyConfig;
import xyz.adscope.amps.init.AMPSInitConfig;
import xyz.adscope.amps.init.inter.IAMPSInitCallback;

public final class BeiziInitializer {
    private static final String TAG = "BeiziInitializer";
    private static boolean initialized;
    private static boolean initializing;

    private BeiziInitializer() {
    }

    public static synchronized void init(Context context) {
        if (initialized || initializing) {
            return;
        }
        initializing = true;
        AMPSInitConfig config = new AMPSInitConfig.Builder()
                .setAppId(Constants.AMPS_APPID)
                .setAppName("testAppName")
                .openDebugLog(true)
                .setAMPSPrivacyConfig(new AMPSPrivacyConfig() {
                })
                .build();
        AMPSSDK.init(context, config, new IAMPSInitCallback() {
            @Override
            public void successCallback() {
                synchronized (BeiziInitializer.class) {
                    initialized = true;
                    initializing = false;
                }
                Log.i(AMPSConstants.AMPS_LOG_TAG, TAG + " init success");
            }

            @Override
            public void failCallback(AMPSError ampsError) {
                synchronized (BeiziInitializer.class) {
                    initializing = false;
                }
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " init fail " + ampsError);
            }
        });
    }
}
