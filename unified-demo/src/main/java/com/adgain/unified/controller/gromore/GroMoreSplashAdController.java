package com.adgain.unified.controller.gromore;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.CSJSplashCloseType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.gromore.adapter.adgain.GMBiddingUtil;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.UIUtils;

public class GroMoreSplashAdController implements UnifiedAdController {
    private CSJSplashAd splashAd;
    private View splashView;
    private ViewGroup splashLayer;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        GroMoreAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 GroMore 开屏: " + placementId);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(placementId)
                .setMediationAdSlot(new GroMoreMediationAdSlot())
                .setImageAcceptedSize(UIUtils.getScreenWidthInPx(activity), UIUtils.getScreenHeightInPx(activity))
                .build();

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(activity);
        if (adNativeLoader == null) {
            callback.log("TTAdNative 创建失败");
            return;
        }
        adNativeLoader.loadSplashAd(adSlot, new TTAdNative.CSJSplashAdListener() {
            @Override
            public void onSplashRenderSuccess(CSJSplashAd csjSplashAd) {
                splashAd = csjSplashAd;
                splashAd.setSplashAdListener(new CSJSplashAd.SplashAdListener() {
                    @Override
                    public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                        callback.log("onSplashAdShow");
                        GMBiddingUtil.gmNotifyLoss(csjSplashAd);
                    }

                    @Override
                    public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                        callback.log("onSplashAdClick");
                    }

                    @Override
                    public void onSplashAdClose(CSJSplashAd csjSplashAd, int closeType) {
                        callback.log("onSplashAdClose: " + closeTypeName(closeType));
                        removeSplashLayer();
                    }
                });
                splashView = csjSplashAd.getSplashView();
                callback.log("onSplashRenderSuccess");
            }

            public void onSplashLoadSuccess() {
                callback.log("onSplashLoadSuccess");
            }

            @Override
            public void onSplashLoadSuccess(CSJSplashAd csjSplashAd) {
                callback.log("onSplashLoadSuccess");
            }

            @Override
            public void onSplashLoadFail(CSJAdError csjAdError) {
                callback.log("onSplashLoadFail: " + errorInfo(csjAdError));
            }

            @Override
            public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
                callback.log("onSplashRenderFail: " + errorInfo(csjAdError));
            }
        }, 3500);
    }

    @Override
    public boolean isReady() {
        return splashAd != null && splashView != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 GroMore 开屏");
        initSplashLayer(activity);
        UIUtils.removeFromParent(splashView);
        splashLayer.removeAllViews();
        splashLayer.addView(splashView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
    }

    @Override
    public void destroy() {
        removeSplashLayer();
        if (splashAd != null && splashAd.getMediationManager() != null) {
            splashAd.getMediationManager().destroy();
        }
        splashAd = null;
        splashView = null;
    }

    private String errorInfo(CSJAdError error) {
        return error == null ? "" : error.getCode() + ", " + error.getMsg();
    }

    private String closeTypeName(int closeType) {
        if (closeType == CSJSplashCloseType.CLICK_SKIP) {
            return "CLICK_SKIP";
        }
        if (closeType == CSJSplashCloseType.COUNT_DOWN_OVER) {
            return "COUNT_DOWN_OVER";
        }
        if (closeType == CSJSplashCloseType.CLICK_JUMP) {
            return "CLICK_JUMP";
        }
        return String.valueOf(closeType);
    }

    private void initSplashLayer(Activity activity) {
        if (splashLayer != null) {
            return;
        }
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
}
