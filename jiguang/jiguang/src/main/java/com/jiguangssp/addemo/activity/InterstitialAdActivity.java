package com.jiguangssp.addemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;import android.view.View;
import android.widget.Button;

import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgInterstitialAd;
import cn.jiguang.jgssp.ad.data.ADJgInterstitialAdInfo;
import cn.jiguang.jgssp.ad.entity.ADJgExtraParams;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgInterstitialAdListener;
import cn.jiguang.jgssp.util.ADJgAdUtil;
import cn.jiguang.jgssp.util.ADJgToastUtil;
import com.jiguangssp.addemo.R;

/**
 * @author ciba
 * @description 插屏广告示例
 * @date 2020/3/27
 */
public class InterstitialAdActivity extends BaseAdActivity implements View.OnClickListener {
    private ADJgInterstitialAd interstitialAd;
    private ADJgInterstitialAdInfo interstitialAdInfo;

    private boolean loadAndShow;

    private boolean isLoad;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);

        initListener();
        initAd();
    }

    private void initListener() {
        Button btnLoadAd = findViewById(R.id.btnLoadAd);
        Button btnShowAd = findViewById(R.id.btnShowAd);
        Button btnLoadAndShowAd = findViewById(R.id.btnLoadAndShowAd);

        btnLoadAd.setOnClickListener(this);
        btnShowAd.setOnClickListener(this);
        btnLoadAndShowAd.setOnClickListener(this);
    }

    private void initAd() {
        interstitialAd = new ADJgInterstitialAd(this);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        interstitialAd.setOnlySupportPlatform(ADJgDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM);
        // 创建额外参数实例
        ADJgExtraParams extraParams = new ADJgExtraParams.Builder()
                // 设置视频类广告是否静音
                .setVideoWithMute(ADJgDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE)
                .build();
        interstitialAd.setLocalExtraParams(extraParams);
        // 设置插屏广告监听
        interstitialAd.setListener(new ADJgInterstitialAdListener() {
            @Override
            public void onAdReady(ADJgInterstitialAdInfo interstitialAdInfo) {
                // 建议在该回调之后展示广告
                Log.d(ADJgDemoConstant.TAG, "onAdReady...");
            }

            @Override
            public void onAdReceive(ADJgInterstitialAdInfo interstitialAdInfo) {
                InterstitialAdActivity.this.interstitialAdInfo = interstitialAdInfo;
                ADJgToastUtil.show(getApplicationContext(), "插屏广告获取成功");
                Log.d(ADJgDemoConstant.TAG, "onAdReceive...");

                if (loadAndShow) {
                    ADJgAdUtil.showInterstitialAdConvenient(InterstitialAdActivity.this, interstitialAdInfo);
                }

                isLoad = false;
            }

            @Override
            public void onAdExpose(ADJgInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(ADJgInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(ADJgInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClose...");
            }

            @Override
            public void onAdFailed(ADJgError adJgError) {
                if (adJgError != null) {
                    String failedJson = adJgError.toString();
                    Log.d(ADJgDemoConstant.TAG, "onAdFailed..." + failedJson);
                    ADJgToastUtil.show(getApplicationContext(), "广告获取失败" + failedJson);
                }

                isLoad = false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadAd:
                loadAndShow = false;
                loadAd();
                break;
            case R.id.btnShowAd:
                ADJgAdUtil.showInterstitialAdConvenient(this, interstitialAdInfo);
                break;
            case R.id.btnLoadAndShowAd:
                loadAndShow = true;
                loadAd();
                break;
            default:
                break;
        }
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        if (isLoad) {
            ADJgToastUtil.show(this, "广告加载中...");
            return;
        }
        isLoad = true;

        // 插屏广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        interstitialAd.setSceneId(ADJgDemoConstant.INTERSTITIAL_AD_SCENE_ID);
        // 加载插屏广告，参数为广告位ID
        interstitialAd.loadAd(ADJgDemoConstant.INTERSTITIAL_AD_POS_ID);
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
                showAdTypeCheckDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showAdTypeCheckDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.interstitial_ad_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toastContent = "设置完成，已生效";
                        switch (which) {
                            case 0:
                                ADJgDemoConstant.INTERSTITIAL_AD_POS_ID = ADJgDemoConstant.INTERSTITIAL_AD_POS_ID1;
                                break;
                            case 1:
                                ADJgDemoConstant.INTERSTITIAL_AD_POS_ID = ADJgDemoConstant.INTERSTITIAL_AD_POS_ID2;;
                                break;
                            default:
                                break;
                        }
                        ADJgToastUtil.show(InterstitialAdActivity.this, toastContent);
                        dialog.dismiss();
                    }
                }).create().show();
    }



}
