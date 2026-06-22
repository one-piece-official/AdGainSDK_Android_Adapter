package com.meishu.sdkdemo.custom.interstitial;

import android.util.DisplayMetrics;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.meishu.sdk.core.ad.interstitial.InterstitialAdLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.platform.custom.interstitial.MsCustomInterstitialAdapter;
import com.meishu.sdkdemo.custom.CustomInitManager;

public class CustomInterstitialAdLoader extends MsCustomInterstitialAdapter {
    private static final String TAG = "CustomInterstitialAdLoa";
    private TTAdNative ttAdNative;
    public CustomInterstitialAdLoader(InterstitialAdLoader adLoader, SdkAdInfo sdkAdInfo) {
        super(adLoader, sdkAdInfo);

        this.ttAdNative = TTAdSdk.getAdManager().createAdNative(adLoader.getContext());
    }

    @Override
    public void loadCustomAd(String app_id,String app_key, String pid,String custom_ext) {
        CustomInitManager.getInstance().initSdk(context, app_id, new CustomInitManager.InitCallback() {
            @Override
            public void onSuccess() {
                startLoadAd(pid);
            }

            @Override
            public void onError(int code, String msg) {
                CustomInterstitialAdLoader.this.onError(code,msg);
            }
        });
    }

    private void startLoadAd(String pid) {
        int adContentWidth = 1080;
        int adContentHeight = 1920;
        try {
            DisplayMetrics displayMetrics = this.adLoader.getContext().getResources().getDisplayMetrics();
            if (0 < displayMetrics.widthPixels && 0 < displayMetrics.heightPixels) {
                adContentWidth  = displayMetrics.widthPixels;
                adContentHeight = displayMetrics.heightPixels;
            }
        } catch (Exception e) {}


        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(pid)
                .setExpressViewAcceptedSize(adContentWidth, adContentHeight)
                .setSupportDeepLink(true)
                .build();
        ttAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                CustomInterstitialAdLoader.this.onError(i,s);
            }
            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {

                final CustomInterstitialAd csjFullScreenVideoAd = new CustomInterstitialAd(CustomInterstitialAdLoader.this,ttFullScreenVideoAd);
                ttFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        CustomInterstitialAdLoader.this.onAdExposure(csjFullScreenVideoAd);
                    }
                    @Override
                    public void onAdVideoBarClick() {
                        CustomInterstitialAdLoader.this.onAdClick(csjFullScreenVideoAd);
                    }
                    @Override
                    public void onAdClose() {
                        CustomInterstitialAdLoader.this.onAdClosed(csjFullScreenVideoAd);
                    }
                    @Override
                    public void onVideoComplete() {
                    }
                    @Override
                    public void onSkippedVideo() {
                    }
                });

                CustomInterstitialAdLoader.this.onRenderSuccess(csjFullScreenVideoAd);

            }

            public void onFullScreenVideoCached() {}


            public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {

            }
        });
    }
}
