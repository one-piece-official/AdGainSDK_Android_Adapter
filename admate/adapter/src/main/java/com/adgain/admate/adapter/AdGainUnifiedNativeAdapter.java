package com.adgain.admate.adapter;

import android.util.Log;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;
import com.meishu.sdk.core.ad.recycler.RecyclerAdData;
import com.meishu.sdk.core.ad.recycler.RecyclerMixAdLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.core.utils.MsAdPatternType;
import com.meishu.sdk.platform.custom.recycler.MsCustomRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 信息流模板、自渲染Adapter
 */
public class AdGainUnifiedNativeAdapter extends MsCustomRecyclerAdapter {

    private NativeAdData nativeAdData;

    public AdGainUnifiedNativeAdapter(RecyclerMixAdLoader recyclerMixAdLoader, SdkAdInfo sdkAdInfo) {
        super(recyclerMixAdLoader, sdkAdInfo);
    }

    public void loadCustomAd(String appId, String s1, String codeId, String s3) {
        Log.d("----AdGain", "loadCustomAd appId: " + appId + " codeId: " + codeId + " s3 " + getSdkAdInfo().getDrawing() + " s1: " + s1 + " " + getAdLoader().getAdPatternType() + " " + MsAdPatternType.MIX_RENDER);
        AdGainInitAdapter.getInstance().initADN(appId, new InitCallback() {
            @Override
            public void onSuccess() {
                loadAd(appId, codeId);
            }

            @Override
            public void onFail(int i, String s) {
                onError(i, s);
            }
        });
    }

    private void loadAd(String appId, String codeId) {
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId)
                .setAppId(appId)
                .build();
        Log.d("----AdGain", "loadAd appId: " + appId + " codeId: " + codeId);
        NativeUnifiedAd nativeUnifiedAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
            @Override
            public void onAdError(AdError adError) {
                if (adError != null)
                    onError(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onAdLoad(List<NativeAdData> list) {
                try {
                    if (list != null && !list.isEmpty()) {
                        nativeAdData = list.get(0);
                        List<RecyclerAdData> adDatas = new ArrayList<>();
                        RecyclerAdData customFeedAd;
                        for (NativeAdData adData : list) {
                            Log.d("---AdGain", " onAdLoad: " + (nativeAdData.getFeedView() != null) + " " + adData.getPrice());
                            if (nativeAdData.getFeedView() != null) { // 信息流模板
                                customFeedAd = new CustomExpressAd(AdGainUnifiedNativeAdapter.this, adData, adData.getPrice());
                            } else {
                                customFeedAd = new CustomFeedAd(AdGainUnifiedNativeAdapter.this, adData);
                            }
                            if (getSdkAdInfo() != null)
                                getSdkAdInfo().setEcpm(adData.getPrice() + "");
                            adDatas.add(customFeedAd);
                        }
                        AdGainUnifiedNativeAdapter.this.onFeedAdLoad(adDatas);
                    } else {
                        onError(-1, "没有数据");
                    }
                } catch (Exception e) {
                }
            }
        });
        nativeUnifiedAd.loadAd();
    }


}
