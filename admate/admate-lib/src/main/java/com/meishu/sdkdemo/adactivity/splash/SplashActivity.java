package com.meishu.sdkdemo.adactivity.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.splash.ISplashAd;
import com.meishu.sdk.core.ad.splash.SplashAdEventListener;
import com.meishu.sdk.core.ad.splash.SplashAdLoader;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;
import com.meishu.sdk.core.utils.LogUtil;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity_";

    private ISplashAd splashAd;
    private boolean canJump = false;
    private Button btnShow;
    private Button btnSkip;
    private boolean autoShow;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.admate_activity_splash);
        final ViewGroup adContainer = findViewById(R.id.splash_container);
        adContainer.post(new Runnable() {
            @Override
            public void run() {
                adContainer.getWidth();
                adContainer.getHeight();
            }
        });
        ImageView logoImage = findViewById(R.id.splash_holder);

        Log.e(TAG, "onCreate: ---" + adContainer.getContext());
        String pid = getIntent().getStringExtra("alternativePlaceId");

        if (TextUtils.isEmpty(pid)) {
            pid = IdProviderFactory.getDefaultProvider().splash();
        }

        btnShow = findViewById(R.id.btn_show);
        btnSkip = findViewById(R.id.btn_skip);


        SplashAdLoader splashAdLoader;
        Integer id = getIntent().getIntExtra("id", -1);
        DisplayMetrics dm = getResources().getDisplayMetrics();


        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
//        LogUtil.d(TAG,"屏幕宽度 widthPixel ="+dm.widthPixels+" 屏幕高度 heightPixel="+dm.heightPixels);
//        LogUtil.d(TAG,"acceptAdHeight ="+(int) (dm.heightPixels-120*dm.density));

        int statusHeight = 0;
        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        LogUtil.d(TAG, "statusHeight=" + statusHeight);
        int bottomHeight = (int) (120 * dm.density);
        MsAdSlot msAdSlot = new MsAdSlot.Builder()
                .setPid(pid)
                .setFetchCount(1)
                .setWidth(dm.widthPixels)
                .setHeight(dm.heightPixels - bottomHeight)
//                .setWidth(1176)
//                .setHeight(2058)
                .build();

        if (id == R.id.loadAndShowSplashAd) {
            splashAdLoader = new SplashAdLoader(this, adContainer, msAdSlot, adEventListener, 5000);
            splashAdLoader.loadAndShow();
            autoShow = true;
            canJump = true;
        } else if (id == R.id.loadSplashAd) {
            splashAdLoader = new SplashAdLoader(this, msAdSlot, adEventListener, 5000);
            btnShow.setVisibility(View.VISIBLE);
            splashAdLoader.loadAd();
            autoShow = false;
            canJump = true;
        } else if (id == R.id.customSkipSplashAd) {
            splashAdLoader = new SplashAdLoader(this, adContainer, msAdSlot, adEventListener, 5000);
            btnSkip.setVisibility(View.VISIBLE);
            splashAdLoader.loadAndShow(btnSkip);
            autoShow = true;
        } else {
            throw new IllegalStateException("Unexpected value: " + id);
        }

        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splashAd != null) {
                    splashAd.showAd(adContainer);
                }
//                adContainer.addView(frameLayout);
            }
        });
    }

    SplashAdEventListener adEventListener = new SplashAdEventListener() {

        /**
         * 广告已经准备完毕，此时可以进行广告的展示操作和相关逻辑处理
         */
        @Override
        public void onAdReady(ISplashAd splashAd) {
            if (splashAd == null) return;
            Log.e(TAG, "onRenderSuccess: " + splashAd.getData().getEcpm());
            SplashActivity.this.splashAd = splashAd;
            if (!autoShow) {
                btnShow.setEnabled(true);
            }
//            frameLayout = new FrameLayout(SplashActivity.this);
//            splashAd.showAd(frameLayout);

//            frameLayout = new FrameLayout(SplashActivity.this);
//            splashAd.showAd(frameLayout);
            splashAd.setInteractionListener(new InteractionListener() {

                @Override
                public void onAdClicked() {
                    LogUtil.d(TAG, "DEMO onAdClicked " + splashAd.getData().getEcpm() );
                }

                @Override
                public void onAdExposure() {
                    LogUtil.d(TAG, "DEMO onAdExposure "  + " " + splashAd.getData().getEcpm());
                }

                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    next();
                }

            });
        }


        @Override
        public void onAdPresent(ISplashAd splashAd) {
            Log.d(TAG, "DEMO onAdPresent " + splashAd.getData().getEcpm());
        }

        @Override
        public void onAdSkip(ISplashAd splashAd) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            next();
        }

        @Override
        public void onAdTimeOver(ISplashAd splashAd) {
            // 仅支持msad和穿山甲，倒计时结束时回调
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            next();
        }

        @Override
        public void onAdTick(long leftMilliseconds) {
            // 仅支持msad和广点通，回调剩余时间
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()) + " " + leftMilliseconds);
            btnSkip.setText(leftMilliseconds + "");
        }

        @Override
        public void onAdError(AdErrorInfo errorInfo) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(SplashActivity.this.getApplicationContext(), "没有加载到广告", Toast.LENGTH_SHORT).show();
            SplashActivity.this.finish();
        }

    };


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        if (this.splashAd != null && canJump) {
            next();
        }
        canJump = true;
    }

    private void next() {
        Log.d(TAG, "DEMO next " + canJump);
        if (canJump) {
            this.finish();
        } else {
            canJump = true;
        }

    }


}
