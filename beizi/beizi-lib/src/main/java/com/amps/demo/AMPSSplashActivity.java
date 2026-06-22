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

import xyz.adscope.amps.ad.splash.AMPSSplashAd;
import xyz.adscope.amps.ad.splash.AMPSSplashLoadEventListener;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;
import xyz.adscope.amps.tool.util.AMPSScreenUtil;


/**
 * 聚合开屏广告
 */
public class AMPSSplashActivity extends Activity {

    private static final String TAG = "AMPSSplashActivity";
    private AMPSSplashAd mSplashAd;
    private RelativeLayout containerFl;
    private String mSpaceId;
    private boolean finishOnDismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beizi_activity_splash);
        containerFl = (RelativeLayout) this.findViewById(R.id.adsFl);
        Intent intent = getIntent();
        if (intent != null) {
            mSpaceId = intent.getStringExtra("splashId");
            finishOnDismiss = "true".equals(intent.getStringExtra("finishOnDismiss"));
        }
        loadAMPSAd();
    }

    private void loadAMPSAd() {
        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(mSpaceId)
                .setTimeOut(5000)
                .setWidth(AMPSScreenUtil.getScreenWidth(this)) //单位PX
                .setHeight(AMPSScreenUtil.getScreenHeight(this))//单位PX
                .build();
        mSplashAd = new AMPSSplashAd(this, parameter, new AMPSSplashLoadEventListener() {

            @Override
            public void onAmpsAdLoaded() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdLoad");
                if (mSplashAd != null) {
                    mSplashAd.show(containerFl);
                }
            }

            @Override
            public void onAmpsAdFailed(AMPSError ampsError) {
                Toast.makeText(AMPSSplashActivity.this, "request fail ", Toast.LENGTH_LONG).show();
                Log.e(AMPSConstants.AMPS_LOG_TAG,
                        TAG + " onAdError code:" + ampsError.getCode() + ";" +
                                " message:" + ampsError.getMessage());
                jump();
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
                jumpWhenCanClick();
            }
        });
        mSplashAd.loadAd();
    }


    /**
     * 必须添加的标识
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。
     * 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick() {
        Log.d(AMPSConstants.AMPS_LOG_TAG,
                "jumpWhenCanClick canJumpImmediately== " + canJumpImmediately);
        if (canJumpImmediately) {
            jump();
        } else {
            canJumpImmediately = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(AMPSConstants.AMPS_LOG_TAG, "onResume canJumpImmediately== " + canJumpImmediately);
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(AMPSConstants.AMPS_LOG_TAG, "onPause canJumpImmediately== " + canJumpImmediately);
        canJumpImmediately = false;
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
        if (!finishOnDismiss) {
            this.startActivity(new Intent(AMPSSplashActivity.this, AMPSMainActivity.class));
        }
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onDestroy");
        if (mSplashAd != null) {
            mSplashAd.destroy();
        }
        mSplashAd = null;
    }

}
