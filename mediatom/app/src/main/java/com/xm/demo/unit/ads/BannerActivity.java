package com.xm.demo.unit.ads;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.xm.demo.R;
import com.yd.saas.base.interfaces.AdViewBannerListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.config.utils.LogcatUtil;
import com.yd.saas.ydsdk.YdBanner;

public class BannerActivity extends Activity {

    private FrameLayout mFlContainer;
    YdBanner ydBanner;
    private String key = "9ad1d92c6db6d082";

    public static void launch(Context context, String trim) {
        Intent _Intent = new Intent(context, BannerActivity.class);
        _Intent.putExtra("media_id", trim);
        context.startActivity(_Intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        mFlContainer = (FrameLayout) findViewById(R.id.fl_container);
        String mediaId = getIntent().getStringExtra("media_id");
        if (!TextUtils.isEmpty(mediaId)) {
            this.key = mediaId;
        }
        loadAd();
    }

    private void loadAd() {
        ydBanner = new YdBanner.Builder(this)
                .setKey(key)
                .setBannerListener(new AdViewBannerListener() {

                    @Override
                    public void onReceived(View view) {
                        if (view != null) {
                            mFlContainer.addView(view);
                        }
                    }

                    @Override
                    public void onAdExposure() {

                    }

                    @Override
                    public void onClosed() {
                        LogcatUtil.e("YdSDK-Demo", "onClosed");
                    }

                    @Override
                    public void onAdClick(String url) {

                    }

                    @Override
                    public void onAdFailed(YdError error) {

                    }
                })
                .build();
        ydBanner.requestBanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ydBanner.destroy();
    }
}
