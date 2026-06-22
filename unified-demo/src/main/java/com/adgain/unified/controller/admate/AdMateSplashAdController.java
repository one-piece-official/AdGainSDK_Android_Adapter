package com.adgain.unified.controller.admate;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.splash.ISplashAd;
import com.meishu.sdk.core.ad.splash.SplashAdEventListener;
import com.meishu.sdk.core.ad.splash.SplashAdLoader;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;

public class AdMateSplashAdController implements UnifiedAdController {
    private SplashAdLoader splashLoader;
    private ISplashAd splashAd;
    private FrameLayout splashContainer;
    private ViewGroup splashLayer;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        AdMateAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 AdMate 开屏: " + placementId);

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int bottomHeight = AdMateAdControllerUtils.dp(adContainer, 120);
        MsAdSlot adSlot = new MsAdSlot.Builder()
                .setPid(placementId)
                .setFetchCount(1)
                .setWidth(dm.widthPixels)
                .setHeight(Math.max(1, dm.heightPixels - bottomHeight))
                .build();
        splashLoader = new SplashAdLoader(activity, adSlot, new SplashAdEventListener() {
            @Override
            public void onAdReady(ISplashAd ad) {
                splashAd = ad;
                if (splashAd != null) {
                    splashAd.setInteractionListener(new InteractionListener() {
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
                            removeSplashLayer();
                        }
                    });
                }
                callback.log("onAdReady");
            }

            @Override
            public void onAdPresent(ISplashAd ad) {
                callback.log("onAdPresent");
            }

            @Override
            public void onAdSkip(ISplashAd ad) {
                callback.log("onAdSkip");
                removeSplashLayer();
            }

            @Override
            public void onAdTimeOver(ISplashAd ad) {
                callback.log("onAdTimeOver");
                removeSplashLayer();
            }

            @Override
            public void onAdTick(long leftMilliseconds) {
                callback.log("onAdTick: " + leftMilliseconds);
            }

            @Override
            public void onAdError(AdErrorInfo errorInfo) {
                callback.log("onAdError: " + errorInfo);
            }
        }, 5000);
        splashLoader.loadAd();
    }

    @Override
    public boolean isReady() {
        return splashAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        initSplashLayer(activity);
        splashAd.showAd(splashContainer);
        callback.log("展示 AdMate 开屏");
    }

    @Override
    public void destroy() {
        if (splashLoader != null) {
            splashLoader.destroy();
            splashLoader = null;
        }
        splashAd = null;
        removeSplashLayer();
    }

    private void initSplashLayer(Activity activity) {
        if (splashContainer == null) {
            splashContainer = new FrameLayout(activity);
        }
        if (splashLayer != null) {
            return;
        }
        RelativeLayout layer = new RelativeLayout(activity);
        layer.setBackgroundColor(0xffffffff);
        layer.addView(splashContainer, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(layer, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        splashLayer = layer;
    }

    private void removeSplashLayer() {
        if (splashContainer != null) {
            splashContainer.removeAllViews();
        }
        if (splashLayer != null) {
            splashLayer.removeAllViews();
            if (splashLayer.getParent() instanceof ViewGroup) {
                ((ViewGroup) splashLayer.getParent()).removeView(splashLayer);
            }
            splashLayer = null;
        }
    }
}
