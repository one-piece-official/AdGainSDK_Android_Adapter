package com.jiguangssp.addemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jiguangssp.addemo.util.SPUtil;

import cn.jiguang.jgssp.ADJgSdk;
import com.jiguangssp.addemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch switchPersonalized;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.tvVersion)).setText("V" + ADJgSdk.getInstance().getSdkVersion());

        findViewById(R.id.btnSplashAd).setOnClickListener(this);
        findViewById(R.id.btnSplashAdLoadShowSeparation).setOnClickListener(this);
        findViewById(R.id.btnBannerAd).setOnClickListener(this);
        findViewById(R.id.btnNativeAd).setOnClickListener(this);
        findViewById(R.id.btnNativeExpressAd).setOnClickListener(this);
        findViewById(R.id.btnRewardVodAd).setOnClickListener(this);
        findViewById(R.id.btnInterstitialAd).setOnClickListener(this);

        switchPersonalized = findViewById(R.id.switch_personalized);

        boolean personalized = SPUtil.getBoolean(this, SettingActivity.KEY_PERSONALIZED, true);
        switchPersonalized.setChecked(personalized);
        ADJgSdk.setPersonalizedAdEnabled(personalized);

        switchPersonalized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ADJgSdk.setPersonalizedAdEnabled(isChecked);
                SPUtil.putBoolean(MainActivity.this, SettingActivity.KEY_PERSONALIZED, isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSplashAd:
                startActivity(SplashAdActivity.class);
                break;
            case R.id.btnSplashAdLoadShowSeparation:
                startActivity(SplashAdLoadShowSeparationActivity.class);
                break;
            case R.id.btnBannerAd:
                startActivity(BannerAdActivity.class);
                break;
            case R.id.btnNativeAd:
                startActivity(NativeAdActivity.class);
                break;
            case R.id.btnNativeExpressAd:
                startActivity(NativeExpressAdActivity.class);
                break;
            case R.id.btnRewardVodAd:
                startActivity(RewardVodAdActivity.class);
                break;
            case R.id.btnInterstitialAd:
                startActivity(InterstitialAdActivity.class);
                break;
            default:
                break;
        }
    }

    private void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_setting:
                startActivity(SettingActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
