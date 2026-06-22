package com.adgain.unified.controller.jiguang;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.jiguang.jgssp.ad.ADJgNativeAd;
import cn.jiguang.jgssp.ad.data.ADJgNativeExpressAdInfo;
import cn.jiguang.jgssp.ad.data.ADJgNativeFeedAdInfo;
import cn.jiguang.jgssp.ad.data.ADJgNativeAdInfo;
import cn.jiguang.jgssp.ad.entity.ADJgAdSize;
import cn.jiguang.jgssp.ad.entity.ADJgExtraParams;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgNativeAdListener;
import cn.jiguang.jgssp.util.ADJgAdUtil;
import cn.jiguang.jgssp.util.ADJgDisplayUtil;
import cn.jiguang.jgssp.util.ADJgViewUtil;

public class JiGuangNativeAdController implements UnifiedAdController {
    private final boolean expressMode;
    private ADJgNativeAd nativeAd;
    private final List<ADJgNativeAdInfo> adData = new ArrayList<>();

    public JiGuangNativeAdController(boolean expressMode) {
        this.expressMode = expressMode;
    }

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        JiGuangAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 JiGuang " + adTypeName() + ": " + placementId);

        int widthPixels = activity.getResources().getDisplayMetrics().widthPixels - ADJgDisplayUtil.dp2px(20);
        nativeAd = new ADJgNativeAd(activity);
        nativeAd.setSceneId(ADJgDemoConstant.NATIVE_AD_SCENE_ID);
        nativeAd.setOnlySupportPlatform(ADJgDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM);
        nativeAd.setLocalExtraParams(new ADJgExtraParams.Builder()
                .adSize(new ADJgAdSize(widthPixels, 0))
                .nativeAdMediaViewSize(new ADJgAdSize((int) (widthPixels - 24 * activity.getResources().getDisplayMetrics().density)))
                .nativeAdPlayWithMute(ADJgDemoConstant.NATIVE_AD_PLAY_WITH_MUTE)
                .build());
        nativeAd.setListener(new ADJgNativeAdListener() {
            @Override
            public void onRenderFailed(ADJgNativeAdInfo adInfo, ADJgError error) {
                callback.log("onRenderFailed: " + JiGuangAdControllerUtils.errorInfo(error));
                removeAdInfo(adInfo);
            }

            @Override
            public void onAdReceive(List<ADJgNativeAdInfo> adInfoList) {
                adData.clear();
                if (adInfoList != null) {
                    adData.addAll(adInfoList);
                }
                callback.log("onAdReceive: " + adData.size());
            }

            @Override
            public void onAdExpose(ADJgNativeAdInfo adInfo) {
                callback.log("onAdExpose");
            }

            @Override
            public void onAdClick(ADJgNativeAdInfo adInfo) {
                callback.log("onAdClick");
            }

            @Override
            public void onAdClose(ADJgNativeAdInfo adInfo) {
                callback.log("onAdClose");
                removeAdInfo(adInfo);
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailed(ADJgError error) {
                callback.log("onAdFailed: " + JiGuangAdControllerUtils.errorInfo(error));
            }
        });
        nativeAd.loadAd(placementId, ADJgDemoConstant.NATIVE_AD_COUNT);
    }

    @Override
    public boolean isReady() {
        return !adData.isEmpty();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            return;
        }
        ADJgNativeAdInfo adInfo = adData.remove(0);
        View adView = createAdView(activity, adInfo, adContainer, callback);
        if (adView == null) {
            callback.log("广告类型和当前入口不匹配");
            return;
        }
        JiGuangAdControllerUtils.attachView(adContainer, adView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 JiGuang " + adTypeName());
    }

    @Override
    public void destroy() {
        if (nativeAd != null) {
            nativeAd.release();
            nativeAd = null;
        }
        releaseAdData();
        adData.clear();
    }

