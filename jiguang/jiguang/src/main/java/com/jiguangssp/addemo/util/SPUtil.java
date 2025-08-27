package com.jiguangssp.addemo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    private static SharedPreferences getSP( Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getSP(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSP(context).getBoolean(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        getSP(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getSP(context).getString(key, defaultValue);
    }
}
