package com.meishu.sdkdemo.adactivity.fullscreenvideo;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.fullscreenvideo.FullScreenAdLoadListener;
import com.meishu.sdk.core.ad.fullscreenvideo.FullScreenVideoEventAdLoader;
import com.meishu.sdk.core.ad.fullscreenvideo.IFullScreenMediaListener;
import com.meishu.sdk.core.ad.fullscreenvideo.IFullScreenVideoAd;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdError;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;
import com.meishu.sdkdemo.utils.LogUtil;

public class FullScreenVideoActivity extends Activity {

    private final static String TAG = "FullScreenVideoActivity";
    private FullScreenVideoEventAdLoader fullScreenVideoAdLoader;
    private IFullScreenVideoAd fullScreenVideoAd;

    private Button btnLoad;
    private Button btnShow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        btnLoad = findViewById(R.id.loadFullScreenVideoAd);
        btnShow = findViewById(R.id.showFullScreenVideoAd);
        EditText editPid = findViewById(R.id.alternativeFullScreenVideoAdPlaceID);
        editPid.setText(IdProviderFactory.getDefaultProvider().fullScreenVideo());

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShow.setEnabled(false);
                if (fullScreenVideoAdLoader != null) {
                    fullScreenVideoAdLoader.destroy();
                }
                MsAdSlot adSlot = new MsAdSlot.Builder()
                        .setPid(editPid.getText().toString())
                        .build();
                fullScreenVideoAdLoader = new FullScreenVideoEventAdLoader(FullScreenVideoActivity.this, adSlot, new FullScreenAdLoadListener() {
                    @Override
                    public void onLoadSuccess(IFullScreenVideoAd ad) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    }

                    @Override
                    public void onLoadFail(AdError adError) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    }

                    @Override
                    public void onRenderSuccess(IFullScreenVideoAd ad) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        btnShow.setEnabled(true);
                        fullScreenVideoAd = ad;
                        fullScreenVideoAd.setInteractionListener(new InteractionListener() {
                            @Override
                            public void onAdClicked() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onAdExposure() {
                                LogUtil.e(TAG,"ecpm="+fullScreenVideoAd.getData().getEcpm());
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onAdClosed() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                        });
                        fullScreenVideoAd.setMediaListener(new IFullScreenMediaListener() {
                            @Override
                            public void onVideoLoaded() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onVideoStart() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onVideoPause() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onVideoResume() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onVideoCompleted() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onVideoError() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }

                            @Override
                            public void onSkippedVideo() {
                                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                            }
                        });
                    }

                    @Override
                    public void onAdFail(IFullScreenVideoAd iFullScreenVideoAd, AdError adError, int i) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));

                    }

                });
                fullScreenVideoAdLoader.loadAd();
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullScreenVideoAd != null) {
                    fullScreenVideoAd.showAd(FullScreenVideoActivity.this);
                }
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fullScreenVideoAdLoader != null) {
            fullScreenVideoAdLoader.destroy();
        }
    }
}
