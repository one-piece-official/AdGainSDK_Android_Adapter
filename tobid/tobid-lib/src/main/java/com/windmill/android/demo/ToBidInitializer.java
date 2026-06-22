package com.windmill.android.demo;

import android.content.Context;
import android.location.Location;

import com.windmill.sdk.WMAdConfig;
import com.windmill.sdk.WMCustomController;
import com.windmill.sdk.WindMillAd;
import com.windmill.sdk.WindMillConsentStatus;
import com.windmill.sdk.WindMillUserAgeStatus;

public final class ToBidInitializer {
    private static boolean initialized;

    private ToBidInitializer() {
    }

    public static void init(Context context) {
        if (initialized) {
            return;
        }

        WindMillAd ads = WindMillAd.sharedAds();
        ads.setUserAge(18);
        ads.setAdult(true);
        ads.setPersonalizedAdvertisingOn(true);
        ads.setIsAgeRestrictedUser(WindMillUserAgeStatus.WindAgeRestrictedStatusNO);
        ads.setUserGDPRConsentStatus(WindMillConsentStatus.ACCEPT);

        ads.startWithAppId(context.getApplicationContext(), "57381", new WMAdConfig.Builder()
                .customController(new WMCustomController() {
                    @Override
                    public boolean isCanUseLocation() {
                        return super.isCanUseLocation();
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
                    public String getDevImei() {
                        return super.getDevImei();
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
                    public String getDevOaid() {
                        return super.getDevOaid();
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
                    public boolean isCanUseWriteExternal() {
                        return super.isCanUseWriteExternal();
                    }

                    @Override
                    public boolean isCanUseAppList() {
                        return super.isCanUseAppList();
                    }

                    @Override
                    public boolean isCanUsePermissionRecordAudio() {
                        return super.isCanUsePermissionRecordAudio();
                    }
                })
                .build());
        initialized = true;
    }
}
