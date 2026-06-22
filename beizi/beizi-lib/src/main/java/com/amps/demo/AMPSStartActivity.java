package com.amps.demo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import xyz.adscope.amps.AMPSSDK;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSPrivacyConfig;
import xyz.adscope.amps.init.AMPSInitConfig;
import xyz.adscope.amps.init.inter.IAMPSInitCallback;
import xyz.adscope.common.tool.LogUtil;

public class AMPSStartActivity extends Activity {

    private EditText initEt, splashEt, nativeEt, interstitialEt, rewardVideoEt, bannerEt, drawEt, unifiedNativeEt;
    private TextView initTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beizi_activity_amps_start);
        initEt = findViewById(R.id.init_et);
        splashEt = findViewById(R.id.splash_et);
        nativeEt = findViewById(R.id.native_et);
        interstitialEt = findViewById(R.id.interstitial_et);
        rewardVideoEt = findViewById(R.id.reward_video_et);
        bannerEt = findViewById(R.id.banner_et);
        drawEt = findViewById(R.id.draw_et);
        unifiedNativeEt = findViewById(R.id.unified_native_et);
        initTv = findViewById(R.id.init_tv);
        setAdInfo();
        initTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAMPSSDK();
            }
        });
    }

    /**
     * 设置广告位信息
     */
    private void setAdInfo() {
        initEt.setText(Constants.AMPS_APPID);
        splashEt.setText(Constants.AMPS_SPACE_ID_SPLASH);
        nativeEt.setText(Constants.AMPS_SPACE_ID_NATIVE);
        interstitialEt.setText(Constants.AMPS_SPACE_ID_INTERSTITIAL);
        rewardVideoEt.setText(Constants.AMPS_SPACE_ID_REWARDVIDEO);
        bannerEt.setText(Constants.AMPS_SPACE_ID_BANNER);
        unifiedNativeEt.setText(Constants.AMPS_SPACE_ID_UNIFIED);
    }

    private void initAMPSSDK() {
        //建议开发者在此处调用初始化的方法，且此方法只需要一次调用
        AMPSInitConfig config = new AMPSInitConfig.Builder()
                .setAppId(initEt.getText().toString())
                .setAppName("testAppName")
                .openDebugLog(true)
                .setAMPSPrivacyConfig(new AMPSPrivacyConfig() {

                    //如需设置隐私政策相关内容，重写相应的方法进行设置

                    //如需关闭个性化推荐重写方法 return false
//                    @Override
//                    public boolean isSupportPersonalized() {
//                        return super.isSupportPersonalized();
//                    }

                    //如需传递oaid给sdk 重写方法 return 设备真实oaid值
//                    @Override
//                    public String getDevOaid() {
//                        return super.getDevOaid();
//                    }
                })
                .build();
        LogUtil.i(AMPSConstants.AMPS_LOG_TAG, "initAMPSSDK start");
        AMPSSDK.init(this, config, new IAMPSInitCallback() {
            @Override
            public void successCallback() {
                LogUtil.i(AMPSConstants.AMPS_LOG_TAG, "initAMPSSDK successCallback");
                Intent intent = new Intent();
                intent.putExtra("splashId", splashEt.getText().toString());
                intent.putExtra("nativeId", nativeEt.getText().toString());
                intent.putExtra("interstitialId", interstitialEt.getText().toString());
                intent.putExtra("rewardVideoId", rewardVideoEt.getText().toString());
                intent.putExtra("bannerId", bannerEt.getText().toString());
                intent.putExtra("drawId", drawEt.getText().toString());
                intent.putExtra("unifiedNativeId", unifiedNativeEt.getText().toString());
                intent.setClass(AMPSStartActivity.this, AMPSMainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void failCallback(AMPSError ampsError) {
                Toast.makeText(AMPSStartActivity.this, "init fail ", Toast.LENGTH_LONG).show();
                LogUtil.i(AMPSConstants.AMPS_LOG_TAG,
                        "initAMPSSDK failCallback ampsError : " + ampsError.toString());
            }
        });
    }

}
