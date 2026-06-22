package com.adgain.unified.controller.jiguang;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgBannerAd;
import cn.jiguang.jgssp.ad.data.ADJgAdInfo;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgBannerAdListener;

public class JiGuangBannerAdController implements UnifiedAdController {
    private ADJgBannerAd bannerAd;
    private FrameLayout bannerContainer;
    private boolean ready;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        JiGuangAdControllerUtils.resetContainer(adContainer);
        bannerContainer = new FrameLayout(activity);
        ready = false;
        callback.log("开始加载 JiGuang Banner: " + placementId);

        bannerAd = new ADJgBannerAd(activity, bannerContainer);
        bannerAd.setAutoRefreshInterval(ADJgDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL);
        bannerAd.setOnlySupportPlatform(ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
        bannerAd.setSceneId(ADJgDemoConstant.BANNER_AD_SCENE_ID);
        bannerAd.setListener(new ADJgBannerAdListener() {
            @Override
            public void onAdReceive(ADJgAdInfo adJgAdInfo) {
                ready = true;
                callback.log("onAdReceive");
            }

            @Override
            public void onAdExpose(ADJgAdInfo adJgAdInfo) {
                callback.log("onAdExpose");
            }

            @Override
            public void onAdClick(ADJgAdInfo adJgAdInfo) {
                callback.log("onAdClick");
            }

            @Override
            public void onAdClose(ADJgAdInfo adJgAdInfo) {
                callback.log("onAdClose");
            }

            @Override
            public void onAdFailed(ADJgError error) {
                ready = false;
                callback.log("onAdFailed: " + JiGuangAdControllerUtils.errorInfo(error));
            }
        });
        bannerAd.loadAd(placementId);
    }

    @Override
    public boolean isReady() {
        return ready && bannerAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (bannerContainer == null) {
            callback.log("请先加载广告");
            return;
        }
        JiGuangAdControllerUtils.attachView(adContainer, bannerContainer, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 JiGuang Banner");
    }

    @Override
    public void destroy() {
        if (bannerAd != null) {
            bannerAd.release();
            bannerAd = null;
        }
        if (bannerContainer != null) {
            bannerContainer.removeAllViews();
            bannerContainer = null;
        }
        ready = false;
    }
}
