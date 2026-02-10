package com.adgain.mediatom.adapter;

import android.app.Activity;
import android.content.Context;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.RewardAd;
import com.adgain.sdk.api.RewardAdListener;
import com.yd.saas.base.custom.rewardvideo.CustomRewardVideoAdapter;
import com.yd.saas.config.utils.LogcatUtil;

import java.util.Map;

public class AdGainRewardAdapter extends CustomRewardVideoAdapter {

    private RewardAd mRewardAd;

    @Override
    public void show(Activity activity) {
        if (mRewardAd != null)
            mRewardAd.showAd(activity);
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> map, Map<String, Object> map1) {
        try {
            LogcatUtil.d("AdGainRewardAdapter map " + map + " " + map1);
            AdGainInitAdapter.getInstance().initSDK(context, map, new InitCallback() {
                @Override
                public void onSuccess() {
                    if (map.containsKey(AdGainInitAdapter.codeId))
                        loadAd(String.valueOf(map.get(AdGainInitAdapter.codeId)));
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

    private void loadAd(String codeId) {
        AdRequest adRequest = new AdRequest.Builder().setCodeId(codeId).build();
        mRewardAd = new RewardAd(adRequest, new RewardAdListener() {
            @Override
            public void onRewardAdLoadSuccess() {

            }

            @Override
            public void onRewardAdLoadCached() {
                try {
                    LogcatUtil.d("AdGainRewardAdapter onRewardAdLoadCached ");
                    if (mAdSource != null && mAdSource.isC2SBidAd && mLoadListener != null && mRewardAd != null) {
//                    setECPM(10000);
                        setECPM(mRewardAd.getBidPrice());
                    }
                    if (mLoadListener != null)
                        mLoadListener.onAdDataLoaded();
                }catch (Exception e){
                }
            }

            @Override
            public void onRewardAdLoadError(AdError adError) {
                LogcatUtil.d("AdGainRewardAdapter onRewardAdLoadError ");
                if (mLoadListener != null && adError != null)
                    mLoadListener.onAdLoadError(adError.getErrorCode() + "", adError.getMessage());
            }

            @Override
            public void onRewardAdShow() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onRewardedVideoAdPlayStart();
            }

            @Override
            public void onRewardAdPlayStart() {
            }

            @Override
            public void onRewardAdPlayEnd() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onRewardedVideoAdPlayEnd();
            }

            @Override
            public void onRewardAdClick() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onRewardedVideoAdPlayClicked();
            }

            @Override
            public void onRewardAdClosed() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onRewardedVideoAdClosed();
            }


            @Override
            public void onRewardAdShowError(AdError adError) {
            }

            @Override
            public void onRewardVerify() {
                if (mImpressionEventListener != null)
                    mImpressionEventListener.onReward();
            }

            @Override
            public void onAdSkip() {

            }
        });
        mRewardAd.loadAd();
    }


}
