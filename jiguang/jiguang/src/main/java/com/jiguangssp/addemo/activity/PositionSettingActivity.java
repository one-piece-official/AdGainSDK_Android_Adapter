package com.jiguangssp.addemo.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jiguang.jgssp.ad.data.ADJgAdType;
import cn.jiguang.jgssp.util.ADJgToastUtil;

import com.jiguangssp.addemo.R;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

/**
 * @author ciba
 * @description 设置界面
 * @date 2020/4/7
 */
public class PositionSettingActivity extends BaseAdActivity {
    private static final String AD_TYPE = "AD_TYPE";
    private static final String POS_ID_LIST = "POS_ID_LIST";
    private EditText etPosId;
    private TextView tvCount;
    private EditText etCount;
    private TextView tvAutoRefreshInterval;
    private EditText etAutoRefreshInterval;
    private SwitchCompat cbNativeMute;
    private String adType;
    private EditText etOnlySupportPlatform;
    private List<String> posIdList;
    private SwitchCompat cbOnlySupportPlatform;
    private TextView tvScene;
    private EditText etScene;
    private HashMap<String, String> platformMap = new HashMap<>();

    public static void start(Context context, String adType, ArrayList<String> posIdList) {
        Intent intent = new Intent(context, PositionSettingActivity.class);
        intent.putExtra(AD_TYPE, adType);
        intent.putStringArrayListExtra(POS_ID_LIST, posIdList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_setting);
        initPlatformData();
        initView();
        initListener();
        initData();
    }

    private void initPlatformData() {
        platformMap.put("所有(null或空字符串)", "");
        platformMap.put("天目(tianmu)", "tianmu");
        platformMap.put("极光Ads(jgads)", "jgads");
        platformMap.put("优量汇(gdt)", "gdt");
        platformMap.put("头条/穿山甲(toutiao)", "toutiao");
        platformMap.put("百度/百青藤(baidu)", "baidu");
        platformMap.put("快手(ksad)", "ksad");
        platformMap.put("汇量/Mobvsita(mintegral)", "mintegral");
    }

