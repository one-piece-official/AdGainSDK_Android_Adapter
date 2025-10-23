package com.adgain.amps.adapter;

import static com.adgain.amps.adapter.AdGainInitAdapter.errorCode;
import static com.adgain.amps.adapter.AdGainInitAdapter.errorMsg;
import static xyz.adscope.amps.common.AMPSErrorCode.ChannelErrorEnum.CHANNEL_ERROR_AD_CONTAINER_NULL;

import android.app.Activity;
import android.content.Context;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InterstitialAd;
import com.adgain.sdk.api.InterstitialAdListener;

import xyz.adscope.amps.ad.interstitial.adapter.AMPSInterstitialAdAdapterListener;
import xyz.adscope.amps.ad.interstitial.adapter.AMPSInterstitialAdapter;
import xyz.adscope.amps.base.AMPSBidResult;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.init.inter.IAMPSChannelInitCallBack;
import xyz.adscope.amps.inner.AMPSAdBiddingListener;
import xyz.adscope.amps.model.AMPSAdapterModel;
import xyz.adscope.amps.tool.AMPSLogUtil;

public class AdGainInterstitialAdapter extends AMPSInterstitialAdapter {

    private InterstitialAd mInterstitialAd;

    @Override
    public void loadNetworkAd(Context context, AMPSAdapterModel ampsAdapterModel, AMPSInterstitialAdAdapterListener listener) {
        super.loadNetworkAd(context, ampsAdapterModel, listener);
        if (isBidding && null != mInterstitialAd) {
            AdGainInterstitialAdapter.this.onAdLoad();
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
    public boolean isValid() {
        return mInterstitialAd != null && mInterstitialAd.isReady();
    }

    @Override
    public void showAd(Activity activity) {
        if (null == activity || null == mInterstitialAd) {
            AdGainInterstitialAdapter.this.onAdShowFailed(CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorCode(), CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorMsg());
            return;
        }
        mInterstitialAd.showAd(activity);
    }

    @Override
    public void sendWinNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendWinNotice(mInterstitialAd, ampsBidResult);
    }

    @Override
    public void sendLossNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendLossNotice(mInterstitialAd, ampsBidResult);
    }

    private void initSDK(final Context context) {
        AdGainInitAdapter.getInstance().initSDK(mInitAdapterConfig, new IAMPSChannelInitCallBack() {
            @Override
            public void successCallBack() {
                loadInterstitialAd(context);
            }

            @Override
            public void failCallBack(AMPSError ampsError) {
                AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, " AdGain loadInterstitialAd onAdLoadFailed " + "code:" + ampsError.getCode() + " message:" + ampsError.getMessage());
                AdGainInterstitialAdapter.this.onAdFailed(ampsError.getCode(), ampsError.getMessage());
            }
        });
    }

    private void loadInterstitialAd(Context context) {
        AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, " AdGain start loadInterstitialAd  spaceId:" + mSpaceId);
        AdRequest adRequest = new AdRequest.Builder().setCodeId(mSpaceId).build();
        mInterstitialAd = new InterstitialAd(adRequest, new InterstitialAdListener() {
            @Override
            public void onInterstitialAdLoadError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    onAdFailed(adError.getErrorCode() + "", adError.getMessage());
                else
                    onAdFailed(errorCode, errorMsg);
            }

            @Override
            public void onInterstitialAdLoadSuccess() {
            }

            @Override
            public void onInterstitialAdLoadCached() {
                if (isBidding && mInterstitialAd != null) {
                    onC2SBiddingSuccess(mInterstitialAd.getBidPrice());
                } else {
                    AdGainInterstitialAdapter.this.onAdLoad();
                }
            }

            @Override
            public void onInterstitialAdShow() {
                onAdShow();
            }

            @Override
            public void onInterstitialAdPlayEnd() {
                onVideoPlayEnd();
            }

            @Override
            public void onInterstitialAdClick() {
                onAdClicked();
            }

            @Override
            public void onInterstitialAdClosed() {
                onAdDismiss();
            }

            @Override
            public void onInterstitialAdShowError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    onAdShowFailed(adError.getErrorCode() + "", adError.getMessage());
            }
        });
        mInterstitialAd.loadAd();

    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != mInterstitialAd) mInterstitialAd.destroyAd();
    }
}
