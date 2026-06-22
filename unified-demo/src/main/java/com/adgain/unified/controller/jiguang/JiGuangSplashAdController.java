package com.adgain.unified.controller.jiguang;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgSplashAd;
import cn.jiguang.jgssp.ad.data.ADJgAdInfo;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgSplashAdListener;

public class JiGuangSplashAdController implements UnifiedAdController {
    private ADJgSplashAd splashAd;
    private RelativeLayout splashLayer;
    private FrameLayout splashContainer;
    private boolean ready;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        JiGuangAdControllerUtils.resetContainer(adContainer);
        initSplashLayer(activity);
        ready = false;
        callback.log("开始加载 JiGuang 开屏: " + placementId);

        splashAd = new ADJgSplashAd(activity, splashContainer);
        splashAd.setImmersive(true);
        splashAd.setOnlySupportPlatform(ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM);
        splashAd.setListener(new ADJgSplashAdListener() {
            @Override
            public void onADTick(long millisUntilFinished) {
                callback.log("onADTick: " + millisUntilFinished);
            }

            @Override
            public void onReward(ADJgAdInfo adJgAdInfo) {
                callback.log("onReward");
            }

            @Override
            public void onAdSkip(ADJgAdInfo adJgAdInfo) {
                callback.log("onAdSkip");
            }

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
                ready = false;
                callback.log("onAdClose");
                removeSplashLayer();
            }

            @Override
            public void onAdFailed(ADJgError error) {
                ready = false;
                callback.log("onAdFailed: " + JiGuangAdControllerUtils.errorInfo(error));
                removeSplashLayer();
            }
        });
        splashAd.loadOnly(placementId);
    }

    @Override
    public boolean isReady() {
        return ready && splashAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        if (splashLayer == null) {
            initSplashLayer(activity);
        }
        splashLayer.setVisibility(View.VISIBLE);
        callback.log("展示 JiGuang 开屏");
        splashAd.showSplash();
    }

    @Override
    public void destroy() {
        removeSplashLayer();
        if (splashAd != null) {
            splashAd.release();
            splashAd = null;
        }
        ready = false;
    }

    private void initSplashLayer(Activity activity) {
        removeSplashLayer();
        RelativeLayout layer = new RelativeLayout(activity);
        layer.setVisibility(View.GONE);
        FrameLayout container = new FrameLayout(activity);
        layer.addView(container, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(layer, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        splashLayer = layer;
        splashContainer = container;
    }

    private void removeSplashLayer() {
        if (splashLayer != null) {
            splashLayer.removeAllViews();
            if (splashLayer.getParent() instanceof ViewGroup) {
                ((ViewGroup) splashLayer.getParent()).removeView(splashLayer);
            }
        }
        splashLayer = null;
        splashContainer = null;
    }
}
