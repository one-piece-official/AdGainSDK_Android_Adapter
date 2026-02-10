package com.adgain.mediatom.adapter;

import android.content.Context;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.InitCallback;

import java.util.Map;


public class AdGainInitAdapter {

    private static AdGainInitAdapter instance;
    public static final String codeId = "codeId";
    public static final String appId = "appId";
    public static boolean isCanShake = true;

    public synchronized static AdGainInitAdapter getInstance() {
        if (null == instance) {
            synchronized (AdGainInitAdapter.class) {
                instance = new AdGainInitAdapter();
            }
        }
        return instance;
    }

    public void initSDK(final Context context, Map<String, Object> map, InitCallback initCallback) {
        String appID = (String) map.get(appId);
        AdGainSdk.getInstance().setPersonalizedAdvertisingOn(true);
        AdGainSdk.getInstance().init(context.getApplicationContext(), new AdGainSdkConfig.Builder()
                .appId(appID)
                .customController(null)
                .setInitCallback(initCallback).build());
    }



/*
    private CustomController getController() {
        CustomController controller = null;
        try {
            if (config != null) {
                controller = new CustomController() {
                    @Override
                    public boolean canReadLocation() {
                        return config.isCanUseLocation();
                    }

                    @Override
                    public boolean canUseAndroidId() {
                        return config.isCanUseAndroidId();
                    }

                    @Override
                    public boolean canUseWifiState() {
                        return config.isCanUseWifiState();
                    }

                    @Override
                    public boolean canUsePhoneState() {
                        return config.isCanUsePhoneState();
                    }

                    @Override
                    public Location getLocation() {
                        if (config.getLocation() != null) {
                            Location location = new Location("");
                            location.setLatitude(config.getLocation().getLatitude());
                            location.setLongitude(config.getLocation().getLongitude());
                            return location;
                        }
                        return super.getLocation();
                    }

                    @Override
                    public String getImei() {
                        return config.getDevImei();
                    }

                    @Override
                    public String getAndroidId() {
                        return config.getAndroidId();
                    }

                    @Override
                    public String getMacAddress() {
                        return config.getMacAddress();
                    }

                    @Override
                    public String getOaid() {
                        return config.getDevOaid();
                    }

                };
            } else {
                controller = new CustomController() {
                    @Override
                    public boolean canReadLocation() {
                        return super.canReadLocation();
                    }
                };
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return controller;
    }
*/
}
