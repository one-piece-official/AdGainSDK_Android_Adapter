package com.adgain.admate.adapter;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.RewardAd;
import com.adgain.sdk.api.RewardAdListener;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.reward.RewardVideoLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.platform.custom.reward.MsCustomRewardAd;
import com.meishu.sdk.platform.custom.reward.MsCustomRewardAdapter;

import java.util.HashMap;

public class AdGainRewardAdapter extends MsCustomRewardAdapter {

    private RewardAd mRewardAd;
    private CustomRewardAd ad;

    public AdGainRewardAdapter(RewardVideoLoader rewardVideoLoader, SdkAdInfo sdkAdInfo) {
        super(rewardVideoLoader, sdkAdInfo);
    }

    public void showAd(Activity activity) {
        if (null == activity || null == mRewardAd) {
            return;
        }
        mRewardAd.showAd(activity);
    }

    public void loadCustomAd(Context context, MsAdSlot msAdSlot) {
    }


    private void loadRewardAd(String appId, String codeId) {
        HashMap<String, Object> extras = new HashMap<>();
//        extras.put("disableShake", !AdGainInitAdapter.isCanShake);
        AdRequest adRequest = new AdRequest.Builder().setExtOption(extras).setAppId(appId)
                .setCodeId(codeId).build();
        mRewardAd = new RewardAd(adRequest, new RewardAdListener() {
            @Override
            public void onRewardAdLoadSuccess() {
                if (mRewardAd != null) {
                    Log.d("----AdGain", "onRewardAdLoadSuccess " + mRewardAd.getBidPrice());
                    setEcpm(mRewardAd.getBidPrice());
                }
//                callLoadedSuccess();

            }

            @Override
            public void onRewardAdLoadCached() {
                if (mRewardAd != null) {
                    Log.d("----AdGain", "onRewardAdLoadCached " + mRewardAd.getBidPrice());
                    ad = new CustomRewardAd(AdGainRewardAdapter.this, mRewardAd);
                    onRenderSuccess(ad);
                }
            }

            @Override
            public void onRewardAdShow() {
//                callAdExposure();
                onAdExposure(ad);
            }

            @Override
            public void onRewardAdPlayStart() {
//                callVideoStart();
            }

            @Override
            public void onRewardAdPlayEnd() {
//                callVideoCompleted();
                onVideoCompleted(ad);
            }

            @Override
            public void onRewardAdClick() {
//                callAdClicked();
                onAdClick(ad);
            }

            @Override
            public void onRewardAdClosed() {
//                callAdClosed();
                onAdClosed(ad);
            }

            @Override
            public void onRewardAdLoadError(AdError adError) {
                Log.d("----AdGain", "onRewardAdLoadError " + adError);
                if (adError != null)
                    onError(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onRewardAdShowError(AdError adError) {
            }

            @Override
            public void onRewardVerify() {
//                callReward(new HashMap<>());
                onReward(ad, new HashMap<>());
            }

            @Override
            public void onAdSkip() {
//                callSkippedVideo();
                onSkippedVideo(ad);
            }
        });
        mRewardAd.loadAd();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != mRewardAd) mRewardAd.destroyAd();
    }

    @Override
    public void loadCustomAd(String appId, String s1, String codeId, String s3) {
        Log.d("----AdGain", "loadCustomAd " + codeId);
        AdGainInitAdapter.getInstance().initADN(appId, new InitCallback() {
            @Override
            public void onSuccess() {
                loadRewardAd(appId, codeId);
            }

            @Override
            public void onFail(int i, String s) {
                onError(i, s);
            }
        });
    }

    static class CustomRewardAd extends MsCustomRewardAd {
        private final RewardAd mRewardAd;

        public CustomRewardAd(MsCustomRewardAdapter adWrapper, RewardAd rewardAd) {
            super(adWrapper);
            this.mRewardAd = rewardAd;
        }

        @Override
        public void showAd(Activity activity) {
            Log.d("----AdGain", "showAd ");
            if (mRewardAd != null) {
                mRewardAd.showAd(activity);
            }
        }

        @Override
        public void destroy() {
            if (mRewardAd != null) {
                mRewardAd.destroyAd();
            }
        }
    }


}
