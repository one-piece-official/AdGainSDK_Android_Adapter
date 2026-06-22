package com.adgain.unified.controller.mediatom;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.yd.saas.base.interfaces.AdViewInterstitialListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdInterstitial;

public class MediatomInterstitialAdController implements UnifiedAdController {
    private YdInterstitial interstitial;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        MediatomAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 Mediatom 插屏: " + placementId);

        interstitial = new YdInterstitial.Builder(activity)
                .setKey(placementId)
                .setWidth(600)
                .setHeight(800)
                .setInterstitialListener(new AdViewInterstitialListener() {
                    @Override
                    public void onAdReady() {
                        callback.log("onAdReady");
                    }

                    @Override
                    public void onAdDisplay() {
                        callback.log("onAdDisplay");
                    }

                    @Override
                    public void onAdClick(String url) {
                        callback.log("onAdClick: " + url);
                    }

                    @Override
                    public void onAdClosed() {
                        callback.log("onAdClosed");
                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        callback.log("onAdFailed: " + MediatomAdControllerUtils.errorInfo(error));
                    }
                })
                .build();
        interstitial.requestInterstitial();
    }

    @Override
    public boolean isReady() {
        return interstitial != null && interstitial.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 Mediatom 插屏");
        interstitial.show();
    }

    @Override
    public void destroy() {
        if (interstitial != null) {
            interstitial.destroy();
            interstitial = null;
        }
    }
}
