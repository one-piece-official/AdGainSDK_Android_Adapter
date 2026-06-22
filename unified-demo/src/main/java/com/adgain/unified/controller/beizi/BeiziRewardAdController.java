package com.adgain.unified.controller.beizi;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;

import java.util.Map;

import xyz.adscope.amps.ad.reward.AMPSRewardVideoAd;
import xyz.adscope.amps.ad.reward.AMPSRewardVideoLoadEventListener;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;

public class BeiziRewardAdController implements UnifiedAdController {
    private AMPSRewardVideoAd rewardVideoAd;
    private boolean ready;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        BeiziAdControllerUtils.resetContainer(adContainer);
        ready = false;
        callback.log("开始加载 Beizi 激励视频: " + placementId);

        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(placementId)
                .setTimeOut(5000)
                .setAdCount(1)
                .build();
        rewardVideoAd = new AMPSRewardVideoAd(activity, parameter, new AMPSRewardVideoLoadEventListener() {
            @Override
            public void onAmpsAdLoad() {
                ready = true;
                callback.log("onAmpsAdLoad");
            }

            @Override
            public void onAmpsAdCached() {
                callback.log("onAmpsAdCached");
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
            public void onAmpsAdDismiss() {
                ready = false;
                callback.log("onAmpsAdDismiss");
            }

            @Override
            public void onAmpsAdVideoClick() {
                callback.log("onAmpsAdVideoClick");
            }

            @Override
            public void onAmpsAdVideoComplete() {
                callback.log("onAmpsAdVideoComplete");
            }

            @Override
            public void onAmpsAdVideoError() {
                callback.log("onAmpsAdVideoError");
            }

            @Override
            public void onAmpsAdRewardArrived(boolean isRewardValid, int rewardType, Map<String, Object> extraInfo) {
                callback.log("onAmpsAdRewardArrived: " + isRewardValid + ", rewardType=" + rewardType);
            }
        });
        rewardVideoAd.loadAd();
    }

    @Override
    public boolean isReady() {
        return ready && rewardVideoAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 Beizi 激励视频");
        rewardVideoAd.show(activity);
    }

    @Override
    public void destroy() {
        if (rewardVideoAd != null) {
            rewardVideoAd.destroy();
            rewardVideoAd = null;
        }
        ready = false;
    }
}
