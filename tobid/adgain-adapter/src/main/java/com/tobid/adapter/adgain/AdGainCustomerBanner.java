package com.tobid.adapter.adgain;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.BannerAd;
import com.adgain.sdk.api.BannerAdListener;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.custom.WMCustomBannerAdapter;
import com.windmill.sdk.models.BidPrice;

import java.util.HashMap;
import java.util.Map;

public class AdGainCustomerBanner extends WMCustomBannerAdapter implements BannerAdListener {
    private static final String TAG = "AdGainBanner";

    BannerAd mBannerAd;

    public void loadAd(Activity activity, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            Log.d(TAG, "loadAd: l: " + localExtra + " s: " + serverExtra + "  " + AdGainAdapterUtil.getBidFloor(this, serverExtra));
            loadAd(activity.getApplicationContext(), localExtra, serverExtra);
        } catch (Exception tr) {
            Log.e(TAG, "loadAd exception: ", tr);
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(),
                    "catch AdGain loadAd error " + Log.getStackTraceString(tr)));
        }

    }

    @Override
    public boolean isReady() {
        boolean result = mBannerAd != null && mBannerAd.isReady();
        Log.d(TAG, "banner isReady = " + result);
        return result;
    }

    @Override
    public void destroyAd() {
        Log.d(TAG, "destroyAd ");
        if (mBannerAd != null) {
            mBannerAd.destroyAd();
        }
    }

    public void loadAd(Context context, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        Log.d(TAG, "loadAd: l: " + localExtra + " s: " + serverExtra + "  " + AdGainAdapterUtil.getBidFloor(this, serverExtra));
        try {
            String codeId = (String) serverExtra.get(WMConstants.PLACEMENT_ID);
            String appId = (String) serverExtra.get(WMConstants.APP_ID);
            AdRequest adRequest = new AdRequest.Builder()
                    .setCodeId(codeId)
                    .setAppId(appId)
                    .build();
            mBannerAd = new BannerAd(adRequest, this, true, true);
            mBannerAd.loadAd();
        } catch (Exception tr) {
            Log.e(TAG, "loadAd exception: ", tr);
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(),
                    "catch AdGain loadAd error " + Log.getStackTraceString(tr)));
        }
    }

    @Override
    public View getBannerView() {
        Log.d(TAG, "getBannerView ");
        if (mBannerAd != null && isReady()) {
            return mBannerAd.getBannerView();
        }
        return null;
    }

    @Override
    public void onBannerAdLoadSuccess() {
        Log.d(TAG, "onBannerAdLoadSuccess");
        if (mBannerAd != null && getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
            BidPrice bidPrice = new BidPrice(String.valueOf(mBannerAd.getBidPrice()));
            callLoadBiddingSuccess(bidPrice);
        }
        callLoadSuccess();

    }

    @Override
    public void onBannerAdShow() {
        Log.d(TAG, "onBannerAdShow");
        callBannerAdShow();
    }

    @Override
    public void onBannerAdClick() {
        Log.d(TAG, "onBannerAdClick");
        callBannerAdClick();
    }

    @Override
    public void onBannerAdClosed() {
        Log.d(TAG, "onBannerAdClosed");
        callBannerAdClosed();
    }

    @Override
    public void onBannerAdLoadError(AdError adError) {
        Log.d(TAG, "onBannerAdLoadError " + adError);
        callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(),
                adError.getMessage()));

    }

    @Override
    public void onBannerAdShowError(AdError adError) {
        Log.d(TAG, "onBannerAdShowError " + adError);
    }

    @Override
    public Object getChannelObject() {
        return mBannerAd;
    }

    @Override
    public Map<String, Object> getMediaExtraOption() {
        HashMap<String, Object> map = new HashMap<>();
        if (mBannerAd != null) {
            map.put(WMConstants.REQUEST_ID, mBannerAd.getExtraInfo().get("loadId"));
        }
        return map;
    }

}
