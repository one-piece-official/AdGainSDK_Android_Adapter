package com.meishu.sdkdemo.custom.banner;

import android.util.DisplayMetrics;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.meishu.sdk.core.ad.banner.BannerAdLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.platform.custom.banner.MsCustomBannerAdapter;
import com.meishu.sdkdemo.custom.CustomInitManager;

import java.util.List;

public class CustomBannerAdLoader extends MsCustomBannerAdapter {
    private static final String TAG = "CustomBannerAdLoader";
    private TTAdNative ttAdNative;
    public CustomBannerAdLoader(BannerAdLoader adLoader, SdkAdInfo sdkAdInfo) {
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
                CustomBannerAdLoader.this.onError(code,msg);
            }
        });

    }

    private void startLoadAd(String pid) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();

        float expressWidthDp;
        float expressHeightDp   = 0;    //自适应
        if (null != getAdLoader().getAcceptWidth() && 0 < getAdLoader().getAcceptWidth()) {
            expressWidthDp  = (float) getAdLoader().getAcceptWidth() / dm.density;
        } else {
            expressWidthDp  = dm.widthPixels / dm.density;
        }
        if (null != getAdLoader().getAcceptHeight() && 0 < getAdLoader().getAcceptHeight()) {
            expressHeightDp = (float) getAdLoader().getAcceptHeight() / dm.density;
        }else {
            expressHeightDp = 100;
        }

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(pid) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize((int)(expressWidthDp*dm.density), (int)(expressHeightDp*dm.density))
                .setExpressViewAcceptedSize(expressWidthDp, expressHeightDp)
                .build();
        ttAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int i, String s) {
                CustomBannerAdLoader.this.onError(i,s);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                if (list == null || list.isEmpty()) {
                    return;
                }
                final TTNativeExpressAd ad = list.get(0); // banner 只取一个

                final CustomBannerAd bannerAd = new CustomBannerAd(CustomBannerAdLoader.this,ad);


                ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int i) {
                        CustomBannerAdLoader.this.onAdClick(bannerAd);
                    }
                    @Override
                    public void onAdShow(View view, int i) {
                        CustomBannerAdLoader.this.onAdExposure(bannerAd);

                    }
                    @Override
                    public void onRenderFail(View view, String s, int i) {
                        CustomBannerAdLoader.this.onRenderFail(i,s);
                    }
                    @Override
                    public void onRenderSuccess(View view, float width, float height) {
                        CustomBannerAdLoader.this.onRenderSuccess(view,bannerAd);
                    }
                });
                ad.render();
            }
        });
    }

    @Override
    public void destory() {

    }
}
