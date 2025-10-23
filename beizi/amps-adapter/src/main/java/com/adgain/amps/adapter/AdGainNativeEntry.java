package com.adgain.amps.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdGainImage;
import com.adgain.sdk.api.NativeAdAllEventListener;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdPatternType;

import java.util.ArrayList;
import java.util.List;

import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdExpressListener;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeAdError;
import xyz.adscope.amps.ad.unified.adapter.AMPSUnifiedNativeAdapter;
import xyz.adscope.amps.ad.unified.inter.AMPSAppDetail;
import xyz.adscope.amps.ad.unified.inter.AMPSBaseTransformEntry;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedPattern;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedVideoListener;
import xyz.adscope.amps.ad.unified.inter.AMPSVideoConfig;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedMediaViewStub;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedRootContainer;

public class AdGainNativeEntry extends AMPSBaseTransformEntry {

    private NativeAdData nativeAdData;

    public AdGainNativeEntry(Context context, int ecpm, AMPSVideoConfig config, AMPSUnifiedNativeAdapter adapter, NativeAdData nativeAdData) {
        super(context, ecpm, config, adapter);
        this.nativeAdData = nativeAdData;
    }


    @Override
    public boolean isValid() {
        if (nativeAdData == null) {
            return false;
        }
        return true;
    }

    @Override
    public String getTitle() {
        return nativeAdData != null ? nativeAdData.getTitle() : "";
    }

    @Override
    public String getDesc() {
        return nativeAdData != null ? nativeAdData.getDesc() : "";
    }

    @Override
    public String getActionButtonText() {
        return nativeAdData != null ? nativeAdData.getCTAText() : "";
    }

    @Override
    public boolean isExpressAd() {
        return nativeAdData != null && nativeAdData.getFeedView() != null;
    }

    private AMPSNativeAdExpressListener expressListener;

    @Override
    public void setNativeAdExpressListener(AMPSNativeAdExpressListener ampsNativeAdExpressListener) {
        super.setNativeAdExpressListener(ampsNativeAdExpressListener);
        this.expressListener = ampsNativeAdExpressListener;
    }