    private View createAdView(Activity activity, ADJgNativeAdInfo adInfo, ViewGroup adContainer,
                              UnifiedAdLoadCallback callback) {
        if (adInfo == null || ADJgAdUtil.adInfoIsRelease(adInfo)) {
            return null;
        }
        if (expressMode) {
            if (!adInfo.isNativeExpress()) {
                callback.log("当前返回非模板广告");
                return null;
            }
            return createExpressView(activity, (ADJgNativeExpressAdInfo) adInfo, callback);
        }
        if (adInfo.isNativeExpress()) {
            callback.log("当前返回模板广告");
            return null;
        }
        return createSelfRenderView(activity, (ADJgNativeFeedAdInfo) adInfo, adContainer);
    }

    private View createExpressView(Activity activity, ADJgNativeExpressAdInfo adInfo,
                                   UnifiedAdLoadCallback callback) {
        FrameLayout root = new FrameLayout(activity);
        View nativeExpressAdView = adInfo.getNativeExpressAdView(root);
        ADJgViewUtil.addAdViewToAdContainer(root, nativeExpressAdView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        adInfo.render(root);
        callback.log("渲染 JiGuang 原生模板");
        return root;
    }

    private View createSelfRenderView(Activity activity, ADJgNativeFeedAdInfo adInfo, ViewGroup adContainer) {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(activity, 12), dp(activity, 12), dp(activity, 12), dp(activity, 12));
        root.setBackgroundColor(Color.WHITE);

        LinearLayout infoRow = new LinearLayout(activity);
        infoRow.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(infoRow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView iconView = new ImageView(activity);
        infoRow.addView(iconView, new LinearLayout.LayoutParams(dp(activity, 42), dp(activity, 42)));
        if (!TextUtils.isEmpty(adInfo.getIconUrl())) {
            Glide.with(activity).load(adInfo.getIconUrl()).into(iconView);
        }

        LinearLayout textColumn = new LinearLayout(activity);
        textColumn.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.leftMargin = dp(activity, 10);
        textParams.weight = 1;
        infoRow.addView(textColumn, textParams);

        TextView titleView = new TextView(activity);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(16);
        titleView.setText(adInfo.getTitle());
        textColumn.addView(titleView);

        TextView descView = new TextView(activity);
        descView.setTextColor(Color.DKGRAY);
        descView.setTextSize(13);
        descView.setMaxLines(2);
        descView.setText(adInfo.getDesc());
        textColumn.addView(descView);

        TextView ctaView = new TextView(activity);
        ctaView.setTextColor(Color.WHITE);
        ctaView.setTextSize(14);
        ctaView.setGravity(Gravity.CENTER);
        ctaView.setPadding(dp(activity, 12), dp(activity, 8), dp(activity, 12), dp(activity, 8));
        ctaView.setBackgroundColor(Color.parseColor("#1677FF"));
        ctaView.setText(TextUtils.isEmpty(adInfo.getCtaText()) ? "查看详情" : adInfo.getCtaText());
        infoRow.addView(ctaView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        FrameLayout mediaContainer = new FrameLayout(activity);
        LinearLayout.LayoutParams mediaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(activity, 200)
        );
        mediaParams.topMargin = dp(activity, 10);
        root.addView(mediaContainer, mediaParams);
        if (adInfo.hasMediaView()) {
            View mediaView = adInfo.getMediaView(mediaContainer);
            ADJgViewUtil.addAdViewToAdContainer(mediaContainer, mediaView);
        } else if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
            ImageView imageView = new ImageView(activity);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(activity).load(adInfo.getImageUrl()).into(imageView);
            mediaContainer.addView(imageView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
        }

        adInfo.registerViewForInteraction(root, mediaContainer, ctaView);
        return root;
    }

    private void removeAdInfo(ADJgNativeAdInfo adInfo) {
        if (adInfo == null) {
            return;
        }
        adData.remove(adInfo);
        adInfo.release();
    }

    private void releaseAdData() {
        for (ADJgNativeAdInfo adInfo : new ArrayList<>(adData)) {
            if (adInfo != null) {
                adInfo.release();
            }
        }
    }

    private int dp(Activity activity, int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }

    private String adTypeName() {
        return expressMode ? "原生模板" : "原生自渲染";
    }
}
