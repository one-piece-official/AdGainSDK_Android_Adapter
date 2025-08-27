package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bytedance.tools.util.ToolsUtil;
import com.union_test.toutiao.R;

public class AllTestToolActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_test_tool);





        bindButton(R.id.btn_ip_port, IpPortToolActivity.class);
        Button btn = (Button)findViewById(R.id.btn_tools_back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_check_tool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolsUtil.start(AllTestToolActivity.this);
            }
        });
    }

    private void bindButton( int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllTestToolActivity.this, clz);
                startActivity(intent);
            }
        });
    }
















}