    @Override
    public View getNativeExpressAdView() {
        View view = null;
        if (nativeAdData != null) {
            view = nativeAdData.getFeedView();
            if (view != null) {
                nativeAdData.setNativeAdEventListener(new NativeAdAllEventListener() {
                    @Override
                    public void onAdClose(View view) {
                        if (mAdapter != null) {
                            mAdapter.onAdDismiss();
                        }
                        if (expressListener != null) {
                            expressListener.onAdClosed(view);
                        }
                    }

                    @Override
                    public void onAdExposed() {
                        if (mAdapter != null) {
                            mAdapter.onAdShow();
                        }
                        if (expressListener != null) {
                            expressListener.onAdShow();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        if (mAdapter != null) {
                            mAdapter.onAdClicked();
                        }
                        if (expressListener != null) {
                            expressListener.onAdClicked();
                        }
                    }

                    @Override
                    public void onAdRenderFail(AdError adError) {
                        if (mAdapter != null) {
                            mAdapter.onAdShowFailed(adError.getErrorCode() + "", adError.getMessage());
                        }
                        if (expressListener != null) {
                            expressListener.onAdClicked();
                        }
                    }
                });
            }
        }
        return view;
    }


    @Override
    public View getAdSourceLogo() {
        return null;
    }

    @Override
    public String getIconUrl() {
        if (nativeAdData != null) {
            return nativeAdData.getIconUrl();
        }
        return "";
    }

    @Override
    public String getMainImageUrl() {
        if (nativeAdData != null && nativeAdData.getImageList() != null && !nativeAdData.getImageList().isEmpty()) {
            return nativeAdData.getImageList().get(0).imageUrl;
        }
        return "";
    }

    @Override
    public AMPSUnifiedPattern getAdPattern() {
        if (nativeAdData == null) {
            return null;
        }
        int mode = nativeAdData.getAdPatternType();
        switch (mode) {
            case NativeAdPatternType.NATIVE_BIG_IMAGE_AD:
                return AMPSUnifiedPattern.AD_PATTERN_TEXT_IMAGE;
            case NativeAdPatternType.NATIVE_GROUP_IMAGE_AD:
                return AMPSUnifiedPattern.AD_PATTERN_3_IMAGES;
            case NativeAdPatternType.NATIVE_VIDEO_AD:
                return AMPSUnifiedPattern.AD_PATTERN_VIDEO;
        }
        return AMPSUnifiedPattern.AD_PATTERN_UNKNOWN;
    }

    @Override
    public List<String> getImagesUrl() {
        List<String> list = new ArrayList<>();
        if (nativeAdData != null) {
            List<AdGainImage> imageList = nativeAdData.getImageList();
            for (int i = 0; i < imageList.size(); i++) {
                AdGainImage adGainImage = imageList.get(i);
                list.add(adGainImage.getImageUrl());
            }
            return list;
        }
        return list;
    }

    @Override
    public AMPSAppDetail getAppDetail() {
        return this;
    }

    @Override
    public void bindAdToRootContainer(Activity context, AMPSUnifiedRootContainer rootView,
                                      List<View> clickViews, List<View> actionView) {
        if (nativeAdData == null) {
            return;
        }
        nativeAdData.bindViewForInteraction(rootView, clickViews, new NativeAdEventListener() {
            @Override
            public void onAdExposed() {
                if (mAdapter != null) {
                    mAdapter.onAdShow();
                }
                if (mAdEventListener != null) {
                    mAdEventListener.onADExposed();
                }
            }

            @Override
            public void onAdClicked() {
                if (mAdapter != null) {
                    mAdapter.onAdClicked();
                }
                if (mAdEventListener != null) {
                    mAdEventListener.onADClicked();
                }
            }

            @Override
            public void onAdRenderFail(AdError adError) {

            }
        });
    }

    @Override
    public void bindAdToMediaView(Activity context, AMPSUnifiedMediaViewStub stub,
                                  AMPSUnifiedVideoListener listener) {
        if (nativeAdData == null || stub == null) {
            return;
        }
        nativeAdData.bindMediaView(stub, new NativeAdData.NativeAdMediaListener() {
            @Override
            public void onVideoLoad() {
                if (listener != null)
                    listener.onVideoLoaded(100);
            }

            @Override
            public void onVideoError(AdError adError) {
                if (listener != null && adError != null)
                    listener.onVideoError(new AMPSUnifiedNativeAdError(adError.getErrorCode(), adError.getMessage()));
            }

            @Override
            public void onVideoStart() {
                if (listener != null)
                    listener.onVideoStart();
            }

            @Override
            public void onVideoPause() {
                if (listener != null)
                    listener.onVideoPause();
            }

            @Override
            public void onVideoResume() {
                if (listener != null)
                    listener.onVideoResume();
            }

            @Override
            public void onVideoCompleted() {
                if (listener != null)
                    listener.onVideoCompleted();
            }
        });
    }

    @Override
    public void destroy() {
        if (nativeAdData == null) {
            return;
        }
        nativeAdData.destroy();
        super.destroy();
    }

    @Override
    public String getAppScore() {
        if (nativeAdData == null) {
            return null;
        }
        return "";
    }

    @Override
    public String getAppName() {
        if (nativeAdData != null && nativeAdData.getAdAppInfo() != null) {
            return nativeAdData.getAdAppInfo().getAppName();
        }
        return "";
    }

    @Override
    public String getAppSize() {
        if (nativeAdData != null && nativeAdData.getAdAppInfo() != null) {
            return nativeAdData.getAdAppInfo().getAppSize() + "";
        }
        return "";
    }

    @Override
    public String getAppIconUrl() {
        return "";
    }

    @Override
    public String getAppVersion() {
        if (nativeAdData != null && nativeAdData.getAdAppInfo() != null) {
            return nativeAdData.getAdAppInfo().getVersionName();
        }
        return "";
    }

    @Override
    public String getAppPermissionInfo() {
        if (nativeAdData != null && nativeAdData.getAdAppInfo() != null) {
            return nativeAdData.getAdAppInfo().getPermissionsUrl();
        }
        return "";
    }

    @Override
    public String getAppPrivacyPolicy() {
        if (nativeAdData != null && nativeAdData.getAdAppInfo() != null) {
            return nativeAdData.getAdAppInfo().getPrivacyUrl();
        }
        return "";
    }

    @Override
    public String getAppDescription() {
        if (nativeAdData != null && nativeAdData.getAdAppInfo() != null) {
            return nativeAdData.getAdAppInfo().getAppDescriptionUrl();
        }
        return "";
    }

    @Override
    public String getAppDeveloper() {
        if (nativeAdData != null && nativeAdData.getAdAppInfo() != null) {
            return nativeAdData.getAdAppInfo().getDeveloper();
        }
        return "";
    }

}


