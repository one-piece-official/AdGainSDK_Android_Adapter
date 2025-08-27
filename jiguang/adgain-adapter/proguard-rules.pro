# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lpjehy/Documents/workspace/adt-bundle-mac-x86_64/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
###############################################################
-dontwarn # 不用输出警告
-ignorewarnings # 忽略警告
#####################################################

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
 #优化  不优化输入的类文件
-dontoptimize
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
# 保持哪些类不被混淆
-keep class android.support.v4.**{*;}
-keep class android.support.v4.app.**{*;}
-keep public class * extends android.app.Activity
-keep public class * extends android.content.ContentProvider
-keep class android.view.**{*;}
-keep class android.support.v4.view.**{*;}
-keep class android.support.v7.widget.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn **.R$*
-keep class m.framework.**{*;}
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
###混淆代码后的错误提示
-keepattributes SourceFile,LineNumberTable
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#忽略警告
-ignorewarnings
-keepattributes *JavascriptInterface*
#忽略警告
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
#不混淆资源类
 -keepclassmembers class **.R$* {
     public static <fields>;
 }

 #如果引用了v4或者v7包
 -dontwarn android.support.**
 #保持 native 方法不被混淆
 -keepclasseswithmembernames class * {
     native <methods>;
 }

 #不混淆资源类
 -keepclassmembers class **.R$* {
     public static <fields>;
 }

-dontwarn cn.jiguang.jgssp.**

-keepattributes Signature
-keep class org.**{*;}
-keep class android.os.**{*;}
-keep class android.transition.*{*;}
-keep class android.app.*{*;}
-keepattributes Annotation,EnclosingMethod,Signature,InnerClasses
-keep class org.codehaus.** { *; }

# 对内混淆
-keep class com.android.**{*;}
-keep interface com.android.**{*;}

-keep class cn.jiguang.jgssp.adapter.adgain.ADSuyiIniter1{public *;}
-keep class cn.jiguang.jgssp.adapter.adgain.loader.**{public <methods>;}
-keep class cn.jiguang.jgssp.adapter.adgain.provider.**{public <methods>;}

-dontwarn androidx.**
-keep class com.qq.e.** {public protected *;}
-keep class android.support.v4.**{public *;}
-keep class android.support.v7.**{public *;}
-keep class MTT.ThirdAppInfoNew {*;}
-keep class com.tencent.** {*;}

# 如果使用了tbs版本的sdk需要进行以下配置
-keep class com.tencent.smtt.** { *; }
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**
