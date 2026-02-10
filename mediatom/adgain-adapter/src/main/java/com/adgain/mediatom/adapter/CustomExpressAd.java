package com.adgain.mediatom.adapter;

import static com.adgain.sdk.api.BiddingLossReason.LOW_PRICE;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.IBidding;
import com.adgain.sdk.api.NativeAdAllEventListener;
import com.adgain.sdk.api.NativeAdData;
import com.yd.saas.api.mixNative.NativeAdAppInfo;
import com.yd.saas.api.mixNative.NativeMaterial;
import com.yd.saas.api.mixNative.NativePrepareInfo;
import com.yd.saas.base.custom.mixnative.CustomNativeAd;
import com.yd.saas.common.util.feature.Size;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomExpressAd extends CustomNativeAd<NativeAdData> {


    protected CustomExpressAd(Context context, NativeAdData nativeAd) {
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
        if (getNativeAd() != null)
            getNativeAd().setNativeAdEventListener(new NativeAdAllEventListener() {
                @Override
                public void onAdClose(View view) {
                    if (getEventListener() != null)
                        getEventListener().onNativeAdClose();
                }

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
    }

    @Override
    protected NativeMaterial createNativeMaterial(NativeAdData nativeAd) {
        return new NativeMaterial() {
            @Override
            public int getAdType() {
                return 0;
            }

            @Override
            public boolean isNativeAppAd() {
                return false;
            }

            @Override
            public String getMainImageUrl() {
                return "";
            }

            @Override
            public List<String> getImageUrlList() {
                return Collections.emptyList();
            }

            @Override
            public String getTitle() {
                return "";
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public String getCallToAction() {
                return "";
            }

            @Override
            public String getIconUrl() {
                return "";
            }

            @Override
            public View getAdMediaView() {
                return nativeAd.getFeedView();
            }

            @Override
            public String getVideoUrl() {
                return "";
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
                return "";
            }

            @Override
            public double getVideoDuration() {
                return 0;
            }

            @Override
            public NativeAdAppInfo getAdAppInfo() {
                return null;
            }

            @Override
            public Size getSize() {
                return null;
            }
        };
    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }
}
