package com.adgain.unified.controller.gromore;

import static com.union_test.toutiao.utils.UIUtils.dp2px;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationExpressRenderListener;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationNativeManager;
import com.gromore.adapter.adgain.GMBiddingUtil;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.mediation.java.utils.FeedAdUtils;
import com.union_test.toutiao.utils.UIUtils;

import java.util.List;

public class GroMoreNativeAdController implements UnifiedAdController {
    private final boolean expressMode;
    private TTFeedAd feedAd;

    public GroMoreNativeAdController(boolean expressMode) {
        this.expressMode = expressMode;
    }

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        GroMoreAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 GroMore " + adTypeName() + ": " + placementId);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(placementId)
                .setImageAcceptedSize(UIUtils.getScreenWidthInPx(activity), dp2px(activity, 340))
                .setAdCount(1)
                .setMediationAdSlot(new GroMoreMediationAdSlot())
                .build();
        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(activity);
        if (adNativeLoader == null) {
            callback.log("TTAdNative 创建失败");
            return;
        }
        adNativeLoader.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                callback.log("onError: " + code + ", " + message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                if (list != null && !list.isEmpty()) {
                    feedAd = list.get(0);
                    callback.log("onFeedAdLoad: " + list.size());
                } else {
                    callback.log("onFeedAdLoad: empty");
                }
            }
        });
    }

    @Override
    public boolean isReady() {
        return feedAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        feedAd.uploadDislikeEvent("mediation_dislike_event");
        MediationNativeManager manager = feedAd.getMediationManager();
        if (manager == null) {
            callback.log("MediationNativeManager 为空");
            return;
        }
        callback.log("展示 GroMore " + adTypeName() + ", isExpress=" + manager.isExpress());
        if (manager.isExpress()) {
            feedAd.setExpressRenderListener(new MediationExpressRenderListener() {
                @Override
                public void onAdShow() {
                    callback.log("onAdShow");
                    GMBiddingUtil.gmNotifyLoss(feedAd);
                }

                @Override
                public void onRenderFail(View view, String message, int code) {
                    callback.log("onRenderFail: " + code + ", " + message);
                }

                @Override
                public void onAdClick() {
                    callback.log("onAdClick");
                }

                @Override
                public void onRenderSuccess(View view, float width, float height, boolean isExpress) {
                    callback.log("onRenderSuccess: " + width + "x" + height);
                    View expressFeedView = feedAd.getAdView();
                    if (expressFeedView != null) {
                        GroMoreAdControllerUtils.attachView(adContainer, expressFeedView, new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                        ));
                    }
                }
            });
            feedAd.render();
        } else {
            View feedView = FeedAdUtils.getFeedAdFromFeedInfo(feedAd, activity, null, new TTNativeAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                    callback.log("onAdClicked");
                }

                @Override
                public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                    callback.log("onAdCreativeClick");
                }

                @Override
                public void onAdShow(TTNativeAd ttNativeAd) {
                    callback.log("onAdShow");
                }
            });
            if (feedView == null) {
                callback.log("自渲染 Feed view 为空");
                return;
            }
            GroMoreAdControllerUtils.attachView(adContainer, feedView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            ));
        }
    }

    @Override
    public void destroy() {
        if (feedAd != null) {
            feedAd.destroy();
            feedAd = null;
        }
    }

    private String adTypeName() {
        return expressMode ? "原生模板" : "原生自渲染";
    }
}
