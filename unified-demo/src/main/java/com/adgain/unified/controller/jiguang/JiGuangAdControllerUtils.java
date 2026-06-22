package com.adgain.unified.controller.jiguang;

import android.view.View;
import android.view.ViewGroup;

import cn.jiguang.jgssp.ad.error.ADJgError;

final class JiGuangAdControllerUtils {
    private JiGuangAdControllerUtils() {
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

    static String errorInfo(ADJgError error) {
        return error == null ? "" : error.toString();
    }
}
