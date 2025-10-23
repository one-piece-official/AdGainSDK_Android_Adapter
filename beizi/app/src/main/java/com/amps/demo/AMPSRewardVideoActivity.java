/*
 * Copyright © 2017 Hubcloud.com.cn. All rights reserved.
 * SplashActivity.java
 * BeiZiSDK
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 */

package com.amps.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import xyz.adscope.amps.ad.reward.AMPSRewardVideoAd;
import xyz.adscope.amps.ad.reward.AMPSRewardVideoLoadEventListener;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;


/**
 * 聚合开屏广告
 */
public class AMPSRewardVideoActivity extends Activity {

    private static final String TAG = "AMPSRewardVideoActivity";
    private AMPSRewardVideoAd mAMPSRewardVideoAd;
    //超时时间
    private long mTimeOut = 5000;
    private TextView loadView, showView;
    private String mRewardVideoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amps_reward_video);
        loadView = findViewById(R.id.amps_reward_video_ad_load_button);
        showView = findViewById(R.id.amps_reward_video_ad_show_button);
        Intent intent = getIntent();
        if (intent != null) {
            mRewardVideoId = intent.getStringExtra("rewardVideoId");
        }
        setListener();
    }


    private void setListener() {
        showView.setVisibility(View.GONE);
        loadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView.setVisibility(View.GONE);
                loadAMPSAd();

            }
        });
        showView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAMPSRewardVideoAd != null) {
                    mAMPSRewardVideoAd.show(AMPSRewardVideoActivity.this);
                }
            }
        });
    }

    private void loadAMPSAd() {
        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(mRewardVideoId)
                .setTimeOut(5000)
                .setAdCount(1)
                .build();
        mAMPSRewardVideoAd = new AMPSRewardVideoAd(this, parameter,
                new AMPSRewardVideoLoadEventListener() {
                    @Override
                    public void onAmpsAdLoad() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdLoad");
                        showView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAmpsAdCached() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdCached");
                    }

                    @Override
                    public void onAmpsAdFailed(AMPSError error) {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdFailed");
                    }

                    @Override
                    public void onAmpsAdShow() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdShow");
                    }

                    @Override
                    public void onAmpsAdDismiss() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdClose");
                        showView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAmpsAdVideoClick() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdVideoClick");
                    }

                    @Override
                    public void onAmpsAdVideoComplete() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdVideoComplete");
                    }

                    @Override
                    public void onAmpsAdVideoError() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdVideoError");
                    }

                    @Override
                    public void onAmpsAdRewardArrived(boolean isRewardValid, int rewardType,
                                                      Map<String, Object> extraInfo) {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdRewardArrived");
                    }
                });
        mAMPSRewardVideoAd.loadAd();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAMPSRewardVideoAd != null) {
            mAMPSRewardVideoAd.destroy();
        }
        mAMPSRewardVideoAd = null;
    }

}
