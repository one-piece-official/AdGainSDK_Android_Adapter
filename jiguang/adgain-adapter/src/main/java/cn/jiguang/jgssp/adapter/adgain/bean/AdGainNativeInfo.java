package cn.jiguang.jgssp.adapter.adgain.bean;

import android.view.View;
import android.view.ViewGroup;

import com.adgain.sdk.api.AdAppInfo;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdPatternType;
import com.adgain.sdk.base.natives.AdgainNativeAdMediaView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jiguang.jgssp.ad.adapter.bean.ADNativeInfo;
import cn.jiguang.jgssp.ad.entity.ADJgActionType;
import cn.jiguang.jgssp.ad.entity.ADJgAppInfo;
import cn.jiguang.jgssp.bid.ADSuyiBidLossCode;

public class AdGainNativeInfo extends ADNativeInfo<NativeAdData> {
    private boolean isMute;
    private NativeAdData adData;

    public AdGainNativeInfo(NativeAdData adInfo, boolean isMute) {
        super(adInfo);
        this.isMute = isMute;
        this.adData = adInfo;
        if (adInfo != null) {
            setTitle(adInfo.getTitle());
            setDesc(adInfo.getDesc());
            setActionType(adInfo.getAdPatternType());
            setCtaText(ADJgActionType.getActionTex(getActionType(), getCtaText() == null ? null : adInfo.getCTAText()));
            if (adInfo.getAdAppInfo() != null) {
                ADJgAppInfo appInfo = new ADJgAppInfo();
                AdAppInfo adGainAppInfo = getAdInfo().getAdAppInfo();
                appInfo.setName(adGainAppInfo.getAppName());
                appInfo.setDeveloper(adGainAppInfo.getAuthorName());
                appInfo.setVersion(adGainAppInfo.getVersionName());
                appInfo.setPermissionsUrl(adGainAppInfo.getPermissionsUrl());
                appInfo.setPrivacyUrl(adGainAppInfo.getPrivacyUrl());
                appInfo.setDescriptionUrl(adGainAppInfo.getAppDescriptionUrl());
//                appInfo.setIcp(adGainAppInfo.getIcpNumber());
                appInfo.setSize(adGainAppInfo.getAppSize());
                setAppInfo(appInfo);
            }
            setIconUrl(adInfo.getIconUrl());
            List<String> imgList = new ArrayList<>();
            for (int i = 0; i < adInfo.getImageList().size(); i++) {
                imgList.add(adInfo.getImageList().get(i).imageUrl);
                setImageUrl(adInfo.getImageList().get(0).imageUrl);
            }
            setIconUrl(adInfo.getIconUrl());
            setImageUrlList(imgList);
            setIsVideo(NativeAdPatternType.NATIVE_VIDEO_AD == adInfo.getAdPatternType());

        }
    }

    @Override
    public View getMediaView(ViewGroup container) {
        AdgainNativeAdMediaView mMediaView = new AdgainNativeAdMediaView(container.getContext());
        ViewGroup.LayoutParams _params = mMediaView.getLayoutParams();
        if (_params == null) {
            _params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mMediaView.setLayoutParams(_params);
        return mMediaView;
    }

    @Override
    public void registerViewForInteraction(ViewGroup viewGroup, View... actionViews) {
        if (viewGroup == null || getAdInfo() == null) {
            return;
        }
        List<View> viewList = null;
        if (actionViews != null && actionViews.length > 0) {
            viewList = Arrays.asList(actionViews);
        }
        getAdInfo().bindViewForInteraction(viewGroup, viewList, new NativeAdEventListener() {
            @Override
            public void onAdExposed() {
                callExpose();
            }

            @Override
            public void onAdClicked() {
                callClick();
            }

            @Override
            public void onAdRenderFail(AdError adError) {

            }
        });
        // 设置广告播放时静音
        getAdInfo().setVideoMute(isMute);

        if (adData.getAdPatternType() == NativeAdPatternType.NATIVE_VIDEO_AD) {
            adData.bindMediaView(viewGroup, new NativeAdData.NativeAdMediaListener() {
                @Override
                public void onVideoLoad() {
                    callVideoLoad();
                }

                @Override
                public void onVideoError(AdError adError) {
                }

                @Override
                public void onVideoStart() {
                    callVideoStart();
                }

                @Override
                public void onVideoPause() {
                    callVideoPause();
                }

                @Override
                public void onVideoResume() {
                }

                @Override
                public void onVideoCompleted() {
                    callVideoFinish();
                }
            });
        }
    }

    @Override
    public void registerCloseView(View closeView) {
        if (closeView == null) {
            return;
        }
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callClose();
            }
        });
    }

    @Override
    public void adapterBiddingResult(int bidCode, ArrayList<Double> hbPriceList) {
        if (bidCode == ADSuyiBidLossCode.BID_WIN) {
//            BidPriceUtil.sendWin(getAdInfo(), hbPriceList);
        } else {
//            BidPriceUtil.sendLoss(getAdInfo(), bidCode, hbPriceList);
        }
    }
}
