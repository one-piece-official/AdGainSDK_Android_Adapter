package com.amps.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class AMPSDownloadAppInfoActivity extends Activity {

    private TextView appInfoBackTv, appInfoTitleTv;
    private TextView permissionTv, privacyTv, introTv;
    private WebView mWebView;
    private String titleContent = "";//App名字
    private String permissionContent = "";//权限内容
    private String privacyContent = "";//隐私内容
    private String introContent = "";//功能简介内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beizi_activity_download_appinfo);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            titleContent = bundle.getString("title_content_key");
            permissionContent = bundle.getString("permission_content_key");
            privacyContent = bundle.getString("privacy_content_key");
            introContent = bundle.getString("intro_content_key");
        }
        appInfoBackTv = findViewById(R.id.download_back);
        appInfoTitleTv = findViewById(R.id.download_title);
        permissionTv = findViewById(R.id.download_permission_tv);
        privacyTv = findViewById(R.id.download_privacy_tv);
        introTv = findViewById(R.id.download_intro_tv);
        mWebView = findViewById(R.id.download_wv);
        if (!TextUtils.isEmpty(titleContent)) {
            appInfoTitleTv.setText(titleContent);
        }
        setOnClickEvent();
        permissionTv.setTextColor(Color.parseColor("#3D7BF9"));
        privacyTv.setTextColor(Color.parseColor("#C2C3C5"));
        introTv.setTextColor(Color.parseColor("#C2C3C5"));
        showTabView(permissionContent);
    }

    private void setOnClickEvent() {
        appInfoBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        permissionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionTv.setTextColor(Color.parseColor("#3D7BF9"));
                privacyTv.setTextColor(Color.parseColor("#C2C3C5"));
                introTv.setTextColor(Color.parseColor("#C2C3C5"));
                showTabView(permissionContent);
            }
        });
        privacyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionTv.setTextColor(Color.parseColor("#C2C3C5"));
                privacyTv.setTextColor(Color.parseColor("#3D7BF9"));
                introTv.setTextColor(Color.parseColor("#C2C3C5"));
                showTabView(privacyContent);
            }
        });
        introTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionTv.setTextColor(Color.parseColor("#C2C3C5"));
                privacyTv.setTextColor(Color.parseColor("#C2C3C5"));
                introTv.setTextColor(Color.parseColor("#3D7BF9"));
                showTabView(introContent);
            }
        });
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setEnableSmoothTransition(true);
        mWebSettings.setLightTouchEnabled(false);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setSavePassword(false);
        // mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setSupportZoom(false);
        // Don't allow view port.
        mWebSettings.setUseWideViewPort(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // Allow debugging webview.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(false);
        }

        // Enable Application cache.
        if (Build.VERSION.SDK_INT <= 33) {
            String appCachePath = this.getCacheDir().getAbsolutePath();
            mWebSettings.setAppCachePath(appCachePath);
            mWebSettings.setAppCacheEnabled(true);
        }
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.removeJavascriptInterface("accessibility");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
        }
        mWebSettings.setAllowFileAccess(false);
        mWebSettings.setAllowContentAccess(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebSettings.setAllowFileAccessFromFileURLs(false);
            mWebSettings.setAllowUniversalAccessFromFileURLs(false);
        }
        mWebSettings.setGeolocationEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cm = CookieManager.getInstance();
            if (cm != null) {
                cm.setAcceptThirdPartyCookies(mWebView, true);
            } else {
            }
        }
        mWebView.setHorizontalScrollbarOverlay(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollbarOverlay(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
    }

    /**
     * 具体显示View逻辑处理
     *
     * @param contentStr
     */
    private void showTabView(String contentStr) {
        if (TextUtils.isEmpty(contentStr)) {
            return;
        }
        String content = contentStr;
        if (!contentStr.startsWith("http")) {
            content = "<html><body>" + contentStr + "</body></html>";
        }
        if (content.startsWith("http")) {
            mWebView.loadUrl(content);
        } else {
            mWebView.loadData(content, "text/html", "utf-8");
        }
    }
}
