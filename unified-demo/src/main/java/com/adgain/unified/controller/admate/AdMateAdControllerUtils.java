package com.adgain.unified.controller.admate;

import android.view.View;
import android.view.ViewGroup;

final class AdMateAdControllerUtils {
    private AdMateAdControllerUtils() {
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

    static int dp(View view, int value) {
        return (int) (value * view.getResources().getDisplayMetrics().density + 0.5f);
    }
}
