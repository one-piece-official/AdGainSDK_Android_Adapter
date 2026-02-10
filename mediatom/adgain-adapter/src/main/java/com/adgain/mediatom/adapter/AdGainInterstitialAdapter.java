package com.adgain.mediatom.adapter;

import android.app.Activity;
import android.content.Context;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.InterstitialAd;
import com.adgain.sdk.api.InterstitialAdListener;
import com.yd.saas.base.custom.Interstitial.CustomInterstitialAdapter;
import com.yd.saas.config.utils.LogcatUtil;

import java.util.Map;


public class AdGainInterstitialAdapter extends CustomInterstitialAdapter {

    private InterstitialAd mInterstitialAd;

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> map, Map<String, Object> map1) {
        try {
            LogcatUtil.d("Adgain AdGainSplashAdapter map " + map + " " + map1);
            AdGainInitAdapter.getInstance().initSDK(context, map, new InitCallback() {
                @Override
                public void onSuccess() {
                    if (map.containsKey(AdGainInitAdapter.codeId))
                        loadInterstitialAd(String.valueOf(map.get(AdGainInitAdapter.codeId)));
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

    private void loadInterstitialAd(String codeId) {
        AdRequest adRequest = new AdRequest.Builder().
                setCodeId(codeId)
                .build();
        mInterstitialAd = new InterstitialAd(adRequest, new InterstitialAdListener() {
            @Override
            public void onInterstitialAdLoadError(com.adgain.sdk.api.AdError adError) {
                LogcatUtil.d("AdGainRewardAdapter onInterstitialAdLoadError ");
                if (adError != null && mLoadListener != null)
                    mLoadListener.onAdLoadError(adError.getErrorCode() + "", adError.getMessage());
            }

            @Override
            public void onInterstitialAdLoadSuccess() {
            }

            @Override
            public void onInterstitialAdLoadCached() {
                try {
                    LogcatUtil.d("AdGainRewardAdapter onInterstitialAdLoadCached ");
                    if (mAdSource != null && mAdSource.isC2SBidAd && mLoadListener != null && mInterstitialAd != null) {
                        setECPM(mInterstitialAd.getBidPrice());
//                    setECPM(10000);
                    }
                    if (mLoadListener != null)
                        mLoadListener.onAdDataLoaded();
                } catch (Exception e) {
                }
            }

            @Override
            public void onInterstitialAdShow() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onInterstitialAdShow();
            }

            @Override
            public void onInterstitialAdPlayEnd() {

            }

            @Override
            public void onInterstitialAdClick() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onInterstitialAdClicked();
            }

            @Override
            public void onInterstitialAdClosed() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onInterstitialAdClose();
            }

            @Override
            public void onInterstitialAdShowError(com.adgain.sdk.api.AdError adError) {
            }
        });
        mInterstitialAd.loadAd();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != mInterstitialAd) mInterstitialAd.destroyAd();
    }

    @Override
    public void show(Activity activity) {
        if (mInterstitialAd != null)
            mInterstitialAd.showAd(activity);
    }


}
