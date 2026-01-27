package com.ad.taku.adgainadapter;

import static com.ad.taku.adgainadapter.AdGainInitManager.getAppId;
import static com.ad.taku.adgainadapter.AdGainInitManager.getCodeId;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.SplashAd;
import com.adgain.sdk.api.SplashAdListener;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.ErrorCode;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;

import java.util.HashMap;
import java.util.Map;

public class AdGainSplashAdapter extends CustomSplashAdapter {
    private String mAppId;
    private String codeId;
    private SplashAd splashAD;
    boolean isC2SBidding = false;

    @Override
    public boolean startBiddingRequest(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {

        Log.d(AdGainInitManager.TAG, "\n splash startBiddingRequest   serverExtra = " + serverExtra + "   localExtra = " + localExtra + "   biddingListener = " + biddingListener);

        isC2SBidding = true;

        loadCustomNetworkAd(context, serverExtra, localExtra);

        return true;
    }

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, final Map<String, Object> localExtra) {

        mAppId = getAppId(serverExtra);
        codeId = getCodeId(serverExtra);
        Log.d(AdGainInitManager.TAG, mAppId + " codeId: " + codeId + " map " + serverExtra);
        if (TextUtils.isEmpty(mAppId)) {
            notifyATLoadFail("", "AdGain app_id is empty.");
            return;
        }
        AdGainInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                startLoadAd(context, serverExtra);
            }

            @Override
            public void onFail(String errorMsg) {
                notifyATLoadFail("", errorMsg);
            }
        });
    }

    private void startLoadAd(final Context context, Map<String, Object> serverExtra) {
        AdRequest adRequest = new AdRequest.Builder().setCodeId(codeId)
                .setAppId(mAppId)
                .setBidFloor(AdGainInitManager.getBidFloor(serverExtra)).build();
        splashAD = new SplashAd(adRequest, new SplashAdListener() {
            @Override
            public void onAdLoadSuccess() {
                if (mLoadListener != null)
                    mLoadListener.onAdDataLoaded();
            }

            @Override
            public void onAdCacheSuccess() {
                try {
                    if (isC2SBidding) {
                        if (mBiddingListener != null) {
                            if (splashAD != null) {
                                mBiddingListener.onC2SBiddingResultWithCache(BiddingNotice.biddingResult(splashAD, splashAD.getBidPrice()), null);
                            } else {
                                notifyATLoadFail("", "AdGain SplashAD had been destroy.");
                            }
                        }
                    } else if (mLoadListener != null) {
                        mLoadListener.onAdCacheLoaded();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onSplashAdLoadFail(AdError adError) {
                if (adError != null) {
                    notifyATLoadFail(adError.getErrorCode() + "", adError.getMessage());

                    if (mImpressionListener != null) {
                        Log.e(AdGainInitManager.TAG, "AdGain Splash show fail:[errorCode:" + adError.getErrorCode() + ",errorMsg:" + adError.getMessage() + "]");
                        mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                        mImpressionListener.onSplashAdShowFail(ErrorCode.getErrorCode(ErrorCode.adShowError, "" + adError.getErrorCode(), adError.getMessage()));
                        mImpressionListener.onSplashAdDismiss();
                    }

                } else {
                    notifyATLoadFail("", "AdGain Splash show fail");

                    if (mImpressionListener != null) {
                        mImpressionListener.onSplashAdShowFail(ErrorCode.getErrorCode(ErrorCode.adShowError, "", "AdGain Splash show fail"));
                        mImpressionListener.onSplashAdDismiss();
                    }
                }
            }

            @Override
            public void onSplashAdShow() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdShow();
                }
            }

            @Override
            public void onSplashAdShowError(AdError error) {

            }

            @Override
            public void onSplashAdClick() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdClicked();
                }
            }

            @Override
            public void onSplashAdClose(boolean isSkip) {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdDismiss();
                }
            }
        });

        splashAD.loadAd();
    }

    @Override
    public String getNetworkName() {
        return AdGainInitManager.getInstance().getNetworkName();
    }

    @Override
    public boolean isAdReady() {
        if (splashAD != null) {
            return splashAD.isReady();
        }
        return false;
    }

    @Override
    public void show(Activity activity, ViewGroup container) {

        if (container == null) {
            if (mImpressionListener != null) {
                mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                mImpressionListener.onSplashAdShowFail(ErrorCode.getErrorCode(ErrorCode.adShowError, "", "Container is null"));
                mImpressionListener.onSplashAdDismiss();
            }
            return;
        }

        if (splashAD != null) {

            container.post(() -> {

                try {

                    if (splashAD != null) {
                        splashAD.showAd(container);
                    }

                } catch (Throwable t) {

                    if (mImpressionListener != null) {
                        mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                        mImpressionListener.onSplashAdShowFail(ErrorCode.getErrorCode(ErrorCode.adShowError, "", "AdGain Splash show with exception"));
                        mImpressionListener.onSplashAdDismiss();
                    }
                }
            });
        }
    }

    @Override
    public String getNetworkPlacementId() {
        return codeId;
    }


    @Override
    public Map<String, Object> getNetworkInfoMap() {
        if (splashAD != null && splashAD.getExtraInfo() != null) {
            Map<String, Object> networkInfoMap = new HashMap<>();
            networkInfoMap.put("request_id", splashAD.getExtraInfo().get("loadId"));
            networkInfoMap.put("code_id", splashAD.getExtraInfo().get("codeId"));
            return networkInfoMap;
        }
        return super.getNetworkInfoMap();
    }

    @Override
    public String getNetworkSDKVersion() {
        return AdGainInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public ATInitMediation getMediationInitManager() {
        return AdGainInitManager.getInstance();
    }

    @Override
    public void destory() {
        if (splashAD != null) {
            splashAD.destroyAd();
            splashAD = null;
        }
    }
}
