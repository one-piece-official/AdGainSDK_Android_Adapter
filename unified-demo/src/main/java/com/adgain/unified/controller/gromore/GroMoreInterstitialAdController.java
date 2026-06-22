package com.adgain.unified.controller.gromore;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.gromore.adapter.adgain.GMBiddingUtil;
import com.union_test.toutiao.config.TTAdManagerHolder;

public class GroMoreInterstitialAdController implements UnifiedAdController {
    private TTFullScreenVideoAd interstitialAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        GroMoreAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 GroMore 插屏: " + placementId);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(placementId)
                .setMediationAdSlot(new GroMoreMediationAdSlot())
                .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)
                .build();
        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(activity);
        if (adNativeLoader == null) {
            callback.log("TTAdNative 创建失败");
            return;
        }
        adNativeLoader.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                callback.log("onError: " + code + ", " + message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                interstitialAd = ad;
                callback.log("onFullScreenVideoAdLoad");
            }

            @Override
            public void onFullScreenVideoCached() {
                callback.log("onFullScreenVideoCached");
            }

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ad) {
                interstitialAd = ad;
                callback.log("onFullScreenVideoCached");
            }
        });
    }

    @Override
    public boolean isReady() {
        return interstitialAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        interstitialAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
            @Override
            public void onAdShow() {
                callback.log("onAdShow");
                GMBiddingUtil.gmNotifyLoss(interstitialAd);
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
            public void onSkippedVideo() {
                callback.log("onSkippedVideo");
            }
        });
        callback.log("展示 GroMore 插屏");
        interstitialAd.showFullScreenVideoAd(activity);
    }

    @Override
    public void destroy() {
        if (interstitialAd != null && interstitialAd.getMediationManager() != null) {
            interstitialAd.getMediationManager().destroy();
        }
        interstitialAd = null;
    }
}
