package com.meishu.sdkdemo.adactivity.splash;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meishu.sdk.platform.ms.splash.ShakeUtil;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;

public class PrepareSplashActivity extends AppCompatActivity {

    private int mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_splash);

        ((EditText) findViewById(R.id.alternativeSplashAdPlaceID)).setText(IdProviderFactory.getDefaultProvider().splash());
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.putExtra("id", v.getId());
                intent.putExtra("alternativePlaceId", ((EditText) findViewById(R.id.alternativeSplashAdPlaceID)).getText().toString().trim());
                startActivity(intent);
//                finish();
            }
        };
        findViewById(R.id.loadSplashAd).setOnClickListener(clickListener);
        findViewById(R.id.loadAndShowSplashAd).setOnClickListener(clickListener);
        findViewById(R.id.customSkipSplashAd).setOnClickListener(clickListener);
        TextView currentText = findViewById(R.id.currentText);

        SeekBar seekBar = findViewById(R.id.seekBar);
        //清除摇一摇进度
        ShakeUtil.getInstance().indexLocal = 0;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                currentText.setText("摇一摇灵敏度: "+progress);
                ShakeUtil.getInstance().indexLocal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
