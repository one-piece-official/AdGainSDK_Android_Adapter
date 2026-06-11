package com.meishu.sdkdemo.adactivity.banner;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.banner.BannerAdEventLoader;
import com.meishu.sdk.core.ad.banner.BannerAdLoadListener;
import com.meishu.sdk.core.ad.banner.IBannerAd;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdError;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adactivity.AdConfig;
import com.meishu.sdkdemo.adactivity.DemoBaseActivity;
import com.meishu.sdkdemo.adid.IdProviderFactory;
import com.meishu.sdkdemo.utils.LogUtil;

public class BannerAdActivity extends DemoBaseActivity implements View.OnClickListener {
    private static final String TAG = "BannerADActivity_";

    private BannerAdEventLoader bannerLoader;
    private ViewGroup bannerContainer;

    private boolean showCloseButton = true;
    private TextView showBannerAd;
    private IBannerAd bannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_ad);

        bannerContainer = findViewById(R.id.bannerContainer);
        showBannerAd = findViewById(R.id.showBannerAd);
        Button bannerAD = findViewById(R.id.loadBannerAd);
        bannerAD.setOnClickListener(this);
        showBannerAd.setOnClickListener(this);
        findViewById(R.id.loadBannerAdWithoutCloseBtn).setOnClickListener(this);

        ((EditText) findViewById(R.id.alternativeBannerAdPlaceID)).setText(IdProviderFactory.getDefaultProvider().banner());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadBannerAd:
            case R.id.loadBannerAdWithoutCloseBtn:
                showCloseButton = v.getId() == R.id.loadBannerAd;

                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

                String pid  = ((EditText) findViewById(R.id.alternativeBannerAdPlaceID)).getText().toString().trim();
                if (TextUtils.isEmpty(pid)) {
                    pid = IdProviderFactory.getDefaultProvider().banner();
                }

                bannerContainer.removeAllViews();
                MsAdSlot adSlot = new MsAdSlot.Builder()
                        .setPid(pid)
                        .setAutoRender(AdConfig.isAutoRender())
                        .setCloseButtonVisible(showCloseButton)
                        .setWidth(bannerContainer.getMeasuredWidth())
                        .setHeight(bannerContainer.getMeasuredHeight())
                        .build();
                bannerLoader = new BannerAdEventLoader(this, adSlot, new BannerAdLoadListener() {
                    @Override
                    public void onLoadSuccess(IBannerAd ad) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        Toast.makeText(BannerAdActivity.this,"加载成功",Toast.LENGTH_SHORT).show();
                        BannerAdActivity.this.bannerAd = ad;
                    }

                    @Override
                    public void onLoadFail(AdError adError) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    }

                    /**
                     * 广告已经准备完毕，此时可以进行广告的展示操作和相关逻辑处理
                     * @param ad
                     */
                    @Override
                    public void onRenderSuccess(IBannerAd ad) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        Toast.makeText(BannerAdActivity.this,"渲染成功",Toast.LENGTH_SHORT).show();
                        ad.getData().getPrice();
                        ad.getData().getEcpm();
                        ad.getData().getFromId();
                        ad.getData().getSdkName();
                    }

                    @Override
                    public void onAdFail(IBannerAd iBannerAd, AdError adError, int i) {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));

                    }

                });
                bannerLoader.loadAd();
                bannerLoader.destroy();
                break;

            case R.id.showBannerAd:

                if (bannerAd!=null){
                    // 不显示关闭按钮，仅限msad
//                    bannerAd.setCloseButtonVisible(showCloseButton);
                    // 适应 container 的大小需要设置宽高，仅限msad
//                    bannerAd.setWidthAndHeight(bannerContainer.getMeasuredWidth(), bannerContainer.getMeasuredHeight());
                    //bannerContainer.addView(bannerAd.getAdView());
                    bannerAd.setInteractionListener(new InteractionListener() {
                        @Override
                        public void onAdClicked() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }

                        @Override
                        public void onAdExposure() {
                            LogUtil.e(TAG,"ecpm="+bannerAd.getData().getEcpm());
                            LogUtil.e(TAG,"onAdExposure");
                        }

                        @Override
                        public void onAdClosed() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }
                    });
                    bannerAd.showAd(bannerContainer);
                }
                break;
        }
    }

    @Override
    public void callRender() {
        if (BannerAdActivity.this.bannerAd != null) {
            BannerAdActivity.this.bannerAd.render();
        }
        super.callRender();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.bannerLoader != null) {
            this.bannerLoader.destroy();
        }
    }
}
