package com.adgain.unified;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UnifiedAdLoadActivity extends Activity {
    public static final String EXTRA_PLATFORM_ID = "platform_id";
    public static final String EXTRA_AD_TYPE = "ad_type";
    public static final String EXTRA_AD_TITLE = "ad_title";
    private static final String[] TOBID_NATIVE_TEMPLATE_NAMES = {
            "AdGain", "SigMob", "穿山甲", "快手", "腾讯广告", "Mtg_模版", "趣盟", "百度", "自定义穿山甲"
    };
    private static final String[] TOBID_NATIVE_TEMPLATE_IDS = {
            "6246197299556931", "9224761251541712", "5771359946996735", "7264823485628063",
            "3175536281468891", "6847370170971294", "4772764698616709", "6728444626644345",
            "4952664543881484"
    };
    private static final String[] TOBID_NATIVE_SELF_NAMES = {
            "AdGain", "SigMob", "穿山甲", "快手", "腾讯广告", "Mtg_自渲染", "趣盟", "百度", "自定义穿山甲"
    };
    private static final String[] TOBID_NATIVE_SELF_IDS = {
            "5625938624221650", "9224761251541712", "5771359946996735", "7264823485628063",
            "3175536281468891", "2809367466265352", "4772764698616709", "6728444626644345",
            "4952664543881484"
    };
    private static final String[] ADMATE_NETWORK_NAMES = {
            "AdGain", "穿山甲", "腾讯广告", "百度", "快手", "京东"
    };
    private static final String[] ADMATE_NETWORK_KEYS = {
            com.meishu.sdkdemo.adid.IdProviderFactory.PLATFORM_MS,
            com.meishu.sdkdemo.adid.IdProviderFactory.PLATFORM_CSJ,
            com.meishu.sdkdemo.adid.IdProviderFactory.PLATFORM_GDT,
            com.meishu.sdkdemo.adid.IdProviderFactory.PLATFORM_BD,
            com.meishu.sdkdemo.adid.IdProviderFactory.PLATFORM_KS,
            com.meishu.sdkdemo.adid.IdProviderFactory.PLATFORM_JD
    };

    private UnifiedAdController controller;
    private EditText placementEditText;
    private TextView logView;
    private ViewGroup adContainer;
    private LinearLayout networkLayout;
    private Spinner networkSpinner;
    private String[] networkPlacementIds = new String[0];
    private String loadedPlacementId;
    private final StringBuilder logs = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unified_ad_load);

        String platformId = getIntent().getStringExtra(EXTRA_PLATFORM_ID);
        String adType = getIntent().getStringExtra(EXTRA_AD_TYPE);
        String adTitle = getIntent().getStringExtra(EXTRA_AD_TITLE);

        PlatformEntry platform = PlatformRegistry.find(platformId);
        controller = UnifiedAdControllerFactory.create(platformId, adType);
        if (platform == null || controller == null) {
            Toast.makeText(this, "统一加载页暂未接入该广告类型", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView titleView = findViewById(R.id.tv_title);
        placementEditText = findViewById(R.id.et_placement_id);
        logView = findViewById(R.id.tv_log);
        adContainer = findViewById(R.id.ad_container);
        networkLayout = findViewById(R.id.layout_network);
        networkSpinner = findViewById(R.id.spinner_network);
        Button loadButton = findViewById(R.id.btn_load);
        Button readyButton = findViewById(R.id.btn_ready);
        Button showButton = findViewById(R.id.btn_show);

        titleView.setText(platform.name + " - " + adTitle);
        setupNetworkSelector(platformId, adType);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placementId = placementEditText.getText().toString().trim();
                if (TextUtils.isEmpty(placementId)) {
                    Toast.makeText(UnifiedAdLoadActivity.this, "请输入广告位 ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadedPlacementId = placementId;
                controller.load(UnifiedAdLoadActivity.this, adContainer, placementId, UnifiedAdLoadActivity.this::appendLog);
            }
        });

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canUseLoadedAd()) {
                    return;
                }
                boolean ready = controller.isReady();
                appendLog("isReady: " + ready);
                Toast.makeText(UnifiedAdLoadActivity.this, "isReady: " + ready, Toast.LENGTH_SHORT).show();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canUseLoadedAd()) {
                    return;
                }
                controller.show(UnifiedAdLoadActivity.this, adContainer, UnifiedAdLoadActivity.this::appendLog);
            }
        });
    }

    private boolean canUseLoadedAd() {
        String placementId = placementEditText.getText().toString().trim();
        if (TextUtils.isEmpty(placementId)) {
            appendLog("请先加载广告");
            Toast.makeText(this, "请先加载广告", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(loadedPlacementId)) {
            appendLog("请先加载广告");
            Toast.makeText(this, "请先加载广告", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!placementId.equals(loadedPlacementId)) {
            appendLog("请先加载广告");
            Toast.makeText(this, "请先加载广告", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupNetworkSelector(String platformId, String adType) {
        String[] networkNames = networkNames(platformId, adType);
        networkPlacementIds = networkPlacementIds(platformId, adType);
        if (networkNames.length == 0 || networkPlacementIds.length == 0) {
            networkLayout.setVisibility(View.GONE);
            placementEditText.setText("");
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, networkNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkSpinner.setAdapter(adapter);
        networkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < networkPlacementIds.length) {
                    placementEditText.setText(networkPlacementIds[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        placementEditText.setText(networkPlacementIds[0]);
    }

    private String[] networkNames(String platformId, String adType) {
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId)) {
            if ("splash".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_splash_adapter);
            }
            if ("banner".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_banner_adapter);
            }
            if ("interstitial".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_interstitial_adapter);
            }
            if ("reward".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_reward_adapter);
            }
            if ("native_template".equals(adType)) {
                return TOBID_NATIVE_TEMPLATE_NAMES;
            }
            if ("native_self".equals(adType)) {
                return TOBID_NATIVE_SELF_NAMES;
            }
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId)) {
            return takuNetworkNames(adType);
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId)) {
            return gromoreNetworkNames(adType);
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId)) {
            return beiziNetworkNames(adType);
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId)) {
            return jiGuangNetworkNames(adType);
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId)) {
            return mediatomNetworkNames(adType);
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId)) {
            return admateNetworkNames(adType);
        }
        return new String[0];
    }

    private String[] networkPlacementIds(String platformId, String adType) {
        if (PlatformRegistry.PLATFORM_TOBID.equals(platformId)) {
            if ("splash".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_splash_id_value);
            }
            if ("banner".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_banner_id_value);
            }
            if ("interstitial".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_interstitial_id_value);
            }
            if ("reward".equals(adType)) {
                return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_reward_id_value);
            }
            if ("native_template".equals(adType)) {
                return TOBID_NATIVE_TEMPLATE_IDS;
            }
            if ("native_self".equals(adType)) {
                return TOBID_NATIVE_SELF_IDS;
            }
        }
        if (PlatformRegistry.PLATFORM_TAKU.equals(platformId)) {
            return takuNetworkPlacementIds(adType);
        }
        if (PlatformRegistry.PLATFORM_GROMORE.equals(platformId)) {
            return gromoreNetworkPlacementIds(adType);
        }
        if (PlatformRegistry.PLATFORM_BEIZI.equals(platformId)) {
            return beiziNetworkPlacementIds(adType);
        }
        if (PlatformRegistry.PLATFORM_JIGUANG.equals(platformId)) {
            return jiGuangNetworkPlacementIds(adType);
        }
        if (PlatformRegistry.PLATFORM_MEDIATOM.equals(platformId)) {
            return mediatomNetworkPlacementIds(adType);
        }
        if (PlatformRegistry.PLATFORM_ADMATE.equals(platformId)) {
            return admateNetworkPlacementIds(adType);
        }
        return new String[0];
    }

    private String[] takuNetworkNames(String adType) {
        if ("splash".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_splash_adapter);
        }
        if ("banner".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_banner_adapter);
        }
        if ("interstitial".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_interstitial_adapter);
        }
        if ("reward".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_reward_adapter);
        }
        if ("native_template".equals(adType)) {
            return TOBID_NATIVE_TEMPLATE_NAMES;
        }
        if ("native_self".equals(adType)) {
            return TOBID_NATIVE_SELF_NAMES;
        }
        return new String[0];
    }

    private String takuDefaultPlacementId(String adType) {
        if ("splash".equals(adType)) {
            return firstPlacementId(PlacementIdUtil.getSplashPlacements(this));
        }
        if ("banner".equals(adType)) {
            return firstPlacementId(PlacementIdUtil.getBannerPlacements(this));
        }
        if ("interstitial".equals(adType)) {
            return firstPlacementId(PlacementIdUtil.getInterstitialPlacements(this));
        }
        if ("reward".equals(adType)) {
            return firstPlacementId(PlacementIdUtil.getRewardedVideoPlacements(this));
        }
        if ("native_template".equals(adType)) {
            return placementIdByKey(PlacementIdUtil.getNativeSelfrenderPlacements(this), "Express");
        }
        if ("native_self".equals(adType)) {
            return placementIdByKey(PlacementIdUtil.getNativeSelfrenderPlacements(this), "Native");
        }
        return "";
    }

    private String[] takuNetworkPlacementIds(String adType) {
        String[] names = takuNetworkNames(adType);
        String[] ids = new String[names.length];
        Map<String, String> placements = takuPlacementMap(adType);
        for (int i = 0; i < names.length; i++) {
            String placementId = placements.get(names[i]);
            if (TextUtils.isEmpty(placementId) && i == 0) {
                placementId = takuDefaultPlacementId(adType);
            }
            ids[i] = TextUtils.isEmpty(placementId) ? "" : placementId;
        }
        return ids;
    }

    private Map<String, String> takuPlacementMap(String adType) {
        if ("splash".equals(adType)) {
            return PlacementIdUtil.getSplashPlacements(this);
        }
        if ("banner".equals(adType)) {
            return PlacementIdUtil.getBannerPlacements(this);
        }
        if ("interstitial".equals(adType)) {
            return PlacementIdUtil.getInterstitialPlacements(this);
        }
        if ("reward".equals(adType)) {
            return PlacementIdUtil.getRewardedVideoPlacements(this);
        }
        if ("native_template".equals(adType) || "native_self".equals(adType)) {
            return PlacementIdUtil.getNativeSelfrenderPlacements(this);
        }
        return Collections.emptyMap();
    }

    private String firstPlacementId(Map<String, String> placements) {
        if (placements.containsKey("AdGain")) {
            return placements.get("AdGain");
        }
        if (placements.containsKey("All")) {
            return placements.get("All");
        }
        List<String> names = new ArrayList<>(placements.keySet());
        Collections.sort(names);
        if (names.isEmpty()) {
            return "";
        }
        return placements.get(names.get(0));
    }

    private String placementIdByKey(Map<String, String> placements, String key) {
        String placementId = placements.get(key);
        if (!TextUtils.isEmpty(placementId)) {
            return placementId;
        }
        return firstPlacementId(placements);
    }

    private String[] gromoreNetworkNames(String adType) {
        if ("splash".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_splash_adapter);
        }
        if ("banner".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_banner_adapter);
        }
        if ("interstitial".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_interstitial_adapter);
        }
        if ("reward".equals(adType)) {
            return getResources().getStringArray(com.windmill.android.demo.R.array.tobid_reward_adapter);
        }
        if ("native_template".equals(adType)) {
            return TOBID_NATIVE_TEMPLATE_NAMES;
        }
        if ("native_self".equals(adType)) {
            return TOBID_NATIVE_SELF_NAMES;
        }
        return new String[0];
    }

    private String[] gromoreNetworkPlacementIds(String adType) {
        String[] names = gromoreNetworkNames(adType);
        String[] ids = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            ids[i] = i == 0 ? gromoreDefaultPlacementId(adType) : "";
        }
        return ids;
    }

    private String gromoreDefaultPlacementId(String adType) {
        if ("splash".equals(adType)) {
            return getString(com.union_test.toutiao.R.string.splash_media_id);
        }
        if ("banner".equals(adType)) {
            return getString(com.union_test.toutiao.R.string.banner_media_id);
        }
        if ("interstitial".equals(adType)) {
            return getString(com.union_test.toutiao.R.string.full_media_id);
        }
        if ("reward".equals(adType)) {
            return getString(com.union_test.toutiao.R.string.reward_media_id);
        }
        if ("native_template".equals(adType)) {
            return getString(com.union_test.toutiao.R.string.feed_native_express_media_id);
        }
        if ("native_self".equals(adType)) {
            return getString(com.union_test.toutiao.R.string.feed_native_media_id);
        }
        return "";
    }

    private String[] beiziNetworkNames(String adType) {
        return gromoreNetworkNames(adType);
    }

    private String[] beiziNetworkPlacementIds(String adType) {
        String[] names = beiziNetworkNames(adType);
        String[] ids = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            ids[i] = i == 0 ? beiziDefaultPlacementId(adType) : "";
        }
        return ids;
    }

    private String beiziDefaultPlacementId(String adType) {
        if ("splash".equals(adType)) {
            return com.amps.demo.Constants.AMPS_SPACE_ID_SPLASH;
        }
        if ("banner".equals(adType)) {
            return com.amps.demo.Constants.AMPS_SPACE_ID_BANNER;
        }
        if ("interstitial".equals(adType)) {
            return com.amps.demo.Constants.AMPS_SPACE_ID_INTERSTITIAL;
        }
        if ("reward".equals(adType)) {
            return com.amps.demo.Constants.AMPS_SPACE_ID_REWARDVIDEO;
        }
        if ("native_template".equals(adType)) {
            return com.amps.demo.Constants.AMPS_SPACE_ID_NATIVE;
        }
        if ("native_self".equals(adType)) {
            return com.amps.demo.Constants.AMPS_SPACE_ID_UNIFIED;
        }
        return "";
    }

    private String[] jiGuangNetworkNames(String adType) {
        return gromoreNetworkNames(adType);
    }

    private String[] jiGuangNetworkPlacementIds(String adType) {
        String[] names = jiGuangNetworkNames(adType);
        String[] ids = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            ids[i] = i == 0 ? jiGuangDefaultPlacementId(adType) : "";
        }
        return ids;
    }

    private String jiGuangDefaultPlacementId(String adType) {
        if ("splash".equals(adType)) {
            return com.jiguangssp.addemo.constant.ADJgDemoConstant.SPLASH_AD_POS_ID1;
        }
        if ("banner".equals(adType)) {
            return com.jiguangssp.addemo.constant.ADJgDemoConstant.BANNER_AD_POS_ID1;
        }
        if ("interstitial".equals(adType)) {
            return com.jiguangssp.addemo.constant.ADJgDemoConstant.INTERSTITIAL_AD_POS_ID1;
        }
        if ("reward".equals(adType)) {
            return com.jiguangssp.addemo.constant.ADJgDemoConstant.REWARD_VOD_AD_POS_ID1;
        }
        if ("native_template".equals(adType)) {
            return com.jiguangssp.addemo.constant.ADJgDemoConstant.NATIVE_AD_POS_ID1;
        }
        if ("native_self".equals(adType)) {
            return com.jiguangssp.addemo.constant.ADJgDemoConstant.NATIVE_AD_POS_ID2;
        }
        return "";
    }

    private String[] mediatomNetworkNames(String adType) {
        return gromoreNetworkNames(adType);
    }

    private String[] mediatomNetworkPlacementIds(String adType) {
        String[] names = mediatomNetworkNames(adType);
        String[] ids = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            ids[i] = i == 0 ? mediatomDefaultPlacementId(adType) : "";
        }
        return ids;
    }

    private String mediatomDefaultPlacementId(String adType) {
        if ("splash".equals(adType)) {
            return "bdc9ebfbf3bb373d";
        }
        if ("banner".equals(adType)) {
            return "9ad1d92c6db6d082";
        }
        if ("interstitial".equals(adType)) {
            return "28af5c3ec9f697d1";
        }
        if ("reward".equals(adType)) {
            return "10e06808bad26969";
        }
        if ("native_template".equals(adType)) {
            return "5d2921d5ecfcd9bc";
        }
        if ("native_self".equals(adType)) {
            return "ed42a944cd707cef";
        }
        return "";
    }

    private String[] admateNetworkNames(String adType) {
        return ADMATE_NETWORK_NAMES;
    }

    private String[] admateNetworkPlacementIds(String adType) {
        String[] ids = new String[ADMATE_NETWORK_KEYS.length];
        for (int i = 0; i < ADMATE_NETWORK_KEYS.length; i++) {
            com.meishu.sdkdemo.adid.IAdIdProvider provider =
                    com.meishu.sdkdemo.adid.IdProviderFactory.getProvider(ADMATE_NETWORK_KEYS[i]);
            ids[i] = provider == null ? "" : admatePlacementId(provider, adType);
        }
        return ids;
    }

    private String admatePlacementId(com.meishu.sdkdemo.adid.IAdIdProvider provider, String adType) {
        String placementId = "";
        if ("splash".equals(adType)) {
            placementId = provider.splash();
        } else if ("banner".equals(adType)) {
            placementId = provider.banner();
        } else if ("interstitial".equals(adType)) {
            placementId = provider.insertScreen();
        } else if ("reward".equals(adType)) {
            placementId = provider.rewardPortrait();
        } else if ("native_template".equals(adType)) {
            placementId = provider.feedMix();
        } else if ("native_self".equals(adType)) {
            placementId = com.meishu.sdkdemo.adid.IdProviderFactory.PLATFORM_MS.equals(provider.platformName())
                    ? "1092421"
                    : provider.feedMix();
        }
        return TextUtils.isEmpty(placementId) ? "" : placementId.trim();
    }

    private void appendLog(String message) {
        logs.append(message).append('\n');
        logView.setText(logs.toString());
    }

    @Override
    protected void onDestroy() {
        if (controller != null) {
            controller.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
