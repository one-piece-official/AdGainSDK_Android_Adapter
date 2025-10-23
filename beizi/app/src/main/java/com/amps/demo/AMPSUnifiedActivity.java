package com.amps.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import xyz.adscope.amps.ad.nativead.adapter.AMPSNativeAdExpressListener;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeAd;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeAdError;
import xyz.adscope.amps.ad.unified.AMPSUnifiedNativeLoadEventListener;
import xyz.adscope.amps.ad.unified.inter.AMPSAppDetail;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedAdEventListener;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedDownloadListener;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedNativeItem;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedPattern;
import xyz.adscope.amps.ad.unified.inter.AMPSUnifiedVideoListener;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedMediaViewStub;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedRootContainer;
import xyz.adscope.amps.ad.unified.view.AMPSUnifiedView;
import xyz.adscope.amps.common.AMPSConstants;
import xyz.adscope.amps.common.AMPSError;
import xyz.adscope.amps.config.AMPSRequestParameters;
import xyz.adscope.amps.tool.util.AMPSScreenUtil;
import xyz.adscope.common.info.deviceinfo.DeviceInfoUtil;
/**
 * 原生自渲染
 */
public class AMPSUnifiedActivity extends Activity {
    private static final String TAG = "AMPSUnifiedActivity";
    private Button loadView, showView;
    private FrameLayout containerFl;
    private List<AMPSUnifiedNativeItem> unifiedNativeItems;
    private List<AMPSUnifiedNativeItem> usedUnifiedNativeItems = new ArrayList<>();
    private EditText widthEt, heightEt;
    private String mUnifiedNativeId;
    private AMPSUnifiedNativeAd mAMPSUnifiedNativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unified_activity_layout);
        Intent intent = getIntent();
        if (intent != null) {
            mUnifiedNativeId = intent.getStringExtra("unifiedNativeId");
        }
        initView();
        initListener();
    }

    private void initView() {
        widthEt = findViewById(R.id.native_unified_ad_width_et);
        heightEt = findViewById(R.id.native_unified_ad_height_et);
        loadView = findViewById(R.id.load_unified);
        showView = findViewById(R.id.show_unified);
        containerFl = findViewById(R.id.native_unified_container_fl);
    }

    private void initListener() {
        loadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showView != null) {
                    showView.setVisibility(View.GONE);
                }
                if (containerFl != null) {
                    containerFl.removeAllViews();
                }
                loadUnified();
            }
        });
        showView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AMPSUnifiedNativeItem unifiedNativeItem = null;
                if (unifiedNativeItems != null && unifiedNativeItems.size() > 0) {
                    //每次取第0个位置的广告对象
                    unifiedNativeItem = unifiedNativeItems.get(0);
                    //从列表中移除广告对象
                    unifiedNativeItems.remove(0);
                }
                if (unifiedNativeItem == null) {
                    return;
                }
                usedUnifiedNativeItems.add(unifiedNativeItem);
                showUnified(unifiedNativeItem);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAMPSUnifiedNativeAd != null) {
            mAMPSUnifiedNativeAd.resume();
        }
        //已经使用的广告需要调用resume方法
        if (usedUnifiedNativeItems != null && usedUnifiedNativeItems.size() > 0) {
            for (AMPSUnifiedNativeItem item : usedUnifiedNativeItems) {
                item.resume();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAMPSUnifiedNativeAd != null) {
            mAMPSUnifiedNativeAd.pause();
        }
        //已经使用的广告需要调用pause方法
        if (usedUnifiedNativeItems != null && usedUnifiedNativeItems.size() > 0) {
            for (AMPSUnifiedNativeItem item : usedUnifiedNativeItems) {
                item.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAMPSUnifiedNativeAd != null) {
            mAMPSUnifiedNativeAd.destroy();
        }
        //已经使用的广告需要调用destroy方法
        if (usedUnifiedNativeItems != null && usedUnifiedNativeItems.size() > 0) {
            for (AMPSUnifiedNativeItem item : usedUnifiedNativeItems) {
                item.destroy();
            }
        }
        //未使用的广告需要调用destroy方法
        if (unifiedNativeItems != null && unifiedNativeItems.size() > 0) {
            for (AMPSUnifiedNativeItem item : unifiedNativeItems) {
                item.destroy();
            }
        }
    }

    private void loadUnified() {
        int width = AMPSScreenUtil.getScreenWidth(this);
        int height = 0;
        String widthStr = widthEt.getText().toString();
        String heightStr = heightEt.getText().toString();
        if (!TextUtils.isEmpty(widthStr) && !"0".equals(widthStr)) {
            width = DeviceInfoUtil.dip2px(this, Integer.parseInt(widthStr));
        }
        if (!TextUtils.isEmpty(heightStr)) {
            height = DeviceInfoUtil.dip2px(this, Integer.parseInt(heightStr));
        }
        if (height == 0) {
            height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        containerFl.setLayoutParams(params);
        if (height <= 0) {
            height = 0;
        }
        AMPSRequestParameters parameter = new AMPSRequestParameters.Builder()
                .setSpaceId(mUnifiedNativeId)
                .setWidth(width)
                .setHeight(height)
                .setTimeOut(5000)
                .setAdCount(3)
                .build();
        mAMPSUnifiedNativeAd = new AMPSUnifiedNativeAd(this, parameter,
                new AMPSUnifiedNativeLoadEventListener() {
                    @Override
                    public void onAmpsAdLoad(List<AMPSUnifiedNativeItem> nativeItems) {
                        if (showView != null) {
                            showView.setVisibility(View.VISIBLE);
                        }
                        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAmpsAdLoad nativeItems:" + nativeItems.size());
                        unifiedNativeItems = nativeItems;
                    }

                    @Override
                    public void onAmpsAdFailed(AMPSError error) {
                        Log.e(AMPSConstants.AMPS_LOG_TAG,
                                TAG + " onAmpsAdFailed " + error.getCode() + " "
                                        + error.getMessage());
                    }
                });
        mAMPSUnifiedNativeAd.loadAd();
    }

    private void showUnified(AMPSUnifiedNativeItem unifiedItem) {
        if (unifiedItem == null) {
            return;
        }
        if (!unifiedItem.isValid()) {
            return;
        }
        if (unifiedItem.isExpressAd()) {
            //渲染模板广告
            renderNativeExpressAd(unifiedItem);
        } else {
            //自渲染广告
            renderUnifiedNativeAd(unifiedItem);
        }
    }

    //渲染模板广告
    private void renderNativeExpressAd(AMPSUnifiedNativeItem unifiedItem) {
        //设置广告交互监听
        unifiedItem.setNativeAdExpressListener(new AMPSNativeAdExpressListener() {
            @Override
            public void onAdShow() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdShow");
            }

            @Override
            public void onAdClicked() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdClicked");
            }

            @Override
            public void onAdClosed(View view) {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onAdClosed");
                containerFl.removeView(view);
                showView.setVisibility(View.GONE);
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onRenderFail");
                showView.setVisibility(View.GONE);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onRenderSuccess ");
                containerFl.removeAllViews();
                containerFl.addView(view);
            }
        });
        //渲染广告
        unifiedItem.render();
    }

    //渲染自选染广告
    private void renderUnifiedNativeAd(AMPSUnifiedNativeItem unifiedItem) {
        //获取广告类型
        AMPSUnifiedPattern pattern = unifiedItem.getAdPattern();
        if (AMPSUnifiedPattern.AD_PATTERN_UNKNOWN.equals(pattern)) {
            return;
        }
        Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + "show unified " + pattern);
        View itemView = null;
        if (AMPSUnifiedPattern.AD_PATTERN_TEXT_IMAGE.equals(pattern)) {
            //渲染图文广告
            itemView = inflateImageText(unifiedItem);
        } else if (AMPSUnifiedPattern.AD_PATTERN_3_IMAGES.equals(pattern)) {
            //渲染组图广告
            itemView = inflateGroupImage(unifiedItem);
        } else if (AMPSUnifiedPattern.AD_PATTERN_VIDEO.equals(pattern)) {
            //渲染视频广告
            itemView = inflateVideoView(unifiedItem);
        }
        containerFl.removeAllViews();
        containerFl.addView(itemView);
    }


    /**
     * 渲染图文
     */
    private View inflateImageText(AMPSUnifiedNativeItem unifiedItem) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.native_unified_item_image_text
                , null);

        AMPSUnifiedRootContainer rootContainerView =
                itemView.findViewById(R.id.ad_unified_container);
        FrameLayout mainImageContainer = itemView.findViewById(R.id.ad_main_image_container);
        RelativeLayout adLogoRl = itemView.findViewById(R.id.ad_logo_rl);
        TextView adDownInfoTv = itemView.findViewById(R.id.ad_download_info_tv);
        FrameLayout adShakeViewFl = itemView.findViewById(R.id.ad_shake_view_fl);
        RelativeLayout actionRl = itemView.findViewById(R.id.ad_action_rl);
        TextView titleTv = itemView.findViewById(R.id.ad_title);
        TextView descTv = itemView.findViewById(R.id.ad_desc);

        List<View> clickViews = new ArrayList<>();
        clickViews.add(mainImageContainer);
        List<View> actionViews = new ArrayList<>();
        actionViews.add(actionRl);
        //渲染标题
        String adTitle = unifiedItem.getTitle();
        if (!TextUtils.isEmpty(adTitle)) {
            clickViews.add(titleTv);
            titleTv.setText(adTitle);
        }
        //渲染描述
        String adDesc = unifiedItem.getDesc();
        if (!TextUtils.isEmpty(adDesc)) {
            clickViews.add(descTv);
            descTv.setText(adDesc);
        }
        //渲染角标
        renderAdLogo(unifiedItem, adLogoRl);
        //渲染操作按钮,需支持文本和图片两种情况
        renderActionButton(unifiedItem, actionRl);
        //渲染下载类广告六要素信息
        renderDownloadInfo(unifiedItem, adDownInfoTv);
        //渲染渠道摇一摇交互view
        renderOptimizeView(unifiedItem, adShakeViewFl);
        //设置广告交互监听
        setNativeAdEventListener(unifiedItem);
        //渲染图片
        if (unifiedItem.isViewObject()) {
            AMPSUnifiedView ampsUnifiedView = unifiedItem.getMainImageView();
            if (ampsUnifiedView != null) {
                View imageView = ampsUnifiedView.getView();
                if (imageView != null) {
                    mainImageContainer.addView(imageView,
                            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        } else {
            Log.e(AMPSConstants.AMPS_LOG_TAG,
                    TAG + "main image url " + unifiedItem.getMainImageUrl());
            ImageView imageView = new ImageView(this);
            Glide.with(this).load(unifiedItem.getMainImageUrl()).into(imageView);
            mainImageContainer.addView(imageView,
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
        }
        unifiedItem.bindAdToRootContainer(this, rootContainerView, clickViews, actionViews);
        return itemView;
    }

    /**
     * 渲染组图
     */
    private View inflateGroupImage(AMPSUnifiedNativeItem unifiedItem) {
        View itemView =
                LayoutInflater.from(this).inflate(R.layout.native_unified_item_group_image, null);

        AMPSUnifiedRootContainer rootContainerView =
                itemView.findViewById(R.id.ad_unified_container);
        LinearLayout mainImageContainer = itemView.findViewById(R.id.ad_main_image_container);
        //角标容器
        RelativeLayout adLogoRl = itemView.findViewById(R.id.ad_logo_rl);
        TextView adDownInfoTv = itemView.findViewById(R.id.ad_download_info_tv);
        FrameLayout adShakeViewFl = itemView.findViewById(R.id.ad_shake_view_fl);
        RelativeLayout actionRl = itemView.findViewById(R.id.ad_action_rl);
        TextView titleTv = itemView.findViewById(R.id.ad_title);
        TextView descTv = itemView.findViewById(R.id.ad_desc);

        List<View> clickViews = new ArrayList<>();
        clickViews.add(mainImageContainer);
        List<View> actionViews = new ArrayList<>();
        actionViews.add(actionRl);
        //渲染标题
        String adTitle = unifiedItem.getTitle();
        if (!TextUtils.isEmpty(adTitle)) {
            clickViews.add(titleTv);
            titleTv.setText(adTitle);
        }
        //渲染描述
        String adDesc = unifiedItem.getDesc();
        if (!TextUtils.isEmpty(adDesc)) {
            clickViews.add(descTv);
            descTv.setText(adDesc);
        }
        //渲染角标
        renderAdLogo(unifiedItem, adLogoRl);
        //渲染操作按钮,需支持文本和图片两种情况
        renderActionButton(unifiedItem, actionRl);
        //渲染下载类广告六要素信息
        renderDownloadInfo(unifiedItem, adDownInfoTv);
        //渲染渠道摇一摇交互view
        renderOptimizeView(unifiedItem, adShakeViewFl);
        //设置广告交互监听
        setNativeAdEventListener(unifiedItem);
        //渲染图片
        if (unifiedItem.isViewObject()) {
            //渠道返回的是view对象
            List<AMPSUnifiedView> views = unifiedItem.getMainImageViews();
            if (views != null && views.size() > 0) {
                for (AMPSUnifiedView view : views) {
                    if (view == null) {
                        continue;
                    }
                    View imageView = view.getView();
                    LinearLayout.LayoutParams imageViewParam = new LinearLayout.LayoutParams(0,
                            160);
                    imageViewParam.leftMargin = 20;
                    imageViewParam.weight = 1;
                    imageView.setLayoutParams(imageViewParam);
                    mainImageContainer.addView(imageView);
                }
            }
        } else {
            //渠道返回的是图片地址
            List<String> imageList = unifiedItem.getImagesUrl();
            if (imageList != null && imageList.size() > 0) {
                for (int i = 0; i < imageList.size(); i++) {
                    Log.e(AMPSConstants.AMPS_LOG_TAG,
                            TAG + "main image url " + imageList.get(i));
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams imageViewParam = new LinearLayout.LayoutParams(0,
                            160);
                    imageViewParam.leftMargin = 20;
                    imageViewParam.weight = 1;
                    imageView.setLayoutParams(imageViewParam);
                    Glide.with(this).load(imageList.get(i)).into(imageView);
                    mainImageContainer.addView(imageView);
                }
            }
        }
        unifiedItem.bindAdToRootContainer(this, rootContainerView, clickViews, actionViews);
        return itemView;
    }

    /**
     * 渲染视频
     */
    private View inflateVideoView(AMPSUnifiedNativeItem unifiedItem) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.native_unified_item_video, null);

        AMPSUnifiedRootContainer rootContainerView =
                itemView.findViewById(R.id.ad_unified_container);
        AMPSUnifiedMediaViewStub videoStub = itemView.findViewById(R.id.ad_main_video);
        RelativeLayout adLogoRl = itemView.findViewById(R.id.ad_logo_rl);
        TextView adDownInfoTv = itemView.findViewById(R.id.ad_download_info_tv);
        FrameLayout adShakeViewFl = itemView.findViewById(R.id.ad_shake_view_fl);
        RelativeLayout actionRl = itemView.findViewById(R.id.ad_action_rl);
        TextView titleTv = itemView.findViewById(R.id.ad_title);
        TextView descTv = itemView.findViewById(R.id.ad_desc);

        List<View> clickViews = new ArrayList<>();
        List<View> actionViews = new ArrayList<>();
        actionViews.add(actionRl);
        //渲染标题
        String adTitle = unifiedItem.getTitle();
        if (!TextUtils.isEmpty(adTitle)) {
            clickViews.add(titleTv);
            titleTv.setText(adTitle);
        }
        //渲染描述
        String adDesc = unifiedItem.getDesc();
        if (!TextUtils.isEmpty(adDesc)) {
            clickViews.add(descTv);
            descTv.setText(adDesc);
        }
        //渲染角标
        renderAdLogo(unifiedItem, adLogoRl);
        //渲染操作按钮,需支持文本和图片两种情况
        renderActionButton(unifiedItem, actionRl);
        //渲染下载类广告六要素信息
        renderDownloadInfo(unifiedItem, adDownInfoTv);
        //渲染渠道摇一摇交互view
        renderOptimizeView(unifiedItem, adShakeViewFl);
        //设置广告交互监听
        setNativeAdEventListener(unifiedItem);
        //绑定广告到视图，必须先调用bindAdToRootContainer, 再调用bindAdToMediaView
        unifiedItem.bindAdToRootContainer(this, rootContainerView, clickViews, actionViews);
        //渲染视频
        unifiedItem.bindAdToMediaView(this, videoStub, new AMPSUnifiedVideoListener() {
            @Override
            public void onVideoInit() {

            }

            @Override
            public void onVideoLoading() {

            }

            @Override
            public void onVideoReady() {

            }

            @Override
            public void onVideoLoaded(int duration) {

            }

            @Override
            public void onVideoStart() {

            }

            @Override
            public void onVideoPause() {

            }

            @Override
            public void onVideoResume() {

            }

            @Override
            public void onVideoCompleted() {

            }

            @Override
            public void onVideoError(AMPSUnifiedNativeAdError adError) {

            }

            @Override
            public void onVideoStop() {

            }

            @Override
            public void onVideoClicked() {

            }
        });
        return itemView;
    }

    /**
     * 渲染广告角标
     *
     * @param unifiedItem
     * @param adLogoRl
     */
    private void renderAdLogo(AMPSUnifiedNativeItem unifiedItem, RelativeLayout adLogoRl) {
        String adSourceLogoUrl = unifiedItem.getAdSourceLogoUrl();
        if (TextUtils.isEmpty(adSourceLogoUrl)) {
            View adSourceLogo = unifiedItem.getAdSourceLogo();
            if (adSourceLogo != null) {
                adLogoRl.addView(adSourceLogo,
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        } else {
            Log.e(AMPSConstants.AMPS_LOG_TAG,
                    TAG + " adSourceLogoUrl: " + adSourceLogoUrl);
            ImageView logoIv = new ImageView(this);
            Glide.with(this).load(adSourceLogoUrl).into(logoIv);
            adLogoRl.addView(logoIv,
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 渲染操作按钮
     *
     * @param unifiedItem
     * @param actionRl
     */
    private void renderActionButton(AMPSUnifiedNativeItem unifiedItem, RelativeLayout actionRl) {
        String actionButtonText = unifiedItem.getActionButtonText();
        if (!TextUtils.isEmpty(actionButtonText)) {
            if (actionButtonText.startsWith("http")) {
                Log.e(AMPSConstants.AMPS_LOG_TAG,
                        TAG + " actionButtonText: " + actionButtonText);
                ImageView actionIv = new ImageView(this);
                Glide.with(this).load(actionButtonText).into(actionIv);
                actionRl.addView(actionIv,
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
            } else {
                TextView textView = new TextView(this);
                textView.setText(actionButtonText);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setPadding(20, 10, 20, 10);
                textView.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setGravity(Gravity.CENTER);
                actionRl.addView(textView,
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        }
    }

    /**
     * 渲染下载类六要素
     *
     * @param unifiedItem
     * @param adDownInfoTv
     */
    private void renderDownloadInfo(AMPSUnifiedNativeItem unifiedItem, TextView adDownInfoTv) {
        //渲染六要素信息
        AMPSAppDetail appDetail = unifiedItem.getAppDetail();
        if (isDownloadAd(appDetail)) {
            adDownInfoTv.setVisibility(View.VISIBLE);
            String content =
                    "应用名称：" + appDetail.getAppName() + " | 开发者：" + appDetail.getAppDeveloper() +
                            " | 应用版本：" + appDetail.getAppVersion() + " | 权限详情 | 隐私协议 | 功能介绍";
            adDownInfoTv.setText(content);
            adDownInfoTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //app层面处理点击权限，隐私、功能介绍显示页面，需要兼容处理文本和url地址两种情况
                    Log.e(AMPSConstants.AMPS_LOG_TAG,
                            TAG + "adDownInfoTv click "
                                    + "\n权限：" + appDetail.getAppPermissionInfo()
                                    + "\n隐私：" + appDetail.getAppPrivacyPolicy()
                                    + "\n功能：" + appDetail.getAppDescription());
                    Intent intent = new Intent();
                    intent.setClass(AMPSUnifiedActivity.this, AMPSDownloadAppInfoActivity.class);
                    intent.putExtra("title_content_key", appDetail.getAppName());
                    intent.putExtra("permission_content_key", appDetail.getAppPermissionInfo());
                    intent.putExtra("privacy_content_key", appDetail.getAppPrivacyPolicy());
                    intent.putExtra("intro_content_key", appDetail.getAppDescription());
                    startActivity(intent);
                }
            });
            unifiedItem.setDownloadListener(new AMPSUnifiedDownloadListener() {
                @Override
                public void onDownloadPaused(int position) {

                }

                @Override
                public void onDownloadStarted() {

                }

                @Override
                public void onDownloadProgressUpdate(int position) {

                }

                @Override
                public void onDownloadFinished() {

                }

                @Override
                public void onDownloadFailed() {

                }

                @Override
                public void onInstalled() {

                }
            });
        } else {
            adDownInfoTv.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否下载类广告
     *
     * @param appDetail
     * @return
     */
    private boolean isDownloadAd(AMPSAppDetail appDetail) {
        if (appDetail == null) {
            return false;
        }
        if (TextUtils.isEmpty(appDetail.getAppName())) {
            return false;
        }
        if (TextUtils.isEmpty(appDetail.getAppVersion())) {
            return false;
        }
        if (TextUtils.isEmpty(appDetail.getAppDeveloper())) {
            return false;
        }
        if (TextUtils.isEmpty(appDetail.getAppPermissionInfo())) {
            return false;
        }
        if (TextUtils.isEmpty(appDetail.getAppPrivacyPolicy())) {
            return false;
        }
        if (TextUtils.isEmpty(appDetail.getAppDescription())) {
            return false;
        }
        return true;
    }

    /**
     * 渲染交互view 摇一摇、滑一滑
     */
    private void renderOptimizeView(AMPSUnifiedNativeItem unifiedItem, FrameLayout adShakeViewFl) {
        //渲染渠道摇一摇交互view，按需添加交互，也可以不添加
        if (unifiedItem.getOptimizeController() != null) {
            //渲染渠道摇一摇交互view
            View shakeView = unifiedItem.getOptimizeController().getOptimizeShakeView(160, 160);
            if (shakeView != null) {
                adShakeViewFl.addView(shakeView);
            }
        }
    }

    /**
     * 设置广告交互事件监听
     *
     * @param unifiedItem
     */
    private void setNativeAdEventListener(AMPSUnifiedNativeItem unifiedItem) {
        unifiedItem.setNativeAdEventListener(new AMPSUnifiedAdEventListener() {
            @Override
            public void onADExposed() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onADExposed");
            }

            @Override
            public void onADClicked() {
                Log.e(AMPSConstants.AMPS_LOG_TAG, TAG + " onADClicked");
            }

            @Override
            public void onADExposeError(int errorCode, String errorMsg) {
                Log.e(AMPSConstants.AMPS_LOG_TAG,
                        TAG + " onADExposeError " + errorCode + " " + errorMsg);
            }
        });
    }
}
