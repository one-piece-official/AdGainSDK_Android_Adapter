package com.adgain.unified.controller.beizi;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;

import xyz.adscope.amps.ad.splash.AMPSSplashAd;
import xyz.adscope.amps.ad.splash.AMPSSplashLoadEventListener;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;
import xyz.adscope.amps.tool.util.AMPSScreenUtil;

public class BeiziSplashAdController implements UnifiedAdController {
    private AMPSSplashAd splashAd;
    private ViewGroup splashLayer;
    private boolean ready;
    private boolean canJumpImmediately;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        BeiziAdControllerUtils.resetContainer(adContainer);
        ready = false;
        callback.log("开始加载 Beizi 开屏: " + placementId);

        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(placementId)
                .setTimeOut(5000)
                .setWidth(AMPSScreenUtil.getScreenWidth(activity))
                .setHeight(AMPSScreenUtil.getScreenHeight(activity))
                .build();
        splashAd = new AMPSSplashAd(activity, parameter, new AMPSSplashLoadEventListener() {
            @Override
            public void onAmpsAdLoaded() {
                ready = true;
                callback.log("onAmpsAdLoaded");
            }

            @Override
            public void onAmpsAdFailed(AMPSError error) {
                ready = false;
                callback.log("onAmpsAdFailed: " + BeiziAdControllerUtils.errorInfo(error));
                removeSplashLayer();
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
                callback.log("onAmpsAdDismiss");
                jumpWhenCanClick();
            }
        });
        splashAd.loadAd();
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
        initSplashLayer(activity);
        callback.log("展示 Beizi 开屏");
        splashAd.show((RelativeLayout) splashLayer);
    }

    @Override
    public void destroy() {
        removeSplashLayer();
        if (splashAd != null) {
            splashAd.destroy();
            splashAd = null;
        }
        ready = false;
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
        canJumpImmediately = true;
    }

    private void jumpWhenCanClick() {
        if (canJumpImmediately) {
            removeSplashLayer();
        } else {
            canJumpImmediately = true;
        }
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
