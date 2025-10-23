package com.amps.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AMPSMainActivity extends AppCompatActivity {
    private static final String TAG = "AMPSMainActivity";

    TextView ampsSplashAdButton, ampsNativeAdButton,
            ampsRewardVideoAdButton, ampsInteractionAdButton, ampsBannerAdButton,
            ampsUnifiedNativeAdButton;
    private int count = 0;
    private String mSpaceId, mNativeId, mInterstitialId, mRewardVideoId, mBannerId, mUnifiedNativeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amps_main);
        Intent intent = getIntent();
        if (intent != null) {
            mSpaceId = intent.getStringExtra("splashId");
            mNativeId = intent.getStringExtra("nativeId");
            mInterstitialId = intent.getStringExtra("interstitialId");
            mRewardVideoId = intent.getStringExtra("rewardVideoId");
            mBannerId = intent.getStringExtra("bannerId");
            mUnifiedNativeId = intent.getStringExtra("unifiedNativeId");
        }
        initView();
        setOnClickEvent();
    }

    private void initView() {
        ampsSplashAdButton = findViewById(R.id.amps_splash_ad_button);
        ampsNativeAdButton = findViewById(R.id.amps_native_ad_button);
        ampsRewardVideoAdButton = findViewById(R.id.amps_reward_video_ad_button);
        ampsInteractionAdButton = findViewById(R.id.amps_interaction_ad_button);
        ampsBannerAdButton = findViewById(R.id.amps_banner_ad_button);
        ampsUnifiedNativeAdButton = findViewById(R.id.amps_unified_native_ad_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setOnClickEvent() {
        ampsSplashAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("splashId", mSpaceId);
                intent.setClass(AMPSMainActivity.this, AMPSSplashActivity.class);
                startActivity(intent);
            }
        });
        ampsNativeAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("nativeId", mNativeId);
                intent.setClass(AMPSMainActivity.this, AMPSNativeActivity.class);
                startActivity(intent);
            }
        });
        ampsRewardVideoAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("rewardVideoId", mRewardVideoId);
                intent.setClass(AMPSMainActivity.this, AMPSRewardVideoActivity.class);
                startActivity(intent);
            }
        });
        ampsInteractionAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("interstitialId", mInterstitialId);
                intent.setClass(AMPSMainActivity.this, AMPSInterstitialActivity.class);
                startActivity(intent);
            }
        });
        ampsBannerAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("bannerId", mBannerId);
                intent.setClass(AMPSMainActivity.this, AMPSBannerActivity.class);
                startActivity(intent);
            }
        });
        ampsUnifiedNativeAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("unifiedNativeId", mUnifiedNativeId);
                intent.setClass(AMPSMainActivity.this, AMPSUnifiedActivity.class);
                startActivity(intent);
            }
        });

    }

}