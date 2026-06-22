package com.meishu.sdkdemo;

import android.content.Context;
import android.location.Location;

import com.bytedance.sdk.openadsdk.LocationProvider;
import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.MSAdConfig;
import com.tencent.bugly.crashreport.CrashReport;

public final class AdMateInitializer {
    private static boolean initialized;
    private static Context appContext;

    private AdMateInitializer() {
    }

    public static synchronized void init(Context context) {
        if (context == null || initialized) {
            return;
        }
        appContext = context.getApplicationContext();
        CrashReport.initCrashReport(appContext, "9d18334ffa", true);
        MSAdConfig sdkConfig = new MSAdConfig.Builder()
                .appId("106083")
                .enableDebug(true)
                .downloadConfirm(MSAdConfig.DOWNLOAD_CONFIRM_ALWAYS)
                .customController(new MSAdConfig.CustomController() {
                    @Override
                    public String getOaid() {
                        return super.getOaid();
                    }

                    @Override
                    public boolean isCanUseLocation() {
                        return super.isCanUseLocation();
                    }

                    @Override
                    public LocationProvider getTTLocation() {
                        return super.getTTLocation();
                    }

                    @Override
                    public Location getLocation() {
                        return super.getLocation();
                    }

                    @Override
                    public boolean isCanUsePhoneState() {
                        return super.isCanUsePhoneState();
                    }

                    @Override
                    public boolean isCsjUsePhoneState() {
                        return super.isCsjUsePhoneState();
                    }

                    @Override
                    public String getDevImei() {
                        return super.getDevImei();
                    }

                    @Override
                    public boolean isCanUseWifiState() {
                        return super.isCanUseWifiState();
                    }

                    @Override
                    public String getMacAddress() {
                        return super.getMacAddress();
                    }

                    @Override
                    public boolean isCanUseAndroidId() {
                        return super.isCanUseAndroidId();
                    }

                    @Override
                    public String getAndroidId() {
                        return super.getAndroidId();
                    }

                    @Override
                    public boolean canUseMacAddress() {
                        return super.canUseMacAddress();
                    }

                    @Override
                    public boolean canUseNetworkState() {
                        return super.canUseNetworkState();
                    }

                    @Override
                    public boolean canUseStoragePermission() {
                        return super.canUseStoragePermission();
                    }

                    @Override
                    public boolean canReadInstalledPackages() {
                        return super.canReadInstalledPackages();
                    }

                    @Override
                    public boolean isCanUseImsi() {
                        return super.isCanUseImsi();
                    }

                    @Override
                    public boolean isCanUsePermissionRecordAudio() {
                        return super.isCanUsePermissionRecordAudio();
                    }

                    @Override
                    public boolean isStorageCollectEnable() {
                        return super.isStorageCollectEnable();
                    }
                })
                .build();

        AdSdk.init(appContext, sdkConfig);
        initialized = true;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
