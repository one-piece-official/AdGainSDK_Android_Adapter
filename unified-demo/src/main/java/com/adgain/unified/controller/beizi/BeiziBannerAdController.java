package com.adgain.unified.controller.beizi;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;

import xyz.adscope.amps.ad.banner.AMPSBannerAd;
import xyz.adscope.amps.ad.banner.AMPSBannerLoadEventListener;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;
import xyz.adscope.amps.tool.util.AMPSScreenUtil;

public class BeiziBannerAdController implements UnifiedAdController {
    private AMPSBannerAd bannerAd;
    private RelativeLayout bannerContainer;
    private boolean ready;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        BeiziAdControllerUtils.resetContainer(adContainer);
        bannerContainer = BeiziAdControllerUtils.newRelativeContainer(activity);
        ready = false;
        callback.log("开始加载 Beizi Banner: " + placementId);

        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(placementId)
                .setTimeOut(5000)
                .setWidth(AMPSScreenUtil.getScreenWidth(activity))
                .setHeight(0)
                .build();
        bannerAd = new AMPSBannerAd(activity, parameter, new AMPSBannerLoadEventListener() {
            @Override
            public void onAmpsAdLoaded() {
                ready = true;
                callback.log("onAmpsAdLoaded");
            }

            @Override
            public void onAmpsAdFailed(AMPSError error) {
                ready = false;
                callback.log("onAmpsAdFailed: " + BeiziAdControllerUtils.errorInfo(error));
            }

            @Override
            public void onAmpsAdShow() {
                callback.log("onAmpsAdShow");
            }

            @Override
            public void onAmpsAdClicked() {
                callback.log("onAmpsAdClicked");
            }

            @Override
            public void onAmpsAdDismiss() {
                callback.log("onAmpsAdDismiss");
            }
        });
        bannerAd.loadAd();
    }

    @Override
    public boolean isReady() {
        return ready && bannerAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bannerContainer == null) {
            bannerContainer = BeiziAdControllerUtils.newRelativeContainer(activity);
        }
        BeiziAdControllerUtils.attachView(adContainer, bannerContainer, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        bannerContainer.removeAllViews();
        bannerAd.show(bannerContainer);
        callback.log("展示 Beizi Banner");
    }

    @Override
    public void destroy() {
        if (bannerAd != null) {
            bannerAd.destroy();
            bannerAd = null;
        }
        if (bannerContainer != null) {
            bannerContainer.removeAllViews();
            bannerContainer = null;
        }
        ready = false;
    }
}
