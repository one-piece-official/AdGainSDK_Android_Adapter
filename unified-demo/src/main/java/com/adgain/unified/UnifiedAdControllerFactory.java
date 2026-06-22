package com.adgain.unified;

import com.adgain.unified.controller.admate.AdMateBannerAdController;
import com.adgain.unified.controller.admate.AdMateInterstitialAdController;
import com.adgain.unified.controller.admate.AdMateNativeAdController;
import com.adgain.unified.controller.admate.AdMateRewardAdController;
import com.adgain.unified.controller.admate.AdMateSplashAdController;
import com.adgain.unified.controller.beizi.BeiziBannerAdController;
import com.adgain.unified.controller.beizi.BeiziInterstitialAdController;
import com.adgain.unified.controller.beizi.BeiziNativeAdController;
import com.adgain.unified.controller.beizi.BeiziRewardAdController;
import com.adgain.unified.controller.beizi.BeiziSplashAdController;
import com.adgain.unified.controller.gromore.GroMoreBannerAdController;
import com.adgain.unified.controller.gromore.GroMoreInterstitialAdController;
import com.adgain.unified.controller.gromore.GroMoreNativeAdController;
import com.adgain.unified.controller.gromore.GroMoreRewardAdController;
import com.adgain.unified.controller.gromore.GroMoreSplashAdController;
import com.adgain.unified.controller.jiguang.JiGuangBannerAdController;
import com.adgain.unified.controller.jiguang.JiGuangInterstitialAdController;
import com.adgain.unified.controller.jiguang.JiGuangNativeAdController;
import com.adgain.unified.controller.jiguang.JiGuangRewardAdController;
import com.adgain.unified.controller.jiguang.JiGuangSplashAdController;
import com.adgain.unified.controller.mediatom.MediatomBannerAdController;
import com.adgain.unified.controller.mediatom.MediatomInterstitialAdController;
import com.adgain.unified.controller.mediatom.MediatomNativeAdController;
import com.adgain.unified.controller.mediatom.MediatomRewardAdController;
import com.adgain.unified.controller.mediatom.MediatomSplashAdController;
import com.adgain.unified.controller.taku.TakuBannerAdController;
import com.adgain.unified.controller.taku.TakuInterstitialAdController;
import com.adgain.unified.controller.taku.TakuNativeAdController;
import com.adgain.unified.controller.taku.TakuRewardAdController;
import com.adgain.unified.controller.taku.TakuSplashAdController;
import com.adgain.unified.controller.tobid.ToBidBannerAdController;
import com.adgain.unified.controller.tobid.ToBidInterstitialAdController;
import com.adgain.unified.controller.tobid.ToBidNativeAdController;
import com.adgain.unified.controller.tobid.ToBidRewardAdController;
import com.adgain.unified.controller.tobid.ToBidSplashAdController;

public final class UnifiedAdControllerFactory {
    private UnifiedAdControllerFactory() {
    }

    public static UnifiedAdController create(String platformId, String adType) {
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId) && "splash".equals(adType)) {
            return new ToBidSplashAdController();
        }
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId) && "banner".equals(adType)) {
            return new ToBidBannerAdController();
        }
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId) && "interstitial".equals(adType)) {
            return new ToBidInterstitialAdController();
        }
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId) && "reward".equals(adType)) {
            return new ToBidRewardAdController();
        }
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId) && "native_template".equals(adType)) {
            return new ToBidNativeAdController(true);
        }
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId) && "native_self".equals(adType)) {
            return new ToBidNativeAdController(false);
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId) && "splash".equals(adType)) {
            return new TakuSplashAdController();
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId) && "banner".equals(adType)) {
            return new TakuBannerAdController();
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId) && "interstitial".equals(adType)) {
            return new TakuInterstitialAdController();
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId) && "reward".equals(adType)) {
            return new TakuRewardAdController();
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId) && "native_template".equals(adType)) {
            return new TakuNativeAdController(true);
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId) && "native_self".equals(adType)) {
            return new TakuNativeAdController(false);
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId) && "splash".equals(adType)) {
            return new GroMoreSplashAdController();
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId) && "banner".equals(adType)) {
            return new GroMoreBannerAdController();
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId) && "interstitial".equals(adType)) {
            return new GroMoreInterstitialAdController();
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId) && "reward".equals(adType)) {
            return new GroMoreRewardAdController();
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId) && "native_template".equals(adType)) {
            return new GroMoreNativeAdController(true);
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId) && "native_self".equals(adType)) {
            return new GroMoreNativeAdController(false);
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId) && "splash".equals(adType)) {
            return new BeiziSplashAdController();
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId) && "banner".equals(adType)) {
            return new BeiziBannerAdController();
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId) && "interstitial".equals(adType)) {
            return new BeiziInterstitialAdController();
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId) && "reward".equals(adType)) {
            return new BeiziRewardAdController();
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId) && "native_template".equals(adType)) {
            return new BeiziNativeAdController(true);
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId) && "native_self".equals(adType)) {
            return new BeiziNativeAdController(false);
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId) && "splash".equals(adType)) {
            return new JiGuangSplashAdController();
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId) && "banner".equals(adType)) {
            return new JiGuangBannerAdController();
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId) && "interstitial".equals(adType)) {
            return new JiGuangInterstitialAdController();
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId) && "reward".equals(adType)) {
            return new JiGuangRewardAdController();
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId) && "native_template".equals(adType)) {
            return new JiGuangNativeAdController(true);
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId) && "native_self".equals(adType)) {
            return new JiGuangNativeAdController(false);
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId) && "splash".equals(adType)) {
            return new MediatomSplashAdController();
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId) && "banner".equals(adType)) {
            return new MediatomBannerAdController();
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId) && "interstitial".equals(adType)) {
            return new MediatomInterstitialAdController();
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId) && "reward".equals(adType)) {
            return new MediatomRewardAdController();
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId) && "native_template".equals(adType)) {
            return new MediatomNativeAdController(true);
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId) && "native_self".equals(adType)) {
            return new MediatomNativeAdController(false);
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId) && "splash".equals(adType)) {
            return new AdMateSplashAdController();
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId) && "banner".equals(adType)) {
            return new AdMateBannerAdController();
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId) && "interstitial".equals(adType)) {
            return new AdMateInterstitialAdController();
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId) && "reward".equals(adType)) {
            return new AdMateRewardAdController();
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId) && "native_template".equals(adType)) {
            return new AdMateNativeAdController(true);
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId) && "native_self".equals(adType)) {
            return new AdMateNativeAdController(false);
        }
        return null;
    }
}
