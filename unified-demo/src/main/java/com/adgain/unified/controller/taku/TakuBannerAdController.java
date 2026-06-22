package com.adgain.unified.controller.taku;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.anythink.banner.api.ATBannerExListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.test.ad.demo.AdConst;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.util.SDKUtil;

import java.util.HashMap;
import java.util.Map;

public class TakuBannerAdController implements UnifiedAdController {
    private ATBannerView bannerView;
    private boolean loaded;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        TakuAdControllerUtils.resetContainer(adContainer);
        SDKUtil.initSDK(activity.getApplicationContext());
        callback.log("开始加载 Taku Banner: " + placementId);

        bannerView = new ATBannerView(activity);
        bannerView.setPlacementId(placementId);
        bannerView.setShowConfig(TakuAdControllerUtils.showConfig(
                AdConst.SCENARIO_ID.BANNER_AD_SCENARIO,
                AdConst.SHOW_CUSTOM_EXT.BANNER_AD_SHOW_CUSTOM_EXT
        ));
        bannerView.setBannerAdListener(new ATBannerExListener() {
            @Override
            public void onDeeplinkCallback(boolean isRefresh, ATAdInfo adInfo, boolean isSuccess) {
                callback.log("onDeeplinkCallback: " + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                callback.log("onDownloadConfirm");
            }

            @Override
            public void onBannerLoaded() {
                loaded = true;
                callback.log("onBannerLoaded");
            }

            @Override
            public void onBannerFailed(AdError adError) {
                loaded = false;
                callback.log("onBannerFailed: " + errorInfo(adError));
            }

            @Override
            public void onBannerClicked(ATAdInfo entity) {
                callback.log("onBannerClicked");
            }

            @Override
            public void onBannerShow(ATAdInfo entity) {
                callback.log("onBannerShow");
            }

            @Override
            public void onBannerClose(ATAdInfo entity) {
                callback.log("onBannerClose");
            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo entity) {
                callback.log("onBannerAutoRefreshed");
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                callback.log("onBannerAutoRefreshFail: " + errorInfo(adError));
            }
        });
        bannerView.setAdSourceStatusListener(new BaseActivity.ATAdSourceStatusListenerImpl());

        int padding = TakuAdControllerUtils.dipToPx(activity, 12);
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.AD_WIDTH, activity.getResources().getDisplayMetrics().widthPixels - 2 * padding);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, TakuAdControllerUtils.dipToPx(activity, 60));
        bannerView.setLocalExtra(localMap);
        bannerView.setNativeAdCustomRender(new BaseActivity.NativeAdCustomRender(activity));
        bannerView.loadAd();
    }

    @Override
    public boolean isReady() {
        return loaded;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (bannerView == null) {
            callback.log("请先加载广告");
            return;
        }
        callback.log(loaded ? "展示 Taku Banner" : "Banner 未回调 ready，先挂载到容器");
        TakuAdControllerUtils.attachView(adContainer, bannerView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                TakuAdControllerUtils.dipToPx(activity, 70)
        ));
    }

    @Override
    public void destroy() {
        loaded = false;
        if (bannerView != null) {
            if (bannerView.getParent() instanceof ViewGroup) {
                ((ViewGroup) bannerView.getParent()).removeView(bannerView);
            }
            bannerView.destroy();
            bannerView = null;
        }
    }

    private String errorInfo(AdError adError) {
        return adError == null ? "" : adError.getFullErrorInfo();
    }
}
