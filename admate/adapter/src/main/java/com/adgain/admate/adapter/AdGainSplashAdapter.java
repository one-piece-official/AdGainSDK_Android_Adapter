package com.adgain.admate.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.api.SplashAd;
import com.adgain.sdk.api.SplashAdListener;
import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.ad.splash.SplashAdLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.platform.custom.splash.MsCustomSplashAd;
import com.meishu.sdk.platform.custom.splash.MsCustomSplashAdapter;

import java.util.HashMap;

// 美数后台自定义只能配置bidding,sdkAdInfo.getOtype()
public class AdGainSplashAdapter extends MsCustomSplashAdapter {

    private SplashAd splashAd;
    private MyMsCustomSplashAd msCustomSplashAdapter = null;

    public AdGainSplashAdapter(SplashAdLoader splashAdLoader, SdkAdInfo sdkAdInfo) {
        super(splashAdLoader, sdkAdInfo);

    }

    private void loadSplashAd(String appId, String codeId) {
        HashMap<String, Object> extras = new HashMap<>();
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId)
                .setExtOption(extras)
                .setAppId(appId)
                .build();
        Log.d("----Adgain", "---loadSplashAd ");
        // 创建开屏AD API对象，监听回调在这里设置,5 * 1000为请求广告超时时间
        splashAd = new SplashAd(adRequest, new SplashAdListener() {
            @Override
            public void onAdLoadSuccess() {
                Log.d("----Adgain", "---onAdLoadSuccess ");
                if (splashAd != null)
                    setEcpm(splashAd.getBidPrice());
            }

            @Override
            public void onAdCacheSuccess() {
                msCustomSplashAdapter = new MyMsCustomSplashAd(AdGainSplashAdapter.this, splashAd);
                onRenderSuccess(new FrameLayout(AdSdk.getContext()), msCustomSplashAdapter);
            }

            @Override
            public void onSplashAdLoadFail(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    onError(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onSplashAdShow() {
                onAdExposure(msCustomSplashAdapter);
            }

            @Override
            public void onSplashAdShowError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    onRenderFail(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onSplashAdClick() {
                onAdClick(msCustomSplashAdapter);
            }

            @Override
            public void onSplashAdClose(boolean b) {
                onAdTimeOver(msCustomSplashAdapter);
            }
        }, 3 * 1000);
        splashAd.loadAd();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != splashAd) {
            splashAd.destroyAd();
        }
    }

    public void loadCustomAd(String appId, String s1, String codeId, String s3) {
        AdGainInitAdapter.getInstance().initADN(appId, new InitCallback() {
            @Override
            public void onSuccess() {
                loadSplashAd(appId, codeId);
            }

            @Override
            public void onFail(int i, String s) {
                onError(i, s);
            }
        });
    }

    static class MyMsCustomSplashAd extends MsCustomSplashAd {
        private final SplashAd splashAd;

        public MyMsCustomSplashAd(MsCustomSplashAdapter msCustomSplashAdapter, SplashAd splashAd) {
            super(msCustomSplashAdapter);
            this.splashAd = splashAd;
        }

        @Override
        public void showAd(ViewGroup adContainer) {
            try {
                adContainer.removeAllViews();
                adContainer.addView(adView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                splashAd.showAd(adContainer);
            } catch (Exception e) {
            }
        }

    }


}
