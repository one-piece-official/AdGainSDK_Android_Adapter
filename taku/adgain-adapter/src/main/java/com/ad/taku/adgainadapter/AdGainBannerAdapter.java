
package com.ad.taku.adgainadapter;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.BannerAd;
import com.adgain.sdk.api.BannerAdListener;
import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;

import java.util.Map;


public class AdGainBannerAdapter extends CustomBannerAdapter implements BannerAdListener {
    public static String TAG = AdGainInitManager.TAG;

    BannerAd mBannerAd;

    String mAppId;
    String codeId;

    @Override
    public boolean startBiddingRequest(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {
        Log.d(TAG, "\n banner startBiddingRequest   serverExtra = " + serverExtra + "   localExtra = " + localExtra + "   biddingListener = " + biddingListener);

        loadCustomNetworkAd(context, serverExtra, localExtra);

        return true;
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {


        mAppId = ATInitMediation.getStringFromMap(serverExtra, "app_id");
        codeId = ATInitMediation.getStringFromMap(serverExtra, "slot_id");
        if (TextUtils.isEmpty(codeId)) {
            codeId = ATInitMediation.getStringFromMap(serverExtra, "unit_id");
        }

        Log.d(TAG, "loadCustomNetworkAd: mAppId = " + mAppId + "  mADUnitId = " + codeId);

        if (TextUtils.isEmpty(mAppId)) {
            notifyATLoadFail("", "AdGain app_id is empty.");
            return;
        }

        AdGainInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                loadAd();
            }

            @Override
            public void onFail(String errorMsg) {
                notifyATLoadFail("", errorMsg);
            }
        });
    }

    private void loadAd() {
        Log.d(TAG, "banner  loadAd ");

        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId)
                .build();

        mBannerAd = new BannerAd(adRequest, this, true, true);

        mBannerAd.loadAd();
    }

    @Override
    public boolean isAdReady() {
        if (mBannerAd != null) {
            return mBannerAd.isReady();
        }
        return false;
    }

    @Override
    public View getBannerView() {
        Log.d(TAG, "banner  getBannerView ");

        if (mBannerAd != null) {
            return mBannerAd.getBannerView();
        }
        return null;
    }

    @Override
    public String getNetworkPlacementId() {
        return codeId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return AdGainInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public String getNetworkName() {
        return AdGainInitManager.getInstance().getNetworkName();
    }

    @Override
    public void destory() {
        if (mBannerAd != null) {
            mBannerAd.destroyAd();
            mBannerAd = null;
        }

    }

    @Override
    public void onBannerAdLoadSuccess() {
        Log.d(TAG, "banner  onBannerAdLoadSuccess ");

        if (mBiddingListener != null) {
            int ecpm = mBannerAd.getBidPrice();

            AdGainBiddingNotice biddingNotice = new AdGainBiddingNotice(mBannerAd);

            mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(ecpm, System.currentTimeMillis() + "", biddingNotice, ATAdConst.CURRENCY.RMB_CENT), null);
        }
    }

    @Override
    public void onBannerAdShow() {
        Log.d(TAG, "banner  onBannerAdShow ");

        if (mImpressionEventListener != null) {
            mImpressionEventListener.onBannerAdShow();
        }

    }

    @Override
    public void onBannerAdClick() {
        Log.d(TAG, "banner  onBannerAdClick ");

        if (mImpressionEventListener != null) {
            mImpressionEventListener.onBannerAdClicked();
        }
    }

    @Override
    public void onBannerAdClosed() {
        Log.d(TAG, "banner  onBannerAdClosed ");

        if (mImpressionEventListener != null) {
            mImpressionEventListener.onBannerAdClose();
        }

    }

    @Override
    public void onBannerAdLoadError(AdError adError) {
        Log.d(TAG, "banner  onBannerAdLoadError " + adError);

        if (mLoadListener != null) {
            mLoadListener.onAdLoadError(adError.getErrorCode() + "", adError.getMessage());
        }

    }

    @Override
    public void onBannerAdShowError(AdError adError) {
        Log.d(TAG, "banner  onBannerAdShowError " + adError);

    }
}
