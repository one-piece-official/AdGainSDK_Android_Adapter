package com.meishu.sdkdemo.adactivity;


import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.sdk.openadsdk.LocationProvider;
import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.MSAdConfig;
import com.meishu.sdkdemo.R;
import com.tencent.bugly.crashreport.CrashReport;


public class InitActivity extends AppCompatActivity {

    private TextView mTVAgree;
    private TextView mTVNoAgree;
    private WebView mPrivacyWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        mTVAgree = findViewById(R.id.privaty_agree);
        mTVNoAgree = findViewById(R.id.privaty_no_agree);
        initWebView();
        initSdk();
        Intent intent = new Intent(InitActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        mTVAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(InitActivity.this,"初始化成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InitActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mTVNoAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initSdk() {
        CrashReport.initCrashReport(getApplicationContext(),"9d18334ffa", true);
        MSAdConfig sdkConfig = new MSAdConfig.Builder()
                .appId("106083")
//                .disableSensorType(Sensor.TYPE_ACCELEROMETER)//禁用摇一摇传感器
//                .disableSensorType(Sensor.TYPE_GRAVITY)//禁用重力传感器
//                .disableSensorType(Sensor.TYPE_GYROSCOPE)//禁用陀螺仪传感器
                .enableDebug(true)  //开启DEBUG模式，打印内部LOG
                .downloadConfirm(MSAdConfig.DOWNLOAD_CONFIRM_ALWAYS)  //下载提示模式
//                .userId("123456")                   //设置用户ID
//                .userType(1)                        //设置用户类型
//                .userKeywords("汽车,漫画")          //设置用户关键词
//                .enableSdkPersonalRecommend(true)   //开启SDK个性化推荐
//                .enableOaid(true)
                .customController(new MSAdConfig.CustomController() {
                    @Override
                    public String getOaid() {
                        //媒体如果设置enableOaid(false)，可以在这传入自己的oaid
                        return super.getOaid();
                    }

                    @Override
                    public boolean isCanUseLocation() {
                        //是否可以获取位置信息
                        return super.isCanUseLocation();
                    }

                    @Override
                    public LocationProvider getTTLocation() {
                        //如果禁用isCanUseLocation，传给穿山甲位置信息
                        return super.getTTLocation();
                    }

                    @Override
                    public Location getLocation() {
                        //快手获取位置信息
                        return super.getLocation();
                    }

                    @Override
                    public boolean isCanUsePhoneState() {
                        //是否允许SDK主动使用手机硬件参数，如：imei
                        return super.isCanUsePhoneState();
                    }

                    @Override
                    public boolean isCsjUsePhoneState() {
                        //针对穿山甲单独开关
                        return super.isCsjUsePhoneState();
                    }

                    @Override
                    public String getDevImei() {
                        //当isCanUsePhoneState=false时，可传入imei信息，sdk使用您传入的imei信息
                        return super.getDevImei();
                    }

                    @Override
                    public boolean isCanUseWifiState() {
                        return super.isCanUseWifiState();
                    }

                    @Override
                    public String getMacAddress() {
                        //是否允许SDK主动使用ACCESS_WIFI_STATE权限
                        return super.getMacAddress();
                    }

                    @Override
                    public boolean isCanUseAndroidId() {
                        //是否允许SDK主动获取ANDROID_ID
                        return super.isCanUseAndroidId();
                    }

                    @Override
                    public String getAndroidId() {
                        //如果isCanUseAndroidId=false，那么sdk获取传入的android
                        return super.getAndroidId();
                    }

                    @Override
                    public boolean canUseMacAddress() {
                        //是否允许获取mac地址
                        return super.canUseMacAddress();
                    }

                    @Override
                    public boolean canUseNetworkState() {
                        //是否允许获取网络状态
                        return super.canUseNetworkState();
                    }

                    @Override
                    public boolean canUseStoragePermission() {
                        ////是否允许SDK主动使用WRITE_EXTERNAL_STORAGE权限
                        return super.canUseStoragePermission();
                    }

                    @Override
                    public boolean canReadInstalledPackages() {
                        //是否允许SDK主动获取设备上应用安装列表
                        return super.canReadInstalledPackages();
                    }

                    @Override
                    public boolean isCanUseImsi() {
                        //是否允许获取imsi
                        return super.isCanUseImsi();
                    }

                    @Override
                    public boolean isCanUsePermissionRecordAudio() {
                        //穿山甲使用，是否允许SDK在申明和授权了的情况下使用录音权限
                        return super.isCanUsePermissionRecordAudio();
                    }

                    @Override
                    public boolean isStorageCollectEnable() {
                        // 是否允许SDK采集设备存储容量信息
                        return super.isStorageCollectEnable();
                    }
                })
                .build();

        AdSdk.init(this, sdkConfig);
    }

    private void initWebView() {
        mPrivacyWebView = findViewById(R.id.privacy_webview);
        WebSettings webSettings = mPrivacyWebView.getSettings();


        mPrivacyWebView.setHorizontalScrollBarEnabled(false);
        mPrivacyWebView.setVerticalScrollBarEnabled(false);
        mPrivacyWebView.setVerticalScrollBarEnabled(false);
        mPrivacyWebView.requestFocus();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSavePassword(false);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= 17) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        mPrivacyWebView.loadUrl("https://wwww.atdplus.cn/html/sdk-privacy-agreement/%E7%BE%8E%E6%95%B0%E8%81%9A%E5%90%88%E5%B9%BF%E5%91%8ASDK%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96.html");

    }

}

