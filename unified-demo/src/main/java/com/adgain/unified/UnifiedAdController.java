package com.adgain.unified;

import android.app.Activity;
import android.view.ViewGroup;

public interface UnifiedAdController {
    void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback);

    boolean isReady();

    void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback);

    void destroy();
}
