package com.xm.demo.unit.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xm.demo.R;
import com.xm.demo.base.BaseActivity;
import com.yd.saas.base.interfaces.AdViewInterstitialListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdInterstitial;

import androidx.annotation.Nullable;

public class InterstitialActivity extends BaseActivity {
    private YdInterstitial ydInterstitial;
    private String key = "28af5c3ec9f697d1";
    private String TAG = "InterstitialActivity";
    public static void launch(Context context, String key) {
        Intent intent = new Intent(context, InterstitialActivity.class);
        intent.putExtra("media_id", key);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_view);
        String mediaId = getIntent().getStringExtra("media_id");
        if (!TextUtils.isEmpty(mediaId)) {
            this.key = mediaId;
        }
        requestInterstitial();
    }

    private void requestInterstitial() {
        ydInterstitial = new YdInterstitial.Builder(this)
                .setKey(key)
                .setWidth(600)
                .setHeight(800)
                .setInterstitialListener(new AdViewInterstitialListener() {
                    @Override
                    public void onAdReady() {
                        //请求成功时回调
                        showAd();
                    }

                    @Override
                    public void onAdDisplay() {
                        //广告展示时回调
                        Log.d(TAG, "onAdDisplay:");

                    }

                    @Override
                    public void onAdClick(String s) {
                        Log.d(TAG, "onAdClick:");

                    }

                    @Override
                    public void onAdClosed() {
                        //广告关闭时回调
                        Log.d(TAG, "onAdClosed:");

                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        //广告请求失败时回调

                        Log.d(TAG, "onAdFailed:"+error.toString());
                    }

                })
                .build();
        ydInterstitial.requestInterstitial();

        Log.d(TAG, "requestInterstitial");
    }
    // 展示广告
    private void showAd() {
        if (ydInterstitial != null && ydInterstitial.isReady()) {
            ydInterstitial.show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ydInterstitial.destroy();
    }
}
