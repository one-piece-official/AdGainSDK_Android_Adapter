package com.meishu.sdkdemo.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class SdkHandler {
    private static final String TAG = "SdkHandler";
    private  Handler mHandler;

    private SdkHandler(){
        mHandler = new Handler(Looper.getMainLooper());
    }

    private static class SdkHandlerHolder {
        private static final SdkHandler instance = new SdkHandler();
    }

    public static SdkHandler getInstance(){
        return SdkHandlerHolder.instance;
    }



    public void runOnUiThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    public void postDelay(Runnable runnable,long time){
        mHandler.postDelayed(runnable,time);
    }


    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
//    private static final Map<String, Long> switchTimeMap = new ConcurrentHashMap<>();

    public static void runOnMainThread(Runnable runnable) {
//        LogUtil.d(TAG,"start runOnMainThread");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            Message message = Message.obtain(MAIN_HANDLER,  runnable);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    message.setAsynchronous(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            message.sendToTarget();

        }
    }
}
