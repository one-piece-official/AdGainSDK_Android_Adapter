package com.adgain.amps.adapter;

import static com.adgain.amps.adapter.AdGainInitAdapter.errorCode;
import static com.adgain.amps.adapter.AdGainInitAdapter.errorMsg;
import static xyz.adscope.amps.common.AMPSErrorCode.ChannelErrorEnum.CHANNEL_ERROR_AD_CONTAINER_NULL;

import android.app.Activity;
import android.content.Context;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.RewardAd;
import com.adgain.sdk.api.RewardAdListener;

import java.util.HashMap;

import xyz.adscope.amps.ad.reward.adapter.AMPSRewardAdapter;
import xyz.adscope.amps.ad.reward.adapter.AMPSRewardVideoAdAdapterListener;
import xyz.adscope.amps.base.AMPSBidResult;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.init.inter.IAMPSChannelInitCallBack;
import xyz.adscope.amps.inner.AMPSAdBiddingListener;
import xyz.adscope.amps.model.AMPSAdapterModel;
import xyz.adscope.amps.tool.AMPSLogUtil;

public class AdGainRewardAdapter extends AMPSRewardAdapter {

    private RewardAd mRewardAd;

    @Override
    public void loadNetworkAd(Context context, AMPSAdapterModel ampsAdapterModel, AMPSRewardVideoAdAdapterListener listener) {
        super.loadNetworkAd(context, ampsAdapterModel, listener);
        if (isBidding && null != mRewardAd) {
            AdGainRewardAdapter.this.onAdLoad();
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
        return mRewardAd != null && mRewardAd.isReady();
    }

    @Override
    public void showAd(Activity activity) {
        if (null == activity || null == mRewardAd) {
            AdGainRewardAdapter.this.onAdShowFailed(CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorCode(), CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorMsg());
            return;
        }
        mRewardAd.showAd(activity);
    }

    @Override
    public void sendWinNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendWinNotice(mRewardAd, ampsBidResult);
    }

    @Override
    public void sendLossNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendLossNotice(mRewardAd, ampsBidResult);
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
                AdGainRewardAdapter.this.onAdFailed(ampsError.getCode(), ampsError.getMessage());
            }
        });
    }

    private void loadInterstitialAd(Context context) {
        AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, " AdGain start loadReward  spaceId:" + mSpaceId);
//        HashMap<String, Object> extras = new HashMap<>();
//        extras.put("disableShake", !AdGainInitAdapter.isCanShake);
        AdRequest adRequest = new AdRequest.Builder().setCodeId(mSpaceId).build();
        mRewardAd = new RewardAd(adRequest, new RewardAdListener() {
            @Override
            public void onRewardAdLoadSuccess() {

            }

            @Override
            public void onRewardAdLoadCached() {
                AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG, " AdGain onRewardAdLoadCached  isBidding:" + isBidding);
                if (isBidding && mRewardAd != null) {
                    onC2SBiddingSuccess(mRewardAd.getBidPrice());
                } else {
                    AdGainRewardAdapter.this.onAdLoad();
                }
            }

            @Override
            public void onRewardAdShow() {
                onAdShow();
            }

            @Override
            public void onRewardAdPlayStart() {
            }

            @Override
            public void onRewardAdPlayEnd() {
                onVideoComplete();
            }

            @Override
            public void onRewardAdClick() {
                onAdClicked();
            }

            @Override
            public void onRewardAdClosed() {
                onAdDismiss();
            }

            @Override
            public void onRewardAdLoadError(AdError adError) {
                if (adError != null)
                    onAdFailed(adError.getErrorCode() + "", adError.getMessage());
                else
                    onAdFailed(errorCode, errorMsg);
            }

            @Override
            public void onRewardAdShowError(AdError adError) {
                if (adError != null)
                    onAdShowFailed(adError.getErrorCode() + "", adError.getMessage());
            }

            @Override
            public void onRewardVerify() {
                onRewardArrived(true, 1, new HashMap<>());
            }

            @Override
            public void onAdSkip() {

            }
        });
        mRewardAd.loadAd();


    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != mRewardAd) mRewardAd.destroyAd();
    }
}
