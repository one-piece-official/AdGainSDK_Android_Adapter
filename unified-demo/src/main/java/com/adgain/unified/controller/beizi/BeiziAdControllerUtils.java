package com.adgain.unified.controller.beizi;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import xyz.adscope.amps.common.AMPSError;

final class BeiziAdControllerUtils {
    private BeiziAdControllerUtils() {
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

    static RelativeLayout newRelativeContainer(Activity activity) {
        RelativeLayout container = new RelativeLayout(activity);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        return container;
    }

    static String errorInfo(AMPSError error) {
        if (error == null) {
            return "";
        }
        return error.getCode() + " " + error.getMessage();
    }
}
