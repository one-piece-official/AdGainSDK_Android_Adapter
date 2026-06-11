package com.meishu.sdkdemo.adactivity.splash;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.splash.ISplashAd;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adactivity.AdConfig;
import com.meishu.sdkdemo.adactivity.DemoBaseActivity;
import com.meishu.sdkdemo.adid.IdProviderFactory;
import com.meishu.sdkdemo.utils.LogUtil;

public class SplashActivity extends DemoBaseActivity {
    private static final String TAG = "SplashActivity_";

    private ISplashAd splashAd;
    private boolean canJump = false;
    private Button btnShow;
    private Button btnSkip;
    private boolean autoShow;
    private FrameLayout frameLayout;
    ViewGroup adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_splash);
        adContainer = findViewById(R.id.splash_container);
        adContainer.post(new Runnable() {
            @Override
            public void run() {
                adContainer.getWidth();
                adContainer.getHeight();
            }
        });
        ImageView logoImage = findViewById(R.id.splash_holder);

        Log.e(TAG, "onCreate: ---"+adContainer.getContext() );
        String pid  = getIntent().getStringExtra("alternativePlaceId");

        if (TextUtils.isEmpty(pid)) {
            pid = IdProviderFactory.getDefaultProvider().splash();
        }

        btnShow = findViewById(R.id.btn_show);
        btnSkip = findViewById(R.id.btn_skip);


        Integer id = getIntent().getIntExtra("id", -1);
        DisplayMetrics dm = getResources().getDisplayMetrics();


        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        LogUtil.d(TAG,"屏幕宽度 widthPixel ="+dm.widthPixels+" 屏幕高度 heightPixel="+dm.heightPixels);
//        LogUtil.d(TAG,"acceptAdHeight ="+(int) (dm.heightPixels-120*dm.density));

        int statusHeight = 0;
        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        LogUtil.d(TAG,"statusHeight="+statusHeight);
        int bottomHeight = (int) (120 * dm.density);
        MsAdSlot adSlot = new MsAdSlot.Builder()
                .setPid(pid)
                .setFetchCount(1)
                .setWidth(dm.widthPixels)
                .setHeight(dm.heightPixels - bottomHeight)
//                .setWidth(1176)
//                .setHeight(2058)
                .setIsHideSkipBtn(false)
                .build();

        switch (id) {
            case R.id.loadAndShowSplashAd:

                splashAdLoader = new SplashAdEventLoader(this, adSlot, adEventListener, 5000);
                splashAdLoader.loadAd();
                autoShow = true;
                break;
            case R.id.loadSplashAd:
                splashAdLoader = new SplashAdEventLoader(this, adSlot, adEventListener, 5000);
                btnShow.setVisibility(View.VISIBLE);
//                splashAdLoader.hideSkipBtn(true);
                splashAdLoader.loadAd();
                autoShow = false;
                break;
            case R.id.customSkipSplashAd:
                splashAdLoader = new SplashAdEventLoader(this, adSlot, adEventListener, 5000);
                btnSkip.setVisibility(View.VISIBLE);
                splashAdLoader.loadAd(btnSkip);
                autoShow = true;
                break;
            default:
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

    SplashAdLoadListener adEventListener = new SplashAdLoadListener() {

        @Override
        public void onLoadSuccess(ISplashAd ad) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(SplashActivity.this,"加载成功",Toast.LENGTH_SHORT).show();
            splashAd = ad;
            try {
                ad.getData().getAdInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadFail(AdError adError) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(SplashActivity.this.getApplicationContext(), "没有加载到广告", Toast.LENGTH_SHORT).show();
            SplashActivity.this.finish();
        }

        @Override
        public void onRenderSuccess(ISplashAd ad) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(SplashActivity.this,"渲染成功",Toast.LENGTH_SHORT).show();
            if (splashAd==null) return;
            if (!autoShow) {
                btnShow.setEnabled(true);
            }
            ad.setInteractionListener(new SplashInteractionListener() {

                @Override
                public void onSkip() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onTimeOver() {
                    // 仅支持msad和穿山甲，倒计时结束时回调
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onTick(long leftMillisecond) {
                    // 仅支持msad和广点通，回调剩余时间
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()) + "  " + leftMillisecond);
                    btnSkip.setText(leftMillisecond + "");
                }

                @Override
                public void onAdClicked() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdExposure() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));

                    next();
                }

            });

            if (autoShow) {
                LogUtil.d(TAG,"autoShow");
                splashAd.showAd(adContainer);
            }

        }

        @Override
        public void onAdFail(ISplashAd iSplashAd, AdError adError, int i) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(SplashActivity.this.getApplicationContext(), "渲染失败:" + adError.getMessage(), Toast.LENGTH_SHORT).show();
            SplashActivity.this.finish();
        }

    };

    @Override
    public void callRender() {
        super.callRender();
        if (splashAd != null) {
            splashAd.render();
        }
    }

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
        if (canJump){
            this.finish();
        }else {
            canJump = true;
        }

    }



}
