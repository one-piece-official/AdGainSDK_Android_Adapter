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

import xyz.adscope.amps.ad.interstitial.AMPSInterstitialAd;
import xyz.adscope.amps.ad.interstitial.AMPSInterstitialLoadEventListener;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;


/**
 * 聚合开屏广告
 */
public class AMPSInterstitialActivity extends Activity {

    private static final String TAG = "AMPSInterstitialActivity";
    private AMPSInterstitialAd mAMPSInterstitialAd;
    //超时时间
    private int mTimeOut = 5000;

    private TextView loadView, showView;
    private String mInterstitialId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beizi_activity_amps_interstitial);
        loadView = findViewById(R.id.amps_interstitial_ad_load_button);
        showView = findViewById(R.id.amps_interstitial_ad_show_button);
        Intent intent = getIntent();
        if (intent != null) {
            mInterstitialId = intent.getStringExtra("interstitialId");
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
                if (mAMPSInterstitialAd != null) {
                    mAMPSInterstitialAd.show(AMPSInterstitialActivity.this);
                }
            }
        });
    }

    private void loadAMPSAd() {
        mAMPSInterstitialAd = null;
        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(mInterstitialId)
                .setTimeOut(mTimeOut)
                .setWidth(600)
                .setHeight(600)
                .build();
        mAMPSInterstitialAd = new AMPSInterstitialAd(this, parameter,
                new AMPSInterstitialLoadEventListener() {
                    @Override
                    public void onAmpsSkippedAd() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsSkippedAd");
                    }

                    @Override
                    public void onAmpsAdLoaded() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdLoad");
                        showView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAmpsAdFailed(AMPSError ampsError) {
                        Log.e(AMPSConstants.AMPS_LOG_TAG,
                                TAG + " onAdError code:" + ampsError.getCode() + ";" +
                                        " message:" + ampsError.getMessage());
                    }

                    @Override
                    public void onAmpsAdShow() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdShow");
                    }

                    @Override
                    public void onAmpsAdClicked() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdClicked");
                    }

                    @Override
                    public void onAmpsAdDismiss() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdDismiss");
                        showView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAmpsVideoPlayStart() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsVideoPlayStart");
                    }

                    @Override
                    public void onAmpsVideoPlayEnd() {
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsVideoPlayEnd");
                    }
                });

        mAMPSInterstitialAd.loadAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAMPSInterstitialAd != null) {
            mAMPSInterstitialAd.destroy();
        }
        mAMPSInterstitialAd = null;
    }

}
