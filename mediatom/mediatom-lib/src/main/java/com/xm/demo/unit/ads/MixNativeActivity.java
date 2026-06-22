package com.xm.demo.unit.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xm.demo.R;
import com.yd.saas.api.AdParams;
import com.yd.saas.api.mixNative.NativeAd;
import com.yd.saas.api.mixNative.NativeAdView;
import com.yd.saas.api.mixNative.NativeEventListener;
import com.yd.saas.api.mixNative.NativeLoadListener;
import com.yd.saas.api.mixNative.NativeMaterial;
import com.yd.saas.api.mixNative.NativePrepareInfo;
import com.yd.saas.common.util.Check;
import com.yd.saas.common.util.ImageUtils;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.config.utils.DeviceUtil;
import com.yd.saas.config.utils.LogcatUtil;
import com.yd.saas.ydsdk.api.YdSDK;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.yd.saas.api.mixNative.NativeAdConst.AD_TYPE_VIDEO;
import static com.yd.saas.common.util.Check.findFirstNonNull;
import static com.yd.saas.common.util.ImageUtils.rsBlur;
import static com.yd.saas.common.util.ImageUtils.setBlurImage;
import static com.yd.saas.config.utils.DeviceUtil.dip2px;
import static com.yd.saas.config.utils.DeviceUtil.px2dip;
import static java.lang.Math.round;

public class MixNativeActivity extends Activity {
    int adContainerW = DeviceUtil.getMobileWidth() - dip2px(12) * 2;
    int adContainerH = (int) (adContainerW * 0.28);

    int imageW = round(adContainerW * 0.4f);
    int imageH = adContainerH;

    public static void launch(Context context) {
        Intent _Intent = new Intent(context, MixNativeActivity.class);
        context.startActivity(_Intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediatom_activity_mix_native);
        FrameLayout container1 = findViewById(R.id.container_ad_exp_1);
        FrameLayout container2 = findViewById(R.id.container_ad_exp_2);
        findViewById(R.id.btn_exp_1).setOnClickListener(v -> {
            View adView = View.inflate(this, R.layout.mediatom_item_mix_native, null);
            loadAd(container1, adView, "5d2921d5ecfcd9bc");//模板ID
        });
        findViewById(R.id.btn_2).setOnClickListener(v -> {
            View adView = View.inflate(this, R.layout.mediatom_item_mix_native_example_2, null);
            loadAd(container2, adView, "ed42a944cd707cef"); // 自渲染ID
        });
    }

