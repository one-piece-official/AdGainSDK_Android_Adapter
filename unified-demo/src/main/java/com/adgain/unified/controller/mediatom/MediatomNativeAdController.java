package com.adgain.unified.controller.mediatom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yd.saas.api.AdParams;
import com.yd.saas.api.mixNative.NativeAd;
import com.yd.saas.api.mixNative.NativeAdConst;
import com.yd.saas.api.mixNative.NativeAdView;
import com.yd.saas.api.mixNative.NativeEventListener;
import com.yd.saas.api.mixNative.NativeLoadListener;
import com.yd.saas.api.mixNative.NativeMaterial;
import com.yd.saas.api.mixNative.NativePrepareInfo;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.config.utils.DeviceUtil;
import com.yd.saas.ydsdk.api.YdSDK;

import java.util.List;

public class MediatomNativeAdController implements UnifiedAdController {
    private final boolean expressMode;
    private NativeAd nativeAd;
    private NativeAdView nativeAdView;
    private Activity activity;
    private int adContainerW;
    private int adContainerH;
    private int imageW;
    private int imageH;

    public MediatomNativeAdController(boolean expressMode) {
        this.expressMode = expressMode;
    }

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        this.activity = activity;
        MediatomAdControllerUtils.resetContainer(adContainer);
        setupSize();
        callback.log("开始加载 Mediatom " + adTypeName() + ": " + placementId);

