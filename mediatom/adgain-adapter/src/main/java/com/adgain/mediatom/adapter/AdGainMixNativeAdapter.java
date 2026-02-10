package com.adgain.mediatom.adapter;

import android.content.Context;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;
import com.yd.saas.base.custom.mixnative.CustomMixNativeHandler;
import com.yd.saas.config.utils.LogcatUtil;

import java.util.List;
import java.util.Map;
public class AdGainMixNativeAdapter extends CustomMixNativeHandler {

    private NativeUnifiedAd nativeAd;


    private void loadAd(Context context, String codeId) {
        LogcatUtil.d("AdGainMixNativeAdapter codeId " + codeId);
        // 创建ad请求
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId) // 广告位ID
                .build();
        nativeAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
            @Override
            public void onAdError(com.adgain.sdk.api.AdError adError) {
                LogcatUtil.d("AdGainMixNativeAdapter onAdError " + adError);
                if (adError != null)
                    onLoadFailed(adError.getErrorCode() + adError.getMessage());
            }

            @Override
            public void onAdLoad(List<NativeAdData> list) {
                try {
                    LogcatUtil.d("AdGainMixNativeAdapter onAdLoad " + list);
                    NativeAdData adData = list.get(0);
                    if (adData.getFeedView() != null) { //信息流模板
                        onNativeAdLoaded(new CustomExpressAd(context, list.get(0)));
                    } else { // 信息流自渲染
                        onNativeAdLoaded(new CustomNativeAd(context, list.get(0)));
                    }
                } catch (Exception e) {
                }
            }
        }); // 创建广告对象
        nativeAd.loadAd();// 请求广告
    }

    @Override
    public void loadNative(Context context, Map<String, Object> map) {
        initSDK(context, map);
    }

    @Override
    public void loadExpress(Context context, Map<String, Object> map, float v, float v1) {
        initSDK(context, map);
    }

    private void initSDK(Context context, Map<String, Object> map) {
        try {
            LogcatUtil.d("AdGainMixNativeAdapter map " + map);
            AdGainInitAdapter.getInstance().initSDK(context, map, new InitCallback() {
                @Override
                public void onSuccess() {
                    if (map.containsKey(AdGainInitAdapter.codeId))
                        loadAd(context, String.valueOf(map.get(AdGainInitAdapter.codeId)));
                }

                @Override
                public void onFail(int i, String s) {
                }
            });
        } catch (Exception e) {
        }
    }


}
