package com.adgain.unified.controller.mediatom;

import android.view.View;
import android.view.ViewGroup;

import com.yd.saas.config.exception.YdError;

final class MediatomAdControllerUtils {
    private MediatomAdControllerUtils() {
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

    static String errorInfo(YdError error) {
        if (error == null) {
            return "";
        }
        return error.getCode() + " " + error.getMsg();
    }
}
