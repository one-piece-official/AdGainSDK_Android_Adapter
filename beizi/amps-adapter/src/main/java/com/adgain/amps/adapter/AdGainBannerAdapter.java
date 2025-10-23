package com.adgain.amps.adapter;

import static com.adgain.amps.adapter.AdGainInitAdapter.errorCode;
import static com.adgain.amps.adapter.AdGainInitAdapter.errorMsg;
import static xyz.adscope.amps.common.AMPSErrorCode.ChannelErrorEnum.CHANNEL_ERROR_AD_CONTAINER_NULL;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.BannerAd;
import com.adgain.sdk.api.BannerAdListener;

import java.util.Map;

import xyz.adscope.amps.ad.banner.adapter.AMPSBannerAdAdapterListener;
import xyz.adscope.amps.ad.banner.adapter.AMPSBannerAdapter;
import xyz.adscope.amps.base.AMPSBidResult;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.init.inter.IAMPSChannelInitCallBack;
import xyz.adscope.amps.inner.AMPSAdBiddingListener;
import xyz.adscope.amps.model.AMPSAdapterModel;
import xyz.adscope.amps.tool.AMPSLogUtil;

public class AdGainBannerAdapter extends AMPSBannerAdapter {

    private BannerAd bannerAd;

    @Override
    public void loadNetworkAd(Context context, AMPSAdapterModel params,
                              AMPSBannerAdAdapterListener listener) {
        super.loadNetworkAd(context, params, listener);
        //实现竞价逻辑时需要判断是否是bidding广告，如果是bidding广告，如果已经发起过请求直接回调广告请求成功
        if (isBidding && bannerAd != null) {
            AdGainBannerAdapter.this.onAdLoad();
            return;
        }
        initSDK();
    }

    //实现竞价功能时，重写该方法,如果渠道sdk不支持竞价可以不用重写该方法
    @Override
    public void startBid(Context context, AMPSAdapterModel ampsAdapterModel,
                         AMPSAdBiddingListener listener) {
        super.startBid(context, ampsAdapterModel, listener);
        initSDK();
    }

    //实现竞价功能时，重写该方法,如果渠道sdk不支持竞价可以不用重写该方法，发送给渠道竞价成功通知
    @Override
    public void sendWinNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendWinNotice(bannerAd, ampsBidResult);
    }

    //实现竞价功能时，重写该方法,如果渠道sdk不支持竞价可以不用重写该方法，发送给渠道竞败通知
    @Override
    public void sendLossNotice(AMPSBidResult ampsBidResult) {
        BiddingUtils.sendLossNotice(bannerAd, ampsBidResult);
    }

    @Override
    public void showAd(ViewGroup container) {
        if (bannerAd == null || container == null) {
            AdGainBannerAdapter.this.onAdShowFailed(CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorCode(),
                    CHANNEL_ERROR_AD_CONTAINER_NULL.getErrorMsg());
            return;
        }
        container.addView(bannerAd.getBannerView());
    }

    @Override
    public boolean isValid() {
        return bannerAd != null && bannerAd.isReady();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (bannerAd != null) {
            bannerAd.destroyAd();
        }
    }

    /**
     * 初始化sdk
     */
    private void initSDK() {
        AdGainInitAdapter.getInstance().initSDK(mInitAdapterConfig, new IAMPSChannelInitCallBack() {
            @Override
            public void successCallBack() {
                loadBannerAd();
            }

            @Override
            public void failCallBack(AMPSError ampsError) {
                AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG,
                        AMPSConstants.AMPS_CHANNEL_LOG_TAG_GM + " AdGainBannerAdapter failCallBack " +
                                "code:" + ampsError.getCode() + " message:" + ampsError.getMessage());
                AdGainBannerAdapter.this.onAdFailed(ampsError.getCode(), ampsError.getMessage());
            }
        });
    }

    /**
     * 请求广告
     */
    private void loadBannerAd() {
        AMPSLogUtil.d(AMPSConstants.AMPS_LOG_TAG,
                " AdGainBannerAdapter start loadBannerAd  spaceId:" + mSpaceId);

        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(mSpaceId)
                .build();

        bannerAd = new BannerAd(adRequest, new BannerAdListener() {
            @Override
            public void onBannerAdLoadSuccess() {
                if (isBidding && bannerAd != null) {
                    onC2SBiddingSuccess(bannerAd.getBidPrice());
                } else {
                    onAdLoad();
                }
            }

            @Override
            public void onBannerAdShow() {
                onAdShow();
            }

            @Override
            public void onBannerAdClick() {
                onAdClicked();
            }

            @Override
            public void onBannerAdClosed() {
                onAdDismiss();
            }

            @Override
            public void onBannerAdLoadError(AdError adError) {
                if (adError != null)
                    onAdFailed(adError.getErrorCode() + "", adError.getMessage());
                else
                    onAdFailed(errorCode, errorMsg);
            }

            @Override
            public void onBannerAdShowError(AdError adError) {
                if (adError != null)
                    onAdShowFailed(adError.getErrorCode() + "", adError.getMessage());
            }
        }, true, true);
        bannerAd.loadAd();
    }


    @Override
    public Map<String, Object> getMediaExtraInfo() {
        if (bannerAd != null) {
            return bannerAd.getExtraInfo();
        }
        return null;
    }

}
