package com.tobid.adapter.adgain;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.BuildConfig;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.IBidding;
import com.adgain.sdk.api.InitCallback;
import com.windmill.sdk.WMAdConfig;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WMCustomController;
import com.windmill.sdk.WindMillAd;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.custom.WMCustomAdapterProxy;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdGainCustomerProxy extends WMCustomAdapterProxy {
    private static final String TAG = "AdGainCustomerProxy";
    private static final String SERVER_EXTRA_CUSTOM_APP_ID = "appId";
    private static final String WXAPPID = "wxAppId"; // 微信开放平台的appID

    private WMCustomController controller = null;
    public static String appId;

    private String buildTs = com.tobid.adapter.adgain.BuildConfig.buildAdapterTs;

    @Override
    public void initializeADN(Context context, Map<String, Object> serverExtra) {
        Log.d(TAG, "initializeADN buildTs: " + buildTs + " s:" + serverExtra);
        try {
            String customInfo = (String) serverExtra.get(WMConstants.CUSTOM_INFO);
            JSONObject joCustom = new JSONObject(customInfo);
            String gtAdAppId = joCustom.getString(SERVER_EXTRA_CUSTOM_APP_ID);
            HashMap<String, Object> customData = new HashMap<>();
            customData.put(IBidding.THIRD_MEDIATION, "tobid");
            customData.put("thirdSdkVer", WindMillAd.getVersion());
            WMAdConfig adConfig = WindMillAd.sharedAds().getAdConfig();
            if (adConfig != null && adConfig.getCustomController() != null) {
                controller = adConfig.getCustomController();
            }
            try {
                if (!TextUtils.isEmpty(joCustom.optString(WXAPPID))) {
                    AdGainSdk.getInstance().setWXAppId(joCustom.optString(WXAPPID));
                }
            } catch (Exception e) {
            }
            appId = gtAdAppId;
            AdGainSdkConfig config = new AdGainSdkConfig.Builder()
                    .appId(gtAdAppId)
                    .showLog(BuildConfig.DEBUG)
                    .addCustomData(customData)
                    .setInitCallback(new InitCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Adgain init onSuccess");
                            callInitSuccess();
                        }

                        @Override
                        public void onFail(int code, String message) {
                            Log.d(TAG, "Adgain init onFail " + code + " msg: " + message);
                            callInitFail(code, message);
                        }
                    })
                    .customController(new CustomController() {
                        @Override
                        public boolean canReadLocation() {
                            if (controller != null) {
                                return controller.isCanUseLocation();
                            }
                            return super.canReadLocation();
                        }

                        @Override
                        public boolean canUsePhoneState() {
                            if (controller != null) {
                                try {
                                    return (boolean) controller.getClass()
                                            .getMethod("isCanUsePhoneState")
                                            .invoke(controller);
                                } catch (Throwable ignored) {
                                }
                            }
                            return super.canUsePhoneState();
                        }

                        @Override
                        public String getMacAddress() {
                            if (controller != null) {
                                try {
                                    return (String) controller.getClass()
                                            .getMethod("getMacAddress")
                                            .invoke(controller);
                                } catch (Throwable ignored) {
                                }
                            }
                            return super.getMacAddress();
                        }

                        @Override
                        public String getImei() {
                            if (controller != null) {
                                try {
                                    return (String) controller.getClass()
                                            .getMethod("getDevImei")
                                            .invoke(controller);
                                } catch (Throwable ignored) {
                                }
                            }
                            return super.getImei();
                        }

                        @Override
                        public boolean canUseWifiState() {
                            if (controller != null) {
                                try {
                                    return (boolean) controller.getClass()
                                            .getMethod("isCanUseWifiState")
                                            .invoke(controller);
                                } catch (Throwable ignored) {
                                }
                            }
                            return super.canUseWifiState();
                        }

                        @Override
                        public boolean canUseAndroidId() {
                            if (controller != null) {
                                try {
                                    return (boolean) controller.getClass()
                                            .getMethod("isCanUseAndroidId")
                                            .invoke(controller);
                                } catch (Throwable ignored) {
                                }
                            }
                            return super.canUseAndroidId();
                        }

                        @Override
                        public String getOaid() {
                            if (controller != null) {
                                try {
                                    return (String) controller.getClass()
                                            .getMethod("getDevOaid")
                                            .invoke(controller);
                                } catch (Throwable ignored) {
                                }
                            }
                            return super.getOaid();
                        }

                        @Override
                        public Location getLocation() {
                            if (controller != null) {
                                return controller.getLocation();
                            }
                            return super.getLocation();
                        }

                        @Override
                        public String getAndroidId() {
                            if (controller != null) {
                                try {
                                    return (String) controller.getClass()
                                            .getMethod("getAndroidId")
                                            .invoke(controller);
                                } catch (Throwable ignored) {
                                }
                            }
                            return super.getAndroidId();
                        }
                    })
                    .build();
            AdGainSdk.getInstance().init(context, config);
            updatePrivacySetting();
        } catch (Throwable tr) {
            Log.e(TAG, "initializeADN exception: ", tr);
            callInitFail(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "initializeADN exception: " + Log.getStackTraceString(tr));
        }
    }

    @Override
    public String getNetworkSdkVersion() {
        return AdGainSdk.getVersionName();
    }

    @Override
    public int baseOnToBidCustomAdapterVersion() {
        return WMConstants.TO_BID_CUSTOM_ADAPTER_VERSION_2;
    }

    @Override
    public void notifyPrivacyStatusChange() {
        Log.d(TAG, "notifyPrivacyStatusChange");
        updatePrivacySetting();
    }

    private void updatePrivacySetting() {
        Log.d(TAG, "updatePrivacySetting");
        AdGainSdk.getInstance().setPersonalizedAdvertisingOn(
                WindMillAd.sharedAds().isPersonalizedAdvertisingOn());
    }
}
