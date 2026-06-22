package com.adgain.unified.controller.taku;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoExListener;
import com.test.ad.demo.AdConst;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.util.SDKUtil;

import java.util.HashMap;
import java.util.Map;

public class TakuRewardAdController implements UnifiedAdController {
    private ATRewardVideoAd rewardVideoAd;
    private String placementId;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        TakuAdControllerUtils.resetContainer(adContainer);
        this.placementId = placementId;
        SDKUtil.initSDK(activity.getApplicationContext());
        callback.log("开始加载 Taku 激励视频: " + placementId);

        rewardVideoAd = new ATRewardVideoAd(activity, placementId);
        rewardVideoAd.setAdListener(new ATRewardVideoExListener() {
            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                callback.log("onDeeplinkCallback: " + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                callback.log("onDownloadConfirm");
            }

            @Override
            public void onRewardedVideoAdAgainPlayStart(ATAdInfo entity) {
                callback.log("onRewardedVideoAdAgainPlayStart");
            }

            @Override
            public void onRewardedVideoAdAgainPlayEnd(ATAdInfo entity) {
                callback.log("onRewardedVideoAdAgainPlayEnd");
            }

            @Override
            public void onRewardedVideoAdAgainPlayFailed(AdError errorCode, ATAdInfo entity) {
                callback.log("onRewardedVideoAdAgainPlayFailed: " + errorInfo(errorCode));
            }

            @Override
            public void onRewardedVideoAdAgainPlayClicked(ATAdInfo entity) {
                callback.log("onRewardedVideoAdAgainPlayClicked");
            }

            @Override
            public void onAgainReward(ATAdInfo entity) {
                callback.log("onAgainReward");
            }

            @Override
            public void onAgainRewardFailed(ATAdInfo entity) {
                callback.log("onAgainRewardFailed");
            }

            @Override
            public void onRewardedVideoAdLoaded() {
                callback.log("onRewardedVideoAdLoaded");
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                callback.log("onRewardedVideoAdFailed: " + errorInfo(errorCode));
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                callback.log("onRewardedVideoAdPlayStart");
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                callback.log("onRewardedVideoAdPlayEnd");
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                callback.log("onRewardedVideoAdPlayFailed: " + errorInfo(errorCode));
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                callback.log("onRewardedVideoAdClosed");
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                callback.log("onRewardedVideoAdPlayClicked");
            }

            @Override
            public void onReward(ATAdInfo entity) {
                callback.log("onReward");
            }

            @Override
            public void onRewardFailed(ATAdInfo entity) {
                callback.log("onRewardFailed");
            }
        });
        rewardVideoAd.setAdSourceStatusListener(new BaseActivity.ATAdSourceStatusListenerImpl());
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, "test_userid_001");
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, "test_userdata_001");
        rewardVideoAd.setLocalExtra(localMap);
        rewardVideoAd.load();
    }

    @Override
    public boolean isReady() {
        if (rewardVideoAd == null) {
            return false;
        }
        ATAdStatusInfo statusInfo = rewardVideoAd.checkAdStatus();
        return statusInfo != null && statusInfo.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 Taku 激励视频");
        ATRewardVideoAd.entryAdScenario(placementId, AdConst.SCENARIO_ID.REWARD_VIDEO_AD_SCENARIO);
        rewardVideoAd.show(activity, TakuAdControllerUtils.showConfig(
                AdConst.SCENARIO_ID.REWARD_VIDEO_AD_SCENARIO,
                AdConst.SHOW_CUSTOM_EXT.REWARD_VIDEO_AD_SHOW_CUSTOM_EXT
        ));
    }

    @Override
    public void destroy() {
        if (rewardVideoAd != null) {
            rewardVideoAd.setAdSourceStatusListener(null);
            rewardVideoAd.setAdDownloadListener(null);
            rewardVideoAd.setAdListener(null);
            rewardVideoAd.setAdMultipleLoadedListener(null);
            rewardVideoAd = null;
        }
    }

    private String errorInfo(AdError adError) {
        return adError == null ? "" : adError.getFullErrorInfo();
    }
}
