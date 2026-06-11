package com.meishu.sdkdemo.adactivity.feed;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;

public class NativeRecyclerListSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private String timplateWidth;
    private String timplateHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_recycler_list_select);
        findViewById(R.id.mix_button).setOnClickListener(this);

        ((EditText) findViewById(R.id.mixAdPlaceID)).setText(IdProviderFactory.getDefaultProvider().feedMix());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        String name = IdProviderFactory.getDefaultProvider().platformName();
        switch (v.getId()) {
            case R.id.mix_button:
                intent = new Intent(this, MixRecyclerActivity.class);
                intent.putExtra("alternativePlaceId", ((EditText) findViewById(R.id.mixAdPlaceID)).getText().toString().trim());
                intent.putExtra(MixRecyclerActivity.SHOW_DETAIL, ((CheckBox) findViewById(R.id.mixAdShowDetail)).isChecked());
                startActivity(intent);
                break;
        }
    }
}
