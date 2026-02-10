package com.adgain.mediatom.adapter;

import static com.adgain.sdk.api.BiddingLossReason.LOW_PRICE;
import static com.yd.saas.api.mixNative.NativeAdConst.AD_TYPE_GROUP_IMG;
import static com.yd.saas.api.mixNative.NativeAdConst.AD_TYPE_SINGLE_IMG;
import static com.yd.saas.api.mixNative.NativeAdConst.AD_TYPE_UNKNOWN;
import static com.yd.saas.api.mixNative.NativeAdConst.AD_TYPE_VIDEO;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdGainImage;
import com.adgain.sdk.api.IBidding;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdPatternType;
import com.yd.saas.api.mixNative.NativeAdAppInfo;
import com.yd.saas.api.mixNative.NativeMaterial;
import com.yd.saas.api.mixNative.NativePrepareInfo;
import com.yd.saas.base.innterNative.AdAppInfo;
import com.yd.saas.common.util.feature.Size;
import com.yd.saas.config.utils.LogcatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomNativeAd extends com.yd.saas.base.custom.mixnative.CustomNativeAd<NativeAdData> {

    protected CustomNativeAd(Context context, NativeAdData nativeAd) {
        super(context, nativeAd);
    }

    @Override
    public void biddingResult(boolean isWinner, int price, int secondPrice, int advId) {
        getNativeAdOpt().ifPresent(adView -> {
            Map<String, Object> map = new HashMap<>();
            if (isWinner) {
                map.put(IBidding.EXPECT_COST_PRICE, price);
                map.put(IBidding.HIGHEST_LOSS_PRICE, secondPrice);
                adView.sendWinNotification(map);
            } else {
                map.put(IBidding.WIN_PRICE, price);
                map.put(IBidding.LOSS_REASON, LOW_PRICE);
                adView.sendLossNotification(map);
            }
        });
    }

    @Override
    public int getC2SBiddingECPM() {
        if (getNativeAd() != null) {
            return getNativeAd().getPrice();
        }
        return 0;
    }

    @Override
    protected void init(NativeAdData nativeAd) {
    }

    @Override
    protected void render(NativeAdData nativeAd, NativePrepareInfo prepareInfo) {
        try {
            nativeAd.bindViewForInteraction(getNativeAdView(), prepareInfo.getClickViewList(), new NativeAdEventListener() {
                @Override
                public void onAdExposed() {
                    if (getEventListener() != null)
                        getEventListener().onNativeAdShow();
                }

                @Override
                public void onAdClicked() {
                    if (getEventListener() != null)
                        getEventListener().onNativeAdClicked();
                }

                @Override
                public void onAdRenderFail(AdError adError) {

                }
            });
            LogcatUtil.d("CustomNativeAd  " + nativeAd.getAdPatternType());

            if (nativeAd.getAdPatternType() == NativeAdPatternType.NATIVE_VIDEO_AD) {
                nativeAd.bindMediaView(getNativeAdView(), new NativeAdData.NativeAdMediaListener() {
                    @Override
                    public void onVideoLoad() {
                    }

                    @Override
                    public void onVideoError(AdError adError) {

                    }

                    @Override
                    public void onVideoStart() {
                        getEventListener().onAdVideoStart();
                    }

                    @Override
                    public void onVideoPause() {
                    }

                    @Override
                    public void onVideoResume() {
                    }

                    @Override
                    public void onVideoCompleted() {
                        getEventListener().onAdVideoEnd();
                    }
                });
            }
            Activity activity = prepareInfo.getActivity();
            if (activity == null || activity.isFinishing()) {
                String msg = "Activity is inactive or null.";
                getEventListener().onNativeAdError(0, msg);
                return;
            }
            if (prepareInfo.getCloseView() != null) {
                prepareInfo.getCloseView().setOnClickListener(v -> {
                    getEventListener().onNativeAdClose();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected NativeMaterial createNativeMaterial(NativeAdData nativeAd) {
        return new NativeMaterial() {

            @Override
            public Size getSize() {
                return new Size(0, 0);
            }

            @Override
            public int getAdType() {
                int value = nativeAd.getAdPatternType();
                if (value == NativeAdPatternType.NATIVE_VIDEO_AD) return AD_TYPE_VIDEO;
                if (value == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) return AD_TYPE_SINGLE_IMG;
                if (value == NativeAdPatternType.NATIVE_GROUP_IMAGE_AD) return AD_TYPE_GROUP_IMG;
                return AD_TYPE_UNKNOWN;
            }

            @Override
            public boolean isNativeAppAd() {
                return nativeAd.getAdAppInfo() != null;
            }

            @Override
            public String getMainImageUrl() {
                if (nativeAd.getImageList() != null && nativeAd.getImageList().size() > 0)
                    return nativeAd.getImageList().get(0).imageUrl;
                return "";
            }

            @Override
            public String getTitle() {
                // 广告标题
                return nativeAd.getTitle();
            }

            @Override
            public String getDescription() {
                // 广告描述
                return nativeAd.getDesc();
            }

            @Override
            public String getIconUrl() {
                // 广告图标Image
                return nativeAd.getIconUrl();
            }

            @Override
            public View getAdMediaView() {
                // 获取广告的view,如视频广告的view,在广告平台可设置是否自动播放、是否静音等
                return nativeAd.getFeedView();
            }

            @Override
            public String getVideoUrl() {
                return null;
            }
            @Override
            public Bitmap getAdLogo() {
                return null;
            }

            @Override
            public Bitmap getAdLogoBitmap() {
                return null;
            }

            @Override
            public String getAdLogoUrl() {
                return null;
            }

            @Override
            public List<String> getImageUrlList() {
                // 广告图片Image list
                List<String> list = new ArrayList<>();
                for (AdGainImage image : nativeAd.getImageList())
                    list.add(image.imageUrl);
                return list;
            }

            @Override
            public String getCallToAction() {
                // 广告创意按钮文案
                return nativeAd.getCTAText();
            }

            @Override
            public double getVideoDuration() {
                return nativeAd.getVideoDuration();
            }

            private NativeAdAppInfo mAdAppInfo;

            @Override
            public NativeAdAppInfo getAdAppInfo() {
                com.adgain.sdk.api.AdAppInfo info = nativeAd.getAdAppInfo();
                if (info == null) return null;
                if (mAdAppInfo == null) {
                    mAdAppInfo = new AdAppInfo() {
                        @Override
                        public String getAppName() {
                            return info.getAppName();
                        }

                        @Override
                        public String getPublisher() {
                            return info.getAuthorName();
                        }

                        @Override
                        public String getAppVersion() {
                            return info.getVersionName();
                        }

                        @Override
                        public String getAppPrivacyUrl() {
                            return info.getPrivacyUrl();
                        }

                        @Override
                        public String getAppPermissionUrl() {
                            return info.getPermissionsUrl();
                        }

                        @Override
                        public String getFunctionUrl() {
                            return info.getAppDescriptionUrl();
                        }
                    };
                }
                return mAdAppInfo;
            }
        };
    }

    @Override
    public boolean isNativeExpress() {
        return false;
    }
}
