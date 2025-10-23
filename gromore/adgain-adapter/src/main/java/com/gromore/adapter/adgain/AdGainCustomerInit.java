package com.gromore.adapter.adgain;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.util.SparseArray;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.BuildConfig;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;
import com.bytedance.sdk.openadsdk.mediation.bridge.custom.MediationCustomInitLoader;
import com.bytedance.sdk.openadsdk.mediation.bridge.valueset.MediationInitConfig;
import com.bytedance.sdk.openadsdk.mediation.custom.MediationCustomInitConfig;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AdGainCustomerInit extends MediationCustomInitLoader {

    public static final String TAG = "AdGainCustomer";
    private MediationInitConfig config = null;

    @Override
    public String getNetworkSdkVersion() {
        return AdGainSdk.getVersionName();
    }

    @Override
    public void initializeADN(Context context, MediationCustomInitConfig mediationCustomInitConfig, Map<String, Object> map) {
        try {
            Map<String, Object> customData = new HashMap<>();
            customData.put("thirdMediation", "gm");
            try {
                Object controlleClz = map.get("custom_controller");
                Class clazz = controlleClz.getClass();
                Field fields[] = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Log.d("--------initializeADN ", "Field name: " + fields[i].getName());
                    Field field = clazz.getDeclaredField(fields[i].getName());
                    field.setAccessible(true);
                    if (field.get(controlleClz) instanceof MediationInitConfig)
                        config = (MediationInitConfig) field.get(controlleClz);
                }
            } catch (Exception e) {
            }
            AdGainSdk.getInstance().init(context, new AdGainSdkConfig.Builder()
                    .appId(mediationCustomInitConfig.getAppId())       //必填，向广推商务获取,配置到 gromore 后台
                    .userId("")  // 用户ID，有就填
                    .showLog(BuildConfig.DEBUG)
                    .addCustomData(customData)  //自定义数据
                    .customController(new CustomController() {
                        @Override
                        public boolean canReadLocation() {
                            return config != null && config.isCanUseLocation();
                        }

                        @Override
                        public boolean canUsePhoneState() {
                            return config != null && config.isCanUsePhoneState();
                        }

                        @Override
                        public boolean canUseWifiState() {
                            return config != null && config.isCanUseWifiState();
                        }

                        @Override
                        public boolean canUseAndroidId() {
                            return config != null && config.isCanUseAndroidId();
                        }

                        @Override
                        public Location getLocation() {
                            if (config != null && config.getLocation() != null) {
                                Location location = new Location("");
                                location.setLatitude(config.getLocation().getLatitude());
                                location.setLongitude(config.getLocation().getLongitude());
                                return location;
                            }
                            return super.getLocation();
                        }

                        @Override
                        public String getOaid() {
                            return config != null ? config.getDevOaid() : ""; // 传信通院ID
                        }
                        @Override
                        public String getAndroidId() {
                            return config != null ? config.getAndroidId() : "";
                        }

                        @Override
                        public String getMacAddress() {
                            return config != null ? config.getMacAddress() : "";
                        }

                        @Override
                        public String getImei() {
                            return config != null ? config.getDevImei() : "";
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
