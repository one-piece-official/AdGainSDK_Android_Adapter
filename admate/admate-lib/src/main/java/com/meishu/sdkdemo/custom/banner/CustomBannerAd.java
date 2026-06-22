package com.meishu.sdkdemo.custom.banner;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.meishu.sdk.platform.custom.banner.MsCustomBannerAd;
import com.meishu.sdk.platform.custom.banner.MsCustomBannerAdapter;

public class CustomBannerAd extends MsCustomBannerAd {

    private MsCustomBannerAdapter adWrapper;
    private TTNativeExpressAd ad;

    public CustomBannerAd(MsCustomBannerAdapter adWrapper, TTNativeExpressAd ad) {
        super(adWrapper);
        this.adWrapper = adWrapper;
        this.ad = ad;
    }

    @Override
    public void showAd(Activity activity, ViewGroup adContainer) {
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int i, String s, boolean b) {
                if (adWrapper.getLoaderListener() != null) {
                    adWrapper.getLoaderListener().onAdClosed();
                }
                View adView = ad.getExpressAdView();
                if (adView != null && adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                ad.destroy();
            }

            @Override
            public void onCancel() {

            }
        });
        super.showAd(activity, adContainer);
    }
}
