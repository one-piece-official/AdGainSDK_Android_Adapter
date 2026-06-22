package com.adgain.unified.controller.taku;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialExListener;
import com.test.ad.demo.AdConst;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.util.SDKUtil;

import java.util.HashMap;

public class TakuInterstitialAdController implements UnifiedAdController {
    private ATInterstitial interstitialAd;
    private String placementId;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        TakuAdControllerUtils.resetContainer(adContainer);
        this.placementId = placementId;
        SDKUtil.initSDK(activity.getApplicationContext());
        callback.log("开始加载 Taku 插屏: " + placementId);

        interstitialAd = new ATInterstitial(activity, placementId);
        interstitialAd.setAdListener(new ATInterstitialExListener() {
            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                callback.log("onDeeplinkCallback: " + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                callback.log("onDownloadConfirm");
            }

            @Override
            public void onInterstitialAdLoaded() {
                callback.log("onInterstitialAdLoaded");
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                callback.log("onInterstitialAdLoadFail: " + errorInfo(adError));
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {
                callback.log("onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {
                callback.log("onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {
                callback.log("onInterstitialAdClose");
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo entity) {
                callback.log("onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo entity) {
                callback.log("onInterstitialAdVideoEnd");
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                callback.log("onInterstitialAdVideoError: " + errorInfo(adError));
            }
        });
        interstitialAd.setAdSourceStatusListener(new BaseActivity.ATAdSourceStatusListenerImpl());
        interstitialAd.setNativeAdCustomRender(new BaseActivity.NativeAdCustomRender(activity));
        interstitialAd.setLocalExtra(new HashMap<String, Object>());
        interstitialAd.load();
    }

    @Override
    public boolean isReady() {
        if (interstitialAd == null) {
            return false;
        }
        ATAdStatusInfo statusInfo = interstitialAd.checkAdStatus();
        return statusInfo != null && statusInfo.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 Taku 插屏");
        ATInterstitial.entryAdScenario(placementId, AdConst.SCENARIO_ID.INTERSTITIAL_AD_SCENARIO);
        interstitialAd.show(activity, TakuAdControllerUtils.showConfig(
                AdConst.SCENARIO_ID.INTERSTITIAL_AD_SCENARIO,
                AdConst.SHOW_CUSTOM_EXT.INTERSTITIAL_AD_SHOW_CUSTOM_EXT
        ));
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.setAdSourceStatusListener(null);
            interstitialAd.setAdDownloadListener(null);
            interstitialAd.setAdListener(null);
            interstitialAd.setAdMultipleLoadedListener(null);
            interstitialAd = null;
        }
    }

    private String errorInfo(AdError adError) {
        return adError == null ? "" : adError.getFullErrorInfo();
    }
}
