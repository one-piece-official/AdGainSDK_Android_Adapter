package com.adgain.unified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlatformRegistry {
    public static final String PLATFORM_TOBID = "tobid";
    public static final String PLATFORM_TAKU = "taku";
    public static final String PLATFORM_GROMORE = "gromore";
    public static final String PLATFORM_BEIZI = "beizi";
    public static final String PLATFORM_JIGUANG = "jiguang";
    public static final String PLATFORM_MEDIATOM = "mediatom";
    public static final String PLATFORM_ADMATE = "admate";

    private PlatformRegistry() {
    }

    public static List<PlatformEntry> all() {
        List<PlatformEntry> entries = new ArrayList<>();
        entries.add(toBid());
        entries.add(taku());
        entries.add(groMore());
        entries.add(beizi());
        entries.add(jiGuang());
        entries.add(mediatom());
        entries.add(admate());
        return entries;
    }

    public static PlatformEntry find(String platformId) {
        for (PlatformEntry entry : all()) {
            if (entry.id.equals(platformId)) {
                return entry;
            }
        }
        return null;
    }

    private static PlatformEntry toBid() {
        return new PlatformEntry(
                PLATFORM_TOBID,
                "ToBid",
                context -> {
                    com.windmill.android.demo.ToBidInitializer.init(context);
                    if (context instanceof android.app.Activity) {
                        com.windmill.sdk.WindMillAd.requestPermission((android.app.Activity) context);
                    }
                },
                Arrays.asList(
                        new AdTypeEntry("splash", "开屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("banner", "Banner 广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("interstitial", "插屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("reward", "激励视频", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_template", "原生模板", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_self", "原生自渲染", "com.adgain.unified.UnifiedAdLoadActivity")
                )
        );
    }

    private static PlatformEntry taku() {
        return new PlatformEntry(
                PLATFORM_TAKU,
                "Taku",
                context -> com.test.ad.demo.util.SDKUtil.initSDK(context),
                Arrays.asList(
                        new AdTypeEntry("splash", "开屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("banner", "Banner 广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("interstitial", "插屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("reward", "激励视频", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_template", "原生模板", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_self", "原生自渲染", "com.adgain.unified.UnifiedAdLoadActivity")
                )
        );
    }

    private static PlatformEntry groMore() {
        return new PlatformEntry(
                PLATFORM_GROMORE,
                "GroMore",
                context -> com.union_test.toutiao.config.TTAdManagerHolder.initForUnified(context.getApplicationContext()),
                Arrays.asList(
                        new AdTypeEntry("splash", "开屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("banner", "Banner 广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("interstitial", "插屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("reward", "激励视频", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_template", "原生模板", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_self", "原生自渲染", "com.adgain.unified.UnifiedAdLoadActivity")
                )
        );
    }

    private static PlatformEntry beizi() {
        return new PlatformEntry(
                PLATFORM_BEIZI,
                "Beizi",
                context -> com.amps.demo.BeiziInitializer.init(context.getApplicationContext()),
                Arrays.asList(
                        new AdTypeEntry("splash", "开屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("banner", "Banner 广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("interstitial", "插屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("reward", "激励视频", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_template", "原生模板", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_self", "原生自渲染", "com.adgain.unified.UnifiedAdLoadActivity")
                )
        );
    }

    private static PlatformEntry jiGuang() {
        return new PlatformEntry(
                PLATFORM_JIGUANG,
                "JiGuang",
                context -> com.jiguangssp.addemo.JiGuangInitializer.init(context.getApplicationContext()),
                Arrays.asList(
                        new AdTypeEntry("splash", "开屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("banner", "Banner 广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("interstitial", "插屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("reward", "激励视频", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_template", "原生模板", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_self", "原生自渲染", "com.adgain.unified.UnifiedAdLoadActivity")
                )
        );
    }

    private static PlatformEntry mediatom() {
        return new PlatformEntry(
                PLATFORM_MEDIATOM,
                "Mediatom",
                context -> com.xm.demo.MediatomInitializer.init(context.getApplicationContext()),
                Arrays.asList(
                        new AdTypeEntry("splash", "开屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("banner", "Banner 广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("interstitial", "插屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("reward", "激励视频", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_template", "原生模板", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_self", "原生自渲染", "com.adgain.unified.UnifiedAdLoadActivity")
                )
        );
    }

    private static PlatformEntry admate() {
        return new PlatformEntry(
                PLATFORM_ADMATE,
                "AdMate",
                context -> com.meishu.sdkdemo.AdMateInitializer.init(context.getApplicationContext()),
                Arrays.asList(
                        new AdTypeEntry("splash", "开屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("banner", "Banner 广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("interstitial", "插屏广告", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("reward", "激励视频", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_template", "原生模板", "com.adgain.unified.UnifiedAdLoadActivity"),
                        new AdTypeEntry("native_self", "原生自渲染", "com.adgain.unified.UnifiedAdLoadActivity")
                )
        );
    }

    private static Map<String, String> extras(String... keyValues) {
        Map<String, String> extras = new HashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            extras.put(keyValues[i], keyValues[i + 1]);
        }
        return extras;
    }

    private static PlatformEntry empty(String id, String name) {
        return new PlatformEntry(id, name, new NoOpInitializer(), Collections.<AdTypeEntry>emptyList());
    }
}
