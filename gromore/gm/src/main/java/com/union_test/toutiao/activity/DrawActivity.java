package com.union_test.toutiao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.union_test.toutiao.R;

public class DrawActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Button button = (Button) findViewById(R.id.btn_Draw_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bindButton(R.id.btn_main_draw_native, DrawNativeVideoActivity.class);
        bindButton(R.id.express_draw_video_ad, DrawNativeExpressVideoActivity.class);

    }

    private void bindButton(int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrawActivity.this, clz);
                startActivity(intent);
            }
        });
    }
}
