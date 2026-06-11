package com.meishu.sdkdemo.utils.lifecycle;

import android.app.Activity;

public interface IAppStateNotifyService {

    void addOnAppStateListener(OnAppStateListener listener);

    void removeOnAppStateListener(OnAppStateListener listener);

    interface OnAppStateListener {
        void onAppStateUpdate(boolean foreground);
        void onActivityResume(Activity activity);
        void onActivityPause(Activity activity);
    }
}
