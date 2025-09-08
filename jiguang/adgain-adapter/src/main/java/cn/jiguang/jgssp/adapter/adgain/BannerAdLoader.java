package cn.jiguang.jgssp.adapter.adgain;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;


import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.BannerAd;
import com.adgain.sdk.api.BannerAdListener;

import cn.jiguang.jgssp.ad.adapter.bean.ADExtraData;
import cn.jiguang.jgssp.ad.adapter.loader.ADBannerLoader;

/**
 * @author maipian
 * @description 描述
 * @date 10/12/24
 */
public class BannerAdLoader extends ADBannerLoader {

    private BannerAd mBannerAd;

    @Override
    public void adapterLoadAd(Context context, String positionId, ADExtraData adExtraData) {

        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(positionId)
                .build();

        mBannerAd = new BannerAd(adRequest, new BannerAdListener() {
            @Override
            public void onBannerAdLoadSuccess() {
                if (isBid()) {
                    if (mBannerAd != null)
                        callSuccess(mBannerAd.getBidPrice());
                } else {
                    callSuccess();
                }
            }

            @Override
            public void onBannerAdShow() {
                callExpose();
            }

            @Override
            public void onBannerAdClick() {
                callClick();
            }

            @Override
            public void onBannerAdClosed() {
                callClose();
            }

            @Override
            public void onBannerAdLoadError(AdError adError) {
                if (adError != null) {
                    callFailed(adError.getErrorCode(), adError.getMessage());
                }
            }

            @Override
            public void onBannerAdShowError(AdError adError) {

            }
        }, true, true);

        mBannerAd.loadAd();

    }

    @Override
    public void adapterShow(ViewGroup container) {
        if (mBannerAd != null) {
            container.addView(mBannerAd.getBannerView());
        }
    }

    @Override
    public void adapterRelease() {

    }
}
