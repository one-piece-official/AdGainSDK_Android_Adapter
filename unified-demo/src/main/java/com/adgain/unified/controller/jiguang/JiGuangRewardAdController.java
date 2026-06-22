package com.adgain.unified.controller.jiguang;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgRewardVodAd;
import cn.jiguang.jgssp.ad.data.ADJgRewardVodAdInfo;
import cn.jiguang.jgssp.ad.entity.ADJgExtraParams;
import cn.jiguang.jgssp.ad.entity.ADJgRewardExtra;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgRewardVodAdListener;
import cn.jiguang.jgssp.util.ADJgAdUtil;

public class JiGuangRewardAdController implements UnifiedAdController {
    private ADJgRewardVodAd rewardVodAd;
    private ADJgRewardVodAdInfo rewardVodAdInfo;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        JiGuangAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 JiGuang 激励视频: " + placementId);

        ADJgRewardExtra rewardExtra = new ADJgRewardExtra("userId");
        rewardExtra.setCustomData("额外参数");
        rewardExtra.setRewardName("激励名称");
        rewardExtra.setRewardAmount(1);

        rewardVodAd = new ADJgRewardVodAd(activity);
        rewardVodAd.setSceneId(ADJgDemoConstant.REWARD_VOD_AD_SCENE_ID);
        rewardVodAd.setOnlySupportPlatform(ADJgDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM);
        rewardVodAd.setLocalExtraParams(new ADJgExtraParams.Builder()
                .rewardExtra(rewardExtra)
                .setVideoWithMute(ADJgDemoConstant.REWARD_AD_PLAY_WITH_MUTE)
                .build());
        rewardVodAd.setListener(new ADJgRewardVodAdListener() {
            @Override
            public void onVideoCache(ADJgRewardVodAdInfo adInfo) {
                callback.log("onVideoCache");
            }

            @Override
            public void onVideoComplete(ADJgRewardVodAdInfo adInfo) {
                callback.log("onVideoComplete");
            }

            @Override
            public void onVideoError(ADJgRewardVodAdInfo adInfo, ADJgError error) {
                callback.log("onVideoError: " + JiGuangAdControllerUtils.errorInfo(error));
            }

            @Override
            public void onReward(ADJgRewardVodAdInfo adInfo) {
                callback.log("onReward");
            }

            @Override
            public void onAdReceive(ADJgRewardVodAdInfo adInfo) {
                rewardVodAdInfo = adInfo;
                callback.log("onAdReceive");
            }

            @Override
            public void onAdExpose(ADJgRewardVodAdInfo adInfo) {
                callback.log("onAdExpose");
            }

            @Override
            public void onAdClick(ADJgRewardVodAdInfo adInfo) {
                callback.log("onAdClick");
            }

            @Override
            public void onAdClose(ADJgRewardVodAdInfo adInfo) {
                callback.log("onAdClose");
            }

            @Override
            public void onAdFailed(ADJgError error) {
                callback.log("onAdFailed: " + JiGuangAdControllerUtils.errorInfo(error));
            }
        });
        rewardVodAd.loadAd(placementId);
    }

    @Override
    public boolean isReady() {
        return rewardVodAdInfo != null;
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        callback.log("展示 JiGuang 激励视频");
        ADJgAdUtil.showRewardVodAdConvenient(activity, rewardVodAdInfo, false);
    }

    @Override
    public void destroy() {
        if (rewardVodAd != null) {
            rewardVodAd.release();
            rewardVodAd = null;
        }
        rewardVodAdInfo = null;
    }
}
