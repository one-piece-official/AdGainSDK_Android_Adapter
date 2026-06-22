package com.adgain.unified.controller.tobid;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.windmill.android.demo.natives.NativeAdDemoRender;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.natives.WMNativeAd;
import com.windmill.sdk.natives.WMNativeAdContainer;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToBidNativeAdController implements UnifiedAdController {
    private final boolean expressMode;
    private int userId;
    private WMNativeAd nativeAd;
    private List<WMNativeAdData> nativeAdDataList;

    public ToBidNativeAdController(boolean expressMode) {
        this.expressMode = expressMode;
    }

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        adContainer.removeAllViews();
        adContainer.setVisibility(View.GONE);
        callback.log("开始加载 ToBid " + adTypeName() + ": " + placementId);

        userId++;
        Map<String, Object> options = new HashMap<>();
        options.put(WMConstants.AD_WIDTH, Math.max(1, screenWidthDp(activity) - 32));
        options.put(WMConstants.AD_HEIGHT, WMConstants.AUTO_SIZE);
        options.put("user_id", String.valueOf(userId));

        nativeAd = new WMNativeAd(activity, new WMNativeAdRequest(placementId, String.valueOf(userId), 3, options));
        nativeAd.loadAd(new WMNativeAd.NativeAdLoadListener() {
            @Override
            public void onError(WindMillError error, String placementId) {
                callback.log("onError: " + error + ", placementId=" + placementId);
                Toast.makeText(activity, "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFeedAdLoad(String placementId) {
                callback.log("onFeedAdLoad: " + placementId);
                List<WMNativeAdData> dataList = nativeAd.getNativeADDataList();
                if (dataList != null && !dataList.isEmpty()) {
                    nativeAdDataList = dataList;
                    callback.log("原生广告数量: " + dataList.size());
                }
            }
        });
    }

    @Override
    public boolean isReady() {
        return nativeAdDataList != null && !nativeAdDataList.isEmpty();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }

        WMNativeAdData nativeAdData = nativeAdDataList.get(0);
        callback.log("展示 ToBid " + adTypeName() + ", isExpressAd=" + nativeAdData.isExpressAd());
        bindListener(activity, adContainer, nativeAdData, callback);

        adContainer.removeAllViews();
        adContainer.setVisibility(View.VISIBLE);
        if (nativeAdData.isExpressAd()) {
            nativeAdData.render();
        } else {
            WMNativeAdContainer nativeAdContainer = new WMNativeAdContainer(activity);
            nativeAdData.connectAdToView(activity, nativeAdContainer, new NativeAdDemoRender());
            adContainer.addView(nativeAdContainer);
        }
    }

    @Override
    public void destroy() {
        if (nativeAdDataList != null) {
            for (WMNativeAdData data : nativeAdDataList) {
                if (data != null) {
                    data.destroy();
                }
            }
            nativeAdDataList = null;
        }
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
    }

    private void bindListener(Activity activity, ViewGroup adContainer, WMNativeAdData nativeAdData,
                              UnifiedAdLoadCallback callback) {
        nativeAdData.setInteractionListener(new WMNativeAdData.NativeAdInteractionListener() {
            @Override
            public void onADExposed(AdInfo adInfo) {
                callback.log("onADExposed");
            }

            @Override
            public void onADClicked(AdInfo adInfo) {
                callback.log("onADClicked");
            }

            @Override
            public void onADRenderSuccess(AdInfo adInfo, View view, float width, float height) {
                callback.log("onADRenderSuccess: " + width + "x" + height);
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(view);
                }
                adContainer.removeAllViews();
                adContainer.setVisibility(View.VISIBLE);
                adContainer.addView(view);
            }

            @Override
            public void onADError(AdInfo adInfo, WindMillError error) {
                callback.log("onADError: " + error);
            }
        });

        if (nativeAdData.getAdPatternType() == WMNativeAdDataType.NATIVE_VIDEO_AD) {
            nativeAdData.setMediaListener(new WMNativeAdData.NativeADMediaListener() {
                @Override
                public void onVideoLoad() {
                    callback.log("onVideoLoad");
                }

                @Override
                public void onVideoError(WindMillError error) {
                    callback.log("onVideoError: " + error);
                }

                @Override
                public void onVideoStart() {
                    callback.log("onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    callback.log("onVideoPause");
                }

                @Override
                public void onVideoResume() {
                    callback.log("onVideoResume");
                }

                @Override
                public void onVideoCompleted() {
                    callback.log("onVideoCompleted");
                }
            });
        }

        if (nativeAdData.getInteractionType() == WMConstants.INTERACTION_TYPE_DOWNLOAD) {
            nativeAdData.setDownloadListener(new WMNativeAdData.AppDownloadListener() {
                @Override
                public void onIdle() {
                    callback.log("onDownloadIdle");
                }

                @Override
                public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                    callback.log("onDownloadActive");
                }

                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                    callback.log("onDownloadPaused");
                }

                @Override
                public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                    callback.log("onDownloadFailed");
                }

                @Override
                public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                    callback.log("onDownloadFinished");
                }

                @Override
                public void onInstalled(String fileName, String appName) {
                    callback.log("onInstalled");
                }
            });
        }

        nativeAdData.setDislikeInteractionCallback(activity, new WMNativeAdData.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                callback.log("dislike onShow");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                callback.log("dislike onSelected: " + value);
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                callback.log("dislike onCancel");
            }
        });
    }

    private String adTypeName() {
        return expressMode ? "原生模板" : "原生自渲染";
    }

    private int screenWidthDp(Activity activity) {
        int pixels = activity.getResources().getDisplayMetrics().widthPixels;
        float density = activity.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }
}
