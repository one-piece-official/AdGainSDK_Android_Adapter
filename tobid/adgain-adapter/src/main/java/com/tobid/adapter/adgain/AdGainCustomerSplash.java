package com.tobid.adapter.adgain;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.SplashAd;
import com.adgain.sdk.api.SplashAdListener;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.custom.WMCustomSplashAdapter;
import com.windmill.sdk.models.BidPrice;
import com.windmill.sdk.widget.SplashViewManager;

import java.util.HashMap;
import java.util.Map;

public class AdGainCustomerSplash extends WMCustomSplashAdapter implements SplashAdListener {

    private static final String TAG = "AdGainCustomerSplash";
    private SplashAd splashAd;
    private String unitId;

    public void loadAd(Activity activity, ViewGroup viewGroup, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            loadAd(activity.getApplicationContext(), viewGroup, localExtra, serverExtra);
        } catch (Exception e) {
        }
    }


    public void loadAd(Context context, ViewGroup viewGroup, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            // 这个数值来自sigmob后台广告位ID的配置
            unitId = (String) serverExtra.get(WMConstants.PLACEMENT_ID);
            int w = context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
            int h = context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
            if (localExtra == null) {
                localExtra = new HashMap<>();
            }
            if (getBiddingType() != WMConstants.AD_TYPE_CLIENT_BIDING) {
                localExtra.put("isBid", "0");
            } else {
                localExtra.remove("isBid");
            }
            AdRequest adRequest = new AdRequest.Builder().setCodeId(unitId)
                    .setBidFloor(AdGainAdapterUtil.getBidFloor(this, serverExtra))
                    .setAppId(AdGainCustomerProxy.appId).setExtOption(localExtra)
                    .setWidth(w).setHeight(h).build();
            splashAd = new SplashAd(adRequest, this);
            splashAd.loadAd();
        } catch (Throwable tr) {
            Log.e(TAG, "loadAd exception: ", tr);
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "catch GtAd loadAd error " + Log.getStackTraceString(tr)));
        }
    }

    @Override
    public void showAd(Activity activity, ViewGroup viewGroup, Map<String, Object> serverExtra) {
        try {
            splashAd.showAd(viewGroup);
        } catch (Throwable tr) {
            Log.e(TAG, "showAd exception: ", tr);
            callSplashAdShowError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), "catch GtAd presentVideoAd error " + Log.getStackTraceString(tr)));
        }
    }

    @Override
    public boolean isReady() {
        return splashAd != null && splashAd.isReady();
    }

    @Override
    public void destroyAd() {
        if (splashAd != null) {
            splashAd.destroyAd();
            splashAd = null;
        }
    }

    @Override
    public void notifyBiddingResult(boolean isWin, String price, Map<String, Object> referBidInfo) {
        if (splashAd != null) {
            Log.d(TAG, "notifyBiddingResult: win: " + isWin + " price: " + price + " refer: " + AdGainAdapterUtil.getBidingWinNoticeParam(price, referBidInfo));
            if (isWin) {
                // 竞价成功
                splashAd.sendWinNotification(AdGainAdapterUtil.getBidingWinNoticeParam(price, referBidInfo));
            } else {
                splashAd.sendLossNotification(AdGainAdapterUtil.getBidingLossNoticeParam(price, referBidInfo));
            }
        }
    }

    @Override
    public void onAdLoadSuccess() {
        try {
            Log.d(TAG, "onSplashAdLoadSuccess: bidtype: " + getBiddingType() + " " + unitId);
            if (splashAd != null && getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                BidPrice bidPrice = new BidPrice(String.valueOf(splashAd.getBidPrice()));
                callLoadBiddingSuccess(bidPrice);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onAdCacheSuccess() {
        callLoadSuccess();
    }

    @Override
    public void onSplashAdLoadFail(AdError error) {
        if (error != null) {
            Log.d(TAG, "onRewardAdLoadError " + error.getErrorCode() + " msg " + error.getMessage());
            callLoadFail(new WMAdapterError(error.getErrorCode(), error.getMessage()));
        }
    }

    @Override
    public void onSplashAdShow() {
        callSplashAdShow();
    }

    @Override
    public void onSplashAdShowError(AdError error) {
        callSplashAdShowError(new WMAdapterError(error.getErrorCode(), error.getMessage()));
    }

    @Override
    public void onSplashAdClick() {
        callSplashAdClick();
    }

    @Override
    public void onSplashAdClose(boolean isSkip) {
        if (isSkip) {
            callSplashAdSkipped();
        } else {
            callSplashAdClosed();
        }
    }
}
