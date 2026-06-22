package com.adgain.unified.controller.beizi;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.amps.demo.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdExpressListener;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeAd;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeAdError;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeLoadEventListener;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedAdEventListener;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedNativeItem;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedPattern;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedVideoListener;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedMediaViewStub;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedRootContainer;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedView;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;
import xyz.adscope.amps.tool.util.AMPSScreenUtil;

public class BeiziNativeAdController implements UnifiedAdController {
    private final boolean expressMode;
    private AMPSUnifiedNativeAd nativeAd;
    private List<AMPSUnifiedNativeItem> nativeItems;
    private final List<AMPSUnifiedNativeItem> usedItems = new ArrayList<>();

    public BeiziNativeAdController(boolean expressMode) {
        this.expressMode = expressMode;
    }

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        BeiziAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 Beizi " + adTypeName() + ": " + placementId);

        int width = AMPSScreenUtil.getScreenWidth(activity);
        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(placementId)
                .setTimeOut(5000)
                .setWidth(width)
                .setHeight(0)
                .setAdCount(expressMode ? 1 : 3)
                .build();
        nativeAd = new AMPSUnifiedNativeAd(activity, parameter, new AMPSUnifiedNativeLoadEventListener() {
            @Override
            public void onAmpsAdLoad(List<AMPSUnifiedNativeItem> items) {
                nativeItems = items;
                int count = items == null ? 0 : items.size();
                callback.log("onAmpsAdLoad: " + count);
            }

            @Override
            public void onAmpsAdFailed(AMPSError error) {
                callback.log("onAmpsAdFailed: " + BeiziAdControllerUtils.errorInfo(error));
            }
        });
        nativeAd.loadAd();
    }

    @Override
    public boolean isReady() {
        return nativeItems != null && !nativeItems.isEmpty();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }

        AMPSUnifiedNativeItem item = nativeItems.remove(0);
        if (item == null || !item.isValid()) {
            callback.log("广告无效");
            return;
        }
        usedItems.add(item);
        if (expressMode || item.isExpressAd()) {
            renderExpress(item, adContainer, callback);
        } else {
            renderSelf(activity, item, adContainer, callback);
        }
    }

    @Override
    public void destroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
        destroyItems(nativeItems);
        destroyItems(usedItems);
        nativeItems = null;
    }

    private void renderExpress(AMPSUnifiedNativeItem item, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        item.setNativeAdExpressListener(new AMPSNativeAdExpressListener() {
            @Override
            public void onAdShow() {
                callback.log("onAdShow");
            }

            @Override
            public void onAdClicked() {
                callback.log("onAdClicked");
            }

            @Override
            public void onAdClosed(View view) {
                callback.log("onAdClosed");
                adContainer.removeView(view);
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                callback.log("onRenderFail: " + code + " " + msg);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                BeiziAdControllerUtils.attachView(adContainer, view, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                ));
                callback.log("onRenderSuccess: " + width + "x" + height);
            }
        });
        callback.log("渲染 Beizi 原生模板");
        item.render();
        View expressView = item.getNativeExpressAdView();
        if (expressView != null) {
            BeiziAdControllerUtils.attachView(adContainer, expressView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            ));
        }
    }

    private void renderSelf(Activity activity, AMPSUnifiedNativeItem item, ViewGroup adContainer,
                            UnifiedAdLoadCallback callback) {
        View itemView = createSelfRenderView(activity, item, callback);
        if (itemView == null) {
            callback.log("不支持的原生自渲染样式: " + item.getAdPattern());
            return;
        }
        BeiziAdControllerUtils.attachView(adContainer, itemView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 Beizi 原生自渲染: " + item.getAdPattern());
    }

    private View createSelfRenderView(Activity activity, AMPSUnifiedNativeItem item,
                                      UnifiedAdLoadCallback callback) {
        AMPSUnifiedPattern pattern = item.getAdPattern();
        if (AMPSUnifiedPattern.AD_PATTERN_VIDEO.equals(pattern)) {
            return inflateVideoView(activity, item, callback);
        }
        if (AMPSUnifiedPattern.AD_PATTERN_3_IMAGES.equals(pattern)) {
            return inflateGroupImage(activity, item, callback);
        }
        if (AMPSUnifiedPattern.AD_PATTERN_TEXT_IMAGE.equals(pattern)) {
            return inflateImageText(activity, item, callback);
        }
        return null;
    }

    private View inflateImageText(Activity activity, AMPSUnifiedNativeItem item, UnifiedAdLoadCallback callback) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.beizi_native_unified_item_image_text, null);
        AMPSUnifiedRootContainer root = itemView.findViewById(R.id.ad_unified_container);
        FrameLayout imageContainer = itemView.findViewById(R.id.ad_main_image_container);
        bindText(item, itemView);
        addMainImage(item, imageContainer);
        bindSelfEvent(activity, item, root, imageContainer, itemView.findViewById(R.id.ad_action_rl), callback);
        return itemView;
    }

    private View inflateGroupImage(Activity activity, AMPSUnifiedNativeItem item, UnifiedAdLoadCallback callback) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.beizi_native_unified_item_group_image, null);
        AMPSUnifiedRootContainer root = itemView.findViewById(R.id.ad_unified_container);
        LinearLayout imageContainer = itemView.findViewById(R.id.ad_main_image_container);
        bindText(item, itemView);
        List<AMPSUnifiedView> imageViews = item.getMainImageViews();
        if (item.isViewObject() && imageViews != null) {
            for (AMPSUnifiedView unifiedView : imageViews) {
                View imageView = unifiedView == null ? null : unifiedView.getView();
                if (imageView != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 160);
                    params.leftMargin = 20;
                    params.weight = 1;
                    imageContainer.addView(imageView, params);
                }
            }
        } else {
            List<String> imageUrls = item.getImagesUrl();
            if (imageUrls != null) {
                for (String imageUrl : imageUrls) {
                    addUrlImage(activity, imageContainer, imageUrl);
                }
            }
        }
        bindSelfEvent(activity, item, root, imageContainer, itemView.findViewById(R.id.ad_action_rl), callback);
        return itemView;
    }

    private View inflateVideoView(Activity activity, AMPSUnifiedNativeItem item, UnifiedAdLoadCallback callback) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.beizi_native_unified_item_video, null);
        AMPSUnifiedRootContainer root = itemView.findViewById(R.id.ad_unified_container);
        AMPSUnifiedMediaViewStub videoStub = itemView.findViewById(R.id.ad_main_video);
        bindText(item, itemView);
        bindSelfEvent(activity, item, root, null, itemView.findViewById(R.id.ad_action_rl), callback);
        item.bindAdToMediaView(activity, videoStub, new AMPSUnifiedVideoListener() {
            @Override
            public void onVideoInit() {
                callback.log("onVideoInit");
            }

            @Override
            public void onVideoLoading() {
                callback.log("onVideoLoading");
            }

            @Override
            public void onVideoReady() {
                callback.log("onVideoReady");
            }

            @Override
            public void onVideoLoaded(int duration) {
                callback.log("onVideoLoaded: " + duration);
            }

            @Override
            public void onVideoStart() {
                callback.log("onVideoStart");
            }

            @Override
            public void onVideoPause() {
                callback.log("onVideoPause");
            }

            @Override
            public void onVideoResume() {
                callback.log("onVideoResume");
            }

            @Override
            public void onVideoCompleted() {
                callback.log("onVideoCompleted");
            }

            @Override
            public void onVideoError(AMPSUnifiedNativeAdError adError) {
                callback.log("onVideoError");
            }

            @Override
            public void onVideoStop() {
                callback.log("onVideoStop");
            }

            @Override
            public void onVideoClicked() {
                callback.log("onVideoClicked");
            }
        });
        return itemView;
    }

    private void bindText(AMPSUnifiedNativeItem item, View itemView) {
        TextView titleView = itemView.findViewById(R.id.ad_title);
        TextView descView = itemView.findViewById(R.id.ad_desc);
        if (!TextUtils.isEmpty(item.getTitle())) {
            titleView.setText(item.getTitle());
        }
        if (!TextUtils.isEmpty(item.getDesc())) {
            descView.setText(item.getDesc());
        }
    }

    private void addMainImage(AMPSUnifiedNativeItem item, FrameLayout imageContainer) {
        if (item.isViewObject()) {
            AMPSUnifiedView unifiedView = item.getMainImageView();
            View imageView = unifiedView == null ? null : unifiedView.getView();
            if (imageView != null) {
                imageContainer.addView(imageView, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                ));
            }
        } else if (!TextUtils.isEmpty(item.getMainImageUrl())) {
            ImageView imageView = new ImageView(imageContainer.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(imageContainer.getContext()).load(item.getMainImageUrl()).into(imageView);
            imageContainer.addView(imageView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
        }
    }

    private void addUrlImage(Activity activity, LinearLayout imageContainer, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        ImageView imageView = new ImageView(activity);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 160);
        params.leftMargin = 20;
        params.weight = 1;
        imageView.setLayoutParams(params);
        Glide.with(activity).load(imageUrl).into(imageView);
        imageContainer.addView(imageView);
    }

    private void bindSelfEvent(Activity activity, AMPSUnifiedNativeItem item, AMPSUnifiedRootContainer root,
                               View clickView, View actionView, UnifiedAdLoadCallback callback) {
        item.setNativeAdEventListener(new AMPSUnifiedAdEventListener() {
            @Override
            public void onADExposed() {
                callback.log("onADExposed");
            }

            @Override
            public void onADClicked() {
                callback.log("onADClicked");
            }

            @Override
            public void onADExposeError(int errorCode, String errorMsg) {
                callback.log("onADExposeError: " + errorCode + " " + errorMsg);
            }
        });
        List<View> clickViews = new ArrayList<>();
        if (clickView != null) {
            clickViews.add(clickView);
        }
        List<View> actionViews = new ArrayList<>();
        if (actionView != null) {
            actionViews.add(actionView);
        }
        item.bindAdToRootContainer(activity, root, clickViews, actionViews);
    }

    private void destroyItems(List<AMPSUnifiedNativeItem> items) {
        if (items == null) {
            return;
        }
        for (AMPSUnifiedNativeItem item : items) {
            if (item != null) {
                item.destroy();
            }
        }
        items.clear();
    }

    private String adTypeName() {
        return expressMode ? "原生模板" : "原生自渲染";
    }
}
