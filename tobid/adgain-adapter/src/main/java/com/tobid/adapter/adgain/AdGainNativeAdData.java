package com.tobid.adapter.adgain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdGainImage;
import com.adgain.sdk.api.ApkDownloadListener;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdInteractiveType;
import com.adgain.sdk.api.NativeAdPatternType;
import com.adgain.sdk.api.NativeUnifiedAd;
import com.bumptech.glide.Glide;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.natives.WMImage;
import com.windmill.sdk.natives.WMNativeAdContainer;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdRender;

import java.util.ArrayList;
import java.util.List;

public class AdGainNativeAdData extends WMNativeAdData {
    private static final String TAG = "AdGainNativeAdData";
    private final NativeAdData nativeAdData;
    private final AdGainCustomerNative adAdapter;
    private NativeADMediaListener nativeADMediaListener;

    private NativeAdInteractionListener nativeAdInteractionListener;
    private final NativeAdEventListener eventListener = new NativeAdEventListener() {
        @Override
        public void onAdExposed() {
            Log.d(TAG, "onAdExposed nal: " + nativeAdInteractionListener);
            AdInfo adInfo = null;
            if (adAdapter != null) {
                adInfo = adAdapter.getAdInFo(AdGainNativeAdData.this);
                adAdapter.callNativeAdShow(AdGainNativeAdData.this);
            }
            if (nativeAdInteractionListener != null) {
                nativeAdInteractionListener.onADExposed(adInfo);
            }

        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "onAdClicked nal: " + nativeAdInteractionListener);
            AdInfo adInfo = null;
            if (adAdapter != null) {
                adInfo = adAdapter.getAdInFo(AdGainNativeAdData.this);
                adAdapter.callNativeAdClick(AdGainNativeAdData.this);
            }
            if (nativeAdInteractionListener != null) {
                nativeAdInteractionListener.onADClicked(adInfo);
            }

        }

