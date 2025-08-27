package com.gromore.adapter.adgain;

import android.content.Context;
import android.util.Log;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.BuildConfig;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;
import com.bytedance.sdk.openadsdk.mediation.bridge.custom.MediationCustomInitLoader;
import com.bytedance.sdk.openadsdk.mediation.custom.MediationCustomInitConfig;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class AdGainCustomerInit extends MediationCustomInitLoader {

    public static final String TAG = "AdGainCustomer";

    @Override
    public String getNetworkSdkVersion() {
        return AdGainSdk.getVersionName();
    }
    @Override
    public void initializeADN(Context context, MediationCustomInitConfig mediationCustomInitConfig, Map<String, Object> map) {
        try {
            Map<String, Object> customData = new HashMap<>();
            customData.put("thirdMediation","gm");
            AdGainSdk.getInstance().init(context, new AdGainSdkConfig.Builder()
                    .appId(mediationCustomInitConfig.getAppId())       //必填，向广推商务获取,配置到 gromore 后台
                    .userId("")  // 用户ID，有就填
                    .showLog(BuildConfig.DEBUG)
                    .addCustomData(customData)  //自定义数据
                    .customController(new CustomController() {
                        // 为SDK提供oaid
                        @Override
                        public String getOaid() {
                            return ""; // 传信通院ID
                        }
                    })
                    .setInitCallback(new InitCallback() {
                        // 初始化成功回调，初始化成功后才可以加载广告
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "init--------------onSuccess-----------");
                            callInitSuccess();
                        }

                        // 初始化失败回调
                        @Override
                        public void onFail(int code, String msg) {
                            Log.d(TAG, "init--------------onFail-----------" + code + ":" + msg);
                        }
                    }).build());

            // 个性化广告开关设置
            AdGainSdk.getInstance().setPersonalizedAdvertisingOn(true);
        } catch (Exception e) {

        }

    }

    @Override
    public String getBiddingToken(Context context, Map<String, Object> extra) {
        return "";
    }

    @Override
    public String getSdkInfo(Context context, Map<String, Object> extra) {
        return "";
    }

    public static int getBidFloor(String json) {
        int bidFloor = 0;
        try {
            JSONTokener token = new JSONTokener(json);
            JSONObject object = new JSONObject(token);
            bidFloor = object.getInt("bid_floor");
        } catch (Exception e) {
        }
        return bidFloor;
    }

}
