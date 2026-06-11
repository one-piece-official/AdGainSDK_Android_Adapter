package com.meishu.sdkdemo.adactivity.paster;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.meishu.sdk.ad.nativ.IPreparedListener;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.paster.PasterAd;
import com.meishu.sdk.core.ad.paster.PasterAdEventLoader;
import com.meishu.sdk.core.ad.paster.PasterAdLoadListener;
import com.meishu.sdk.core.ad.paster.PasterInteractionListener;
import com.meishu.sdk.core.utils.AdError;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adactivity.AdConfig;
import com.meishu.sdkdemo.adactivity.DemoBaseActivity;
import com.meishu.sdkdemo.adid.IdProviderFactory;
import com.meishu.sdkdemo.utils.LogUtil;

/**
 * 视频贴片
 */
public class PasterActivity extends DemoBaseActivity {

    private static final String TAG = "PasterActivity";
    private PasterAd pasterAd;
    private ViewGroup videoContainer;
    private PasterAdEventLoader pasterAdLoader;
    private ImageView closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paster);

        closeBtn = findViewById(R.id.close_button);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pasterAd!=null){
                    pasterAd.destroy();
                }
                if (videoContainer!=null){
                    videoContainer.removeAllViews();
                }
                closeBtn.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.btn_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration mConfiguration = v.getResources().getConfiguration(); //获取设置的配置信息
                int ori = mConfiguration.orientation; //获取屏幕方向
                if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                    //横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
                } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
                    //竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
                }
            }
        });
        findViewById(R.id.btn_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pasterAd != null) {
                    pasterAd.resume();
                }
            }
        });
        findViewById(R.id.btn_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pasterAd != null) {
                    pasterAd.pause();
                }
            }
        });
        findViewById(R.id.btn_mute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pasterAd != null) {
                    pasterAd.mute();
                }
            }
        });
        findViewById(R.id.btn_unmute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pasterAd != null) {
                    pasterAd.unmute();
                }
            }
        });

        DisplayMetrics dm = getResources().getDisplayMetrics();
        ((EditText) findViewById(R.id.alternativePasterAdPlaceID)).setText(IdProviderFactory.getDefaultProvider().paster());
        findViewById(R.id.loadPasterAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

                String pid  = ((EditText) findViewById(R.id.alternativePasterAdPlaceID)).getText().toString().trim();
                if (TextUtils.isEmpty(pid)) {
                    pid = IdProviderFactory.getDefaultProvider().paster();
                }

                videoContainer = findViewById(R.id.video_container);

                if (pasterAd != null) {
                    pasterAd.destroy();
                    videoContainer.removeAllViews();
                }
                MsAdSlot adSlot = new MsAdSlot.Builder()
                        .setPid(pid)
                        .setWidth(dm.widthPixels)
                        .setHeight((int) (400*dm.density))
                        .setAutoRender(AdConfig.isAutoRender())
                        .build();
                pasterAdLoader = new PasterAdEventLoader(PasterActivity.this, videoContainer, adSlot, adEventListener);
                pasterAdLoader.loadAd();
            }
        });
    }

    PasterAdLoadListener adEventListener = new PasterAdLoadListener() {
        @Override
        public void onVideoLoaded() {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            closeBtn.setVisibility(View.VISIBLE);
            pasterAd.start();
            Toast.makeText(PasterActivity.this.getApplicationContext(), "开始播放", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoComplete() {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(PasterActivity.this.getApplicationContext(), "视频播放完毕", Toast.LENGTH_SHORT).show();
            pasterAdLoader.destroy();
        }

        @Override
        public void onLoadSuccess(PasterAd ad) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(PasterActivity.this,"加载成功",Toast.LENGTH_SHORT).show();
            pasterAd = ad;
        }

        @Override
        public void onLoadFail(AdError adError) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        }

        @Override
        public void onRenderSuccess(PasterAd ad) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
            Toast.makeText(PasterActivity.this,"渲染成功",Toast.LENGTH_SHORT).show();
            ad.setInteractionListener(new PasterInteractionListener() {
                @Override
                public void onAdClicked() {
                    // 点击时可以把广告关掉
//                pasterAdLoader.destroy();
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdExposure() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdClosed() {
                    // 媒体主动移除广告，不会触发（ms）adClose事件
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }
            });
            ad.setOnPreparedListener(new IPreparedListener() {
                @Override
                public void onPrepared() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }
            });
        }

        @Override
        public void onAdFail(PasterAd pasterAd, AdError adError, int i) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));

        }

    };


    @Override
    public void callRender() {
        super.callRender();
        if (pasterAd != null) {
            pasterAd.render();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pasterAd != null) {
            pasterAd.destroy();
        }
    }
}
