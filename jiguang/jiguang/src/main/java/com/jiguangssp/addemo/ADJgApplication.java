package com.jiguangssp.addemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;

import com.jiguangssp.addemo.activity.SettingActivity;
import com.jiguangssp.addemo.util.SPUtil;

import com.jiguangssp.addemo.constant.ADJgDemoConstant;

/**
 * @author ciba
 * @description 描述
 * @date 2020/3/25
 */
public class ADJgApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setOnlySupportPlatform();

        interstitialAddClose();
    }

    private void interstitialAddClose() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
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
        });
    }

    /**
     * 设置仅仅支持平台
     */
    private void setOnlySupportPlatform() {
        String onlySupportPlatform = SPUtil.getString(this, SettingActivity.KEY_ONLY_SUPPORT_PLATFORM, null);
        ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADJgDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADJgDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADJgDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
    }

}
