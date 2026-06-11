package com.meishu.sdkdemo.custom.reward;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.ad.reward.RewardVideoLoader;
import com.meishu.sdk.core.domain.SdkAdInfo;
import com.meishu.sdk.platform.custom.reward.MsCustomRewardAdapter;
import com.meishu.sdkdemo.custom.CustomInitManager;

public class CustomRewardAdLoader extends MsCustomRewardAdapter {
    private TTAdNative mTTAdNative;
    private CustomRewardAd customRewardAd;

    public CustomRewardAdLoader(RewardVideoLoader adLoader, SdkAdInfo sdkAdInfo) {
        super(adLoader, sdkAdInfo);
        this.mTTAdNative = TTAdSdk.getAdManager().createAdNative(adLoader.getContext());
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
                CustomRewardAdLoader.this.onError(code,msg);
            }
        });
    }

    private void startLoadAd(String pid) {
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int orientation = TTAdConstant.VERTICAL;
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                orientation = TTAdConstant.VERTICAL;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                orientation = TTAdConstant.HORIZONTAL;
                break;
        }

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
                .setSupportDeepLink(true)
                .setImageAcceptedSize(adContentWidth, adContentHeight)
                //模板渲染广告必须加这行
                .setExpressViewAcceptedSize(adContentWidth, adContentHeight)
                //必传参数，表来标识应用侧唯一用户；若非服务器回调模式或不需sdk透传
                //可设置为空字符串
                .setUserID(AdSdk.adConfig().userId())
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                CustomRewardAdLoader.this.onError(i,s);
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                customRewardAd = new CustomRewardAd(CustomRewardAdLoader.this,ttRewardVideoAd);


                ttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        CustomRewardAdLoader.this.onAdExposure(customRewardAd);
                        CustomRewardAdLoader.this.onVideoStart(customRewardAd);
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        CustomRewardAdLoader.this.onAdClick(customRewardAd);
                    }

                    @Override
                    public void onAdClose() {
                        CustomRewardAdLoader.this.onAdClosed(customRewardAd);
                    }

                    @Override
                    public void onVideoComplete() {
                        CustomRewardAdLoader.this.onVideoCompleted(customRewardAd);
                    }

                    @Override
                    public void onVideoError() {
                        CustomRewardAdLoader.this.onVideoError(customRewardAd);
                    }

                    @Override
                    public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
                        CustomRewardAdLoader.this.onReward(customRewardAd,null);
                    }

                    @Override
                    public void onRewardArrived(boolean b, int i, Bundle bundle) {

                    }

                    @Override
                    public void onSkippedVideo() {
                        CustomRewardAdLoader.this.onSkippedVideo(customRewardAd);
                    }
                });

                CustomRewardAdLoader.this.onRenderSuccess(customRewardAd);
            }

            @Override
            public void onRewardVideoCached() {
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                CustomRewardAdLoader.this.onVideoCached(customRewardAd);
            }
        });
    }
}
