package com.adgain.unified.controller.admate;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.interstitial.InterstitialAd;
import com.meishu.sdk.core.ad.interstitial.InterstitialAdEventListener;
import com.meishu.sdk.core.ad.interstitial.InterstitialAdLoader;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;

public class AdMateInterstitialAdController implements UnifiedAdController {
    private InterstitialAdLoader interstitialLoader;
    private InterstitialAd interstitialAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        AdMateAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 AdMate 插屏: " + placementId);

        MsAdSlot adSlot = new MsAdSlot.Builder()
                .setPid(placementId)
                .setIsClickToClose(true)
                .build();
        interstitialLoader = new InterstitialAdLoader(activity, adSlot, new InterstitialAdEventListener() {
            @Override
            public void onAdError(AdErrorInfo errorInfo) {
                callback.log("onAdError: " + errorInfo);
            }

            @Override
            public void onAdReady(InterstitialAd ad) {
                interstitialAd = ad;
                interstitialAd.setInteractionListener(new InteractionListener() {
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
                callback.log("onAdReady");
            }
        });
        interstitialLoader.loadAd();
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
        callback.log("展示 AdMate 插屏");
        interstitialAd.showAd(activity);
    }

    @Override
    public void destroy() {
        if (interstitialLoader != null) {
            interstitialLoader.destroy();
            interstitialLoader = null;
        }
        interstitialAd = null;
    }
}
