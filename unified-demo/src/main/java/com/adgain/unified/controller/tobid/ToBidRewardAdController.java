package com.adgain.unified.controller.tobid;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.reward.WMRewardAd;
import com.windmill.sdk.reward.WMRewardAdListener;
import com.windmill.sdk.reward.WMRewardAdRequest;
import com.windmill.sdk.reward.WMRewardInfo;

import java.util.HashMap;
import java.util.Map;

public class ToBidRewardAdController implements UnifiedAdController {
    private static final String USER_ID = "123456789";

    private WMRewardAd rewardAd;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        adContainer.removeAllViews();
        adContainer.setVisibility(ViewGroup.GONE);
        callback.log("开始加载 ToBid 激励视频: " + placementId);

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", USER_ID);
        rewardAd = new WMRewardAd(activity, new WMRewardAdRequest(placementId, USER_ID, options));
        rewardAd.setRewardedAdListener(new WMRewardAdListener() {
            @Override
            public void onVideoAdLoadSuccess(String placementId) {
                callback.log("onVideoAdLoadSuccess: " + placementId);
            }

            @Override
            public void onVideoAdPlayEnd(AdInfo adInfo) {
                callback.log("onVideoAdPlayEnd");
            }

            @Override
            public void onVideoAdPlayStart(AdInfo adInfo) {
                callback.log("onVideoAdPlayStart");
            }

            @Override
            public void onVideoAdClicked(AdInfo adInfo) {
                callback.log("onVideoAdClicked");
            }

            @Override
            public void onVideoAdClosed(AdInfo adInfo) {
                callback.log("onVideoAdClosed");
            }

            @Override
            public void onVideoRewarded(AdInfo adInfo, WMRewardInfo rewardInfo) {
                callback.log("onVideoRewarded: " + rewardInfo);
            }

            @Override
            public void onVideoAdLoadError(WindMillError error, String placementId) {
                callback.log("onVideoAdLoadError: " + error + ", placementId=" + placementId);
            }

            @Override
            public void onVideoAdPlayError(WindMillError error, String placementId) {
                callback.log("onVideoAdPlayError: " + error + ", placementId=" + placementId);
            }
        });
        rewardAd.loadAd();
    }

    @Override
    public boolean isReady() {
        return rewardAd != null && rewardAd.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (isReady()) {
            callback.log("展示 ToBid 激励视频");
            HashMap<String, String> options = new HashMap<>();
            options.put(WMConstants.AD_SCENE_ID, "567");
            options.put(WMConstants.AD_SCENE_DESC, "转盘抽奖");
            rewardAd.show(activity, options);
        } else {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void destroy() {
        if (rewardAd != null) {
            rewardAd.destroy();
            rewardAd = null;
        }
    }
}
