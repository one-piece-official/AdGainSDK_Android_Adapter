package com.adgain.unified.controller.tobid;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.interstitial.WMInterstitialAd;
import com.windmill.sdk.interstitial.WMInterstitialAdListener;
import com.windmill.sdk.interstitial.WMInterstitialAdRequest;
import com.windmill.sdk.models.AdInfo;

import java.util.HashMap;
import java.util.Map;

public class ToBidInterstitialAdController implements UnifiedAdController {
    private static final String USER_ID = "123456789";

    private WMInterstitialAd interstitialAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        adContainer.removeAllViews();
        adContainer.setVisibility(ViewGroup.GONE);
        callback.log("开始加载 ToBid 插屏: " + placementId);

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", USER_ID);
        interstitialAd = new WMInterstitialAd(activity, new WMInterstitialAdRequest(placementId, USER_ID, options));
        interstitialAd.setInterstitialAdListener(new WMInterstitialAdListener() {
            @Override
            public void onInterstitialAdLoadSuccess(String placementId) {
                callback.log("onInterstitialAdLoadSuccess: " + placementId);
            }

            @Override
            public void onInterstitialAdPlayStart(AdInfo adInfo) {
                callback.log("onInterstitialAdPlayStart");
            }

            @Override
            public void onInterstitialAdPlayEnd(AdInfo adInfo) {
                callback.log("onInterstitialAdPlayEnd");
            }

            @Override
            public void onInterstitialAdClicked(AdInfo adInfo) {
                callback.log("onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdClosed(AdInfo adInfo) {
                callback.log("onInterstitialAdClosed");
            }

            @Override
            public void onInterstitialAdLoadError(WindMillError error, String placementId) {
                callback.log("onInterstitialAdLoadError: " + error + ", placementId=" + placementId);
            }

            @Override
            public void onInterstitialAdPlayError(WindMillError error, String placementId) {
                callback.log("onInterstitialAdPlayError: " + error + ", placementId=" + placementId);
            }
        });
        interstitialAd.loadAd();
    }

    @Override
    public boolean isReady() {
        return interstitialAd != null && interstitialAd.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (isReady()) {
            callback.log("展示 ToBid 插屏");
            HashMap<String, String> options = new HashMap<>();
            options.put(WMConstants.AD_SCENE_ID, "567");
            options.put(WMConstants.AD_SCENE_DESC, "转盘抽奖");
            interstitialAd.show(activity, options);
        } else {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
    }
}