        @Override
        public void onAdRenderFail(AdError error) {
            Log.d(TAG, "onAdRenderFail nal: " + nativeAdInteractionListener);
            AdInfo adInfo = null;
            if (adAdapter != null) {
                adInfo = adAdapter.getAdInFo(AdGainNativeAdData.this);
                adAdapter.callNativeAdShowError(AdGainNativeAdData.this, new WMAdapterError(error.getErrorCode(), error.getMessage()));
            }
            if (nativeAdInteractionListener != null) {
                nativeAdInteractionListener.onADError(adInfo, WindMillError.ERROR_AD_ADAPTER_PLAY);
            }

        }
    };

    public AdGainNativeAdData(NativeAdData nativeAdData, AdGainCustomerNative adAdapter) {
        this.nativeAdData = nativeAdData;
        this.adAdapter = adAdapter;
    }

    @Override
    public int getInteractionType() {
        if (nativeAdData != null) {
            // 1: 浏览器、2: deepLink、3: 下载
            int interactionType = nativeAdData.getAdInteractiveType();
            switch (interactionType) {
                case NativeAdInteractiveType.NATIVE_BROWSER:
                case NativeAdInteractiveType.NATIVE_DEEP_LINK:
                    return WMConstants.INTERACTION_TYPE_BROWSER;
                case NativeAdInteractiveType.NATIVE_DOWNLOAD:
                    return WMConstants.INTERACTION_TYPE_DOWNLOAD;
            }
        }
        return WMConstants.INTERACTION_TYPE_UNKNOWN;
    }

    @Override
    public int getAdPatternType() {
        if (nativeAdData != null) {
            int patternType = nativeAdData.getAdPatternType();
            switch (patternType) {
                case NativeAdPatternType.NATIVE_VIDEO_AD:
                    return WMNativeAdDataType.NATIVE_VIDEO_AD;
                case NativeAdPatternType.NATIVE_BIG_IMAGE_AD:
                    return WMNativeAdDataType.NATIVE_BIG_IMAGE_AD;
                case NativeAdPatternType.NATIVE_GROUP_IMAGE_AD:
                    return WMNativeAdDataType.NATIVE_GROUP_IMAGE_AD;
            }
        }
        return WMNativeAdDataType.NATIVE_UNKNOWN;
    }

    @Override
    public View getExpressAdView() {
        if (nativeAdData != null) return nativeAdData.getFeedView();
        return null;
    }

    @Override
    public void render() {
        Log.d(TAG, "render");
        if (isExpressAd()) {
            if (nativeAdInteractionListener != null) {
                View feedView = getExpressAdView();
                AdInfo adInfo = adAdapter.getAdInFo(AdGainNativeAdData.this);
                if (feedView != null) {
                    nativeAdData.setNativeAdEventListener(eventListener);
                    nativeAdData.setNativeAdMediaListener(adgainMediaListener);
                    nativeAdInteractionListener.onADRenderSuccess(adInfo, feedView, feedView.getWidth(), feedView.getHeight());

                } else {
                    nativeAdInteractionListener.onADRenderSuccess(adInfo, null, 0, 0);
                }
            }
        }
    }

    @Override
    public boolean isExpressAd() {
        if (nativeAdData != null) return nativeAdData.getFeedView() != null;
        return false;
    }

    @Override
    public boolean isNativeDrawAd() {
        return false;
    }

    @Override
    public String getCTAText() {
        Log.d(TAG, "getCTAText: " + nativeAdData);
        if (nativeAdData != null) {
            return nativeAdData.getCTAText();
        }
        return "";
    }

    @Override
    public String getTitle() {
        Log.d(TAG, "getTitle: " + nativeAdData);
        if (nativeAdData != null) {
            return nativeAdData.getTitle();
        }
        return "";
    }

    @Override
    public String getDesc() {
        Log.d(TAG, "getDesc: " + nativeAdData);
        if (nativeAdData != null) {
            nativeAdData.getDesc();
        }
        return "";
    }

    @Override
    public Bitmap getAdLogo() {
        Log.d(TAG, "getAdLogo");
        return null;
    }

    @Override
    public String getIconUrl() {
        Log.d(TAG, "getIconUrl: " + nativeAdData);
        if (nativeAdData != null) {
            nativeAdData.getIconUrl();
        }
        return "";
    }

    @Override
    public int getNetworkId() {
        Log.d(TAG, "getNetworkId: " + adAdapter);
        if (adAdapter != null) {
            return adAdapter.getChannelId();
        }
        return 0;
    }

    @Override
    public void connectAdToView(Activity activity, WMNativeAdContainer adContainer, WMNativeAdRender adRender) {
        Log.d(TAG, "connectAdToView " + activity + " container: " + adContainer + " render: " + adRender);
        if (adRender != null) {
            View view = adRender.createView(activity, getAdPatternType());
            adRender.renderAdView(view, this);
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @Override
    public void bindImageViews(Context context, List<ImageView> imageViews, int defaultImageRes) {
        Log.d(TAG, "bindImageViews ad: " + nativeAdData);
        try {
            if (imageViews != null && !imageViews.isEmpty()) {
                Glide.with(context.getApplicationContext()).load(nativeAdData.getImageList().get(0).imageUrl).into(imageViews.get(0));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public List<String> getImageUrlList() {
        Log.d(TAG, "getImageUrlList");
        List<String> ret = new ArrayList<>();
        if (nativeAdData != null) {
            List<AdGainImage> imgList = nativeAdData.getImageList();
            if (imgList != null) {
                for (AdGainImage img : imgList) {
                    if (img != null && img.imageUrl != null) {
                        ret.add(img.imageUrl);
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public List<WMImage> getImageList() {
        Log.d(TAG, "getImageList");
        List<WMImage> ret = new ArrayList<>();
        if (nativeAdData != null) {
            List<AdGainImage> imgList = nativeAdData.getImageList();
            if (imgList != null) {
                for (AdGainImage AdGainImage : imgList) {
                    ret.add(new GtAdNativeImage(AdGainImage));
                }
            }
        }
        return ret;
    }

    @Override
    public void bindViewForInteraction(Context context, View view, List<View> clickableViews, List<View> creativeViewList, View disLikeView) {
        Log.d(TAG, "bindViewForInteraction");
        nativeAdData.bindViewForInteraction(view, clickableViews, eventListener);
    }

    @Override
    public void setInteractionListener(NativeAdInteractionListener adInteractionListener) {
        Log.d(TAG, "setInteractionListener: " + adInteractionListener);
        this.nativeAdInteractionListener = adInteractionListener;
    }

    @Override
    public void bindMediaView(Context context, ViewGroup mediaLayout) {
        Log.d(TAG, "bindMediaView");
        if (null == nativeAdData) {
            return;
        }
        nativeAdData.bindMediaView(mediaLayout, adgainMediaListener);
    }


    @Override
    public void setMediaListener(NativeADMediaListener nativeADMediaListener) {
        Log.d(TAG, "setMediaListener: " + nativeADMediaListener);
        if (nativeADMediaListener != null) {
            this.nativeADMediaListener = nativeADMediaListener;
        }
    }

    @Override
    public void setDislikeInteractionCallback(Activity activity, DislikeInteractionCallback dislikeInteractionCallback) {
    }

    @Override
    public void setDownloadListener(AppDownloadListener appDownloadListener) {
        Log.d(TAG, "setDownloadListener: " + appDownloadListener);
        if (adAdapter != null) {
            NativeUnifiedAd ad = adAdapter.getNativeAd();
            if (ad != null) {
                if (null == appDownloadListener) {
                    ad.setApkDownloadListener(null);
                } else {
                    ad.setApkDownloadListener(new ApkDownloadListener() {
                        @Override
                        public void onDownloadStart(boolean success) {
                            Log.d(TAG, "onDownloadStart: " + success);
                            if (success) {
                                appDownloadListener.onDownloadActive(100L, 1L, "", "");
                            } else {
                                appDownloadListener.onDownloadFailed(100L, 1L, "", "");
                            }
                        }

                        @Override
                        public void onDownloadEnd(boolean success) {
                            Log.d(TAG, "onDownloadEnd: " + success);
                            if (success) {
                                appDownloadListener.onDownloadFinished(100L, "", "");
                            } else {
                                appDownloadListener.onDownloadFailed(100L, 1L, "", "");
                            }
                        }

                        @Override
                        public void onDownloadPaused(boolean success) {
                            Log.d(TAG, "onDownloadPaused: " + success);
                            if (success) {
                                appDownloadListener.onDownloadPaused(100L, 50L, "", "");
                            } else {
                                appDownloadListener.onDownloadFailed(100L, 1L, "", "");
                            }
                        }

                        @Override
                        public void onInstallStart(boolean success) {
                            Log.d(TAG, "onInstallStart: " + success);
                        }

                        @Override
                        public void onInstallEnd(boolean success) {
                            Log.d(TAG, "onInstallEnd: " + success);
                            if (success) {
                                appDownloadListener.onInstalled("", "");
                            }
                        }
                    });
                }

            }
        }
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        if (nativeAdData != null) {
            nativeAdData.destroy();
        }
    }

    @Override
    public View getInteractionWidgetView(int width, int height, int actionType, AdShakeViewListener listener) {
        if (nativeAdData != null) {
            return nativeAdData.getWidgetView(width, height);
        }
        return super.getInteractionWidgetView(width, height, actionType, listener);
    }

    private static class GtAdNativeImage extends WMImage {

        private final AdGainImage image;

        public GtAdNativeImage(AdGainImage image) {
            this.image = image;
        }

        @Override
        public int getHeight() {
            if (image != null) {
                return image.getHeight();
            }
            return 0;
        }

        @Override
        public int getWidth() {
            if (image != null) {
                return image.getWidth();
            }
            return 0;
        }

        @Override
        public String getImageUrl() {
            if (image != null) {
                return image.getImageUrl();
            }
            return "";
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    private NativeAdData.NativeAdMediaListener adgainMediaListener = new NativeAdData.NativeAdMediaListener() {
        @Override
        public void onVideoLoad() {
            Log.d(TAG, "onVideoLoad l:" + nativeADMediaListener);
            if (nativeADMediaListener != null) {
                nativeADMediaListener.onVideoLoad();
            }
        }

        @Override
        public void onVideoError(AdError error) {
            Log.d(TAG, "onVideoError: " + error + " l:" + nativeADMediaListener);
            if (nativeADMediaListener != null) {
                nativeADMediaListener.onVideoError(WindMillError.ERROR_AD_PLAY);
            }
        }

        @Override
        public void onVideoStart() {
            Log.d(TAG, "onVideoStart l:" + nativeADMediaListener);
            if (nativeADMediaListener != null) {
                nativeADMediaListener.onVideoStart();
            }
        }

        @Override
        public void onVideoPause() {
            Log.d(TAG, "onVideoPause l:" + nativeADMediaListener);
            if (nativeADMediaListener != null) {
                nativeADMediaListener.onVideoPause();
            }
        }

        @Override
        public void onVideoResume() {
            Log.d(TAG, "onVideoResume l:" + nativeADMediaListener);
            if (nativeADMediaListener != null) {
                nativeADMediaListener.onVideoResume();
            }
        }

        @Override
        public void onVideoCompleted() {
            Log.d(TAG, "onVideoCompleted l:" + nativeADMediaListener);
            if (nativeADMediaListener != null) {
                nativeADMediaListener.onVideoCompleted();
            }
        }
    };

}
