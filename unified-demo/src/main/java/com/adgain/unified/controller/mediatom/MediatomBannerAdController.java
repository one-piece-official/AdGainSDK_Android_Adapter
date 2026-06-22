package com.adgain.unified.controller.mediatom;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.yd.saas.base.interfaces.AdViewBannerListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdBanner;

public class MediatomBannerAdController implements UnifiedAdController {
    private YdBanner banner;
    private View bannerView;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        MediatomAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 Mediatom Banner: " + placementId);

        banner = new YdBanner.Builder(activity)
                .setKey(placementId)
                .setBannerListener(new AdViewBannerListener() {
                    @Override
                    public void onReceived(View view) {
                        bannerView = view;
                        callback.log("onReceived");
                    }

                    @Override
                    public void onAdExposure() {
                        callback.log("onAdExposure");
                    }

                    @Override
                    public void onClosed() {
                        callback.log("onClosed");
                    }

                    @Override
                    public void onAdClick(String url) {
                        callback.log("onAdClick: " + url);
                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        callback.log("onAdFailed: " + MediatomAdControllerUtils.errorInfo(error));
                    }
                })
                .build();
        banner.requestBanner();
    }

    @Override
    public boolean isReady() {
        return bannerView != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            return;
        }
        MediatomAdControllerUtils.attachView(adContainer, bannerView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(activity, 100)
        ));
        callback.log("展示 Mediatom Banner");
    }

    @Override
    public void destroy() {
        if (banner != null) {
            banner.destroy();
            banner = null;
        }
        bannerView = null;
    }

    private int dp(Activity activity, int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }
}
