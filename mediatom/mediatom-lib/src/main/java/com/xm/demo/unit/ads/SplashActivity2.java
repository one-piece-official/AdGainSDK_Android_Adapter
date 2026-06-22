package com.xm.demo.unit.ads;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xm.demo.R;
import com.xm.demo.base.BaseActivity;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.base.interfaces.SpreadLoadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;


/**
 * 启动页式开屏页
 */
public class SplashActivity2 extends BaseActivity {

    private FrameLayout llContainer;

    YdSpread ydSpread;
    private boolean canJump = false;
    private String key = "bdc9ebfbf3bb373d";


    public static void launch(Context context, String key) {
        Intent intent = new Intent(context, SplashActivity2.class);
        intent.putExtra("media_id", key);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediatom_activity_splash2);
        llContainer = (FrameLayout) findViewById(R.id.ll_container);
        String mediaId = getIntent().getStringExtra("media_id");
        if (!TextUtils.isEmpty(mediaId)) {
            this.key = mediaId;
        }

        loadAd();
    }

    private void loadAd() {

        ydSpread = new YdSpread.Builder(this)
                .setKey(key)
                .setContainer(llContainer)//请求和展示分开,可以不设置此参数
                .setSpreadLoadListener(new SpreadLoadListener() {
                    @Override
                    public void onADLoaded(final SpreadAd spreadAd) {
                        spreadAd.show(llContainer);
                    }
                })
                .setSpreadListener(new AdViewSpreadListener() {
                    @Override
                    public void onAdDisplay() {
                        Toast.makeText(SplashActivity2.this, "onAdDisplay", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAdClose() {
                        Toast.makeText(SplashActivity2.this, "onAdClose", Toast.LENGTH_LONG).show();
                        jumpToMain();
                    }

                    @Override
                    public void onAdClick(String url) {
                        Toast.makeText(SplashActivity2.this, "onAdClick", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        Toast.makeText(SplashActivity2.this, "onAdFailed", Toast.LENGTH_LONG).show();
                        // 广告异常、失败，中断时会调用
                        finish();
                    }

                })
                .build();
        ydSpread.requestSpread();
    }

    private void jumpToMain() {
        if (canJump) {
            doJump();
        } else {
            canJump = true;
        }
    }

    private void doJump() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ydSpread != null) {
            ydSpread.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            doJump();
        }
        canJump = true;
    }

    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
