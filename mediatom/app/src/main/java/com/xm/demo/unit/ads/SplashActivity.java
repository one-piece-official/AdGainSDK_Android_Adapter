package com.xm.demo.unit.ads;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.xm.demo.R;
import com.xm.demo.base.BaseActivity;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.base.interfaces.SpreadLoadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;

public class SplashActivity extends BaseActivity {

    private FrameLayout llContainer;
    private ImageView ivLogo;
    private int requestCode;

    YdSpread ydSpread;

    private boolean canJump = false;


    public static void launch(Context context) {
        launch(context, 0);
    }

    public static void launch(Context context, int requestCode) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra("request_code", requestCode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCode = getIntent().getIntExtra("request_code", 0);
        setContentView(R.layout.activity_splash);
        llContainer = (FrameLayout) findViewById(R.id.ll_container);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        //设置底部logo图片
        ivLogo.setImageResource(R.mipmap.ic_logo);

        loadAd();
    }

    private void loadAd() {

        ydSpread = new YdSpread.Builder(this)
                .setKey("bdc9ebfbf3bb373d")
                .setSpreadLoadListener(new SpreadLoadListener() {
                    @Override
                    public void onADLoaded(final SpreadAd spreadAd) {
                        spreadAd.show(llContainer);
                    }
                })
                .setSpreadListener(new AdViewSpreadListener() {
                    @Override
                    public void onAdDisplay() {
                        Toast.makeText(SplashActivity.this,"onAdDisplay",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onAdClose() {
                        Toast.makeText(SplashActivity.this,"onAdClose",Toast.LENGTH_LONG).show();

                        jumpToMain();
                    }

                    @Override
                    public void onAdClick(String url) {
                        Toast.makeText(SplashActivity.this,"onAdClick",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        Toast.makeText(SplashActivity.this,"onAdFailed",Toast.LENGTH_LONG).show();
                        jumpToMain();
                        // 广告异常、失败，中断时会调用
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
        if (requestCode == 0) { //冷启动进入开屏页，接下来要跳到首页；否则只需要关闭当前页面即可
            AdsIndexActivity.launch(this);
        }
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
