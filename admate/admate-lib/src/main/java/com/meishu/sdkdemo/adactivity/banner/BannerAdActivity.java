package com.meishu.sdkdemo.adactivity.banner;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.meishu.sdk.core.ad.banner.BannerAdEventListener;
import com.meishu.sdk.core.ad.banner.BannerAdLoader;
import com.meishu.sdk.core.ad.banner.IBannerAd;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;
import com.meishu.sdk.core.utils.LogUtil;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;

public class BannerAdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BannerADActivity";

    private BannerAdLoader bannerLoader;
    private ViewGroup bannerContainer;

    private TextView showBannerAd;
    private IBannerAd bannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admate_activity_banner_ad);

        bannerContainer = findViewById(R.id.bannerContainer);
        showBannerAd = findViewById(R.id.showBannerAd);
        Button bannerAD = findViewById(R.id.loadBannerAd);
        bannerAD.setOnClickListener(this);
        showBannerAd.setOnClickListener(this);

        ((EditText) findViewById(R.id.alternativeBannerAdPlaceID)).setText(IdProviderFactory.getDefaultProvider().banner());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.loadBannerAd) {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            }

            String pid  = ((EditText) findViewById(R.id.alternativeBannerAdPlaceID)).getText().toString().trim();
            if (TextUtils.isEmpty(pid)) {
                pid = IdProviderFactory.getDefaultProvider().banner();
            }

            bannerContainer.removeAllViews();
            MsAdSlot msAdSlot = new MsAdSlot.Builder()
                    .setPid(pid)
                    .build();
            bannerLoader = new BannerAdLoader(this, msAdSlot, new BannerAdEventListener() {
                @Override
                public void onAdError(AdErrorInfo errorInfo) {
                    Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                }

                /**
                 * 广告已经准备完毕，此时可以进行广告的展示操作和相关逻辑处理
                 */
                @Override
                public void onAdReady(IBannerAd iBannerAd) {
                    Toast.makeText(BannerAdActivity.this,"加载成功",Toast.LENGTH_SHORT).show();
                    BannerAdActivity.this.bannerAd = iBannerAd;
                    iBannerAd.getData().getPrice();
                    iBannerAd.getData().getEcpm();
                    iBannerAd.getData().getFromId();
                    iBannerAd.getData().getSdkName();
                }
            });
            bannerLoader.loadAd();
            bannerLoader.destroy();
        } else if (id == R.id.showBannerAd) {
            if (bannerAd!=null){
                // 适应 container 的大小需要设置宽高，仅限msad
                bannerAd.setWidthAndHeight(bannerContainer.getMeasuredWidth(), bannerContainer.getMeasuredHeight());
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
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.bannerLoader != null) {
            this.bannerLoader.destroy();
        }
    }
}
