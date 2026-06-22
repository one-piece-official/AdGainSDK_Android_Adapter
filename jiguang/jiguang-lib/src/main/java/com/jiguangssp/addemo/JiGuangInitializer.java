package com.jiguangssp.addemo;

import android.content.Context;
import android.util.Log;

import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ADJgSdk;
import cn.jiguang.jgssp.config.ADJgInitConfig;
import cn.jiguang.jgssp.listener.ADJgInitListener;

public final class JiGuangInitializer {
    private static boolean initialized;
    private static boolean initializing;

    private JiGuangInitializer() {
    }

    public static synchronized void init(Context context) {
        if (initialized || initializing) {
            return;
        }
        initializing = true;
        ADJgSdk.getInstance().init(
                context,
                new ADJgInitConfig.Builder()
                        .appId(ADJgDemoConstant.APP_ID)
                        .debug(true)
                        .agreePrivacyStrategy(true)
                        .isCanUseLocation(true)
                        .isCanUsePhoneState(true)
                        .isCanReadInstallList(true)
                        .isCanUseReadWriteExternal(true)
                        .filterThirdQuestion(false)
                        .build(),
                new ADJgInitListener() {
                    @Override
                    public void onSuccess() {
                        synchronized (JiGuangInitializer.class) {
                            initialized = true;
                            initializing = false;
                        }
                        Log.d(ADJgDemoConstant.TAG, "ADJg init onSuccess");
                    }

                    @Override
                    public void onFailed(String error) {
                        synchronized (JiGuangInitializer.class) {
                            initializing = false;
                        }
                        Log.d(ADJgDemoConstant.TAG, "ADJg init onFailed error : " + error);
                    }
                }
        );
    }
}
