package com.meishu.sdkdemo.custom.reward;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.meishu.sdk.platform.custom.reward.MsCustomRewardAd;
import com.meishu.sdk.platform.custom.reward.MsCustomRewardAdapter;

public class CustomRewardAd extends MsCustomRewardAd {
    private TTRewardVideoAd ttRewardVideoAd;

    public CustomRewardAd(MsCustomRewardAdapter adWrapper, @NonNull TTRewardVideoAd ttRewardVideoAd) {
        super(adWrapper);
        this.ttRewardVideoAd = ttRewardVideoAd;
    }

    @Override
    public void showAd(Activity activity) {
        if (ttRewardVideoAd!=null){
            ttRewardVideoAd.showRewardVideoAd(activity);
        }
    }

    @Override
    public void destroy() {

    }
}
