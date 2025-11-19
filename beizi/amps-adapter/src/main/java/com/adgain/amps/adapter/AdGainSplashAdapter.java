package com.adgain.amps.adapter;

import static com.adgain.amps.adapter.AdGainInitAdapter.errorCode;
import static com.adgain.amps.adapter.AdGainInitAdapter.errorMsg;
import static xyz.adscope.amps.common.AMPSErrorCode.ChannelErrorEnum.CHANNEL_ERROR_AD_CONTAINER_NULL;

import android.content.Context;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.SplashAd;
import com.adgain.sdk.api.SplashAdListener;

import java.util.HashMap;

import xyz.adscope.amps.ad.splash.adapter.AMPSSplashAdAdapterListener;
import xyz.adscope.amps.ad.splash.adapter.AMPSSplashAdapter;
import xyz.adscope.amps.base.AMPSBidResult;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.init.inter.IAMPSChannelInitCallBack;
import xyz.adscope.amps.inner.AMPSAdBiddingListener;
import xyz.adscope.amps.model.AMPSAdapterModel;
import xyz.adscope.amps.tool.AMPSLogUtil;

public class AdGainSplashAdapter extends AMPSSplashAdapter {

    private SplashAd splashAd;

    @Override
    public void loadNetworkAd(Context context, AMPSAdapterModel ampsAdapterModel, AMPSSplashAdAdapterListener listener) {
        super.loadNetworkAd(context, ampsAdapterModel, listener);
        if (isBidding && null != splashAd) {
            AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG,
                    " AdGain loadSplashAdPre " +
                            "onAdLoadSucceed spaceId:" + mSpaceId);
            AdGainSplashAdapter.this.onAdLoad();
            return;
        }
        initSDK(context);
    }

    //实现竞价功能时，重写该方法,如果渠道sdk不支持竞价可以不用重写该方法
    @Override
    public void startBid(Context context, AMPSAdapterModel ampsAdapterModel,
                         AMPSAdBiddingListener listener) {
        super.startBid(context, ampsAdapterModel, listener);
        initSDK(context);
    }

    @Override
    public boolean isValid() {
        return splashAd != null && splashAd.isReady();
    }

    @Override
    public void showAd(ViewGroup viewGroup) {
        if (null == splashAd || null == viewGroup) {
            AdGainSplashAdapter.this.onAdShowFailed(CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorCode(),
                    CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorMsg());
            return;
        }
        splashAd.showAd(viewGroup);
    }

    @Override
    public void sendWinNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendWinNotice(splashAd, ampsBidResult);
    }

    @Override
    public void sendLossNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendLossNotice(splashAd, ampsBidResult);
    }

    private void initSDK(final Context context) {
        AdGainInitAdapter.getInstance().initSDK(mInitAdapterConfig, new IAMPSChannelInitCallBack() {
            @Override
            public void successCallBack() {
                loadSplashAd(context);
            }

            @Override
            public void failCallBack(AMPSError ampsError) {
                AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, " AdGain loadSplashAd onSplashLoadFail " +
                        "code:" + ampsError.getCode() + " message:" + ampsError.getMessage());
                AdGainSplashAdapter.this.onAdFailed(ampsError.getCode(), ampsError.getMessage());
            }
        });
    }

    private void loadSplashAd(Context context) {
        AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG,
                " AdGain start loadSplashAd  spaceId:" + mSpaceId);
        HashMap<String, Object> extras = new HashMap<>();
        extras.put("disableShake", !AdGainInitAdapter.isCanShake);// true 就是用户关闭摇一摇
        // 创建ad请求
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(mSpaceId) // 广告位ID
                .setHeight(viewHeightDp)
                .setWidth(viewWidthDp)
                .setExtOption(extras)
                .build();
        // 创建开屏AD API对象，监听回调在这里设置,5 * 1000为请求广告超时时间
        splashAd = new SplashAd(adRequest, new SplashAdListener() {
            @Override
            public void onAdLoadSuccess() {

            }

            @Override
            public void onAdCacheSuccess() {
                AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, "AdGain onAdCacheSuccess： " + isBidding + " ");
                if (isBidding && splashAd != null) {
                    onC2SBiddingSuccess(splashAd.getBidPrice());
                } else {
                    onAdLoad();
                }
            }

            @Override
            public void onSplashAdLoadFail(com.adgain.sdk.api.AdError adError) {
                AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, " AdGain onSplashAdLoadFail ： " + adError);
                if (adError != null)
                    onAdFailed(adError.getErrorCode() + "", adError.getMessage());
                else
                    onAdFailed(errorCode, errorMsg);
            }

            @Override
            public void onSplashAdShow() {
                onAdShow();
            }

            @Override
            public void onSplashAdShowError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    onAdShowFailed(adError.getErrorCode() + "", adError.getMessage());
            }

            @Override
            public void onSplashAdClick() {
                onAdClicked();
            }

            @Override
            public void onSplashAdClose(boolean b) {
                onAdDismiss();
            }
        }, 5 * 1000);
// 加载广告
        splashAd.loadAd();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != splashAd) {
            splashAd.destroyAd();
        }
    }

}
