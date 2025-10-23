package com.adgain.amps.adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;

import xyz.adscope.amps.AMPSSDK;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.common.AMPSErrorCode;
import xyz.adscope.amps.config.AMPSPrivacyConfig;
import xyz.adscope.amps.init.AMPSInitAdapterConfig;
import xyz.adscope.amps.init.inter.AMPSChannelInitMediation;
import xyz.adscope.amps.init.inter.IAMPSChannelInitCallBack;

public class AdGainInitAdapter extends AMPSChannelInitMediation {

    private static AdGainInitAdapter instance;
    private volatile boolean isInit = false;
    public static final String errorCode = "-100";
    public static final String errorMsg = "No Ad And AdGainError 为null ";

    public synchronized static AdGainInitAdapter getInstance() {
        if (null == instance) {
            synchronized (AdGainInitAdapter.class) {
                instance = new AdGainInitAdapter();
            }
        }
        return instance;
    }

    @Override
    public void initSDK(AMPSInitAdapterConfig ampsInitAdapterConfig, IAMPSChannelInitCallBack iampsChannelInitCallBack) {
        try {
            if (isInit) {
                initSDKSuccess(iampsChannelInitCallBack);
            } else {
                Context context = AMPSSDK.getContext();
                initSDK(context, ampsInitAdapterConfig, iampsChannelInitCallBack);
            }
            isInit = true;
        } catch (Throwable e) {
            initSDKFail(iampsChannelInitCallBack, new AMPSError("-100", AMPSErrorCode.ChannelErrorEnum.CHANNEL_ERROR_SDK_NOT_IMPORT.getErrorMsg() + e.getMessage()));
        }
    }

    @Override
    public String getNetworkVersion() {
        return AdGainSdk.getVersionName();
    }

    @Override
    public String getNetworkName() {
        return "AdGain";
    }

    private void initSDK(final Context context, AMPSInitAdapterConfig params, IAMPSChannelInitCallBack iampsChannelInitCallBack) {
        // 个性化广告开关设置
        AdGainSdk.getInstance().setPersonalizedAdvertisingOn(params.getPrivacyConfig().isSupportPersonalized());
        AdGainSdk.getInstance().init(context, new AdGainSdkConfig.Builder()
                .appId(params.getAppId())       //必填，向广推商务获取
                .userId(params.getUserId())      // 用户ID，有就填
                .showLog(params.isDebug())   // 是否展示adsdk内部日志，正式环境务必为false
                .customController(getController(params))
                .setInitCallback(new InitCallback() {
                    // 初始化成功回调，初始化成功后才可以加载广告
                    @Override
                    public void onSuccess() {
                        initSDKSuccess(iampsChannelInitCallBack);
                    }

                    // 初始化失败回调
                    @Override
                    public void onFail(int code, String msg) {
                        Log.d("BeiziAdapter", "init--------------onFail-----------" + code + ":" + msg);
                        initSDKFail(iampsChannelInitCallBack, new AMPSError(code + "", msg));
                    }
                }).build());

    }

    private CustomController getController(AMPSInitAdapterConfig params) {
        CustomController controller = null;
        try {
            if (params.getPrivacyConfig() != null) {
                AMPSPrivacyConfig config = params.getPrivacyConfig();
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
}
