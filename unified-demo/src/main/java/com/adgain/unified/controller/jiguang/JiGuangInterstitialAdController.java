package com.adgain.unified.controller.jiguang;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgInterstitialAd;
import cn.jiguang.jgssp.ad.data.ADJgInterstitialAdInfo;
import cn.jiguang.jgssp.ad.entity.ADJgExtraParams;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgInterstitialAdListener;
import cn.jiguang.jgssp.util.ADJgAdUtil;

public class JiGuangInterstitialAdController implements UnifiedAdController {
    private ADJgInterstitialAd interstitialAd;
    private ADJgInterstitialAdInfo interstitialAdInfo;
    private boolean loading;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        JiGuangAdControllerUtils.resetContainer(adContainer);
        loading = true;
        callback.log("开始加载 JiGuang 插屏: " + placementId);

        interstitialAd = new ADJgInterstitialAd(activity);
        interstitialAd.setOnlySupportPlatform(ADJgDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM);
        interstitialAd.setSceneId(ADJgDemoConstant.INTERSTITIAL_AD_SCENE_ID);
        interstitialAd.setLocalExtraParams(new ADJgExtraParams.Builder()
                .setVideoWithMute(ADJgDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE)
                .build());
        interstitialAd.setListener(new ADJgInterstitialAdListener() {
            @Override
            public void onAdReady(ADJgInterstitialAdInfo adInfo) {
                callback.log("onAdReady");
            }

            @Override
            public void onAdReceive(ADJgInterstitialAdInfo adInfo) {
                interstitialAdInfo = adInfo;
                loading = false;
                callback.log("onAdReceive");
            }

            @Override
            public void onAdExpose(ADJgInterstitialAdInfo adInfo) {
                callback.log("onAdExpose");
            }

            @Override
            public void onAdClick(ADJgInterstitialAdInfo adInfo) {
                callback.log("onAdClick");
            }

            @Override
            public void onAdClose(ADJgInterstitialAdInfo adInfo) {
                callback.log("onAdClose");
            }

            @Override
            public void onAdFailed(ADJgError error) {
                loading = false;
                callback.log("onAdFailed: " + JiGuangAdControllerUtils.errorInfo(error));
            }
        });
        interstitialAd.loadAd(placementId);
    }

    @Override
    public boolean isReady() {
        return interstitialAdInfo != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (loading) {
            callback.log("广告加载中");
            return;
        }
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 JiGuang 插屏");
        ADJgAdUtil.showInterstitialAdConvenient(activity, interstitialAdInfo);
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.release();
            interstitialAd = null;
        }
        interstitialAdInfo = null;
        loading = false;
    }
}
