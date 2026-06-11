package com.meishu.sdkdemo.adactivity.interstitial;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.interstitial.InterstitialAd;
import com.meishu.sdk.core.ad.interstitial.InterstitialAdEventListener;
import com.meishu.sdk.core.ad.interstitial.InterstitialAdLoader;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;
import com.meishu.sdk.core.utils.LogUtil;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;

public class InterstitialAdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InterstitialADActivity";

    private InterstitialAd interstitialAd1;
    private InterstitialAd interstitialAd2;

    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);

        ((EditText) findViewById(R.id.alternativeInterstitailADPlaceID)).setText(IdProviderFactory.getDefaultProvider().insertScreen());

        findViewById(R.id.loadInterstitailAD).setOnClickListener(this);
        findViewById(R.id.showInterstitailAD1).setOnClickListener(this);
        findViewById(R.id.showInterstitailAD2).setOnClickListener(this);
        findViewById(R.id.openNewActivity).setOnClickListener(this);
    }

    private InterstitialAdLoader interstitialAdLoader1;
    private InterstitialAdLoader interstitialAdLoader2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadInterstitailAD:
                findViewById(R.id.showInterstitailAD1).setEnabled(false);
                findViewById(R.id.showInterstitailAD2).setEnabled(false);
                findViewById(R.id.openNewActivity).setEnabled(false);
                if (interstitialAdLoader1 != null) {
                    interstitialAdLoader1.destroy();
                }
                if (interstitialAdLoader2 != null) {
                    interstitialAdLoader2.destroy();
                }

                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

                placeId = ((EditText) findViewById(R.id.alternativeInterstitailADPlaceID)).getText().toString().trim();
                if (TextUtils.isEmpty(placeId)) {
                    placeId = IdProviderFactory.getDefaultProvider().insertScreen();
                }

                MsAdSlot msAdSlot = new MsAdSlot.Builder()
                        .setPid(placeId)
                        .setIsClickToClose(true)
                        .build();
                interstitialAdLoader1 = new InterstitialAdLoader(this, msAdSlot, interstitialAdListener1);
                interstitialAdLoader1.loadAd();
                break;
            case R.id.showInterstitailAD1:
                interstitialAd1.showAd(this);
                break;
            case R.id.showInterstitailAD2:
                interstitialAd2.showAd(this);
                break;
            case R.id.openNewActivity:
                Intent intent = new Intent(this, InterstitialAdNewActivity.class);
                InterstitialAdNewActivity.setInterstitialAd1(interstitialAd1);
                InterstitialAdNewActivity.setInterstitialAd2(interstitialAd2);
                startActivity(intent);
                break;
        }
    }

    private InterstitialAdEventListener interstitialAdListener1 = new InterstitialAdEventListener() {

        @Override
        public void onAdError(AdErrorInfo errorInfo) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        }

        /**
         * 广告已经准备完毕，此时可以进行广告的展示操作和相关逻辑处理
         */
        @Override
        public void onAdReady(InterstitialAd interstitialAd) {
            MsAdSlot msAdSlot = new MsAdSlot.Builder()
                    .setPid(placeId)
                    .setIsClickToClose(true)
                    .build();
            interstitialAdLoader2 = new InterstitialAdLoader(InterstitialAdActivity.this, msAdSlot, interstitialAdListener2);
//            interstitialAdLoader2.loadAd();

            interstitialAd1 = interstitialAd;
            findViewById(R.id.showInterstitailAD1).setEnabled(true);
            findViewById(R.id.openNewActivity).setEnabled(true);
            interstitialAd.setInteractionListener(new InteractionListener() {
                @Override
                public void onAdClicked() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdExposure() {
                    LogUtil.e(TAG,"ecpm="+interstitialAd.getData().getEcpm());
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }
            });
        }


    };

    private InterstitialAdEventListener interstitialAdListener2 = new InterstitialAdEventListener() {

        @Override
        public void onAdError(AdErrorInfo errorInfo) {
            Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
        }

        /**
         * 广告已经准备完毕，此时可以进行广告的展示操作和相关逻辑处理
         */
        @Override
        public void onAdReady(InterstitialAd interstitialAd) {
            interstitialAd2 = interstitialAd;
            findViewById(R.id.showInterstitailAD2).setEnabled(true);
            interstitialAd.setInteractionListener(new InteractionListener() {
                @Override
                public void onAdClicked() {
                    Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                @Override
                public void onAdExposure() {
                    LogUtil.e(TAG,"ecpm="+interstitialAd.getData().getEcpm());
                }

                @Override
                public void onAdClosed() {

                }

            });
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interstitialAdLoader1 != null) {
            interstitialAdLoader1.destroy();
        }
        if (interstitialAdLoader2 != null) {
            interstitialAdLoader2.destroy();
        }
    }
}