    private String getPlatformKey(Map<String, String> map, String value) {
        if (value == null) {
            value = "";
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return "";
    }

    private void initView() {
        etPosId = findViewById(R.id.etPosId);
        tvCount = findViewById(R.id.tvCount);
        etCount = findViewById(R.id.etCount);

        etOnlySupportPlatform = findViewById(R.id.etOnlySupportPlatform);
        cbOnlySupportPlatform = findViewById(R.id.cbOnlySupportPlatform);

        tvAutoRefreshInterval = findViewById(R.id.tvAutoRefreshInterval);
        etAutoRefreshInterval = findViewById(R.id.etAutoRefreshInterval);

        cbNativeMute = findViewById(R.id.cbNativeMute);

        tvScene = findViewById(R.id.tvScene);
        etScene = findViewById(R.id.etScene);

    }

    private void initListener() {
        findViewById(R.id.btnDefine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        etOnlySupportPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlatformSelectDialog();
            }
        });
        etPosId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPosIdSelectDialog();
            }
        });
        etCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdCountSelectDialog();
            }
        });
    }

    private void showAdCountSelectDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.ad_count, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etCount.setText(getResources().getStringArray(R.array.ad_count)[which]);
                    }
                })
                .create()
                .show();
    }

    private void showPosIdSelectDialog() {
        String[] posIds = new String[posIdList.size()];
        posIdList.toArray(posIds);

        new AlertDialog.Builder(this)
                .setItems(posIds, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etPosId.setText(posIdList.get(which));
                    }
                })
                .create()
                .show();
    }

    private void showPlatformSelectDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.platforms_zh, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etOnlySupportPlatform.setText(getResources().getStringArray(R.array.platforms_zh)[which]);
                    }
                })
                .create()
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        adType = getIntent().getStringExtra(AD_TYPE);
        adType = adType == null ? "" : adType;
        posIdList = getIntent().getStringArrayListExtra(POS_ID_LIST);
        posIdList = posIdList == null ? new ArrayList<String>() : posIdList;

        switch (adType) {
            case ADJgAdType.TYPE_SPLASH:
                etPosId.setText(ADJgDemoConstant.SPLASH_AD_POS_ID);
                String platformKey = getPlatformKey(platformMap, ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                break;
            case ADJgAdType.TYPE_BANNER:
                etPosId.setText(ADJgDemoConstant.BANNER_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                etAutoRefreshInterval.setText(String.valueOf(ADJgDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL));
                tvAutoRefreshInterval.setVisibility(View.VISIBLE);
                etAutoRefreshInterval.setVisibility(View.VISIBLE);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                etScene.setText(String.valueOf(ADJgDemoConstant.BANNER_AD_SCENE_ID));
                break;
            case ADJgAdType.TYPE_FLOW:
                etPosId.setText(ADJgDemoConstant.NATIVE_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                etCount.setText(String.valueOf(ADJgDemoConstant.NATIVE_AD_COUNT));
                etCount.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
                cbNativeMute.setVisibility(View.VISIBLE);
                cbNativeMute.setChecked(ADJgDemoConstant.NATIVE_AD_PLAY_WITH_MUTE);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                etScene.setText(String.valueOf(ADJgDemoConstant.NATIVE_AD_SCENE_ID));
                break;
            case ADJgAdType.TYPE_REWARD_VOD:
                etPosId.setText(ADJgDemoConstant.REWARD_VOD_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADJgDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                cbNativeMute.setVisibility(View.VISIBLE);
                cbNativeMute.setChecked(ADJgDemoConstant.REWARD_AD_PLAY_WITH_MUTE);
                etScene.setText(String.valueOf(ADJgDemoConstant.REWARD_VOD_AD_SCENE_ID));
                break;
            case ADJgAdType.TYPE_INTERSTITIAL:
                etPosId.setText(ADJgDemoConstant.INTERSTITIAL_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADJgDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                cbNativeMute.setVisibility(View.VISIBLE);
                cbNativeMute.setChecked(ADJgDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE);
                etScene.setText(String.valueOf(ADJgDemoConstant.INTERSTITIAL_AD_SCENE_ID));
                break;
            default:
                ADJgToastUtil.show(this, "非法广告类型");
                finish();
                break;
        }
    }

    private void updateData() {
        String posId = etPosId.getText().toString().trim();
        String platformKey = etOnlySupportPlatform.getText().toString().trim();
        String onlySupportPlatform = platformMap.get(platformKey);
        switch (adType) {
            case ADJgAdType.TYPE_SPLASH:
                ADJgDemoConstant.SPLASH_AD_POS_ID = posId;
                ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                break;
            case ADJgAdType.TYPE_BANNER:
                ADJgDemoConstant.BANNER_AD_POS_ID = posId;
                ADJgDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL = getAutoRefreshInterval();
                ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADJgDemoConstant.BANNER_AD_SCENE_ID = getSceneId();
                break;
            case ADJgAdType.TYPE_FLOW:
                ADJgDemoConstant.NATIVE_AD_POS_ID = posId;
                ADJgDemoConstant.NATIVE_AD_COUNT = getAdCount();
                ADJgDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADJgDemoConstant.NATIVE_AD_PLAY_WITH_MUTE = cbNativeMute.isChecked();
                ADJgDemoConstant.NATIVE_AD_SCENE_ID = getSceneId();
                break;
            case ADJgAdType.TYPE_REWARD_VOD:
                ADJgDemoConstant.REWARD_VOD_AD_POS_ID = posId;
                ADJgDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADJgDemoConstant.REWARD_AD_PLAY_WITH_MUTE = cbNativeMute.isChecked();
                ADJgDemoConstant.REWARD_VOD_AD_SCENE_ID = getSceneId();
                break;
            case ADJgAdType.TYPE_INTERSTITIAL:
                ADJgDemoConstant.INTERSTITIAL_AD_POS_ID = posId;
                ADJgDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADJgDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE = cbNativeMute.isChecked();
                ADJgDemoConstant.INTERSTITIAL_AD_SCENE_ID = getSceneId();
                break;
            default:
                break;
        }
        if (cbOnlySupportPlatform.isChecked()) {
            ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADJgDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADJgDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADJgDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        }

        ADJgToastUtil.show(this, "修改成功");
        finish();
    }

    private int getAutoRefreshInterval() {
        String autoRefreshIntervalStr = etAutoRefreshInterval.getText().toString().trim();
        try {
            int autoRefreshInterval = Integer.parseInt(autoRefreshIntervalStr);
            return autoRefreshInterval <= 0 ? 0 : autoRefreshInterval < 30 ? 30 : autoRefreshInterval > 120 ? 120 : autoRefreshInterval;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getAdCount() {
        String countStr = etCount.getText().toString().trim();
        try {
            int count = Integer.parseInt(countStr);
            return count <= 0 ? 1 : count > 3 ? 3 : count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String getSceneId() {
        String sceneStr = etScene.getText().toString().trim();
        return sceneStr;
    }
}
