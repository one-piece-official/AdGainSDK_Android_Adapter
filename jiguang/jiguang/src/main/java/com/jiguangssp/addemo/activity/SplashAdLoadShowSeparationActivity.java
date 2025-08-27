package com.jiguangssp.addemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.jiguangssp.addemo.R;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgSplashAd;
import cn.jiguang.jgssp.ad.data.ADJgAdInfo;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgSplashAdListener;
import cn.jiguang.jgssp.util.ADJgToastUtil;

/**
 * @author : maipian
 * @date : 2022/09/06
 * @description : 开屏加载展示分离
 */
public class SplashAdLoadShowSeparationActivity extends AppCompatActivity implements View.OnClickListener {

    private ADJgSplashAd adJgSplashAd;

    private RelativeLayout flSplashContainer;
    private FrameLayout flContainer;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad_load_show_separation);

        flSplashContainer = findViewById(R.id.flSplashContainer);
        flContainer = findViewById(R.id.flContainer);

        initListener();
    }

    private void initListener() {
        findViewById(R.id.btnLoadAd).setOnClickListener(this);
        findViewById(R.id.btnShowAd).setOnClickListener(this);
    }

    private void loadAd() {
        releaseAd();
        // 创建开屏广告实例，第一个参数可以是Activity或Fragment，第二个参数是广告容器（请保证容器不会拦截点击、触摸等事件，高度不小于真实屏幕高度的75%，并且处于可见状态）
        adJgSplashAd = new ADJgSplashAd(this, flContainer);

        adJgSplashAd.setImmersive(true);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        adJgSplashAd.setOnlySupportPlatform(ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM);
        // 设置开屏广告监听
        adJgSplashAd.setListener(new ADJgSplashAdListener() {

            @Override
            public void onADTick(long millisUntilFinished) {
                Log.d(ADJgDemoConstant.TAG, "广告剩余倒计时时长回调：" + millisUntilFinished);
            }

            @Override
            public void onReward(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "优量汇奖励回调");
            }

            @Override
            public void onAdSkip(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "广告跳过回调，不一定准确，埋点数据仅供参考... ");
            }

            @Override
            public void onAdReceive(ADJgAdInfo adJgAdInfo) {
                ADJgToastUtil.show(getApplicationContext(), "开屏广告获取成功");
                Log.d(ADJgDemoConstant.TAG, "广告获取成功回调... ");

                findViewById(R.id.btnLoadAd).setVisibility(View.GONE);
                findViewById(R.id.btnShowAd).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdExpose(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "广告展示回调，有展示回调不一定是有效曝光，如网络等情况导致上报失败");
            }

            @Override
            public void onAdClick(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "广告点击回调，有点击回调不一定是有效点击，如网络等情况导致上报失败");
            }

            @Override
            public void onAdClose(ADJgAdInfo adJgAdInfo) {
                findViewById(R.id.btnLoadAd).setVisibility(View.VISIBLE);
                Log.d(ADJgDemoConstant.TAG, "广告关闭回调，需要在此进行页面跳转");
                jumpMain();
            }

            @Override
            public void onAdFailed(ADJgError adJgError) {
                if (adJgError != null) {
                    String failedJson = adJgError.toString();
                    Log.d(ADJgDemoConstant.TAG, "onAdFailed----->" + failedJson);
                    ADJgToastUtil.show(getApplicationContext(), "广告获取失败 : " + failedJson);
                }
                jumpMain();
            }
        });

        loadSplash();
    }

    /**
     * 释放广告
     */
    private void releaseAd() {
        if (adJgSplashAd != null) {
            adJgSplashAd.release();
            adJgSplashAd = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadAd:
                loadAd();
                break;
            case R.id.btnShowAd:
                findViewById(R.id.btnShowAd).setVisibility(View.GONE);
                flSplashContainer.setVisibility(View.VISIBLE);
                adJgSplashAd.showSplash();
                break;
            default:
                break;
        }
    }

    private void loadSplash() {
        adJgSplashAd.loadOnly(ADJgDemoConstant.SPLASH_AD_POS_ID);
    }

    /**
     * 跳转到主界面
     */
    private void jumpMain() {
        if (flSplashContainer != null) {
            flSplashContainer.setVisibility(View.GONE);
        }
        if (flContainer != null) {
            // 注意，目前已知头条渠道摇一摇是通过view触发的，需要移除视图中的广告布局，避免触发摇一摇
            flContainer.removeAllViews();
        }
        if (adJgSplashAd != null) {
            adJgSplashAd.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAd();
    }
}