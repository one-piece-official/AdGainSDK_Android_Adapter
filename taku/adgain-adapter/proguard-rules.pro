# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

-keep class com.ad.taku.adgainadapter.**{ *;}

-dontwarn com.adgain.**
-keep class com.adgain.**{ *;}
-keep interface com.adgain.**{ *;}