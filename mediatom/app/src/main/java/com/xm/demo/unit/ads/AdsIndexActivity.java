package com.xm.demo.unit.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xm.demo.R;
import com.xm.demo.base.BaseActivity;

public class AdsIndexActivity extends BaseActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, AdsIndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_index);

        final EditText etKey = findViewById(R.id.et_key);

        findViewById(R.id.btn_splash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity2.launch(AdsIndexActivity.this, etKey.getText().toString().trim());
            }
        });

        findViewById(R.id.btn_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterstitialActivity.launch(AdsIndexActivity.this, etKey.getText().toString().trim());
            }
        });

        findViewById(R.id.btn_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerActivity.launch(AdsIndexActivity.this, etKey.getText().toString().trim());
            }
        });
        findViewById(R.id.btn_flow_recycler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlowRecyclerViewActivity.launch(AdsIndexActivity.this, etKey.getText().toString().trim());
            }
        });
        findViewById(R.id.btn_reward_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardVideoActivity.launch(AdsIndexActivity.this, etKey.getText().toString().trim());
            }
        });

        findViewById(R.id.btn_mix_native).setOnClickListener(v ->
                MixNativeActivity.launch(AdsIndexActivity.this));

    }
}
