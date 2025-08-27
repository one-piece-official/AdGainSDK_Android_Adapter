package cn.jiguang.jgssp.adapter.adgain;

import android.app.Activity;
import android.content.Context;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.RewardAd;
import com.adgain.sdk.api.RewardAdListener;
import java.util.ArrayList;

import cn.jiguang.jgssp.ad.adapter.bean.ADExtraData;
import cn.jiguang.jgssp.ad.adapter.loader.ADRewardLoader;
import cn.jiguang.jgssp.bid.ADSuyiBidLossCode;
public class RewardVodAdLoader extends ADRewardLoader {

    private RewardAd mRewardAd;

    @Override
    public void adapterLoadAd(Context context, String positionId, ADExtraData adExtraData) {
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(positionId)
                .build();
        mRewardAd = new RewardAd(adRequest, new RewardAdListener() {
            @Override
            public void onRewardAdLoadSuccess() {
                if (isBid() && mRewardAd != null) {
                    callSuccess(mRewardAd.getBidPrice());
                } else {
                    callSuccess();
                }
            }

            @Override
            public void onRewardAdLoadCached() {
                callVideoCache();
            }

            @Override
            public void onRewardAdShow() {
                callExpose();
            }

            @Override
            public void onRewardAdPlayStart() {
            }

            @Override
            public void onRewardAdPlayEnd() {
                callVideoComplete();
            }

            @Override
            public void onRewardAdClick() {
                callClick();
            }

            @Override
            public void onRewardAdClosed() {
                callClose();
            }

            @Override
            public void onRewardAdLoadError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    callFailed(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onRewardAdShowError(com.adgain.sdk.api.AdError adError) {
                if (adError != null)
                    callVideoError(adError.getErrorCode(), adError.getMessage());
            }

            @Override
            public void onRewardVerify() {
                callReward();
            }

            @Override
            public void onAdSkip() {
            }
        });
        mRewardAd.loadAd();
    }

    @Override
    public void adapterBiddingResult(int bidCode, ArrayList<Double> hbPriceList) {
        if (mRewardAd == null) {
            return;
        }
        if (bidCode == ADSuyiBidLossCode.BID_WIN) {
//            BidPriceUtil.sendWin(rewardVideoAD, hbPriceList);
        } else {
//            BidPriceUtil.sendLoss(rewardVideoAD, bidCode, hbPriceList);
        }
    }

    @Override
    public void adapterShow(Context context) {
        try {
            if (mRewardAd != null)
                mRewardAd.showAd((Activity) context);
        } catch (Exception e) {
        }
    }

    @Override
    public void adapterRelease() {
        if (mRewardAd != null)
            mRewardAd.destroyAd();
    }
}
