# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-ignorewarnings
-dontpreverify

# yd_sdk_ad_xxx.aar
-keep class com.yd.** {*;}

# OAID
-keep class XI.CA.XI.**{*;}
-keep class XI.K0.XI.**{*;}
-keep class XI.XI.K0.**{*;}
-keep class XI.vs.K0.**{*;}
-keep class XI.xo.XI.XI.**{*;}
-keep class com.asus.msa.SupplementaryDID.**{*;}
-keep class com.asus.msa.sdid.**{*;}
-keep class com.bun.lib.**{*;}
-keep class com.bun.miitmdid.**{*;}
-keep class com.huawei.hms.ads.identifier.**{*;}
-keep class com.samsung.android.deviceidservice.**{*;}
-keep class org.json.**{*;}
-keep public class com.netease.nis.sdkwrapper.Utils { public <methods>; }


#demo中自定义广告位，仅供参考
-keep class com.xm.demo.unit.custom.TestCustomBannerAdapter { *;}
-keepclassmembers public class com.xm.demo.unit.custom.TestCustomBannerAdapter {
   public *;
}

-keep class com.xm.demo.unit.custom.TestCustomFullVideoAdapter { *;}
-keepclassmembers public class com.xm.demo.unit.custom.TestCustomFullVideoAdapter {
   public *;
}

-keep class com.xm.demo.unit.custom.TestCustomInterstitialAdapter { *;}
-keepclassmembers public class com.xm.demo.unit.custom.TestCustomInterstitialAdapter {
   public *;
}

-keep class com.xm.demo.unit.custom.TestCustomNativeAdapter { *;}
-keepclassmembers public class com.xm.demo.unit.custom.TestCustomNativeAdapter {
   public *;
}

-keep class com.xm.demo.unit.custom.TestCustomSplashAdapter { *;}
-keepclassmembers public class com.xm.demo.unit.custom.TestCustomSplashAdapter {
   public *;
}

-keep class com.xm.demo.unit.custom.TestCustomTemplateAdapter { *;}
-keepclassmembers public class com.xm.demo.unit.custom.TestCustomTemplateAdapter {
   public *;
}

-keep class com.xm.demo.unit.custom.TestCustomVideoAdapter { *;}
-keepclassmembers public class com.xm.demo.unit.custom.TestCustomVideoAdapter {
   public *;
}