package com.adgain.amps.adapter;

import android.util.Log;
import android.view.View;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.NativeAdAllEventListener;
import com.adgain.sdk.api.NativeAdData;

import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdExpressListener;
import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdapter;
import xyz.adscope.amps.ad.nativead.inter.AMPSNativeAdExpressInfo;

public class AdGainNativeExpressInfo implements AMPSNativeAdExpressInfo {
    private NativeAdData ad;
    private AMPSNativeAdapter adapter;
    private int ecpm;

    private AMPSNativeAdExpressListener ampsNativeAdExpressListener;

    public AdGainNativeExpressInfo(NativeAdData ad, AMPSNativeAdapter adapter, int ecpm) {
        this.ad = ad;
        this.adapter = adapter;
        this.ecpm = ecpm;
    }

    @Override
    public View getNativeExpressAdView() {
        if (null != ad) return ad.getFeedView();
        return null;
    }

    @Override
    public void render() {
        if (null != ad) try {
            if (ad.getFeedView() != null) {
                ad.setNativeAdEventListener(new NativeAdAllEventListener() {
                    @Override
                    public void onAdClose(View view) {
                        Log.d("AdGain", "AdGainNativeExpressInfo onAdClose");
                        if (adapter != null) {
                            adapter.onAdDismiss();
                        }
                        if (ampsNativeAdExpressListener != null) {
                            ampsNativeAdExpressListener.onAdClosed(getNativeExpressAdView());
                        }
                    }

                    @Override
                    public void onAdExposed() {
                        Log.d("AdGain", "AdGainNativeExpressInfo onAdExposed");
                        if (adapter != null) {
                            adapter.onAdShow();
                        }
                        if (ampsNativeAdExpressListener != null) {
                            ampsNativeAdExpressListener.onAdShow();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d("AdGain", "AdGainNativeExpressInfo onAdClicked");
                        if (adapter != null) {
                            adapter.onAdClicked();
                        }
                        if (ampsNativeAdExpressListener != null) {
                            ampsNativeAdExpressListener.onAdClicked();
                        }
                    }

                    @Override
                    public void onAdRenderFail(AdError adError) {
                        if (adapter != null) {
                            adapter.onAdShowFailed(String.valueOf(adError.getErrorCode()), adError.getMessage());
                        }
                    }
                });
                if (adapter != null) {
                    adapter.onRenderSuccess();
                }
                if (ampsNativeAdExpressListener != null) {
                    ampsNativeAdExpressListener.onRenderSuccess(ad.getFeedView(),
                            ad.getFeedView().getWidth(), ad.getFeedView().getHeight());
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void destroy() {
        if (ad != null) ad.destroy();
    }

    @Override
    public void setAMPSNativeAdExpressListener(AMPSNativeAdExpressListener ampsNativeAdExpressListener) {
        this.ampsNativeAdExpressListener = ampsNativeAdExpressListener;
    }

    @Override
    public int getECPM() {
        return ecpm;
    }
}
