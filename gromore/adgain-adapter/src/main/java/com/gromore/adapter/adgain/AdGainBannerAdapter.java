package com.gromore.adapter.adgain;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.BannerAd;
import com.adgain.sdk.api.BannerAdListener;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.mediation.MediationConstant;
import com.bytedance.sdk.openadsdk.mediation.bridge.custom.banner.MediationCustomBannerLoader;
import com.bytedance.sdk.openadsdk.mediation.custom.MediationCustomServiceConfig;

public class AdGainBannerAdapter extends MediationCustomBannerLoader implements GMBiddingUtil.NotifyBiddingListener, BannerAdListener {

    private static final String TAG = AdGainCustomerInit.TAG;

    BannerAd mBannerAd;

    @Override
    public View getAdView() {
        Log.d(TAG, "getBannerView ");

        if (mBannerAd != null) {
            return mBannerAd.getBannerView();
        }
        return null;
    }

    @Override
    public void load(Context context, AdSlot adSlot, MediationCustomServiceConfig serviceConfig) {
        try {
            if (serviceConfig == null) {
                Log.d(TAG, "banner load: serviceConfig is null");
                callLoadFail(40000, "serviceConfig 为 null");
                return;
            }

            String codeid = serviceConfig.getADNNetworkSlotId();
            Log.d(TAG, "banner load codeid =  " + codeid);

            AdRequest adRequest = new AdRequest.Builder()
                    .setCodeId(codeid)
                    .setAppId(AdGainCustomerInit.appId)
                    .build();

            mBannerAd = new BannerAd(adRequest, this, true, true);
            GMBiddingUtil.addNotifyBiddingListener(this);
            mBannerAd.loadAd();
        } catch (Exception e) {
            callLoadFail(40000, "Exception " + e.getMessage());
            Log.d(TAG, "banner load: error = " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void notifyBiddingResult(Object object) {
        Log.d(TAG, "notifyBiddingResult: " + (object instanceof TTNativeExpressAd));
        if (object instanceof TTNativeExpressAd && mBannerAd != null) {// 有填充才进行竞败回传
            String ecpm = ((TTNativeExpressAd) object).getMediationManager().getShowEcpm().getEcpm();
            GMBiddingUtil.adgainNotifyLoss(mBannerAd, ecpm, this);
        }
    }

    public boolean isClientBidding() {
        return getBiddingType() == MediationConstant.AD_TYPE_CLIENT_BIDING;
    }

    @Override
    public void onBannerAdLoadSuccess() {
        Log.d(TAG, "onBannerAdLoadSuccess " + isClientBidding());
        if (isClientBidding() && mBannerAd != null)
            callLoadSuccess(mBannerAd.getBidPrice());  // 单位 分
    }

    @Override
    public void onBannerAdShow() {
        Log.d(TAG, "onBannerAdShow: ");
        callBannerAdShow();
    }

    @Override
    public void onBannerAdClick() {
        Log.d(TAG, "onBannerAdClick: ");
        callBannerAdClick();
    }

    @Override
    public void onBannerAdClosed() {
        Log.d(TAG, "onBannerAdClosed: ");
        callBannerAdClosed();
    }

    @Override
    public void onBannerAdLoadError(AdError error) {
        if (error != null) {
            Log.i(TAG, "onBannerAdLoadError errorCode = " + error.getErrorCode() + " errorMessage = " + error.getMessage());
            callLoadFail(error.getErrorCode(), error.getMessage());

        } else {
            callLoadFail(40000, "no ad");
        }
    }

    @Override
    public void onBannerAdShowError(AdError error) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "banner onDestroy");
        GMBiddingUtil.removeNotifyBiddingListener(this);
        if (mBannerAd != null) {
            mBannerAd.destroyAd();
            mBannerAd = null;
        }
    }
}
