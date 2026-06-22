package com.meishu.sdkdemo.custom.splash;

import android.util.DisplayMetrics;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.meishu.sdk.core.ad.splash.SplashAdLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.platform.custom.splash.MsCustomSplashAdapter;
import com.meishu.sdkdemo.custom.CustomInitManager;
import com.meishu.sdkdemo.utils.DimensionUtils;

public class CustomSplashAdLoader extends MsCustomSplashAdapter {
    private TTAdNative ttAdNative;
    private TTAdNative.CSJSplashAdListener ttAdListener;

    public CustomSplashAdLoader(SplashAdLoader adLoader, SdkAdInfo sdkAdInfo) {
        super(adLoader, sdkAdInfo);
        this.ttAdNative = TTAdSdk.getAdManager().createAdNative(adLoader.getContext().getApplicationContext());
    }

    @Override
    public void loadCustomAd(String app_id,String app_key, String pid,String custom_ext) {
        Toast.makeText(context,"customExt="+custom_ext,Toast.LENGTH_SHORT).show();
        CustomInitManager.getInstance().initSdk(context, app_id, new CustomInitManager.InitCallback() {
            @Override
            public void onSuccess() {
                startLoadAd(pid);
            }

            @Override
            public void onError(int code, String msg) {
                CustomSplashAdLoader.this.onError(code,msg);
            }
        });


    }

    private void startLoadAd(String pid) {
        int adContentWidth = 1080;
        int adContentHeight = 1920;
        if (getAdLoader().getAccept_ad_width() !=null &&0 < getAdLoader().getAccept_ad_width() && getAdLoader().getAccept_ad_height()!=null && 0 < getAdLoader().getAccept_ad_height()) {
            adContentWidth  = getAdLoader().getAccept_ad_width();
            adContentHeight = getAdLoader().getAccept_ad_height();
        }else {
            try {
                DisplayMetrics displayMetrics = this.adLoader.getContext().getResources().getDisplayMetrics();
                if (0 < displayMetrics.widthPixels && 0 < displayMetrics.heightPixels) {
                    adContentWidth  = displayMetrics.widthPixels;
                    adContentHeight = displayMetrics.heightPixels;
                }
            } catch (Exception e) {}
        }

        //step4:创建广告请求参数AdSlot, 具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(pid) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(adContentWidth, adContentHeight)
                .setExpressViewAcceptedSize(DimensionUtils.px2dip(this.adLoader.getContext(),adContentWidth),DimensionUtils.px2dip(this.adLoader.getContext(),adContentHeight))
                .build();

        ttAdNative.loadSplashAd(adSlot, new TTAdNative.CSJSplashAdListener() {
            @Override
            public void onSplashLoadSuccess(CSJSplashAd csjSplashAd) {

            }

            @Override
            public void onSplashLoadFail(CSJAdError csjAdError) {
                CustomSplashAdLoader.this.onError(csjAdError.getCode(),csjAdError.getMsg());
            }

            @Override
            public void onSplashRenderSuccess(CSJSplashAd ttSplashAd) {
                if (ttSplashAd != null) {
                    CustomSplashAd csjCustomSplashAd = new CustomSplashAd(CustomSplashAdLoader.this);


                    ttSplashAd.setSplashAdListener(new CSJSplashAd.SplashAdListener() {
                        @Override
                        public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                            CustomSplashAdLoader.this.onAdExposure(csjCustomSplashAd);
                        }

                        @Override
                        public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                            CustomSplashAdLoader.this.onAdClick(csjCustomSplashAd);
                        }

                        @Override
                        public void onSplashAdClose(CSJSplashAd csjSplashAd, int i) {
                            CustomSplashAdLoader.this.onAdSkip(csjCustomSplashAd);
                        }
                    });
                    CustomSplashAdLoader.this.onRenderSuccess(ttSplashAd.getSplashView(),csjCustomSplashAd);

                }
            }

            @Override
            public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {

            }



        }, 3500);
    }
}
