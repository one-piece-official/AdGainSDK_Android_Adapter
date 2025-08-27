package cn.jiguang.jgssp.adapter.adgain;

import android.app.Activity;
import android.content.Context;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InterstitialAd;
import com.adgain.sdk.api.InterstitialAdListener;

import java.util.ArrayList;

import cn.jiguang.jgssp.ad.adapter.bean.ADExtraData;
import cn.jiguang.jgssp.ad.adapter.loader.ADInterstitialLoader;
import cn.jiguang.jgssp.bid.ADSuyiBidLossCode;
public class InterstitialAdLoader extends ADInterstitialLoader {

    private InterstitialAd mInterstitialAd;

    @Override
    public void adapterLoadAd(Context context, String positionId, ADExtraData adExtraData) {
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(positionId)
                .build();
        mInterstitialAd = new InterstitialAd(adRequest, new InterstitialAdListener() {
            @Override
            public void onInterstitialAdLoadError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    callFailed(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onInterstitialAdLoadSuccess() {
                if (isBid() && mInterstitialAd != null) {
                    callSuccess(mInterstitialAd.getBidPrice());
                }
            }

            @Override
            public void onInterstitialAdLoadCached() {
                if (!isBid())
                    callSuccess();
            }

            @Override
            public void onInterstitialAdShow() {
                callExpose();
            }

            @Override
            public void onInterstitialAdPlayEnd() {

            }

            @Override
            public void onInterstitialAdClick() {
                callClick();
            }

            @Override
            public void onInterstitialAdClosed() {
                callClose();
            }

            @Override
            public void onInterstitialAdShowError(com.adgain.sdk.api.AdError adError) {

            }
        });
        mInterstitialAd.loadAd();
    }

    @Override
    public void adapterBiddingResult(int bidCode, ArrayList<Double> hbPriceList) {
        if (mInterstitialAd == null) {
            return;
        }
        if (bidCode == ADSuyiBidLossCode.BID_WIN) {
//            BidPriceUtil.sendWin(unifiedInterstitialAD, hbPriceList);
        } else {
//            BidPriceUtil.sendLoss(unifiedInterstitialAD, bidCode, hbPriceList);
        }
    }

    @Override
    public void adapterShow(Context context) {
        if (mInterstitialAd != null) {
            mInterstitialAd.showAd((Activity) context);
        }
    }

    @Override
    public void adapterRelease() {

    }
}
