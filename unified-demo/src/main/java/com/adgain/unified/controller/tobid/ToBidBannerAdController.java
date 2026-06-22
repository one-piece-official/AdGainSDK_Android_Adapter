package com.adgain.unified.controller.tobid;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.banner.WMBannerAdListener;
import com.windmill.sdk.banner.WMBannerAdRequest;
import com.windmill.sdk.banner.WMBannerView;
import com.windmill.sdk.models.AdInfo;

import java.util.HashMap;
import java.util.Map;

public class ToBidBannerAdController implements UnifiedAdController {
    private static final String USER_ID = "123456789";

    private WMBannerView bannerView;

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        adContainer.removeAllViews();
        adContainer.setVisibility(View.GONE);
        callback.log("开始加载 ToBid Banner: " + placementId);

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", USER_ID);
        bannerView = new WMBannerView(activity);
        bannerView.setAdListener(new WMBannerAdListener() {
            @Override
            public void onAdLoadSuccess(String placementId) {
                callback.log("onAdLoadSuccess: " + placementId);
            }

            @Override
            public void onAdLoadError(WindMillError error, String placementId) {
                callback.log("onAdLoadError: " + error + ", placementId=" + placementId);
            }

            @Override
            public void onAdShown(AdInfo adInfo) {
                callback.log("onAdShown");
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
                callback.log("onAdClicked");
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                callback.log("onAdClosed");
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAdAutoRefreshed(AdInfo adInfo) {
                callback.log("onAdAutoRefreshed");
            }

            @Override
            public void onAdAutoRefreshFail(WindMillError error, String placementId) {
                callback.log("onAdAutoRefreshFail: " + error + ", placementId=" + placementId);
            }
        });
        bannerView.setAutoAnimation(true);
        bannerView.loadAd(new WMBannerAdRequest(placementId, USER_ID, options));
    }

    @Override
    public boolean isReady() {
        return bannerView != null && bannerView.isReady();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (bannerView == null) {
            callback.log("广告未加载");
            Toast.makeText(activity, "广告未加载", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }

        callback.log("展示 ToBid Banner");
        ViewParent parent = bannerView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(bannerView);
        }
        adContainer.removeAllViews();
        adContainer.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        adContainer.addView(bannerView, layoutParams);
    }

    @Override
    public void destroy() {
        if (bannerView != null) {
            ViewParent parent = bannerView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(bannerView);
            }
            bannerView.destroy();
            bannerView = null;
        }
    }
}
