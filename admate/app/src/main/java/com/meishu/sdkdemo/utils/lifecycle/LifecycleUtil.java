package com.meishu.sdkdemo.utils.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LifecycleUtil {

    public static AtomicInteger activityCount = new AtomicInteger(0);
    public static SoftReference<Activity> activitySoftReference;
    private static final List<IAppStateNotifyService.OnAppStateListener> listeners = Collections.synchronizedList(new ArrayList<>());

    public static void register(Context context) {
        try {
            if (context instanceof Application) {
                ((Application) context).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {
                        if (activityCount.incrementAndGet() == 1) {
                            notifyAppStateUpdate(true);
                        }
                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {
                        activitySoftReference = new SoftReference<>(activity);
                        notifyActivityResume(activity);
                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {
                        notifyActivityPause(activity);
                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {
                        if (activityCount.decrementAndGet() == 0) {
                            notifyAppStateUpdate(false);
                        }
                    }

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {

                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppForeground() {
        return activityCount.get() > 0;
    }

    public static void addOnAppStateListener(IAppStateNotifyService.OnAppStateListener listener) {
        if (listener == null) {
            return;
        }
        try {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        } catch (Exception e) {
        }
    }

    public static void removeOnAppStateListener(IAppStateNotifyService.OnAppStateListener listener) {
        if (listener == null) {
            return;
        }
        try {
            listeners.remove(listener);
        } catch (Exception e) {
        }
    }

    private static void notifyAppStateUpdate(boolean foreground) {
        try {
            if (listeners.isEmpty()) {
                return;
            }
            for (IAppStateNotifyService.OnAppStateListener listener : listeners) {
                try {
                    listener.onAppStateUpdate(foreground);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void notifyActivityResume(Activity activity) {
        try {
            if (listeners.isEmpty()) {
                return;
            }
            for (IAppStateNotifyService.OnAppStateListener listener : listeners) {
                try {
                    listener.onActivityResume(activity);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private static void notifyActivityPause(Activity activity) {
        try {
            if (listeners.isEmpty()) {
                return;
            }
            for (IAppStateNotifyService.OnAppStateListener listener : listeners) {
                try {
                    listener.onActivityPause(activity);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Activity getCurActivity() {
        if (activitySoftReference != null) {
            return activitySoftReference.get();
        }
        return null;
    }
}
