package com.tobid.adapter.adgain;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InterstitialAd;
import com.adgain.sdk.api.InterstitialAdListener;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.custom.WMCustomInterstitialAdapter;
import com.windmill.sdk.models.BidPrice;

import java.util.HashMap;
import java.util.Map;

public class AdGainCustomerInterstitial extends WMCustomInterstitialAdapter implements InterstitialAdListener {

    private InterstitialAd interstitialAd;
    private String TAG = "AdGainCustomerInterstitial";

    public void loadAd(Activity activity, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            loadAd(activity.getApplicationContext(), localExtra, serverExtra);
        } catch (Throwable e) {
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "catch GtAd loadAd error " + Log.getStackTraceString(e)));
        }
    }

    public void loadAd(Context context, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            // 这个数值来自sigmob后台广告位ID的配置
            if (localExtra == null) {
                localExtra = new HashMap<>();
            }
            if (getBiddingType() != WMConstants.AD_TYPE_CLIENT_BIDING) {
                localExtra.put("isBid", "0");
            } else {
                localExtra.remove("isBid");
            }
            String codeId = (String) serverExtra.get(WMConstants.PLACEMENT_ID);
            AdRequest adRequest = new AdRequest.Builder().setCodeId(codeId)
                    .setAppId(AdGainCustomerProxy.appId).setExtOption(localExtra)
                    .setBidFloor(AdGainAdapterUtil.getBidFloor(this, serverExtra)).build();
            interstitialAd = new InterstitialAd(adRequest, this);
            interstitialAd.loadAd();
        } catch (Throwable tr) {
            Log.e(TAG, "loadAd exception: ", tr);
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "catch GtAd loadAd error " + Log.getStackTraceString(tr)));
        }
    }

    @Override
    public void showAd(Activity activity, HashMap<String, String> localExtra, Map<String, Object> serverExtra) {
        try {
            interstitialAd.showAd(activity);
        } catch (Throwable tr) {
            Log.e(TAG, "showAd exception: ", tr);
            callVideoAdPlayError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), "catch GtAd presentVideoAd error " + Log.getStackTraceString(tr)));
        }
    }

    @Override
    public boolean isReady() {
        return interstitialAd != null && interstitialAd.isReady();
    }

    @Override
    public void destroyAd() {
        Log.d(TAG, "destroyAd");
        if (interstitialAd != null) {
            interstitialAd.destroyAd();
            interstitialAd = null;
        }
    }

    @Override
    public void notifyBiddingResult(boolean isWin, String price, Map<String, Object> referBidInfo) {
        if (interstitialAd != null) {
            if (isWin) {
                // 竞价成功
                interstitialAd.sendWinNotification(AdGainAdapterUtil.getBidingWinNoticeParam(price, referBidInfo));
            } else {
                interstitialAd.sendLossNotification(AdGainAdapterUtil.getBidingLossNoticeParam(price, referBidInfo));
            }
        }
    }

    @Override
    public void onInterstitialAdLoadError(AdError adError) {
        callLoadFail(new WMAdapterError(adError.getErrorCode(), adError.getMessage()));
    }

    @Override
    public void onInterstitialAdLoadSuccess() {
        try {
            Log.d(TAG, "onInterstitialAdLoadSuccess: bidtype: " + getBiddingType());
            if (interstitialAd != null && getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                BidPrice bidPrice = new BidPrice(String.valueOf(interstitialAd.getBidPrice()));
                callLoadBiddingSuccess(bidPrice);
            }
            callLoadSuccess();
        } catch (Exception e) {
        }
    }

    @Override
    public void onInterstitialAdLoadCached() {

    }

    @Override
    public void onInterstitialAdShow() {
        callVideoAdShow();
    }

    @Override
    public void onInterstitialAdPlayEnd() {
        callVideoAdPlayComplete();
    }

    @Override
    public void onInterstitialAdClick() {
        callVideoAdClick();
    }

    @Override
    public void onInterstitialAdClosed() {
        callVideoAdClosed();
    }

    @Override
    public void onInterstitialAdShowError(AdError error) {
        if (error != null) {
            Log.d(TAG, "onRewardAdLoadError " + error.getErrorCode() + " msg " + error.getMessage());
            callVideoAdPlayError(new WMAdapterError(error.getErrorCode(), error.getMessage()));
        }
    }

    @Override
    public Object getChannelObject() {
        return interstitialAd;
    }

    @Override
    public Map<String, Object> getMediaExtraOption() {
        HashMap<String, Object> map = new HashMap<>();
        if (interstitialAd != null) {
            map.put(WMConstants.REQUEST_ID, interstitialAd.getExtraInfo().get("loadId"));
        }
        return map;
    }

}
