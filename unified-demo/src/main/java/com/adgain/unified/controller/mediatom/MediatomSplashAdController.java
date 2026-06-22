package com.adgain.unified.controller.mediatom;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.base.interfaces.SpreadLoadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;

public class MediatomSplashAdController implements UnifiedAdController {
    private YdSpread spread;
    private SpreadLoadListener.SpreadAd spreadAd;
    private FrameLayout splashContainer;
    private ViewGroup splashLayer;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        MediatomAdControllerUtils.resetContainer(adContainer);
        splashContainer = new FrameLayout(activity);
        callback.log("开始加载 Mediatom 开屏: " + placementId);

        spread = new YdSpread.Builder(activity)
                .setKey(placementId)
                .setContainer(splashContainer)
                .setSpreadLoadListener(new SpreadLoadListener() {
                    @Override
                    public void onADLoaded(SpreadAd ad) {
                        spreadAd = ad;
                        callback.log("onADLoaded");
                    }
                })
                .setSpreadListener(new AdViewSpreadListener() {
                    @Override
                    public void onAdDisplay() {
                        callback.log("onAdDisplay");
                    }

                    @Override
                    public void onAdClose() {
                        callback.log("onAdClose");
                        removeSplashLayer();
                    }

                    @Override
                    public void onAdClick(String url) {
                        callback.log("onAdClick: " + url);
                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        callback.log("onAdFailed: " + MediatomAdControllerUtils.errorInfo(error));
                        removeSplashLayer();
                    }
                })
                .build();
        spread.requestSpread();
    }

    @Override
    public boolean isReady() {
        return spreadAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        initSplashLayer(activity);
        callback.log("展示 Mediatom 开屏");
        spreadAd.show(splashContainer);
    }

    @Override
    public void destroy() {
        removeSplashLayer();
        if (spread != null) {
            spread.destroy();
            spread = null;
        }
        spreadAd = null;
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
        splashContainer = null;
    }

    private void initSplashLayer(Activity activity) {
        if (splashContainer == null) {
            splashContainer = new FrameLayout(activity);
        }
        if (splashLayer != null) {
            return;
        }
        RelativeLayout layer = new RelativeLayout(activity);
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
}
