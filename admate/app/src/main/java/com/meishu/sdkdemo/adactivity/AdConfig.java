package com.meishu.sdkdemo.adactivity;

public class AdConfig {
    private static boolean isAutoRender = true;

    public static boolean isAutoRender() {
        return isAutoRender;
    }

    public static void setIsAutoRender(boolean isAutoRender) {
        AdConfig.isAutoRender = isAutoRender;
    }
}
