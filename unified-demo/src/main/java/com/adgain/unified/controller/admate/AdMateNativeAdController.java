package com.adgain.unified.controller.admate;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adgain.unified.UnifiedAdController;
import com.adgain.unified.UnifiedAdLoadCallback;
import com.bumptech.glide.Glide;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.recycler.RecyclerAdData;
import com.meishu.sdk.core.ad.recycler.RecyclerAdEventListener;
import com.meishu.sdk.core.ad.recycler.RecyclerAdMediaListener;
import com.meishu.sdk.core.ad.recycler.RecyclerMixAdLoader;
import com.meishu.sdk.core.ad.recycler.RecylcerAdInteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;
import com.meishu.sdk.core.utils.MsAdPatternType;

import java.util.ArrayList;
import java.util.List;

public class AdMateNativeAdController implements UnifiedAdController {
    private final boolean expressMode;
    private RecyclerMixAdLoader nativeLoader;
    private final List<RecyclerAdData> nativeAds = new ArrayList<>();
    private final List<RecyclerAdData> usedAds = new ArrayList<>();

    public AdMateNativeAdController(boolean expressMode) {
        this.expressMode = expressMode;
    }

    @Override
    public void load(Activity activity, ViewGroup adContainer, String placementId, UnifiedAdLoadCallback callback) {
        destroy();
        AdMateAdControllerUtils.resetContainer(adContainer);
        callback.log("开始加载 AdMate " + adTypeName() + ": " + placementId);

        MsAdSlot adSlot = new MsAdSlot.Builder()
                .setPid(placementId)
                .build();
        nativeLoader = new RecyclerMixAdLoader(activity, adSlot, new RecyclerAdEventListener() {
            @Override
            public void onAdError(AdErrorInfo errorInfo) {
                callback.log("onAdError: " + errorInfo);
            }

            @Override
            public void onAdReady(List<RecyclerAdData> list) {
                nativeAds.clear();
                int expressCount = 0;
                int selfCount = 0;
                if (list != null) {
                    for (RecyclerAdData ad : list) {
                        if (ad != null) {
                            nativeAds.add(ad);
                            if (ad.isNativeExpress()) {
                                expressCount++;
                            } else {
                                selfCount++;
                            }
                        }
                    }
                }
                callback.log("onAdReady: " + (list == null ? 0 : list.size())
                        + ", 模板: " + expressCount + ", 自渲染: " + selfCount);
            }
        });
        nativeLoader.loadAd();
    }

    @Override
    public boolean isReady() {
        return !nativeAds.isEmpty();
    }

    @Override
    public void show(Activity activity, ViewGroup adContainer, UnifiedAdLoadCallback callback) {
        if (!isReady()) {
            callback.log("广告未 ready");
            Toast.makeText(activity, "广告未 ready", Toast.LENGTH_SHORT).show();
            return;
        }
        RecyclerAdData ad = removeTargetAd(callback);
        usedAds.add(ad);
        if (ad.isNativeExpress()) {
            renderExpress(activity, adContainer, ad, callback);
        } else {
            renderSelf(activity, adContainer, ad, callback);
        }
    }

    @Override
    public void destroy() {
        if (nativeLoader != null) {
            nativeLoader.destroy();
            nativeLoader = null;
        }
        destroyAds(nativeAds);
        destroyAds(usedAds);
        nativeAds.clear();
        usedAds.clear();
    }

    private RecyclerAdData removeTargetAd(UnifiedAdLoadCallback callback) {
        for (int i = 0; i < nativeAds.size(); i++) {
            RecyclerAdData ad = nativeAds.get(i);
            if (ad != null && ad.isNativeExpress() == expressMode) {
                return nativeAds.remove(i);
            }
        }
        RecyclerAdData ad = nativeAds.remove(0);
        callback.log("当前混合广告位未返回" + adTypeName()
                + "，按实际返回类型展示: " + (ad.isNativeExpress() ? "原生模板" : "原生自渲染"));
        return ad;
    }

