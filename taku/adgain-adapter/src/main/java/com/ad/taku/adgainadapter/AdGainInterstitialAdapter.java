
package com.ad.taku.adgainadapter;


import static com.ad.taku.adgainadapter.AdGainInitManager.getAppId;
import static com.ad.taku.adgainadapter.AdGainInitManager.getCodeId;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InterstitialAd;
import com.adgain.sdk.api.InterstitialAdListener;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;

import java.util.HashMap;
import java.util.Map;


public class AdGainInterstitialAdapter extends CustomInterstitialAdapter {
    public static String TAG = AdGainInitManager.TAG;

    InterstitialAd mGTInterstitialAd;

    String mAppId;
    String codeId;

    boolean isC2SBidding;

    // 参考 ： https://help.takuad.com/docs/qJT7q0
    // serverExtra : Taku后台配置的 Json字符串 中的 key-value
    // localExtra  :  ATInterstitial#setLocalExtra()  本次加载传入自定义参数
    @Override
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {
        Log.d(TAG, "\n inter startBiddingRequest   serverExtra = " + serverExtra + "   localExtra = " + localExtra + "   biddingListener = " + biddingListener);

        isC2SBidding = true;

        loadCustomNetworkAd(context, serverExtra, localExtra);

        return true;
    }
    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, final Map<String, Object> localExtra) {

        mAppId = getAppId(serverExtra);
        codeId = getCodeId(serverExtra);
        Log.d(TAG, "loadCustomNetworkAd: mAppId = " + mAppId + "  mADUnitId = " + codeId + "   isC2SBidding =  " + isC2SBidding);

        if (TextUtils.isEmpty(mAppId)) {
            notifyATLoadFail("", "AdGain app_id is empty.");
            return;
        }

        AdGainInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                loadInterstitial(serverExtra, localExtra);
            }

            @Override
            public void onFail(String errorMsg) {
                notifyATLoadFail("", errorMsg);
            }
        });
    }

    private void loadInterstitial(Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId)
                .setBidFloor(AdGainInitManager.getBidFloor(serverExtra))
                .build();

        mGTInterstitialAd = new InterstitialAd(adRequest, new InterstitialAdListener() {

            @Override
            public void onInterstitialAdLoadError(AdError adError) {
                notifyATLoadFail(String.valueOf(adError.getErrorCode()), adError.getMessage());
            }

            @Override
            public void onInterstitialAdLoadSuccess() {
                if (isC2SBidding) {

                    if (mBiddingListener != null) {
                        int ecpm = mGTInterstitialAd.getBidPrice();

                        AdGainBiddingNotice biddingNotice = new AdGainBiddingNotice(mGTInterstitialAd);

                        mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(ecpm, System.currentTimeMillis() + "", biddingNotice, ATAdConst.CURRENCY.RMB_CENT), null);
                    }

                } else if (mLoadListener != null) {
                    mLoadListener.onAdDataLoaded();
                }
            }

            @Override
            public void onInterstitialAdLoadCached() {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onInterstitialAdShow() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }

                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoStart();
                }
            }

            @Override
            public void onInterstitialAdPlayEnd() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoEnd();
                }
            }

            @Override
            public void onInterstitialAdClick() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onInterstitialAdClosed() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }

            @Override
            public void onInterstitialAdShowError(AdError adError) {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoError(adError.getErrorCode() + "", adError.getMessage());
                }
            }

        });

        mGTInterstitialAd.loadAd();
    }

    @Override
    public boolean isAdReady() {
        if (mGTInterstitialAd != null) {
            return mGTInterstitialAd.isReady();
        }
        return false;
    }

    @Override
    public void show(Activity activity) {

        if (mGTInterstitialAd == null) {
            return;
        }

        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            mGTInterstitialAd.showAd(activity);
        }
    }

    @Override
    public String getNetworkName() {
        return AdGainInitManager.getInstance().getNetworkName();
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
    public ATInitMediation getMediationInitManager() {
        return AdGainInitManager.getInstance();
    }

    @Override
    public void destory() {
        if (mGTInterstitialAd != null) {
            mGTInterstitialAd.destroyAd();
            mGTInterstitialAd = null;
        }
    }

}
