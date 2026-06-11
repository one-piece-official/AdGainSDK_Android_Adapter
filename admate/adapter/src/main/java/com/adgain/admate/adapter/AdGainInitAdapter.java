package com.adgain.admate.adapter;

import android.location.Location;
import android.util.Log;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;
import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.MSAdConfig;

public class AdGainInitAdapter extends com.meishu.sdk.core.b {
    private MSAdConfig.CustomController msController = null;
    private static AdGainInitAdapter instance;
    private volatile boolean isInit = false;

    public synchronized static AdGainInitAdapter getInstance() {
        if (null == instance) {
            synchronized (AdGainInitAdapter.class) {
                instance = new AdGainInitAdapter();
            }
        }
        return instance;
    }


    private CustomController getController() {
        CustomController controller = null;
        try {
            MSAdConfig config = AdSdk.adConfig();
            if (config != null && (msController = config.customController()) != null) {
                controller = new CustomController() {
                    @Override
                    public boolean canReadLocation() {
                        return msController.isCanUseLocation();
                    }

                    @Override
                    public Location getLocation() {
                        if (msController.getLocation() != null) {
                            Location location = new Location("");
                            location.setLatitude(msController.getLocation().getLatitude());
                            location.setLongitude(msController.getLocation().getLongitude());
                            return location;
                        }
                        return super.getLocation();
                    }

                    @Override
                    public boolean canUseAndroidId() {
                        return msController.isCanUseAndroidId();
                    }

                    @Override
                    public boolean canUseWifiState() {
                        return msController.isCanUseWifiState();
                    }

                    @Override
                    public boolean canUsePhoneState() {
                        return msController.isCanUsePhoneState();
                    }


                    @Override
                    public String getImei() {
                        return msController.getDevImei();
                    }

                    @Override
                    public String getAndroidId() {
                        return msController.getAndroidId();
                    }

                    @Override
                    public String getMacAddress() {
                        return msController.getMacAddress();
                    }

                    @Override
                    public String getOaid() {
                        return msController.getOaid();
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

    public void initADN(String appId, InitCallback initListener) {
        MSAdConfig config = AdSdk.adConfig();
        Log.d("----Adgain", "---initADN appid: " + appId);
        if (isInit) {
            initListener.onSuccess();
        }
        AdGainSdk.getInstance().init(AdSdk.getContext(), new AdGainSdkConfig.Builder()
                .appId(appId)
                .userId(config.userId())
                .showLog(config.enableDebug())
                .customController(getController())
                .setInitCallback(new com.adgain.sdk.api.InitCallback() {
                    // 初始化成功回调，初始化成功后才可以加载广告
                    @Override
                    public void onSuccess() {
                        isInit = true;
                        initListener.onSuccess();
                    }

                    // 初始化失败回调
                    @Override
                    public void onFail(int code, String msg) {
                        initListener.onFail(code, msg);
                    }
                }).build());
    }

    public String getPlatform() {
        return "AdGain";
    }

    public String getVersionName() {
        return AdGainSdk.getVersionName();
    }
}
