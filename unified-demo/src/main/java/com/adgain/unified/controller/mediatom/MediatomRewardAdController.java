package com.adgain.unified.controller.mediatom;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.yd.saas.base.interfaces.AdViewVideoListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdVideo;

public class MediatomRewardAdController implements UnifiedAdController {
    private YdVideo video;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        MediatomAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 Mediatom 激励视频: " + placementId);

        video = new YdVideo.Builder(activity)
                .setKey(placementId)
                .setVideoListener(new AdViewVideoListener() {
                    @Override
                    public void onAdShow() {
                        callback.log("onAdShow");
                    }

                    @Override
                    public void onAdClose() {
                        callback.log("onAdClose");
                    }

                    @Override
                    public void onVideoPrepared() {
                        callback.log("onVideoPrepared");
                    }

                    @Override
                    public void onVideoReward(double reward) {
                        callback.log("onVideoReward: " + reward);
                    }

                    @Override
                    public void onVideoCompleted() {
                        callback.log("onVideoCompleted");
                    }

                    @Override
                    public void onAdClick(String url) {
                        callback.log("onAdClick: " + url);
                    }

                    @Override
                    public void onSkipVideo() {
                        callback.log("onSkipVideo");
                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        callback.log("onAdFailed: " + MediatomAdControllerUtils.errorInfo(error));
                    }
                })
                .build();
        video.requestRewardVideo();
    }

    @Override
    public boolean isReady() {
        return video != null && video.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 Mediatom 激励视频");
        video.show();
    }

    @Override
    public void destroy() {
        if (video != null) {
            video.destroy();
            video = null;
        }
    }
}