    private void renderExpress(Activity activity, ViewGroup adContainer, RecyclerAdData ad,
                               UnifiedAdLoadCallback callback) {
        FrameLayout container = new FrameLayout(activity);
        bindAd(activity, ad, container, container, callback);
        AdMateAdControllerUtils.attachView(adContainer, container, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 AdMate 原生模板");
    }

    private void renderSelf(Activity activity, ViewGroup adContainer, RecyclerAdData ad,
                            UnifiedAdLoadCallback callback) {
        FrameLayout root = new FrameLayout(activity);
        root.setBackgroundColor(Color.WHITE);
        int padding = dp(activity, 12);
        root.setPadding(padding, padding, padding, padding);

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        root.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView title = textView(activity, 16, Color.BLACK, true);
        title.setText(nullToEmpty(ad.getTitle()));
        content.addView(title, textParams(activity, 0, 0));

        TextView desc = textView(activity, 13, Color.DKGRAY, false);
        desc.setText(nullToEmpty(ad.getDesc()));
        content.addView(desc, textParams(activity, 4, 0));

        FrameLayout mediaContainer = new FrameLayout(activity);
        LinearLayout.LayoutParams mediaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(activity, 190)
        );
        mediaParams.topMargin = dp(activity, 10);
        content.addView(mediaContainer, mediaParams);

        ImageView poster = new ImageView(activity);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mediaContainer.addView(poster, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        if (ad.getAdPatternType() == MsAdPatternType.VIDEO) {
            poster.setVisibility(View.GONE);
            ad.bindMediaView(mediaContainer, new RecyclerAdMediaListener() {
                @Override
                public void onVideoLoaded() {
                    callback.log("onVideoLoaded");
                }

                @Override
                public void onVideoStart() {
                    callback.log("onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    callback.log("onVideoPause");
                }

                @Override
                public void onVideoCompleted() {
                    callback.log("onVideoCompleted");
                }

                @Override
                public void onVideoError() {
                    callback.log("onVideoError");
                }

                @Override
                public void onVideoResume() {
                    callback.log("onVideoResume");
                }
            });
        } else {
            String imageUrl = firstImageUrl(ad);
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(activity).load(imageUrl).into(poster);
            }
        }

        TextView action = textView(activity, 14, Color.WHITE, true);
        action.setText("查看详情");
        action.setGravity(Gravity.CENTER);
        action.setBackgroundColor(Color.rgb(30, 126, 255));
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(activity, 40)
        );
        actionParams.topMargin = dp(activity, 10);
        content.addView(action, actionParams);

        List<View> clickViews = new ArrayList<>();
        clickViews.add(root);
        clickViews.add(action);
        bindAd(activity, ad, root, clickViews, callback);
        AdMateAdControllerUtils.attachView(adContainer, root, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        callback.log("展示 AdMate 原生自渲染: " + ad.getAdPatternType());
    }

    private void bindAd(Activity activity, RecyclerAdData ad, ViewGroup container, View clickView,
                        UnifiedAdLoadCallback callback) {
        List<View> clickViews = new ArrayList<>();
        clickViews.add(clickView);
        bindAd(activity, ad, container, clickViews, callback);
    }

    private void bindAd(Activity activity, RecyclerAdData ad, ViewGroup container, List<View> clickViews,
                        UnifiedAdLoadCallback callback) {
        ad.bindAdToView(activity, container, clickViews, new RecylcerAdInteractionListener() {
            @Override
            public void onAdClosed() {
                callback.log("onAdClosed");
            }

            @Override
            public void onAdRenderFailed() {
                callback.log("onAdRenderFailed");
            }

            @Override
            public void onAdExposure() {
                callback.log("onAdExposure");
            }

            @Override
            public void onAdClicked() {
                callback.log("onAdClicked");
            }
        });
    }

    private void destroyAds(List<RecyclerAdData> ads) {
        for (RecyclerAdData ad : ads) {
            if (ad != null) {
                ad.destroy();
            }
        }
    }

    private String firstImageUrl(RecyclerAdData ad) {
        String[] images = ad.getImgUrls();
        if (images == null) {
            return "";
        }
        for (String image : images) {
            if (!TextUtils.isEmpty(image)) {
                return image;
            }
        }
        return "";
    }

    private String nullToEmpty(String text) {
        return text == null ? "" : text;
    }

    private TextView textView(Activity activity, int sizeSp, int color, boolean singleLine) {
        TextView textView = new TextView(activity);
        textView.setTextSize(sizeSp);
        textView.setTextColor(color);
        if (singleLine) {
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            textView.setMaxLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        return textView;
    }

    private LinearLayout.LayoutParams textParams(Activity activity, int topMargin, int bottomMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(activity, topMargin);
        params.bottomMargin = dp(activity, bottomMargin);
        return params;
    }

    private int dp(Activity activity, int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }

    private String adTypeName() {
        return expressMode ? "原生模板" : "原生自渲染";
    }
}
