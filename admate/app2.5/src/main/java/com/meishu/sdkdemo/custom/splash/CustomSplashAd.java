package com.meishu.sdkdemo.custom.splash;

import android.view.ViewGroup;

import com.meishu.sdk.platform.custom.splash.MsCustomSplashAd;
import com.meishu.sdk.platform.custom.splash.MsCustomSplashAdapter;

public class CustomSplashAd extends MsCustomSplashAd {

    private boolean showed;

    public CustomSplashAd(MsCustomSplashAdapter adWrapper) {
        super(adWrapper);
    }

    @Override
    public void showAd(ViewGroup adContainer) {
        if (!showed) {
            adContainer.removeAllViews();
            adContainer.addView(adView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            showed = true;
        }
    }
}
