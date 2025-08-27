package com.jiguangssp.addemo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgBannerAd;
import cn.jiguang.jgssp.ad.data.ADJgAdInfo;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgBannerAdListener;

import com.jiguangssp.addemo.R;

/**
 * @author ciba
 * @description Banner广告Fragment示例
 * @date 2020/4/20
 */
public class BannerAdFragment extends BaseFragment {
    private FrameLayout flContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.activity_banner, null);
        flContainer = inflate.findViewById(R.id.flContainer);

        initBannerAd();
        return inflate;
    }

    private void initBannerAd() {
        // 创建Banner广告实例，第一个参数可以是Activity或Fragment，第二个参数是广告容器（请保证容器不会拦截点击、触摸等事件）
        ADJgBannerAd jgBannerAd = new ADJgBannerAd(this, flContainer);
        // 设置自刷新时间间隔，0为不自动刷新，其他取值范围为[30,120]，单位秒
        jgBannerAd.setAutoRefreshInterval(ADJgDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        jgBannerAd.setOnlySupportPlatform(ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
        // 设置Banner广告监听
        jgBannerAd.setListener(new ADJgBannerAdListener() {
            @Override
            public void onAdReceive(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdReceive...");
            }

            @Override
            public void onAdExpose(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(ADJgAdInfo adJgAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClose...");
            }

            @Override
            public void onAdFailed(ADJgError adJgError) {
                if (adJgError != null) {
                    String failedJson = adJgError.toString();
                    Log.d(ADJgDemoConstant.TAG, "onAdFailed..." + failedJson);
                }
            }
        });
        // 加载Banner广告，参数为广告位ID，同一个ADJgBannerAd只有一次loadAd有效
        jgBannerAd.loadAd(ADJgDemoConstant.BANNER_AD_POS_ID);
    }

    @Override
    public String getTitle() {
        return "Banner";
    }
}
