package com.adgain.unified.controller.gromore;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.gromore.adapter.adgain.GMBiddingUtil;
import com.union_test.toutiao.config.TTAdManagerHolder;

public class GroMoreRewardAdController implements UnifiedAdController {
    private TTRewardVideoAd rewardVideoAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        GroMoreAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 GroMore 激励视频: " + placementId);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(placementId)
                .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)
                .setMediationAdSlot(new GroMoreMediationAdSlot())
                .build();
        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(activity);
        if (adNativeLoader == null) {
            callback.log("TTAdNative 创建失败");
            return;
        }
        adNativeLoader.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                callback.log("onError: " + code + ", " + message);
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                rewardVideoAd = ad;
                callback.log("onRewardVideoAdLoad");
            }

            @Override
            public void onRewardVideoCached() {
                callback.log("onRewardVideoCached");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ad) {
                rewardVideoAd = ad;
                callback.log("onRewardVideoCached");
            }
        });
    }

    @Override
    public boolean isReady() {
        return rewardVideoAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        rewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                callback.log("onAdShow");
                GMBiddingUtil.gmNotifyLoss(rewardVideoAd);
            }

            @Override
            public void onAdVideoBarClick() {
                callback.log("onAdVideoBarClick");
            }

            @Override
            public void onAdClose() {
                callback.log("onAdClose");
            }

            @Override
            public void onVideoComplete() {
                callback.log("onVideoComplete");
            }

            @Override
            public void onVideoError() {
                callback.log("onVideoError");
            }

            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                callback.log("onRewardVerify: " + rewardVerify);
            }

            @Override
            public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                callback.log("onRewardArrived: " + isRewardValid);
            }

            @Override
            public void onSkippedVideo() {
                callback.log("onSkippedVideo");
            }
        });
        callback.log("展示 GroMore 激励视频");
        rewardVideoAd.showRewardVideoAd(activity);
    }

    @Override
    public void destroy() {
        if (rewardVideoAd != null && rewardVideoAd.getMediationManager() != null) {
            rewardVideoAd.getMediationManager().destroy();
        }
        rewardVideoAd = null;
    }
}
