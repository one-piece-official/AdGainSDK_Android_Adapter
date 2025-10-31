package com.adgain.amps.adapter;

import static com.adgain.amps.adapter.AdGainInitAdapter.errorCode;
import static com.adgain.amps.adapter.AdGainInitAdapter.errorMsg;

import static xyz.adscope.amps.common.AMPSErrorCode.ChannelErrorEnum.CHANNEL_ERROR_AD_IS_NULL;

import android.content.Context;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdAdapterListener;
import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdapter;
import xyz.adscope.amps.ad.nativead.inter.AMPSNativeAdExpressInfo;
import xyz.adscope.amps.ad.unified.adapter.AMPSUnifiedNativeAdAdapterListener;
import xyz.adscope.amps.ad.unified.adapter.AMPSUnifiedNativeAdapter;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedNativeItem;
import xyz.adscope.amps.base.AMPSBidResult;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.init.inter.IAMPSChannelInitCallBack;
import xyz.adscope.amps.inner.AMPSAdBiddingListener;
import xyz.adscope.amps.model.AMPSAdapterModel;
import xyz.adscope.amps.tool.AMPSLogUtil;

/**
 * 信息流模板Adapter
 */
public class AdGainNativeExpressAdapter extends AMPSNativeAdapter {

    private NativeUnifiedAd nativeAd;
    private NativeAdData nativeAdData;

    @Override
    public void loadNetworkAd(Context context, AMPSAdapterModel ampsAdapterModel, AMPSNativeAdAdapterListener listener) {
        super.loadNetworkAd(context, ampsAdapterModel, listener);
        if (isBidding && null != nativeAd) {
            AdGainNativeExpressAdapter.this.onAdLoad();
            return;
        }
        initSDK(context);
    }

    @Override
    public void startBid(Context context, AMPSAdapterModel ampsAdapterModel, AMPSAdBiddingListener listener) {
        super.startBid(context, ampsAdapterModel, listener);
        initSDK(context);
    }

    @Override
    public List<AMPSNativeAdExpressInfo> getNativeListInfo() {
        List<AMPSNativeAdExpressInfo> ampsNativeAdExpressInfoList = new ArrayList<>();
        try {
            if (null != nativeAdData) {
                int ecpm = 0;
                if (isBidding) {
                    ecpm = nativeAdData.getPrice();
                }
                AMPSNativeAdExpressInfo ampsNativeAdExpressInfo = new AdGainNativeExpressInfo(nativeAdData,
                        this, ecpm);
                ampsNativeAdExpressInfoList.add(ampsNativeAdExpressInfo);
            }
        } catch (Exception e) {
        }
        return ampsNativeAdExpressInfoList;
    }

    @Override
    public void sendWinNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendWinNotice(nativeAd, ampsBidResult);
    }

    @Override
    public void sendLossNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendLossNotice(nativeAd, ampsBidResult);
    }


    @Override
    public boolean isValid() {
        return nativeAd != null && nativeAd.isReady();
    }

    private void initSDK(final Context context) {
        AdGainInitAdapter.getInstance().initSDK(mInitAdapterConfig, new IAMPSChannelInitCallBack() {
            @Override
            public void successCallBack() {
                loadNativeExpressAd(context);
            }

            @Override
            public void failCallBack(AMPSError ampsError) {
                AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, " AdGain loadNativeExpressAd onAdLoadFailed " +
                        "code:" + ampsError.getCode() + " message:" + ampsError.getMessage());
                AdGainNativeExpressAdapter.this.onAdFailed(ampsError.getCode(), ampsError.getMessage());
            }
        });
    }

    private void loadNativeExpressAd(Context context) {
        AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG,
                " AdGain start loadNativeExpressAd  spaceId:" + mSpaceId);
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(mSpaceId) // 设置广告位id
                .build();
        nativeAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
            @Override
            public void onAdError(com.adgain.sdk.api.AdError adError) {
                if (null != adError)
                    onAdFailed(adError.getErrorCode() + "", adError.getMessage());
                else
                    onAdFailed(errorCode, errorMsg);
            }

            @Override
            public void onAdLoad(List<NativeAdData> list) {
                try {
                    if (null == list || list.isEmpty()) {
                        onAdFailed(CHANNEL_ERROR_AD_IS_NULL.getErrorCode(),
                                CHANNEL_ERROR_AD_IS_NULL.getErrorMsg());
                        return;
                    }
                    nativeAdData = list.get(0);
                    if (isBidding && nativeAd != null) {
                        onC2SBiddingSuccess(nativeAd.getBidPrice());
                    } else {
                        AdGainNativeExpressAdapter.this.onAdLoad();
                    }
                } catch (Exception e) {
                }
            }
        }); // 创建广告对象
        nativeAd.loadAd();// 请求广告
    }

}
