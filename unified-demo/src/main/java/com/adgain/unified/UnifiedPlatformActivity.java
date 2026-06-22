package com.adgain.unified;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UnifiedPlatformActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unified_platform);

        LinearLayout container = findViewById(R.id.platform_container);
        for (PlatformEntry platform : PlatformRegistry.all()) {
            Button button = new Button(this);
            button.setText(platform.name);
            button.setAllCaps(false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPlatform(platform.id);
                }
            });
            container.addView(button, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(48)
            ));
        }
    }

    private void openPlatform(String platformId) {
        PlatformEntry platform = PlatformRegistry.find(platformId);
        if (platform == null) {
            Toast.makeText(this, "平台未配置", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            platform.initializer.init(this);
            Toast.makeText(this, platform.name + " 初始化完成", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UnifiedAdTypeActivity.class);
            intent.putExtra(UnifiedAdTypeActivity.EXTRA_PLATFORM_ID, platform.id);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } catch (Throwable throwable) {
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
