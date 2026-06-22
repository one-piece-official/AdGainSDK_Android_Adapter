package com.adgain.unified.controller.taku;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;
import com.test.ad.demo.AdConst;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.util.SDKUtil;

import java.util.HashMap;
import java.util.Map;

public class TakuSplashAdController implements UnifiedAdController {
    private ATSplashAd splashAd;
    private ViewGroup splashLayer;
    private String placementId;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        TakuAdControllerUtils.resetContainer(adContainer);
        this.placementId = placementId;
        SDKUtil.initSDK(activity.getApplicationContext());
        callback.log("开始加载 Taku 开屏: " + placementId);

        splashAd = new ATSplashAd(activity, placementId, new ATSplashExListener() {
            @Override
            public void onAdLoaded(boolean isTimeout) {
                callback.log("onAdLoaded, isTimeout=" + isTimeout);
            }

            @Override
            public void onAdLoadTimeout() {
                callback.log("onAdLoadTimeout");
            }

            @Override
            public void onNoAdError(AdError adError) {
                callback.log("onNoAdError: " + adAdError(adError));
                removeSplashLayer();
            }

            @Override
            public void onAdShow(ATAdInfo entity) {
                callback.log("onAdShow");
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
                callback.log("onAdClick");
            }

            @Override
            public void onAdDismiss(ATAdInfo entity, ATSplashAdExtraInfo splashAdExtraInfo) {
                callback.log("onAdDismiss");
                removeSplashLayer();
            }

            @Override
            public void onDeeplinkCallback(ATAdInfo entity, boolean isSuccess) {
                callback.log("onDeeplinkCallback: " + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                callback.log("onDownloadConfirm");
            }
        }, 5000);

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = (int) (dm.widthPixels * 0.85f);
        } else {
            height = (int) (dm.heightPixels * 0.85f);
        }
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.AD_WIDTH, width);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, height);
        splashAd.setLocalExtra(localMap);
        splashAd.setNativeAdCustomRender(new BaseActivity.NativeAdCustomRender(activity));
        splashAd.setAdSourceStatusListener(new BaseActivity.ATAdSourceStatusListenerImpl());
        splashAd.loadAd();
    }

    @Override
    public boolean isReady() {
        if (splashAd == null) {
            return false;
        }
        ATAdStatusInfo statusInfo = splashAd.checkAdStatus();
        return statusInfo != null && statusInfo.isReady();
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
        callback.log("展示 Taku 开屏");
        ATSplashAd.entryAdScenario(placementId, AdConst.SCENARIO_ID.SPLASH_AD_SCENARIO);
        splashAd.show(activity, splashLayer);
    }

    @Override
    public void destroy() {
        removeSplashLayer();
        if (splashAd != null) {
            splashAd.setAdListener(null);
            splashAd.setAdDownloadListener(null);
            splashAd.setAdSourceStatusListener(null);
            splashAd.setAdMultipleLoadedListener(null);
            splashAd = null;
        }
    }

    private void initSplashLayer(Activity activity) {
        removeSplashLayer();
        RelativeLayout layer = new RelativeLayout(activity);
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(layer, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
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

    private String adAdError(AdError adError) {
        return adError == null ? "" : adError.getFullErrorInfo();
    }
}
