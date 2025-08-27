package cn.jiguang.jgssp.adapter.adgain;

import android.content.Context;
import android.text.TextUtils;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;

import cn.jiguang.jgssp.ADJgSdk;
import cn.jiguang.jgssp.ad.adapter.ADBaseIniter;
import cn.jiguang.jgssp.config.ADJgInitConfig;
import cn.jiguang.jgssp.util.ADJgLogUtil;

public class ADSuyiIniter extends ADBaseIniter {
    @Override
    public void init(Context context, String appId, String appKey, ADJgInitConfig config) {
        if (AdGainSdk.getInstance().isInit()) {
            callInitSuccess();
            return;
        }
        AdGainSdk.getInstance().init(context, new AdGainSdkConfig.Builder()
                .appId(appId)       //必填，向广推商务获取
                .showLog(config != null && config.isDebug())   // 是否展示adsdk内部日志，正式环境务必为false
                .customController(getController(config))
                .setInitCallback(new InitCallback() {
                    // 初始化成功回调，初始化成功后才可以加载广告
                    @Override
                    public void onSuccess() {
                        ADJgLogUtil.d("Adgain init success");
                        callInitSuccess();
                    }

                    // 初始化失败回调
                    @Override
                    public void onFail(int code, String msg) {
                        ADJgLogUtil.d("Adgain init onFail " + msg);
                        callInitFailed();
                    }
                }).build());
    }

    private CustomController getController(ADJgInitConfig config) {
        CustomController controller = new CustomController() {
            // 是否允许SDK获取位置信息
            @Override
            public boolean canReadLocation() {
                if (config != null) return config.isCanUseLocation();
                return true;
            }

            // 是否允许SDK获取手机信息
            @Override
            public boolean canUsePhoneState() {
                if (config != null) return config.isCanUsePhoneState();
                return true;
            }

            // 是否允许SDK使用AndoridId
            @Override
            public boolean canUseAndroidId() {
                if (config != null) return config.isCanUsePhoneState();
                return true;
            }

            // 是否允许SDK获取Wifi状态
            @Override
            public boolean canUseWifiState() {
                if (config != null) return config.isCanUseWifiState();
                return true;
            }

            // 为SDK提供oaid
            @Override
            public String getOaid() {
                if (!TextUtils.isEmpty(ADJgSdk.getInstance().getOAID()))
                    if (!ADJgSdk.getInstance().getOAID().startsWith("000"))
                        ADJgSdk.getInstance().getOAID();
                return "";
            }
        };
        return controller;
    }

    @Override
    public String getAdapterVersion() {
        return AdGainSdk.getVersionName();
    }

    @Override
    public void setPersonalizedAdEnabled(boolean personalizedAdEnabled) {
        AdGainSdk.getInstance().setPersonalizedAdvertisingOn(!personalizedAdEnabled);
    }

    @Override
    public boolean isClientBid() {
        return true;
    }

    @Override
    public boolean isParallelLoad() {
        return true;
    }
}
