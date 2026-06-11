package com.adgain.admate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.NativeAdAllEventListener;
import com.adgain.sdk.api.NativeAdData;
import com.meishu.sdk.core.ad.recycler.ExpressMediaListener;
import com.meishu.sdk.core.utils.MsAdPatternType;
import com.meishu.sdk.core.utils.MsInteractionType;
import com.meishu.sdk.platform.custom.recycler.MsCustomRecyclerAdapter;
import com.meishu.sdk.platform.custom.recycler.MsCustomRecyclerExpressAd;

import java.util.List;

public class CustomExpressAd extends MsCustomRecyclerExpressAd {
    private NativeAdData expressAd;
    private final MsCustomRecyclerAdapter adWrapper;
    private int price;

    public CustomExpressAd(MsCustomRecyclerAdapter wrapper, NativeAdData nativeExpressAd, int price) {
        super(wrapper);
        this.adWrapper = wrapper;
        this.expressAd = nativeExpressAd;
        this.price = price;
    }

    @Override
    public int getAdPatternType() {
        return MsAdPatternType.LARGE_IMAGE;
    }

    @Override
    public int getInteractionType() {
        return MsInteractionType.NORMAL;
    }


    @Override
    public void bindAdToView(Context context, ViewGroup container, List<View> clickableViews) {
        Log.d("----Adgain", "CustomExpressAd bindAdToView " + expressAd);

        expressAd.setNativeAdEventListener(new NativeAdAllEventListener() {
            @Override
            public void onAdClose(View view) {
                adWrapper.onExpressAdClosed(CustomExpressAd.this);
            }

            @Override
            public void onAdExposed() {
                Log.d("----Adgain", "onAdExposed ");

                adWrapper.onExpressAdExposure(CustomExpressAd.this);
            }

            @Override
            public void onAdClicked() {
                adWrapper.onExpressAdClicked(CustomExpressAd.this);
            }

            @Override
            public void onAdRenderFail(AdError adError) {
                Log.d("----Adgain", "onAdRenderFail ");
            }
        });
        if (container != null && getAdView() != null) {
            container.removeAllViews();
            container.addView(getAdView());
        }
    }

    @Override
    public void setExpressMediaListener(final ExpressMediaListener mediaListener) {
        Log.d("----Adgain", "setExpressMediaListener ");
        if (expressAd != null) {
        }
    }

    @Override
    public View getAdView() {
        Log.d("----Adgain", "getAdView " + expressAd);
        return expressAd.getFeedView();
    }

    @Override
    public void destroy() {
        if (expressAd != null) {
            expressAd.destroy();
            expressAd = null;
        }
    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }
}
