package com.adgain.unified.controller.taku;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATShowConfig;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventExListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.api.ATNativeView;
import com.anythink.nativead.api.NativeAd;
import com.anythink.nativead.api.NativeAdInteractionType;
import com.anythink.sdk.demo.R;
import com.test.ad.demo.AdConst;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.util.SDKUtil;
import com.test.ad.demo.util.SelfRenderViewUtil;

import java.util.HashMap;
import java.util.Map;

public class TakuNativeAdController implements UnifiedAdController {
    private final boolean expressMode;
    private ATNative nativeLoader;
    private NativeAd nativeAd;
    private ATNativeView nativeView;
    private View selfRenderView;
    private String placementId;

    public TakuNativeAdController(boolean expressMode) {
        this.expressMode = expressMode;
    }

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        TakuAdControllerUtils.resetContainer(adContainer);
        this.placementId = placementId;
        SDKUtil.initSDK(activity.getApplicationContext());
        callback.log("开始加载 Taku " + adTypeName() + ": " + placementId);

        nativeLoader = new ATNative(activity, placementId, new ATNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                callback.log("onNativeAdLoaded");
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                callback.log("onNativeAdLoadFail: " + errorInfo(adError));
            }
        });
        nativeLoader.setAdSourceStatusListener(new BaseActivity.ATAdSourceStatusListenerImpl());

        int width = activity.getResources().getDisplayMetrics().widthPixels;
        Map<String, Object> localExtra = new HashMap<>();
        localExtra.put(ATAdConst.KEY.AD_WIDTH, width);
        localExtra.put(ATAdConst.KEY.AD_HEIGHT, width * 3 / 4);
        nativeLoader.setLocalExtra(localExtra);
        nativeLoader.makeAdRequest();
    }

    @Override
    public boolean isReady() {
        return nativeLoader != null
                && nativeLoader.checkAdStatus() != null
                && nativeLoader.checkAdStatus().isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }

        ATNative.entryAdScenario(placementId, AdConst.SCENARIO_ID.NATIVE_AD_SCENARIO);
        NativeAd nextNativeAd = nativeLoader.getNativeAd(showConfig());
        if (nextNativeAd == null) {
            callback.log("this placement no cache!");
            return;
        }

        if (nativeAd != null) {
            nativeAd.destory();
        }
        nativeAd = nextNativeAd;
        bindNativeAd(activity, adContainer, callback);

        nativeView = new ATNativeView(activity);
        nativeView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        ATNativePrepareInfo prepareInfo = new ATNativePrepareExInfo();
        if (nativeAd.isNativeExpress()) {
            nativeAd.renderAdContainer(nativeView, null);
        } else {
            selfRenderView = LayoutInflater.from(activity).inflate(R.layout.taku_layout_native_self, nativeView, false);
            SelfRenderViewUtil.bindSelfRenderView(activity, nativeAd.getAdMaterial(), selfRenderView, prepareInfo);
            nativeAd.renderAdContainer(nativeView, selfRenderView);
        }
        nativeAd.prepare(nativeView, prepareInfo);
        TakuAdControllerUtils.attachView(adContainer, nativeView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 Taku " + adTypeName() + ", isExpressAd=" + nativeAd.isNativeExpress());
    }

    @Override
    public void destroy() {
        if (nativeAd != null) {
            nativeAd.destory();
            nativeAd = null;
        }
        if (nativeLoader != null) {
            nativeLoader.setAdListener(null);
            nativeLoader.setAdSourceStatusListener(null);
            nativeLoader.setAdMultipleLoadedListener(null);
            nativeLoader = null;
        }
        nativeView = null;
        selfRenderView = null;
    }

    private void bindNativeAd(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        nativeAd.setNativeEventListener(new ATNativeEventExListener() {
            @Override
            public void onDeeplinkCallback(ATNativeAdView view, ATAdInfo adInfo, boolean isSuccess) {
                callback.log("onDeeplinkCallback: " + isSuccess);
            }

            @Override
            public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                callback.log("onAdImpressed");
            }

            @Override
            public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                callback.log("onAdClicked");
            }

            @Override
            public void onAdVideoStart(ATNativeAdView view) {
                callback.log("onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(ATNativeAdView view) {
                callback.log("onAdVideoEnd");
            }

            @Override
            public void onAdVideoProgress(ATNativeAdView view, int progress) {
                callback.log("onAdVideoProgress: " + progress);
            }
        });
        nativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
            @Override
            public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                callback.log("onAdCloseButtonClick");
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }
        });

        if (!nativeAd.isNativeExpress()
                && nativeAd.getAdMaterial() != null
                && nativeAd.getAdMaterial().getNativeAdInteractionType() == NativeAdInteractionType.APP_DOWNLOAD_TYPE) {
            callback.log("原生自渲染下载类广告");
        }
    }

    private ATShowConfig showConfig() {
        return TakuAdControllerUtils.showConfig(
                AdConst.SCENARIO_ID.NATIVE_AD_SCENARIO,
                AdConst.SHOW_CUSTOM_EXT.NATIVE_AD_SHOW_CUSTOM_EXT
        );
    }

    private String adTypeName() {
        return expressMode ? "原生模板" : "原生自渲染";
    }

    private String errorInfo(AdError adError) {
        return adError == null ? "" : adError.getFullErrorInfo();
    }
}
