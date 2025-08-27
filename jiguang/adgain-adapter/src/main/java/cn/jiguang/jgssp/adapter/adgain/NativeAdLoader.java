package cn.jiguang.jgssp.adapter.adgain;

import android.content.Context;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;

import java.util.ArrayList;
import java.util.List;

import cn.jiguang.jgssp.ad.adapter.bean.ADExtraData;
import cn.jiguang.jgssp.ad.adapter.loader.ADNativeLoader;
import cn.jiguang.jgssp.adapter.adgain.bean.AdGainNativeExpressInfo;
import cn.jiguang.jgssp.adapter.adgain.bean.AdGainNativeInfo;

/**
 * @author maipian
 * @description 描述
 * @date 10/14/24
 */
public class NativeAdLoader extends ADNativeLoader {


    NativeUnifiedAd nativeAd;
    private List<AdGainNativeExpressInfo> mNativeExpressInfos;

    private List<AdGainNativeInfo> mNativeInfos;

    @Override
    public void adapterLoadExpressAd(Context context, String positionId, ADExtraData adExtraData) {
        loadAd(positionId, adExtraData, 0);
    }

    @Override
    public void adapterLoadNativeAd(Context context, String positionId, final ADExtraData adExtraData) {
        loadAd(positionId, adExtraData, 1);
    }


    private void loadAd(String positionId, final ADExtraData adExtraData, int type) {
        AdRequest adRequest = new AdRequest.Builder().setCodeId(positionId) // 设置广告位id
                .build();
        nativeAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
            @Override
            public void onAdError(com.adgain.sdk.api.AdError adError) {
                if (adError != null) callFailed(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onAdLoad(List<NativeAdData> list) {
                mNativeExpressInfos = new ArrayList<>();
                mNativeInfos = new ArrayList<>();
                if (list != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (type == 0) {
                            AdGainNativeExpressInfo nativeExpressInfo = new AdGainNativeExpressInfo(list.get(i));
                            if (isBid()) nativeExpressInfo.setEcpm(list.get(i).getPrice());
                            mNativeExpressInfos.add(nativeExpressInfo);
                        } else {
                            AdGainNativeInfo nativeInfo = new AdGainNativeInfo(list.get(i), true);
                            if (isBid()) nativeInfo.setEcpm(list.get(i).getPrice());
                            mNativeInfos.add(nativeInfo);
                        }
                    }
                }
                if (type == 0) callSuccess(mNativeExpressInfos);
                else callSuccess(mNativeInfos);
            }
        }); // 创建广告对象
        nativeAd.loadAd();// 请求广告
    }

    @Override
    public void adapterRelease() {
    }
}
