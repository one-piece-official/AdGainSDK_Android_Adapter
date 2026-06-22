package com.adgain.unified.controller.gromore;

import com.bytedance.sdk.openadsdk.mediation.ad.IMediationAdSlot;
import com.bytedance.sdk.openadsdk.mediation.ad.IMediationNativeToBannerListener;
import com.bytedance.sdk.openadsdk.mediation.ad.IMediationSplashRequestInfo;

import java.util.Collections;
import java.util.Map;

public class GroMoreMediationAdSlot implements IMediationAdSlot {
    @Override
    public boolean isSplashShakeButton() {
        return false;
    }

    @Override
    public boolean isSplashPreLoad() {
        return false;
    }

    @Override
    public boolean isMuted() {
        return false;
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public boolean isUseSurfaceView() {
        return false;
    }

    @Override
    public Map<String, Object> getExtraObject() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isBidNotify() {
        return true;
    }

    @Override
    public String getScenarioId() {
        return "";
    }

    @Override
    public boolean isAllowShowCloseBtn() {
        return false;
    }

    @Override
    public IMediationNativeToBannerListener getMediationNativeToBannerListener() {
        return null;
    }

    @Override
    public float getShakeViewWidth() {
        return 0;
    }

    @Override
    public float getShakeViewHeight() {
        return 0;
    }

    @Override
    public String getWxAppId() {
        return "";
    }

    @Override
    public IMediationSplashRequestInfo getMediationSplashRequestInfo() {
        return null;
    }

    @Override
    public String getRewardName() {
        return "";
    }

    @Override
    public int getRewardAmount() {
        return 0;
    }
}
