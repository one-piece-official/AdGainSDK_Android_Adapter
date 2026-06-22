package com.adgain.unified.controller.taku;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.anythink.core.api.ATShowConfig;

final class TakuAdControllerUtils {
    private TakuAdControllerUtils() {
    }

    static void resetContainer(ViewGroup adContainer) {
        adContainer.removeAllViews();
        adContainer.setVisibility(View.GONE);
    }

    static void attachView(ViewGroup adContainer, View view, ViewGroup.LayoutParams params) {
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        adContainer.removeAllViews();
        adContainer.setVisibility(View.VISIBLE);
        adContainer.addView(view, params);
    }

    static ATShowConfig showConfig(String scenarioId, String customExt) {
        return new ATShowConfig.Builder()
                .scenarioId(scenarioId)
                .showCustomExt(customExt)
                .build();
    }

    static int dipToPx(Activity activity, float dipValue) {
        float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