    private void loadAd(ViewGroup container, View adView, String codeId) {
        int width = px2dip(adContainerW);
        int height = px2dip(adContainerH);
        AdParams params = new AdParams.Builder(codeId)
                .setExpressHeight(height)
                .setExpressWidth(width)
                .setExpressAutoHeight()
                .setExpressFullWidth()
                .setImageAcceptedHeight(imageH)
                .setImageAcceptedWidth(imageW)
                .build();
        YdSDK.loadMixNative(this, params, new NativeLoadListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                inflateAd(container, adView, nativeAd);
            }

            @Override
            public void onAdFailed(YdError error) {
                String msg = String.format(Locale.getDefault(), "load onAdFailed, code:%d,msg:%s", error.getCode(), error.getMsg());
                showToast(msg);

            }
        });
    }

    private void setNativeEvent(ViewGroup container, NativeAd nativeAd) {
        nativeAd.setNativeEventListener(new NativeEventListener() {
            @Override
            public void onAdImpressed(NativeAdView adView) {
                // 广告展示成功
                showToast("onAdImpressed");
            }

            @Override
            public void onAdClicked(NativeAdView adView) {
                // 广告被点击
                showToast("onAdClicked");
            }

            /**
             * 用户点击关闭按钮，收到需调用方将广告View{@link NativeAdView}移除界面，部分广告联盟会自动移除广告View。
             * 模板广告均支持此回调，穿山甲广告渲染时需绑定Activity后支持；
             * 自渲染广告仅穿山甲、京准通绑定closeView后，支持此回调。
             */
            @Override
            public void onAdClose(NativeAdView adView) {
                // 广告被关闭，
                showToast("onAdClose");
                container.removeAllViews();
            }

            @Override
            public void onAdVideoStart(NativeAdView adView) {
                // 视频播放开始
                showToast("onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(NativeAdView adView) {
                // 视频播放结束
                showToast("onAdVideoEnd");
            }

            @Override
            public void onAdVideoProgress(NativeAdView adView, long progress) {
                // 视频播放进度，仅部分广告联盟支持
                showToast("onAdVideoProgress, progress:" + progress);
            }

            @Override
            public void onAdFailed(NativeAdView adView, YdError error) {
                // 广告渲染异常
                String msg = String.format(Locale.getDefault(), "Event onAdFailed, code:%d,msg:%s", error.getCode(), error.getMsg());
                showToast(msg);
            }
        });
    }

    private void inflateAd(ViewGroup container, View adView, NativeAd nativeAd) {
        setNativeEvent(container, nativeAd);

        container.removeAllViews();
        NativeAdView nativeAdView = new NativeAdView(this);

        container.addView(nativeAdView, new FrameLayout.LayoutParams(MATCH_PARENT, adContainerH));

        if (nativeAd.isNativeExpress()) {
            View mediaView = nativeAd.getAdMaterial().getAdMediaView();
            nativeAdView.addView(mediaView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            nativeAd.renderAdContainer(nativeAdView, null);
            NativePrepareInfo prepareInfo = new NativePrepareInfo();
            prepareInfo.setActivity(this);
            nativeAd.prepare(prepareInfo);
            return;
        }


        nativeAdView.addView(adView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        nativeAd.renderAdContainer(nativeAdView, adView);


        TextView tvTitle = adView.findViewById(R.id.title);
        TextView tvDesc = adView.findViewById(R.id.des);
        ImageView pic = adView.findViewById(R.id.image);
        FrameLayout mediaContainer = adView.findViewById(R.id.media_container);
        ImageView adLogo = adView.findViewById(R.id.iv_ad_logo);
        View colsed = adView.findViewById(R.id.iv_close);
        TextView action = adView.findViewById(R.id.btn_action);
        colsed.setOnClickListener(v -> container.removeAllViews());

        NativeMaterial nativeMaterial = nativeAd.getAdMaterial();
        tvTitle.setText(nativeMaterial.getTitle());
        tvDesc.setText(nativeMaterial.getDescription());

        if (TextUtils.isEmpty(nativeMaterial.getCallToAction())) {
            action.setText("查看详情");
        } else {
            action.setText(nativeMaterial.getCallToAction());
        }

        if (nativeMaterial.getAdType() == AD_TYPE_VIDEO && nativeMaterial.getAdMediaView() != null) {
            pic.setVisibility(View.GONE);
            mediaContainer.addView(nativeMaterial.getAdMediaView(), new ViewGroup.LayoutParams(imageW, MATCH_PARENT));
        } else {
            String imgUrl = nativeMaterial.getMainImageUrl();
            if (TextUtils.isEmpty(imgUrl)) {
                imgUrl = findFirstNonNull(nativeMaterial.getImageUrlList());
            }
            LogcatUtil.d("TEST", imgUrl);

            if (!TextUtils.isEmpty(imgUrl)) {
                Glide.with(this)
                        .load(imgUrl)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                int w = resource.getIntrinsicWidth();
                                int h = resource.getIntrinsicHeight();
                                if (h > w) {

                                    if (adView instanceof ConstraintLayout) {
                                        ViewGroup.LayoutParams layoutParams = pic.getLayoutParams();
                                        layoutParams.width = imageW;
                                        pic.setLayoutParams(layoutParams);

                                        ImageView bgView = new ImageView(getApplication());
                                        bgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        mediaContainer.addView(bgView, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                                        setBlurImage(bgView, resource);
                                    }
                                }
                                pic.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }

        if (nativeMaterial.getAdLogo() != null) {
            adLogo.setImageBitmap(nativeMaterial.getAdLogo());
        } else if (!TextUtils.isEmpty(nativeMaterial.getAdLogoUrl())) {
            Glide.with(getApplicationContext()).load(nativeMaterial.getAdLogoUrl()).into(adLogo);
        }

        NativePrepareInfo prepareInfo = new NativePrepareInfo();
        prepareInfo.setActivity(this);
        prepareInfo.setCloseView(colsed);
        prepareInfo.setClickView(adView);
        prepareInfo.setCtaView(action);
        prepareInfo.setImageView(pic);

        nativeAd.prepare(prepareInfo);
    }

    private void showToast(String msg) {
        Toast.makeText(MixNativeActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}
