package com.adgain.mediatom.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.SplashAd;
import com.adgain.sdk.api.SplashAdListener;
import com.yd.saas.base.custom.spread.CustomSpreadAdapter;
import com.yd.saas.common.saas.bean.AdSource;
import com.yd.saas.config.utils.LogcatUtil;

import java.util.HashMap;
import java.util.Map;


public class AdGainSplashAdapter extends CustomSpreadAdapter {

    private SplashAd splashAd;
    private AdSource adSource;

    @Override
    public void show(ViewGroup viewGroup) {
        if (splashAd != null)
            splashAd.showAd(viewGroup);
    }

    @Override
    public void loadCustomNetworkAd(Activity activity, Map<String, Object> map, Map<String, Object> map1) {
        try {
            LogcatUtil.d("AdGainSplashAdapter map " + map + " " + map1);
            AdGainInitAdapter.getInstance().initSDK(activity, map, new InitCallback() {
                @Override
                public void onSuccess() {
                    if (map.containsKey(AdGainInitAdapter.codeId))
                        loadSplashAd(String.valueOf(map.get(AdGainInitAdapter.codeId)));
                }

                @Override
                public void onFail(int i, String s) {
                    if (mLoadListener != null)
                        mLoadListener.onAdLoadError(i + "", s);
                }
            });
        } catch (Exception e) {
        }
    }

    private void loadSplashAd(String codeId) {
        HashMap<String, Object> extras = new HashMap<>();
        extras.put("disableShake", !AdGainInitAdapter.isCanShake);// true 就是用户关闭摇一摇
        // 创建ad请求
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId) // 广告位ID
                .setExtOption(extras)
                .build();
        adSource = getAdSource();
        // 创建开屏AD API对象，监听回调在这里设置,5 * 1000为请求广告超时时间
        splashAd = new SplashAd(adRequest, new SplashAdListener() {
            @Override
            public void onAdLoadSuccess() {
            }

            @Override
            public void onAdCacheSuccess() {
                try {
                    LogcatUtil.d("AdGainSplashAdapter onAdCacheSuccess " + adSource.isC2SBidAd);
                    if (adSource != null && adSource.isC2SBidAd && splashAd != null) {
//                        setECPM(10000);
                        setECPM(splashAd.getBidPrice());
                    }
                    mLoadListener.onAdDataLoaded();
                } catch (Exception e) {
                }
            }

            @Override
            public void onSplashAdLoadFail(com.adgain.sdk.api.AdError adError) {
                LogcatUtil.d("AdGainSplashAdapter onSplashAdLoadFail ");
                if (adError != null && mLoadListener != null)
                    mLoadListener.onAdLoadError(adError.getErrorCode() + "", adError.getMessage());
            }

            @Override
            public void onSplashAdShow() {
                if (mImpressionListener != null)
                    mImpressionListener.onSplashAdShow();
            }

            @Override
            public void onSplashAdShowError(com.adgain.sdk.api.AdError adError) {
            }

            @Override
            public void onSplashAdClick() {
                if (mImpressionListener != null)
                    mImpressionListener.onSplashAdClicked();
            }

            @Override
            public void onSplashAdClose(boolean b) {
                if (mImpressionListener != null)
                    mImpressionListener.onSplashAdDismiss();
            }
        }, 5 * 1000);
        // 加载广告
        splashAd.loadAd();
    }


    @Override
    public void biddingResult(boolean b, int i, int i1, int i2) {
        super.biddingResult(b, i, i1, i2);
    }

}
