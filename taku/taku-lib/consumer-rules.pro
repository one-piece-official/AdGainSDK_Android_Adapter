# Consumer ProGuard rules for the Taku demo library.

# Keep the demo entry points and helper classes available to host apps.
-keep class com.test.ad.demo.** { *; }

# Keep AnyThink/Taku SDK classes used reflectively by the vendor SDKs.
-keep class com.anythink.** { *; }
-dontwarn com.anythink.**
-dontwarn com.android.support.**