        int width = DeviceUtil.px2dip(adContainerW);
        int height = DeviceUtil.px2dip(adContainerH);
        AdParams params = new AdParams.Builder(placementId)
                .setExpressHeight(height)
                .setExpressWidth(width)
                .setExpressAutoHeight()
                .setExpressFullWidth()
                .setImageAcceptedHeight(imageH)
                .setImageAcceptedWidth(imageW)
                .build();
        YdSDK.loadMixNative(activity, params, new NativeLoadListener() {
            @Override
            public void onNativeAdLoaded(NativeAd ad) {
                nativeAd = ad;
                callback.log("onNativeAdLoaded, isNativeExpress=" + ad.isNativeExpress());
            }

            @Override
            public void onAdFailed(YdError error) {
                callback.log("onAdFailed: " + MediatomAdControllerUtils.errorInfo(error));
            }
        });
    }

    @Override
    public boolean isReady() {
        return nativeAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        if (expressMode != nativeAd.isNativeExpress()) {
            callback.log("广告类型和当前入口不匹配, isNativeExpress=" + nativeAd.isNativeExpress());
            return;
        }
        nativeAdView = createAdView(activity, adContainer, callback);
        MediatomAdControllerUtils.attachView(adContainer, nativeAdView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 Mediatom " + adTypeName());
    }

    @Override
    public void destroy() {
        if (nativeAdView != null) {
            nativeAdView.removeAllViews();
            nativeAdView = null;
        }
        nativeAd = null;
        activity = null;
    }

    private NativeAdView createAdView(Activity activity, ViewGroup container, UnifiedAdLoadCallback callback) {
        setNativeEvent(container, nativeAd, callback);

        NativeAdView adView = new NativeAdView(activity);
        if (nativeAd.isNativeExpress()) {
            View mediaView = nativeAd.getAdMaterial().getAdMediaView();
            adView.addView(mediaView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            nativeAd.renderAdContainer(adView, null);
            NativePrepareInfo prepareInfo = new NativePrepareInfo();
            prepareInfo.setActivity(activity);
            nativeAd.prepare(prepareInfo);
            return adView;
        }

        View contentView = createSelfRenderContentView(activity);
        adView.addView(contentView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        nativeAd.renderAdContainer(adView, contentView);
        bindSelfRenderView(activity, container, contentView, nativeAd);
        return adView;
    }

    private void bindSelfRenderView(Activity activity, ViewGroup container, View adView, NativeAd nativeAd) {
        TextView titleView = adView.findViewWithTag("title");
        TextView descView = adView.findViewWithTag("desc");
        ImageView imageView = adView.findViewWithTag("image");
        FrameLayout mediaContainer = adView.findViewWithTag("media");
        ImageView adLogo = adView.findViewWithTag("logo");
        View closeView = adView.findViewWithTag("close");
        TextView actionView = adView.findViewWithTag("action");
        closeView.setOnClickListener(v -> container.removeAllViews());

        NativeMaterial material = nativeAd.getAdMaterial();
        titleView.setText(material.getTitle());
        descView.setText(material.getDescription());
        actionView.setText(TextUtils.isEmpty(material.getCallToAction()) ? "查看详情" : material.getCallToAction());

        if (material.getAdType() == NativeAdConst.AD_TYPE_VIDEO && material.getAdMediaView() != null) {
            imageView.setVisibility(View.GONE);
            mediaContainer.addView(material.getAdMediaView(), new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        } else {
            String imageUrl = material.getMainImageUrl();
            if (TextUtils.isEmpty(imageUrl)) {
                imageUrl = firstNonEmpty(material.getImageUrlList());
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(activity)
                        .load(imageUrl)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource,
                                                        @Nullable Transition<? super Drawable> transition) {
                                imageView.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        }

        if (material.getAdLogo() != null) {
            adLogo.setImageBitmap(material.getAdLogo());
        } else if (!TextUtils.isEmpty(material.getAdLogoUrl())) {
            Glide.with(activity.getApplicationContext()).load(material.getAdLogoUrl()).into(adLogo);
        }

        NativePrepareInfo prepareInfo = new NativePrepareInfo();
        prepareInfo.setActivity(activity);
        prepareInfo.setCloseView(closeView);
        prepareInfo.setClickView(adView);
        prepareInfo.setCtaView(actionView);
        prepareInfo.setImageView(imageView);
        nativeAd.prepare(prepareInfo);
    }

    private View createSelfRenderContentView(Activity activity) {
        FrameLayout root = new FrameLayout(activity);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(dp(activity, 12), dp(activity, 12), dp(activity, 12), dp(activity, 12));

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        root.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView titleView = new TextView(activity);
        titleView.setTag("title");
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(16);
        titleView.setMaxLines(1);
        content.addView(titleView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView descView = new TextView(activity);
        descView.setTag("desc");
        descView.setTextColor(Color.DKGRAY);
        descView.setTextSize(13);
        descView.setMaxLines(2);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        descParams.topMargin = dp(activity, 4);
        content.addView(descView, descParams);

        FrameLayout mediaContainer = new FrameLayout(activity);
        mediaContainer.setTag("media");
        LinearLayout.LayoutParams mediaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(activity, 200)
        );
        mediaParams.topMargin = dp(activity, 10);
        content.addView(mediaContainer, mediaParams);

        ImageView imageView = new ImageView(activity);
        imageView.setTag("image");
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mediaContainer.addView(imageView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        TextView actionView = new TextView(activity);
        actionView.setTag("action");
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(Color.WHITE);
        actionView.setTextSize(14);
        actionView.setBackgroundColor(Color.parseColor("#1677FF"));
        actionView.setPadding(dp(activity, 16), dp(activity, 8), dp(activity, 16), dp(activity, 8));
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        actionParams.topMargin = dp(activity, 10);
        actionParams.gravity = Gravity.CENTER_HORIZONTAL;
        content.addView(actionView, actionParams);

        ImageView closeView = new ImageView(activity);
        closeView.setTag("close");
        closeView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(dp(activity, 22), dp(activity, 22));
        closeParams.gravity = Gravity.TOP | Gravity.RIGHT;
        closeParams.topMargin = dp(activity, 16);
        closeParams.rightMargin = dp(activity, 16);
        root.addView(closeView, closeParams);

        ImageView logoView = new ImageView(activity);
        logoView.setTag("logo");
        logoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(dp(activity, 48), dp(activity, 18));
        logoParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        logoParams.rightMargin = dp(activity, 16);
        logoParams.bottomMargin = dp(activity, 82);
        root.addView(logoView, logoParams);

        return root;
    }

    private void setNativeEvent(ViewGroup container, NativeAd nativeAd, UnifiedAdLoadCallback callback) {
        nativeAd.setNativeEventListener(new NativeEventListener() {
            @Override
            public void onAdImpressed(NativeAdView adView) {
                callback.log("onAdImpressed");
            }

            @Override
            public void onAdClicked(NativeAdView adView) {
                callback.log("onAdClicked");
            }

            @Override
            public void onAdClose(NativeAdView adView) {
                callback.log("onAdClose");
                container.removeAllViews();
            }

            @Override
            public void onAdVideoStart(NativeAdView adView) {
                callback.log("onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(NativeAdView adView) {
                callback.log("onAdVideoEnd");
            }

            @Override
            public void onAdVideoProgress(NativeAdView adView, long progress) {
                callback.log("onAdVideoProgress: " + progress);
            }

            @Override
            public void onAdFailed(NativeAdView adView, YdError error) {
                callback.log("onAdFailed: " + MediatomAdControllerUtils.errorInfo(error));
            }
        });
    }

    private void setupSize() {
        adContainerW = DeviceUtil.getMobileWidth() - DeviceUtil.dip2px(12) * 2;
        adContainerH = (int) (adContainerW * 0.28f);
        imageW = Math.round(adContainerW * 0.4f);
        imageH = adContainerH;
    }

    private int dp(Activity activity, int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }

    private String firstNonEmpty(List<String> values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    private String adTypeName() {
        return expressMode ? "原生模板" : "原生自渲染";
    }
}
