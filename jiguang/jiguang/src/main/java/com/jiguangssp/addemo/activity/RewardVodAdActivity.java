package com.jiguangssp.addemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.jiguang.jgssp.ad.ADJgRewardVodAd;
import cn.jiguang.jgssp.ad.data.ADJgRewardVodAdInfo;
import cn.jiguang.jgssp.ad.entity.ADJgExtraParams;
import cn.jiguang.jgssp.ad.entity.ADJgRewardExtra;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgRewardVodAdListener;
import cn.jiguang.jgssp.util.ADJgAdUtil;
import com.jiguangssp.addemo.R;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

/**
 * @author ciba
 * @description 激励视频广告示例
 * @date 2020/3/27
 */
public class RewardVodAdActivity extends BaseAdActivity implements View.OnClickListener {
    private ADJgRewardVodAdInfo rewardVodAdInfo;
    private ADJgRewardVodAd rewardVodAd;

    private boolean loadAndShow;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_vod);
        initListener();
        initAd();
    }

    private void initListener() {
        findViewById(R.id.btnLoadAd).setOnClickListener(this);
        findViewById(R.id.btnShowAd).setOnClickListener(this);
        findViewById(R.id.btnLoadAndShowAd).setOnClickListener(this);
    }

    private void initAd() {
        // 创建激励视频广告实例
        rewardVodAd = new ADJgRewardVodAd(this);
        ADJgRewardExtra adJgRewardExtra = new ADJgRewardExtra("userId");
        adJgRewardExtra.setCustomData("额外参数");
        adJgRewardExtra.setRewardName("激励名称");
        adJgRewardExtra.setRewardAmount(1);

        // 创建额外参数实例
        ADJgExtraParams extraParams = new ADJgExtraParams.Builder()
                .rewardExtra(adJgRewardExtra)
                // 设置视频类广告是否静音
                .setVideoWithMute(ADJgDemoConstant.REWARD_AD_PLAY_WITH_MUTE)
                .build();

        rewardVodAd.setLocalExtraParams(extraParams);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        rewardVodAd.setOnlySupportPlatform(ADJgDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM);
        // 设置激励视频广告监听
        rewardVodAd.setListener(new ADJgRewardVodAdListener() {
            @Override
            public void onVideoCache(ADJgRewardVodAdInfo adJgRewardVodAdInfo) {
                // 目前汇量和Inmobi走了该回调之后才准备好
                Log.d(ADJgDemoConstant.TAG, "onVideoCache...");
            }

            @Override
            public void onVideoComplete(ADJgRewardVodAdInfo adJgRewardVodAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onVideoComplete...");
            }

            @Override
            public void onVideoError(ADJgRewardVodAdInfo adJgRewardVodAdInfo, ADJgError adJgError) {
                Log.d(ADJgDemoConstant.TAG, "onVideoError..." + adJgError.toString());
            }

            @Override
            public void onReward(ADJgRewardVodAdInfo adJgRewardVodAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onReward...");
            }

            @Override
            public void onAdReceive(ADJgRewardVodAdInfo rewardVodAdInfo) {
                RewardVodAdActivity.this.rewardVodAdInfo = rewardVodAdInfo;
                Toast.makeText(getApplicationContext(), "激励视频广告获取成功", Toast.LENGTH_LONG).show();
                Log.d(ADJgDemoConstant.TAG, "onAdReceive...");
                if (loadAndShow) {
                    ADJgAdUtil.showRewardVodAdConvenient(RewardVodAdActivity.this, rewardVodAdInfo, false);
                }
            }

            @Override
            public void onAdExpose(ADJgRewardVodAdInfo adJgRewardVodAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(ADJgRewardVodAdInfo adJgRewardVodAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(ADJgRewardVodAdInfo adJgRewardVodAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClose...");
            }

            @Override
            public void onAdFailed(ADJgError adJgError) {
                if (adJgError != null) {
                    String failedJosn = adJgError.toString();
                    Log.d(ADJgDemoConstant.TAG, "onAdFailed..." + failedJosn);
                    Toast.makeText(getApplicationContext(), "广告获取失败" + failedJosn, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadAd:
                loadAndShow = false;
                loadAd();
                break;
            case R.id.btnShowAd:
                ADJgAdUtil.showRewardVodAdConvenient(this, rewardVodAdInfo, false);
                break;
            case R.id.btnLoadAndShowAd:
                loadAndShow = true;
                loadAd();
                break;
            default:
                break;
        }
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        // 激励广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        rewardVodAd.setSceneId(ADJgDemoConstant.REWARD_VOD_AD_SCENE_ID);
        // 加载激励视频广告，参数为广告位ID
        rewardVodAd.loadAd(ADJgDemoConstant.REWARD_VOD_AD_POS_ID);
    }

}
