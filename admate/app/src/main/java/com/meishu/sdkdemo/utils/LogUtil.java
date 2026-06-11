package com.meishu.sdkdemo.utils;

import android.util.Log;

public class LogUtil {
    public static void i(String tag, String msg, Throwable tr) {
        Log.i(tag, "" + msg, tr);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, "" + msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, "" + msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, "" + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, "" + msg);
    }

    public static void e(String tag, String msg, Exception e) {
        Log.e(tag, "" + msg, e);
    }
}
