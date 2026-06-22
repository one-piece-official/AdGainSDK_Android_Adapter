package com.meishu.sdkdemo.custom.recycler;

import android.util.DisplayMetrics;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.meishu.sdk.core.ad.recycler.RecyclerAdData;
import com.meishu.sdk.core.ad.recycler.RecyclerMixAdLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.core.utils.MsAdPatternType;
import com.meishu.sdk.platform.custom.recycler.MsCustomRecyclerAdapter;
import com.meishu.sdkdemo.custom.CustomInitManager;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerAdLoader extends MsCustomRecyclerAdapter {
    private final TTAdNative ttAdNative;

    public CustomRecyclerAdLoader(RecyclerMixAdLoader adLoader, SdkAdInfo sdkAdInfo) {
        super(adLoader, sdkAdInfo);
        this.ttAdNative = TTAdSdk.getAdManager().createAdNative(adLoader.getContext().getApplicationContext());
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
                CustomRecyclerAdLoader.this.onError(code,msg);
            }
        });
    }

    private void startLoadAd(String pid) {
        int fetchAdCount = getAdLoader().getFetchCount() <= 0 ? 1 : getAdLoader().getFetchCount();

        int adContentWidth = 1080;
        int adContentHeight = 1920;

        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        float expressViewWidth;
        float expressViewHeight;

        if (getAdLoader().getAccept_ad_width() !=null &&0 < getAdLoader().getAccept_ad_width()){
            expressViewWidth  = getAdLoader().getAccept_ad_width()/ dm.density;
        } else {
            expressViewWidth = dm.widthPixels / dm.density;
        }

        if (getAdLoader().getAccept_ad_height()!=null && 0 < getAdLoader().getAccept_ad_height()){
            expressViewHeight = getAdLoader().getAccept_ad_height()/ dm.density;
        }else {
            expressViewHeight = 0; // 高设为 0，可以自适应
        }
        //feed广告请求类型参数
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(pid)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(adContentWidth, adContentHeight)
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                .setIsAutoPlay(getAdLoader().getIsVideoAutoPlay())
                .setAdCount(fetchAdCount) // 有时候获取到的个数会小于 adCount 数
                .build();

        //如果是混合模式
        int adPatternType = getAdLoader().getAdPatternType();
        boolean isPreRender=false;
        if (adPatternType== MsAdPatternType.MIX_RENDER){
            int drawing = getSdkAdInfo().getDrawing();
            if (drawing == 1){
                //自渲染
                isPreRender = false;
            }else if (drawing == 2){
                isPreRender=true;
            }else {
//                new CSJPlatformError("信息流模式不支持", -1, CSJTTAdNativeWrapper.this.getSdkAdInfo()).post(CSJTTAdNativeWrapper.this.loadListener);
                return;
            }
        }else if (adPatternType == MsAdPatternType.PRE_RENDER){
            isPreRender = true;
        }
        if (isPreRender) {
            loadExpressAd(adSlot);
        } else {
            loadFeedAd(adSlot);
        }
    }

    private void loadFeedAd(AdSlot adSlot) {
        ttAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {
                CustomRecyclerAdLoader.this.onError(i,s);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                if (list != null) {
                    List<RecyclerAdData> adDatas = new ArrayList<>();
                    for (TTFeedAd ttFeedAd : list) {
                        CustomFeedAd customFeedAd = new CustomFeedAd(CustomRecyclerAdLoader.this,ttFeedAd);
                        adDatas.add(customFeedAd);
                    }
                    CustomRecyclerAdLoader.this.onFeedAdLoad(adDatas);
                }
            }
        });
    }

    private void loadExpressAd(AdSlot adSlot) {
        ttAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int i, String s) {
                CustomRecyclerAdLoader.this.onError(i,s);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                if (list != null ) {
                    List<RecyclerAdData> recyclerAdDatas = new ArrayList<>();
                    for (TTNativeExpressAd ttNativeExpressAd : list) {
                        CustomExpressAd adData = new CustomExpressAd(CustomRecyclerAdLoader.this, ttNativeExpressAd);
                        ttNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                            @Override
                            public void onAdClicked(View view, int i) {
                                CustomRecyclerAdLoader.this.onExpressAdClicked(adData);
                            }

                            @Override
                            public void onAdShow(View view, int i) {
                                CustomRecyclerAdLoader.this.onExpressAdExposure(adData);
                            }

                            @Override
                            public void onRenderFail(View view, String s, int i) {
                                CustomRecyclerAdLoader.this.onRenderFail(i,s);
                            }

                            @Override
                            public void onRenderSuccess(View view, float v, float v1) {
                                CustomRecyclerAdLoader.this.onExpressAdRenderSuccess(adData);
                            }
                        });
                        ttNativeExpressAd.render();
                        recyclerAdDatas.add(adData);
                    }

                    CustomRecyclerAdLoader.this.onNativeExpressAdLoad(recyclerAdDatas);

                }
            }
        });
    }
}
