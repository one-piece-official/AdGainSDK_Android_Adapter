package com.adgain.unified.controller.admate;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.reward.RewardAdEventListener;
import com.meishu.sdk.core.ad.reward.RewardAdMediaListener;
import com.meishu.sdk.core.ad.reward.RewardVideoAd;
import com.meishu.sdk.core.ad.reward.RewardVideoLoader;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;

import java.util.Map;

public class AdMateRewardAdController implements UnifiedAdController {
    private RewardVideoLoader rewardLoader;
    private RewardVideoAd rewardAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        AdMateAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 AdMate 激励视频: " + placementId);

        MsAdSlot adSlot = new MsAdSlot.Builder()
                .setPid(placementId)
                .build();
        rewardLoader = new RewardVideoLoader(activity, adSlot, new RewardAdEventListener() {
            @Override
            public void onVideoCached(RewardVideoAd ad) {
                callback.log("onVideoCached");
            }

            @Override
            public void onReward(Map<String, Object> map) {
                callback.log("onReward: " + map);
            }

            @Override
            public void onAdError(AdErrorInfo errorInfo) {
                callback.log("onAdError: " + errorInfo);
            }

            @Override
            public void onAdReady(RewardVideoAd ad) {
                rewardAd = ad;
                rewardAd.setInteractionListener(new InteractionListener() {
                    @Override
                    public void onAdClicked() {
                        callback.log("onAdClicked");
                    }

                    @Override
                    public void onAdExposure() {
                        callback.log("onAdExposure");
                    }

                    @Override
                    public void onAdClosed() {
                        callback.log("onAdClosed");
                    }
                });
                rewardAd.setMediaListener(new RewardAdMediaListener() {
                    @Override
                    public void onVideoStart() {
                        callback.log("onVideoStart");
                    }

                    @Override
                    public void onVideoPause() {
                        callback.log("onVideoPause");
                    }

                    @Override
                    public void onVideoResume() {
                        callback.log("onVideoResume");
                    }

                    @Override
                    public void onVideoCompleted() {
                        callback.log("onVideoCompleted");
                    }

                    @Override
                    public void onVideoError() {
                        callback.log("onVideoError");
                    }

                    @Override
                    public void onSkippedVideo() {
                        callback.log("onSkippedVideo");
                    }
                });
                callback.log("onAdReady");
            }
        });
        rewardLoader.loadAd();
    }

    @Override
    public boolean isReady() {
        return rewardAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 AdMate 激励视频");
        rewardAd.showAd(activity);
    }

    @Override
    public void destroy() {
        if (rewardLoader != null) {
            rewardLoader.destroy();
            rewardLoader = null;
        }
        rewardAd = null;
    }
}
