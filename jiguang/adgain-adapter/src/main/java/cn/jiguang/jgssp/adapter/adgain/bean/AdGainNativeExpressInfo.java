package cn.jiguang.jgssp.adapter.adgain.bean;

import android.view.View;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;

import java.util.ArrayList;

import cn.jiguang.jgssp.ad.adapter.bean.ADNativeExpressInfo;
import cn.jiguang.jgssp.bid.ADSuyiBidLossCode;
public class AdGainNativeExpressInfo extends ADNativeExpressInfo<NativeAdData> {

    public AdGainNativeExpressInfo(NativeAdData adInfo) {
        super(adInfo);
        getAdInfo().setNativeAdMediaListener(new NativeAdData.NativeAdMediaListener() {
            @Override
            public void onVideoLoad() {
                callVideoLoad();
            }

            @Override
            public void onVideoError(com.adgain.sdk.api.AdError adError) {

            }

            @Override
            public void onVideoStart() {
                callVideoStart();
            }

            @Override
            public void onVideoPause() {
                callVideoPause();
            }

            @Override
            public void onVideoResume() {
            }

            @Override
            public void onVideoCompleted() {
                callVideoFinish();
            }
        });
    }

    @Override
    public View getNativeExpressAdView(ViewGroup container) {
        return getAdInfo().getFeedView();
    }

    @Override
    public void render(ViewGroup container) {
        if (getAdInfo() != null) {
            getAdInfo().setNativeAdEventListener(new NativeAdEventListener() {
                @Override
                public void onAdExposed() {
                    callExpose();
                }

                @Override
                public void onAdClicked() {
                    callClick();
                }

                @Override
                public void onAdRenderFail(AdError adError) {

                }
            });
        }
    }

    @Override
    public void adapterBiddingResult(int bidCode, ArrayList<Double> hbPriceList) {
        if (bidCode == ADSuyiBidLossCode.BID_WIN) {
//            BidPriceUtil.sendWin(getAdInfo(), hbPriceList);
        } else {
//            BidPriceUtil.sendLoss(getAdInfo(), bidCode, hbPriceList);
        }
    }
}
