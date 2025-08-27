package com.gromore.adapter.adgain;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.RewardAd;
import com.adgain.sdk.api.RewardAdListener;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.mediation.MediationConstant;
import com.bytedance.sdk.openadsdk.mediation.bridge.custom.reward.MediationCustomRewardVideoLoader;
import com.bytedance.sdk.openadsdk.mediation.custom.MediationCustomServiceConfig;
import com.bytedance.sdk.openadsdk.mediation.custom.MediationRewardItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Date   :   2025/5/13
 * Time   :   10:27
 * https://www.csjplatform.com/union/media/union/download/detail?id=195&docId=28430&locale=zh-CN&osType=android
 * MediationConstant.AD_TYPE_CLIENT_BIDING
 */
public class AdGainRewardAdapter extends MediationCustomRewardVideoLoader implements GMBiddingUtil.NotifyBiddingListener {

    private static final String TAG = AdGainCustomerInit.TAG;

    private RewardAd mRewardAd;

    public AdGainRewardAdapter() {
        Log.d(TAG, "AdGainRewardAdapter: constructor");
    }

    @Override
    public void load(Context context, AdSlot adSlot, MediationCustomServiceConfig serviceConfig) {

        try {

            if (serviceConfig == null) {
                Log.d(TAG, "reward load: serviceConfig is null");
                callLoadFail(40000, "serviceConfig 为 null");
                return;
            }

            Log.d(TAG, "reward getADNNetworkSlotId: " + serviceConfig.getADNNetworkSlotId());
//            Log.d(TAG, "reward  getADNNetworkName: " + serviceConfig.getADNNetworkName());
//            Log.d(TAG, "reward  getCustomAdapterJson: " + AdGainCustomerInit.getBidFloor(serviceConfig.getCustomAdapterJson()));
            RewardAdListener rewardAdListener = new RewardAdListener() {
                @Override
                public void onRewardAdLoadSuccess() {
                    Log.d(TAG, "reward AdLoadSuccess: " + isClientBidding());
                    if (isClientBidding())
                        callLoadSuccess(mRewardAd.getBidPrice());  // 单位 分
                }

                @Override
                public void onRewardAdLoadCached() {
                    Log.d(TAG, "onRewardAdLoadCached: " + getBiddingType());
                    if (!isClientBidding())
                        callAdVideoCache();
                }

                @Override
                public void onRewardAdShow() {
                    Log.d(TAG, "onRewardAdShow: ");
                    callRewardVideoAdShow();
                }

                @Override
                public void onRewardAdPlayStart() {

                }

                @Override
                public void onRewardAdPlayEnd() {
                    Log.d(TAG, "onRewardAdPLayEnd: ");
                    callRewardVideoComplete();
                }

                @Override
                public void onRewardAdClick() {
                    Log.d(TAG, "onRewardAdClick: ");
                    callRewardVideoAdClick();
                }

                @Override
                public void onRewardAdClosed() {
                    Log.d(TAG, "onRewardAdClosed: ");
                    callRewardVideoAdClosed();
                }

                @Override
                public void onRewardAdLoadError(AdError error) {
                    Log.d(TAG, "reward AdLoadError: " + error.getMessage());

                    if (error != null) {
                        Log.i(TAG, "onRewardAdLoadError errorCode = " + error.getErrorCode() + " errorMessage = " + error.getMessage());
                        callLoadFail(error.getErrorCode(), error.getMessage());

                    } else {
                        callLoadFail(40000, "no ad");
                    }
                }

                @Override
                public void onRewardAdShowError(AdError error) {

                }

                @Override
                public void onRewardVerify() {
                    Log.i(TAG, "onReward");

                    callRewardVideoRewardVerify(new MediationRewardItem() {
                        @Override
                        public boolean rewardVerify() {
                            return true;
                        }

                        @Override
                        public float getAmount() {
                            return 1;
                        }

                        @Override
                        public String getRewardName() {
                            return "";
                        }

                        @Override
                        public Map<String, Object> getCustomData() {
                            return new HashMap<>();
                        }
                    });
                }

                @Override
                public void onAdSkip() {

                }

            };

            Map<String, Object> options = new HashMap<>();

            AdRequest adRequest = new AdRequest.Builder()
                    .setCodeId(serviceConfig.getADNNetworkSlotId())     // 广推广告位 从商务获取
                    .setExtOption(options)
                    .setBidFloor(AdGainCustomerInit.getBidFloor(serviceConfig.getCustomAdapterJson()))
                    .build();

            mRewardAd = new RewardAd(adRequest, rewardAdListener);
            GMBiddingUtil.addNotifyBiddingListener(this);

            mRewardAd.loadAd();

        } catch (Exception e) {
            callLoadFail(40000, "Exception " + e.getMessage());
            Log.d(TAG, "reward load: error = " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void showAd(Activity activity) {
        try {
            if (mRewardAd != null && mRewardAd.isReady()) {
                mRewardAd.showAd(activity);
            }
        } catch (Exception e) {
            Log.d(TAG, "reward showAd: error = " + Log.getStackTraceString(e));
        }
    }

    @Override
    public MediationConstant.AdIsReadyStatus isReadyCondition() {
        return mRewardAd != null && mRewardAd.isReady() ?
                MediationConstant.AdIsReadyStatus.AD_IS_READY
                : MediationConstant.AdIsReadyStatus.AD_IS_NOT_READY;
    }

    public boolean isClientBidding() {
        return getBiddingType() == MediationConstant.AD_TYPE_CLIENT_BIDING;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GMBiddingUtil.removeNotifyBiddingListener(this);
        Log.i(TAG, "reward onDestroy");
        if (mRewardAd != null) {
            mRewardAd.destroyAd();
            mRewardAd = null;
        }
    }

    @Override
    public void notifyBiddingResult(Object object) {
        if (object instanceof TTRewardVideoAd && mRewardAd != null && mRewardAd.isReady()) {// 有填充才进行竞败回传
            String ecpm = ((TTRewardVideoAd) object).getMediationManager().getShowEcpm().getEcpm();
            GMBiddingUtil.adgainNotifyLoss(mRewardAd, ecpm, this);
        }
    }
}
