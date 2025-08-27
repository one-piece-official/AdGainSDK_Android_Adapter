package cn.jiguang.jgssp.adapter.adgain;

import android.content.Context;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.SplashAd;
import com.adgain.sdk.api.SplashAdListener;

import java.util.ArrayList;

import cn.jiguang.jgssp.ad.adapter.bean.ADExtraData;
import cn.jiguang.jgssp.ad.adapter.loader.ADSplashLoader;
import cn.jiguang.jgssp.bid.ADSuyiBidLossCode;

public class SplashAdLoader extends ADSplashLoader {
    private SplashAd splashAd;

    @Override
    public void adapterLoadAd(Context context, String positionId, ADExtraData adExtraData) {
        // 创建ad请求
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(positionId) // 广告位ID
                .build();
        // 创建开屏AD API对象，监听回调在这里设置,5 * 1000为请求广告超时时间
        splashAd = new SplashAd(adRequest, new SplashAdListener() {
            @Override
            public void onAdLoadSuccess() {
                if (isBid() && splashAd != null) {
                    callSuccess(splashAd.getBidPrice());
                }
            }

            @Override
            public void onAdCacheSuccess() {
                if (!isBid()) {
                    callSuccess();
                }
            }

            @Override
            public void onSplashAdLoadFail(com.adgain.sdk.api.AdError adError) {
                if (adError != null) {
                    callFailed(adError.getErrorCode(), adError.getMessage());
                }
            }

            @Override
            public void onSplashAdShow() {
                callExpose();
            }

            @Override
            public void onSplashAdShowError(com.adgain.sdk.api.AdError adError) {
                if (adError != null) {
                    callFailed(adError.getErrorCode(), adError.getMessage());
                }
            }

            @Override
            public void onSplashAdClick() {
                callClick();
            }

            @Override
            public void onSplashAdClose(boolean b) {
             /*   if (b) {
                    callSkip();
                } else {
                }*/
                callClose();
            }
        }, 5 * 1000);
        splashAd.loadAd();  // 加载广告
    }

    @Override
    public void adapterShow(ViewGroup container) {
        if (splashAd != null)
            splashAd.showAd(container);
    }


    @Override
    public boolean isExpired() {
        if (splashAd != null) return splashAd.isReady();
        return super.isExpired();
    }

    @Override
    public void adapterBiddingResult(int bidCode, ArrayList<Double> hbPriceList) {
        if (splashAd == null) {
            return;
        }
        if (bidCode == ADSuyiBidLossCode.BID_WIN) {
//            BidPriceUtil.sendWin(splashAd, hbPriceList);
        } else {
//            BidPriceUtil.sendLoss(splashAd, bidCode, hbPriceList);
        }
    }


    @Override
    public void adapterRelease() {
        if (splashAd != null) splashAd.destroyAd();
    }
}
