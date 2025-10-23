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
import android.widget.RelativeLayout;
import android.widget.Toast;

import xyz.adscope.amps.ad.banner.AMPSBannerAd;
import xyz.adscope.amps.ad.banner.AMPSBannerLoadEventListener;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;
import xyz.adscope.amps.tool.util.AMPSScreenUtil;


/**
 * 聚合开屏广告
 */
public class AMPSBannerActivity extends Activity {

    private static final String TAG = "AMPSBannerActivity";
    private AMPSBannerAd ampsBannerAd;
    private RelativeLayout containerFl;
    //超时时间
    private long mTimeOut = 5000;
    private String mBannerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amps_banner);
        containerFl = (RelativeLayout) this.findViewById(R.id.adsFl_banner);
        Intent intent = getIntent();
        if (intent != null) {
            mBannerId = intent.getStringExtra("bannerId");
        }
        loadAMPSAd();
    }

    private void loadAMPSAd() {
        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(mBannerId)
                .setTimeOut(5000)
                .setWidth(AMPSScreenUtil.getScreenWidth(this))
                .setHeight(0)
                .build();
        ampsBannerAd = new AMPSBannerAd(this, parameter, new AMPSBannerLoadEventListener() {
            @Override
            public void onAmpsAdLoaded() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdLoaded");
                ampsBannerAd.show(containerFl);
            }

            @Override
            public void onAmpsAdFailed(AMPSError ampsError) {
                Toast.makeText(AMPSBannerActivity.this, "request fail ", Toast.LENGTH_LONG).show();
                Log.e(AMPSConstants.AMPS_LOG_TAG,
                        TAG + " onAmpsAdFailed code:" + ampsError.getCode() + ";" +
                                " message:" + ampsError.getMessage());
                finish();
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
            }
        });
        ampsBannerAd.loadAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ampsBannerAd != null) {
            ampsBannerAd.destroy();
        }
        ampsBannerAd = null;
        Log.e(TAG, "onDestroy");
    }


}
