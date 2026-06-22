package com.adgain.unified;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class UnifiedAdTypeActivity extends Activity {
    public static final String EXTRA_PLATFORM_ID = "platform_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unified_ad_type);

        String platformId = getIntent().getStringExtra(EXTRA_PLATFORM_ID);
        PlatformEntry platform = PlatformRegistry.find(platformId);
        if (platform == null) {
            Toast.makeText(this, "平台未配置", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView titleView = findViewById(R.id.tv_title);
        TextView subtitleView = findViewById(R.id.tv_subtitle);
        LinearLayout container = findViewById(R.id.ad_type_container);
        Button switchSdkButton = findViewById(R.id.btn_switch_sdk);
        titleView.setText(platform.name + " 广告类型");
        subtitleView.setText("选择广告类型后进入对应加载页。");
        switchSdkButton.setAllCaps(false);
        switchSdkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSdk();
            }
        });

        if (platform.adTypes.isEmpty()) {
            subtitleView.setText("该平台入口已预留，广告类型后续接入。");
            return;
        }

        for (AdTypeEntry adType : platform.adTypes) {
            Button button = new Button(this);
            button.setText(adType.title);
            button.setAllCaps(false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAdPage(adType);
                }
            });
            container.addView(button, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(48)
            ));
        }
    }

    private void openAdPage(AdTypeEntry adType) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(getPackageName(), adType.targetClassName));
            intent.putExtra(UnifiedAdLoadActivity.EXTRA_PLATFORM_ID, getIntent().getStringExtra(EXTRA_PLATFORM_ID));
            intent.putExtra(UnifiedAdLoadActivity.EXTRA_AD_TYPE, adType.type);
            intent.putExtra(UnifiedAdLoadActivity.EXTRA_AD_TITLE, adType.title);
            for (Map.Entry<String, String> extra : adType.extras.entrySet()) {
                intent.putExtra(extra.getKey(), extra.getValue());
            }
            startActivity(intent);
        } catch (Throwable throwable) {
            Toast.makeText(this, adType.title + " 页面待接入：" + adType.targetClassName, Toast.LENGTH_LONG).show();
        }
    }

    private void switchSdk() {
        Intent intent = new Intent(this, UnifiedPlatformActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
