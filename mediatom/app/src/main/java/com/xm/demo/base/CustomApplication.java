package com.xm.demo.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xm.demo.unit.ads.SplashActivity;

import java.util.List;

import androidx.multidex.MultiDexApplication;

public abstract class CustomApplication extends MultiDexApplication {
    private static final String TAG = "CustomApplication";
    private static final int MAX_SLEEP_TIME = 1 * 30 * 1000; //应用退到后台后最大的休眠唤起广告时间
    // 正常状态
    public static final int STATE_NORMAL = 0;
    // 从后台回到前台
    public static final int STATE_BACK_TO_FRONT = 1;
    // 从前台进入后台
    public static final int STATE_FRONT_TO_BACK = 2;

    private static final int REQUEST_CODE_SPLASH = 1001;

    // APP状态
    private static int sAppState = STATE_NORMAL;
    // 标记程序是否已进入后台(依据onStop回调)
    private boolean flag;
    // 标记程序是否已进入后台(依据onTrimMemory回调)
    private boolean background;
    // 从前台进入后台的时间
    private static long frontToBackTime;
    // 从后台返回前台的时间
    private static long backToFrontTime;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (background || flag) {
                    background = false;
                    flag = false;
                    sAppState = STATE_BACK_TO_FRONT;
                    backToFrontTime = System.currentTimeMillis();
                    Log.e(TAG, "onResume: STATE_BACK_TO_FRONT");
                    if (canShowAd()) {
                        //此处跳转到开屏页广告页面，但要做好区分。跟冷启动不同，广告展示完毕后不往主页跳转，只需关闭广告页即可
                        SplashActivity.launch(activity, REQUEST_CODE_SPLASH);
                    }
                } else {
                    sAppState = STATE_NORMAL;
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //判断当前activity是否处于前台
                if (!isCurAppTop(activity)) {
                    // 从前台进入后台
                    sAppState = STATE_FRONT_TO_BACK;
                    frontToBackTime = System.currentTimeMillis();
                    flag = true;
                    Log.e(TAG, "onStop: " + "STATE_FRONT_TO_BACK");
                } else {
                    // 否则是正常状态
                    sAppState = STATE_NORMAL;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // TRIM_MEMORY_UI_HIDDEN是UI不可见的回调, 通常程序进入后台后都会触发此回调,大部分手机多是回调这个参数
        // TRIM_MEMORY_BACKGROUND也是程序进入后台的回调, 不同厂商不太一样, 魅族手机就是回调这个参数
        if (level == Application.TRIM_MEMORY_UI_HIDDEN || level == TRIM_MEMORY_BACKGROUND) {
            background = true;
        } else if (level == Application.TRIM_MEMORY_COMPLETE) {
            background = !isCurAppTop(this);
        }
        if (background) {
            frontToBackTime = System.currentTimeMillis();
            sAppState = STATE_FRONT_TO_BACK;
            Log.e(TAG, "onTrimMemory: TRIM_MEMORY_UI_HIDDEN || TRIM_MEMORY_BACKGROUND");
        } else {
            sAppState = STATE_NORMAL;
        }

    }

    /**
     * 进入后台间隔10分钟以后可以再次显示广告
     *
     * @return 是否能显示广告
     */
    public static boolean canShowAd() {
        return sAppState == STATE_BACK_TO_FRONT &&
                (backToFrontTime - frontToBackTime) > MAX_SLEEP_TIME;
    }

    /**
     * 判断当前程序是否前台进程
     *
     * @param context
     * @return
     */
    private boolean isCurAppTop(Context context) {
        if (context == null) {
            return false;
        }
        String curPackageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ActivityManager.RunningTaskInfo info = list.get(0);
            String topPackageName = info.topActivity.getPackageName();
            String basePackageName = info.baseActivity.getPackageName();
            if (topPackageName.equals(curPackageName) && basePackageName.equals(curPackageName)) {
                return true;
            }
        }
        return false;
    }

}
