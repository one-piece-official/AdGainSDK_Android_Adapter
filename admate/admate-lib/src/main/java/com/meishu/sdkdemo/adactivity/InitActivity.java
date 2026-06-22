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
import com.meishu.sdkdemo.AdMateInitializer;
import com.meishu.sdkdemo.R;
import com.tencent.bugly.crashreport.CrashReport;


public class InitActivity extends AppCompatActivity {

    private TextView mTVAgree;
    private TextView mTVNoAgree;
    private WebView mPrivacyWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admate_activity_init);

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
        AdMateInitializer.init(this);
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
