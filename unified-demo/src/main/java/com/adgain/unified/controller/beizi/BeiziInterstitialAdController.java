package com.adgain.unified.controller.beizi;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;

import xyz.adscope.amps.ad.interstitial.AMPSInterstitialAd;
import xyz.adscope.amps.ad.interstitial.AMPSInterstitialLoadEventListener;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;

public class BeiziInterstitialAdController implements UnifiedAdController {
    private AMPSInterstitialAd interstitialAd;
    private boolean ready;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        BeiziAdControllerUtils.resetContainer(adContainer);
        ready = false;
        callback.log("开始加载 Beizi 插屏: " + placementId);

        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(placementId)
                .setTimeOut(5000)
                .setWidth(600)
                .setHeight(600)
                .build();
        interstitialAd = new AMPSInterstitialAd(activity, parameter, new AMPSInterstitialLoadEventListener() {
            @Override
            public void onAmpsSkippedAd() {
                callback.log("onAmpsSkippedAd");
            }

            @Override
            public void onAmpsAdLoaded() {
                ready = true;
                callback.log("onAmpsAdLoaded");
            }

            @Override
            public void onAmpsAdFailed(AMPSError error) {
                ready = false;
                callback.log("onAmpsAdFailed: " + BeiziAdControllerUtils.errorInfo(error));
            }

            @Override
            public void onAmpsAdShow() {
                callback.log("onAmpsAdShow");
            }

            @Override
            public void onAmpsAdClicked() {
                callback.log("onAmpsAdClicked");
            }

            @Override
            public void onAmpsAdDismiss() {
                ready = false;
                callback.log("onAmpsAdDismiss");
            }

            @Override
            public void onAmpsVideoPlayStart() {
                callback.log("onAmpsVideoPlayStart");
            }

            @Override
            public void onAmpsVideoPlayEnd() {
                callback.log("onAmpsVideoPlayEnd");
            }
        });
        interstitialAd.loadAd();
    }

    @Override
    public boolean isReady() {
        return ready && interstitialAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 Beizi 插屏");
        interstitialAd.show(activity);
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
        ready = false;
    }
}
