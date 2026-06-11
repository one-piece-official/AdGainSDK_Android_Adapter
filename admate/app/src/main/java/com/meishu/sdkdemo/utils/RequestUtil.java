package com.meishu.sdkdemo.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public class RequestUtil {

    private static String androidId = null;

    public static String getAndroidId(Context context) {
        if (TextUtils.isEmpty(androidId)) {
            androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return androidId;
    }
}
