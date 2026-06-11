package com.adgain.admate.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdGainImage;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdPatternType;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.meishu.sdk.core.ad.recycler.RecyclerAdMediaListener;
import com.meishu.sdk.core.domain.MeishuAdInfo;
import com.meishu.sdk.core.utils.MsAdPatternType;
import com.meishu.sdk.core.utils.MsInteractionType;
import com.meishu.sdk.platform.custom.recycler.MsCustomRecyclerAdapter;
import com.meishu.sdk.platform.custom.recycler.MsCustomRecyclerFeedAd;

import java.util.List;

public class CustomFeedAd extends MsCustomRecyclerFeedAd {

    private MsCustomRecyclerAdapter adWrapper;
    private NativeAdData nativeData;

    public CustomFeedAd(MsCustomRecyclerAdapter adWrapper, NativeAdData feedAd) {
        super(adWrapper);
        this.adWrapper = adWrapper;
        this.nativeData = feedAd;
    }


    @Override
    public int getAdPatternType() {
        int imageMode = nativeData.getAdPatternType();
        int adPatternType = MsAdPatternType.LARGE_IMAGE;
        switch (imageMode) {
            case NativeAdPatternType.NATIVE_VIDEO_AD:
                adPatternType = MsAdPatternType.VIDEO;
                break;
            case NativeAdPatternType.NATIVE_BIG_IMAGE_AD:
                adPatternType = MsAdPatternType.LARGE_IMAGE;
                break;
            case NativeAdPatternType.NATIVE_GROUP_IMAGE_AD:
                adPatternType = MsAdPatternType.THREE_IMAGE;
                break;
        }
        return adPatternType;
    }

    @Override
    public int getInteractionType() {
        int interactionType = MsInteractionType.NORMAL;
        switch (nativeData.getAdInteractiveType()) {
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                interactionType = MsInteractionType.NORMAL;
                break;
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                interactionType = MsInteractionType.DOWNLOAD_APP;
                break;
        }
        return interactionType;
    }


    @Override
    public void bindAdToView(Context context, ViewGroup container, List<View> clickableViews) {
        //创意点击views是指，点击对应的views会执行广告的目的，比如下载app、跳转目标网页等
        //clickableViews，在视频类广告中，点击clickableViews会跳转到视频页面
        //viewGroup参数必须为container的根view，否则穿山甲无法加载广告
        nativeData.bindViewForInteraction(container, clickableViews, new NativeAdEventListener() {
            @Override
            public void onAdExposed() {
                adWrapper.onFeedAdExposure(CustomFeedAd.this);
            }

            @Override
            public void onAdClicked() {
                adWrapper.onFeedAdClicked(CustomFeedAd.this);
            }

            @Override
            public void onAdRenderFail(AdError adError) {

            }
        });
    }

    @Override
    public void bindMediaView(ViewGroup mediaView, RecyclerAdMediaListener nativeRecyclerAdMediaListener) {
        nativeData.bindMediaView(mediaView, new NativeAdData.NativeAdMediaListener() {
            @Override
            public void onVideoLoad() {

            }

            @Override
            public void onVideoError(AdError adError) {

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
        });
        mediaView.removeAllViews();
    }

    @Override
    public String getTitle() {
        return nativeData.getTitle();
    }

    @Override
    public String getContent() {
        return nativeData.getDesc();
    }

    @Override
    public String getActionText() {
        return null;
    }

    @Override
    public String getIconUrl() {
        return nativeData.getIconUrl();
    }

    @Override
    public String getIconTitle() {
        return null;
    }

    @Override
    public String getFromLogo() {
        return null;
    }

    @Override
    public String getFrom() {
        return null;
    }

    @Override
    public String getFromId() {
        return null;
    }

    @Override
    public String getAppName() {
        if (nativeData != null && nativeData.getAdAppInfo() != null)
            return nativeData.getAdAppInfo().getAppName();
        return "";
    }

    @Override
    public String getAppVersion() {
        if (nativeData != null && nativeData.getAdAppInfo() != null)
            return nativeData.getAdAppInfo().getVersionName();
        return super.getAppVersion();
    }

    @Override
    public String getAppSize() {
        if (nativeData != null && nativeData.getAdAppInfo() != null)
            return nativeData.getAdAppInfo().getAppSize() + "";
        return super.getAppSize();
    }

    @Override
    public String getAppPremissionUrl() {
        if (nativeData != null && nativeData.getAdAppInfo() != null)
            return nativeData.getAdAppInfo().getPermissionsUrl();
        return super.getAppPremissionUrl();
    }

    @Override
    public String getAppIntroUrl() {
        if (nativeData != null && nativeData.getAdAppInfo() != null)
            return nativeData.getAdAppInfo().getAppDescriptionUrl();
        return super.getAppIntroUrl();
    }

    @Override
    public String getPackageName() {
        if (nativeData != null && nativeData.getAdAppInfo() != null)
            return nativeData.getAdAppInfo().getPackageName();
        return null;
    }


    @Override
    public String getDesc() {
        return nativeData.getDesc();
    }

    @Override
    public String[] getImgUrls() {
        //穿山甲逻辑：大图、一图、三图，图片都在ImageList中
        String imgUrls[] = null;
        List<AdGainImage> images = nativeData.getImageList();
        if (images != null) {
            imgUrls = new String[images.size()];
            int i = 0;
            for (AdGainImage image : images) {
                imgUrls[i++] = image.getImageUrl();
            }
        }
        return imgUrls;
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void startVideo() {

    }

    @Override
    public void stopVideo() {

    }

    @Override
    public void pauseVideo() {

    }

    @Override
    public void resumeVideo() {

    }

    @Override
    public void replay() {

    }

    @Override
    public void mute() {

    }

    @Override
    public void unmute() {

    }

    @Override
    public void destroy() {
        if (nativeData != null) {
            nativeData.destroy();
            nativeData = null;
        }
    }

    @Override
    public boolean isNativeExpress() {
        return false;
    }
}
