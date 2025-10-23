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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import xyz.adscope.amps.ad.nativead.AMPSNativeAd;
import xyz.adscope.amps.ad.nativead.AMPSNativeLoadEventListener;
import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdExpressListener;
import xyz.adscope.amps.ad.nativead.inter.AMPSNativeAdExpressInfo;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeAd;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeLoadEventListener;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedNativeItem;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;
import xyz.adscope.amps.tool.util.AMPSScreenUtil;


/**
 * 信息流模板
 */
public class AMPSNativeActivity extends Activity {

    private static final String TAG = "AMPSNativeActivity";
    private AMPSUnifiedNativeAd mNativeAd;
    private RelativeLayout containerFl;
    private List<AMPSUnifiedNativeItem> ampsNativeAdExpressInfoList;
    private String mNativeId;
    private TextView loadView, showView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amps_native);
        containerFl = (RelativeLayout) this.findViewById(R.id.amps_native_normal_layout);
        loadView = findViewById(R.id.amps_native_ad_load_button);
        showView = findViewById(R.id.amps_native_ad_show_button);
        Intent intent = getIntent();
        if (intent != null) {
            mNativeId = intent.getStringExtra("nativeId");
        }
        setListener();
    }

    private void setListener() {
        showView.setVisibility(View.GONE);
        loadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView.setVisibility(View.GONE);
                containerFl.removeAllViews();
                loadAMPSAd();

            }
        });
        showView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AMPSUnifiedNativeItem ampsNativeAdExpressInfo =
                        ampsNativeAdExpressInfoList.get(0);
                bindNativeExpressListener(ampsNativeAdExpressInfo);
                if (ampsNativeAdExpressInfo.getNativeExpressAdView() != null)
                    containerFl.addView(ampsNativeAdExpressInfo.getNativeExpressAdView());
            }
        });
    }

    private void loadAMPSAd() {
        if (mNativeAd != null) {
            mNativeAd.destroy();
            mNativeAd = null;
        }
        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(mNativeId)
                .setTimeOut(5000)
                .setWidth(AMPSScreenUtil.getScreenWidth(this)) //单位PX
                .setHeight(0)//单位PX 0代表自适应
                .setAdCount(1)
                .build();
        mNativeAd = new AMPSUnifiedNativeAd(this, parameter, new AMPSUnifiedNativeLoadEventListener() {
            @Override
            public void onAmpsAdLoad(List<AMPSUnifiedNativeItem> list) {
                ampsNativeAdExpressInfoList = list;
                showView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAmpsAdFailed(AMPSError ampsError) {
                Toast.makeText(AMPSNativeActivity.this, "request fail ", Toast.LENGTH_LONG).show();
                Log.e(AMPSConstants.AMPS_LOG_TAG,
                        TAG + " onAdError code:" + ampsError.getCode() + ";" +
                                " message:" + ampsError.getMessage());
            }

        });
        mNativeAd.loadAd();
    }

    private void bindNativeExpressListener(AMPSUnifiedNativeItem ampsNativeAdExpressInfo) {
        ampsNativeAdExpressInfo.setNativeAdExpressListener(new AMPSNativeAdExpressListener() {
            @Override
            public void onAdShow() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdShow");
                mNativeAd.destroy();
            }

            @Override
            public void onAdClicked() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdClicked");
            }

            @Override
            public void onAdClosed(View view) {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdClosed");
                containerFl.removeView(view);
                showView.setVisibility(View.GONE);
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onRenderFail");
                showView.setVisibility(View.GONE);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onRenderSuccess");
                containerFl.removeAllViews();
                containerFl.addView(view);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNativeAd != null) {
            mNativeAd.destroy();
        }
        Log.e(TAG, "onDestroy");
    }

}
