package com.meishu.sdkdemo.adid;

import java.util.HashMap;
import java.util.Map;

public class IdProviderFactory {

    public static final String PLATFORM_MS = "meishu";  //msad
    public static final String PLATFORM_HY = "huiying";  //汇盈
    public static final String PLATFORM_CSJ = "csj";    //穿山甲
    public static final String PLATFORM_GDT = "gdt";    //广点通
    public static final String PLATFORM_BD = "bd";      //百度
    public static final String PLATFORM_KS = "ks";      //快手
    public static final String PLATFORM_OPPO = "oppo";  //oppo
    public static final String PLATFORM_MIMO = "mimo";  //小米
    public static final String PLATFORM_JD = "jd";      //京东
    public static final String PLATFORM_CSJ_OPPO = "csj_oppo";      //穿山甲Oppo版
    public static final String PLATFORM_BZ = "bz";      //倍孜
    public static final String PLATFORM_SGM = "sgm";      //sigmob
    public static final String PLATFORM_HW = "hw";      //华为
    public static String PLATFORM_DEFAULT = PLATFORM_MS;

    private static final Map<String, IAdIdProvider> providers;

    static {
        providers = new HashMap<>();
        providers.put(PLATFORM_MS, new MSIdProvider());
        providers.put(PLATFORM_HY, new HYIdProvider());
        providers.put(PLATFORM_CSJ, new CSJIdProvider());
        providers.put(PLATFORM_GDT, new GDTIdProvider());
        providers.put(PLATFORM_BD, new BDIdProvider());
        providers.put(PLATFORM_KS, new KSIdProvider());
        providers.put(PLATFORM_OPPO, new OPPOIdProvider());
        providers.put(PLATFORM_MIMO, new MimoIdProvider());
        providers.put(PLATFORM_JD, new JDIdProvider());
        providers.put(PLATFORM_CSJ_OPPO, new CSJOPPOIdProvider());
        providers.put(PLATFORM_BZ, new BZIdProvider());
        providers.put(PLATFORM_SGM, new SGMIdProvider());
        providers.put(PLATFORM_HW, new HWIdProvider());
    }

    public static IAdIdProvider getProvider(String platform) {
        return providers.get(platform);
    }

    public static void setDefaultPlatform(String platform) {
        PLATFORM_DEFAULT = platform;
    }

    /**
     * 获取默认的广告提供者
     * @return
     */
    public static IAdIdProvider getDefaultProvider() {
        return providers.get(PLATFORM_DEFAULT);
    }
}
