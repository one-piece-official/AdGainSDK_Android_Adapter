package com.meishu.sdkdemo.custom.interstitial;

import android.app.Activity;

import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.meishu.sdk.platform.custom.interstitial.MsCustomInterstitialAd;
import com.meishu.sdk.platform.custom.interstitial.MsCustomInterstitialAdapter;

public class CustomInterstitialAd extends MsCustomInterstitialAd {
    private TTFullScreenVideoAd ttFullScreenVideoAd;

    public CustomInterstitialAd(MsCustomInterstitialAdapter adWrapper, TTFullScreenVideoAd ttFullScreenVideoAd) {
        super(adWrapper);
        this.ttFullScreenVideoAd = ttFullScreenVideoAd;
    }

    @Override
    public void showAd(Activity activity) {
        if (ttFullScreenVideoAd!=null){
            ttFullScreenVideoAd.showFullScreenVideoAd(activity);
        }
    }
}
