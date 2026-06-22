package com.adgain.unified.controller.gromore;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.gromore.adapter.adgain.GMBiddingUtil;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.UIUtils;

import java.util.List;

public class GroMoreBannerAdController implements UnifiedAdController {
    private TTNativeExpressAd bannerAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        GroMoreAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 GroMore Banner: " + placementId);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(placementId)
                .setImageAcceptedSize(UIUtils.dp2px(activity, 320f), UIUtils.dp2px(activity, 150f))
                .build();
        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(activity);
        if (adNativeLoader == null) {
            callback.log("TTAdNative 创建失败");
            return;
        }
        adNativeLoader.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                callback.log("onError: " + code + ", " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                if (list != null && !list.isEmpty()) {
                    bannerAd = list.get(0);
                    callback.log("onNativeExpressAdLoad: " + list.size());
                } else {
                    callback.log("onNativeExpressAdLoad: empty");
                }
            }
        });
    }

    @Override
    public boolean isReady() {
        return bannerAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            return;
        }
        bannerAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                callback.log("onAdClicked");
            }

            @Override
            public void onAdShow(View view, int type) {
                callback.log("onAdShow");
                GMBiddingUtil.gmNotifyLoss(bannerAd);
            }

            @Override
            public void onRenderFail(View view, String message, int code) {
                callback.log("onRenderFail: " + code + ", " + message);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                callback.log("onRenderSuccess: " + width + "x" + height);
            }
        });
        bannerAd.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                callback.log("onDislikeSelected: " + value);
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
            }
        });
        bannerAd.uploadDislikeEvent("mediation_dislike_event");
        View bannerView = bannerAd.getExpressAdView();
        if (bannerView == null) {
            callback.log("Banner view 为空");
            return;
        }
        GroMoreAdControllerUtils.attachView(adContainer, bannerView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 GroMore Banner");
    }

    @Override
    public void destroy() {
        if (bannerAd != null) {
            bannerAd.destroy();
            bannerAd = null;
        }
    }
}
