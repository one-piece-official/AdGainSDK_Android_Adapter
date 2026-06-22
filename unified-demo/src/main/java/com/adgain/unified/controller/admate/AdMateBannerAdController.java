package com.adgain.unified.controller.admate;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.banner.BannerAdEventListener;
import com.meishu.sdk.core.ad.banner.BannerAdLoader;
import com.meishu.sdk.core.ad.banner.IBannerAd;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;

public class AdMateBannerAdController implements UnifiedAdController {
    private BannerAdLoader bannerLoader;
    private IBannerAd bannerAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        AdMateAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 AdMate Banner: " + placementId);

        MsAdSlot adSlot = new MsAdSlot.Builder()
                .setPid(placementId)
                .build();
        bannerLoader = new BannerAdLoader(activity, adSlot, new BannerAdEventListener() {
            @Override
            public void onAdError(AdErrorInfo errorInfo) {
                callback.log("onAdError: " + errorInfo);
            }

            @Override
            public void onAdReady(IBannerAd ad) {
                bannerAd = ad;
                callback.log("onAdReady");
            }
        });
        bannerLoader.loadAd();
    }

    @Override
    public boolean isReady() {
        return bannerAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        adContainer.removeAllViews();
        adContainer.setVisibility(android.view.View.VISIBLE);
        adContainer.requestLayout();
        adContainer.post(new Runnable() {
            @Override
            public void run() {
                int width = adContainer.getMeasuredWidth();
                if (width <= 0) {
                    width = activity.getResources().getDisplayMetrics().widthPixels;
                }
                int height = AdMateAdControllerUtils.dp(adContainer, 100);
                bannerAd.setWidthAndHeight(width, height);
                bannerAd.setInteractionListener(new InteractionListener() {
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
                    }
                });
                bannerAd.showAd(adContainer);
                applyCenterInside(adContainer);
                callback.log("展示 AdMate Banner: " + width + "x" + height);
            }
        });
    }

    @Override
    public void destroy() {
        if (bannerLoader != null) {
            bannerLoader.destroy();
            bannerLoader = null;
        }
        bannerAd = null;
    }

    private void applyCenterInside(View view) {
        if (view instanceof ImageView) {
            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
            return;
        }
        if (!(view instanceof ViewGroup)) {
            return;
        }
        ViewGroup group = (ViewGroup) view;
        for (int i = 0; i < group.getChildCount(); i++) {
            applyCenterInside(group.getChildAt(i));
        }
    }
}
