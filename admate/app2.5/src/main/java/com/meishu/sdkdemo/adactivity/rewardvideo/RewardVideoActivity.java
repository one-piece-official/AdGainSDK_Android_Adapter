package com.meishu.sdkdemo.adactivity.rewardvideo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.reward.RewardAdEventListener;
import com.meishu.sdk.core.ad.reward.RewardAdMediaListener;
import com.meishu.sdk.core.ad.reward.RewardVideoAd;
import com.meishu.sdk.core.ad.reward.RewardVideoLoader;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;
import com.meishu.sdk.core.utils.LogUtil;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;

import java.util.Map;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class RewardVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RewardVideoActivity_";

    private RewardVideoAd ad;
    private RewardVideoAd ad2;
    private RewardVideoLoader rewardVideoLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
        findViewById(R.id.change_orientation).setOnClickListener(this);
        findViewById(R.id.load_video).setOnClickListener(this);

        String posId;
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == ORIENTATION_PORTRAIT) {
            posId = IdProviderFactory.getDefaultProvider().rewardPortrait();
        } else if (currentOrientation == ORIENTATION_LANDSCAPE) {
            posId = IdProviderFactory.getDefaultProvider().rewardLandscape();
        } else {
            LogUtil.e(TAG, "orientation error");
            return;
        }

        ((EditText) findViewById(R.id.alternativeRewardVideoAdPlaceID)).setText(posId);

        MsAdSlot msAdSlot = new MsAdSlot.Builder()
                .setPid(posId)
                .setVideoMute(false)
                .build();
        rewardVideoLoader = new RewardVideoLoader(this, msAdSlot, adEventListener);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.change_orientation:
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (currentOrientation == ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.load_video:
                this.ad = null;
                this.ad2 = null;

                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
                String pid  = ((EditText) findViewById(R.id.alternativeRewardVideoAdPlaceID)).getText().toString().trim();
                if (TextUtils.isEmpty(pid)) {
                    return;
                }

                MsAdSlot msAdSlot = new MsAdSlot.Builder()
                        .setPid(pid)
                        .build();
                if (null == rewardVideoLoader) {
                    rewardVideoLoader   = new RewardVideoLoader(this, msAdSlot, adEventListener);
                } else if (!rewardVideoLoader.getPosId().equals(pid)) {
                    rewardVideoLoader.destroy();
                    rewardVideoLoader   = new RewardVideoLoader(this, msAdSlot, adEventListener);
                }

                rewardVideoLoader.loadAd();
//                rewardVideoLoader.loadAd();
                break;
        }
    }

    RewardAdEventListener adEventListener = new RewardAdEventListener() {
        @Override
        public void onVideoCached(RewardVideoAd ad) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        }

        @Override
        public void onReward(Map<String, Object> map) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        }



        @Override
        public void onAdError(AdErrorInfo errorInfo) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        }

        /**
         * 广告已经准备完毕，此时可以进行广告的展示操作和相关逻辑处理
         */
        @Override
        public void onAdReady(RewardVideoAd ad) {
            LogUtil.e(TAG,"onAdReady: ad="+ad);
            if (RewardVideoActivity.this.ad == null) {
                RewardVideoActivity.this.ad = ad;
                View show1 = findViewById(R.id.show1);
                show1.setEnabled(true);
                show1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (RewardVideoActivity.this.ad==null)return;
                        RewardVideoActivity.this.ad.showAd(RewardVideoActivity.this);
                    }
                });
            } else {
                RewardVideoActivity.this.ad2 = ad;
                View show2 = findViewById(R.id.show2);
                show2.setEnabled(true);
                show2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ad2==null)return;
                        RewardVideoActivity.this.ad2.showAd(RewardVideoActivity.this);
                    }
                });
            }
//        ad.setInteractionListener(new InteractionListener() {
//            @Override
//            public void onAdClicked() {
//                Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
//            }
//        });
            ad.setInteractionListener(new InteractionListener() {
                @Override
                public void onAdClicked() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdExposure() {
                    LogUtil.e(TAG,"ecpm="+ad.getData().getEcpm());
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

            });
            ad.setMediaListener(new RewardAdMediaListener() {
                @Override
                public void onVideoStart() {

                }

                @Override
                public void onVideoPause() {

                }

                @Override
                public void onVideoResume() {

                }

                @Override
                public void onVideoCompleted() {
                    Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onVideoError() {

                }

                @Override
                public void onSkippedVideo() {

                }
            });
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 单独销毁广告对象
//        if (ad != null) {
//            ad.destroy();
//        }
//        if (ad2 != null) {
//            ad2.destroy();
//        }
        // 销毁 loader 会销毁已加载的所有广告，建议使用此方法，而不是每个广告单独销毁
        // 一定要调用此方法，否则会产生内存泄漏
        if (rewardVideoLoader != null) {
            rewardVideoLoader.destroy();
        }
    }
}
