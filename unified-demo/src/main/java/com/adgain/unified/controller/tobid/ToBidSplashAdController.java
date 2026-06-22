package com.adgain.unified.controller.tobid;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.splash.IWMSplashEyeAd;
import com.windmill.sdk.splash.WMSplashAd;
import com.windmill.sdk.splash.WMSplashAdListener;
import com.windmill.sdk.splash.WMSplashAdRequest;

public class ToBidSplashAdController implements UnifiedAdController {
    private WMSplashAd splashAd;
    private ViewGroup splashLayer;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        adContainer.removeAllViews();
        adContainer.setVisibility(View.GONE);
        initSplashLayer(activity);
        callback.log("开始加载 ToBid 开屏: " + placementId);

        WMSplashAdRequest adRequest = new WMSplashAdRequest(placementId, String.valueOf(0), null);
        splashAd = new WMSplashAd(activity, adRequest, new WMSplashAdListener() {
            @Override
            public void onSplashAdSuccessPresent(AdInfo adInfo) {
                callback.log("onSplashAdSuccessPresent");
            }

            @Override
            public void onSplashAdFailToPresent(WindMillError error, String placementId) {
                callback.log("onSplashAdFailToPresent: " + error);
            }

            @Override
            public void onSplashAdSuccessLoad(String placementId) {
                callback.log("onSplashAdSuccessLoad: " + placementId);
            }

            @Override
            public void onSplashAdFailToLoad(WindMillError error, String placementId) {
                callback.log("onSplashAdFailToLoad: " + error);
                removeSplashLayer();
            }

            @Override
            public void onSplashAdClicked(AdInfo adInfo) {
                callback.log("onSplashAdClicked");
            }

            @Override
            public void onSplashClosed(AdInfo adInfo, IWMSplashEyeAd splashEyeAd) {
                callback.log("onSplashClosed");
                removeSplashLayer();
                if (splashEyeAd != null) {
                    splashEyeAd.destroy();
                }
            }
        });
        splashAd.loadAdOnly();
    }

    @Override
    public boolean isReady() {
        return splashAd != null && splashAd.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (isReady()) {
            callback.log("展示 ToBid 开屏");
            if (splashLayer == null) {
                initSplashLayer(activity);
            }
            splashAd.showAd(splashLayer);
        } else {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void destroy() {
        removeSplashLayer();
        splashAd = null;
    }

    private void initSplashLayer(Activity activity) {
        removeSplashLayer();
        RelativeLayout layer = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(layer, layoutParams);
        splashLayer = layer;
    }

    private void removeSplashLayer() {
        if (splashLayer == null) {
            return;
        }
        splashLayer.removeAllViews();
        if (splashLayer.getParent() instanceof ViewGroup) {
            ((ViewGroup) splashLayer.getParent()).removeView(splashLayer);
        }
        splashLayer = null;
    }
}
