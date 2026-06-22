package com.meishu.sdkdemo.adactivity.fullscreenvideo;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.fullscreenvideo.FullScreenAdEventListener;
import com.meishu.sdk.core.ad.fullscreenvideo.FullScreenVideoAdLoader;
//import com.meishu.sdk.core.ad.fullscreenvideo.IFullScreenMediaListener;
import com.meishu.sdk.core.ad.fullscreenvideo.IFullScreenVideoAd;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;
import com.meishu.sdk.core.utils.LogUtil;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;

public class FullScreenVideoActivity extends Activity {

    private final static String TAG = "FullScreenVideoActivity";
    private FullScreenVideoAdLoader fullScreenVideoAdLoader;
    private IFullScreenVideoAd fullScreenVideoAd;

    private Button btnLoad;
    private Button btnShow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admate_activity_full_screen_video);

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
                MsAdSlot msAdSlot = new MsAdSlot.Builder()
                        .setPid(editPid.getText().toString())
                        .build();
                fullScreenVideoAdLoader = new FullScreenVideoAdLoader(FullScreenVideoActivity.this, msAdSlot, new FullScreenAdEventListener() {
                    @Override
                    public void onAdError(AdErrorInfo errorInfo) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    }

                    /**
                     * 广告已经准备完毕，此时可以进行广告的展示操作和相关逻辑处理
                     */
                    @Override
                    public void onAdReady(IFullScreenVideoAd fullScreenVideoAd) {
                        btnShow.setEnabled(true);
                        FullScreenVideoActivity.this.fullScreenVideoAd = fullScreenVideoAd;
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
