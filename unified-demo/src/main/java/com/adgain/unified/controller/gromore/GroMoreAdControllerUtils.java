package com.adgain.unified.controller.gromore;

import android.view.View;
import android.view.ViewGroup;

final class GroMoreAdControllerUtils {
    private GroMoreAdControllerUtils() {
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
}
