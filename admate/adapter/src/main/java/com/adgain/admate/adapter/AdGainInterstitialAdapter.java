package com.adgain.admate.adapter;

import android.app.Activity;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.InterstitialAd;
import com.adgain.sdk.api.InterstitialAdListener;
import com.meishu.sdk.core.ad.interstitial.InterstitialAdLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.platform.custom.interstitial.MsCustomInterstitialAd;
import com.meishu.sdk.platform.custom.interstitial.MsCustomInterstitialAdapter;

import java.util.HashMap;

public class AdGainInterstitialAdapter extends MsCustomInterstitialAdapter {

    private InterstitialAd mInterstitialAd;
    private CustomInterstitialAd ad = null;

    public AdGainInterstitialAdapter(InterstitialAdLoader interstitialAdLoader, SdkAdInfo sdkAdInfo) {
        super(interstitialAdLoader, sdkAdInfo);
    }

    private void loadInterstitialAd(String appId, String codeId) {
        HashMap<String, Object> extras = new HashMap<>();
        AdRequest adRequest = new AdRequest.Builder().
                setAppId(appId).setCodeId(codeId).setExtOption(extras)
                .build();
        mInterstitialAd = new InterstitialAd(adRequest, new InterstitialAdListener() {
            @Override
            public void onInterstitialAdLoadError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    onError(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onInterstitialAdLoadSuccess() {
                if (mInterstitialAd != null)
                    setEcpm(mInterstitialAd.getBidPrice());
            }

            @Override
            public void onInterstitialAdLoadCached() {
                ad = new CustomInterstitialAd(AdGainInterstitialAdapter.this, mInterstitialAd);
                onRenderSuccess(ad);
            }

            @Override
            public void onInterstitialAdShow() {
                onAdExposure(ad);
            }

            @Override
            public void onInterstitialAdPlayEnd() {
            }

            @Override
            public void onInterstitialAdClick() {
                onAdClick(ad);
            }

            @Override
            public void onInterstitialAdClosed() {
                onAdClosed(ad);
            }

            @Override
            public void onInterstitialAdShowError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    onRenderFail(adError.getErrorCode(), adError.getMessage());
            }
        });
        mInterstitialAd.loadAd();

    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != mInterstitialAd) mInterstitialAd.destroyAd();
    }

    @Override
    public void loadCustomAd(String appId, String s1, String codeId, String s3) {
        AdGainInitAdapter.getInstance().initADN(appId, new InitCallback() {
            @Override
            public void onSuccess() {
                loadInterstitialAd(appId, codeId);
            }

            @Override
            public void onFail(int i, String s) {
                onError(i, s);
            }
        });
    }


    static class CustomInterstitialAd extends MsCustomInterstitialAd {
        private final InterstitialAd interstitialAd;

        public CustomInterstitialAd(MsCustomInterstitialAdapter adWrapper, InterstitialAd interstitialAd) {
            super(adWrapper);
            this.interstitialAd = interstitialAd;
        }

        @Override
        public void showAd(Activity activity) {
            if (interstitialAd != null) {
                interstitialAd.showAd(activity);
            }
        }
    }

}
