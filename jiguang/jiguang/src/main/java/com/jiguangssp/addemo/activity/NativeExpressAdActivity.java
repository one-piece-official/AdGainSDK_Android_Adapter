package com.jiguangssp.addemo.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jiguangssp.addemo.adapter.NativeAdAdapter;
import com.jiguangssp.addemo.entity.NativeAdSampleData;

import java.util.ArrayList;
import java.util.List;

import com.jiguangssp.addemo.R;

import com.jiguangssp.addemo.constant.ADJgDemoConstant;

import cn.jiguang.jgssp.ad.ADJgNativeAd;
import cn.jiguang.jgssp.ad.data.ADJgNativeAdInfo;
import cn.jiguang.jgssp.ad.entity.ADJgAdSize;
import cn.jiguang.jgssp.ad.entity.ADJgExtraParams;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgNativeAdListener;
import cn.jiguang.jgssp.util.ADJgDisplayUtil;

/**
 * @author ciba
 * @description 信息流广告示例
 * @date 2020/4/1
 */
public class NativeExpressAdActivity extends BaseAdActivity {
    private NativeAdAdapter nativeAdAdapter;
    private ADJgNativeAd adJgNativeAd;
    private List<NativeAdSampleData> tempDataList = new ArrayList<>();
    private int refreshType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad);
        initView();
        initListener();
        initAd();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nativeAdAdapter = new NativeAdAdapter(this);
        recyclerView.setAdapter(nativeAdAdapter);
    }

    private void initListener() {
    }

    private void initAd() {
        int widthPixels = getResources().getDisplayMetrics().widthPixels - ADJgDisplayUtil.dp2px(20);
        // 创建信息流广告实例
        adJgNativeAd = new ADJgNativeAd(this);

        // 创建额外参数实例
        ADJgExtraParams extraParams = new ADJgExtraParams.Builder()
                // 设置整个广告视图预期宽高(目前仅头条平台需要，没有接入头条可不设置)，单位为px，高度如果小于等于0则高度自适应
                .adSize(new ADJgAdSize(widthPixels, 0))
                // 设置广告视图中MediaView的预期宽高(目前仅Inmobi平台需要,Inmobi的MediaView高度为自适应，没有接入Inmobi平台可不设置)，单位为px
                .nativeAdMediaViewSize(new ADJgAdSize((int) (widthPixels - 24 * getResources().getDisplayMetrics().density)))
//                .nativeStyle(adNativeStyle)
                // 设置信息流广告适配播放是否静音，默认静音，目前优量汇、百度、汇量、快手、Admobile支持修改
                .nativeAdPlayWithMute(ADJgDemoConstant.NATIVE_AD_PLAY_WITH_MUTE)
                .build();
        // 设置一些额外参数，有些平台的广告可能需要传入一些额外参数，如果有接入头条、Inmobi平台，如果包含这些平台该参数必须设置
        adJgNativeAd.setLocalExtraParams(extraParams);
        Log.d(ADJgDemoConstant.TAG, "Express Load: " );

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        adJgNativeAd.setOnlySupportPlatform(ADJgDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM);
        // 设置广告监听
        adJgNativeAd.setListener(new ADJgNativeAdListener() {
            @Override
            public void onRenderFailed(ADJgNativeAdInfo adJgNativeAdInfo, ADJgError adJgError) {
                Log.d(ADJgDemoConstant.TAG, "onRenderFailed: " + adJgError.toString());
                // 广告渲染失败，释放和移除ADJgNativeAdInfo
                nativeAdAdapter.removeData(adJgNativeAdInfo);
            }

            @Override
            public void onAdReceive(List<ADJgNativeAdInfo> adInfoList) {
                Log.d(ADJgDemoConstant.TAG, "onAdReceive: " + adInfoList.size());
                for (int i = 0; i < adInfoList.size(); i++) {
                    int index = i * 5;
                    ADJgNativeAdInfo nativeAdInfo = adInfoList.get(i);
                    if (index >= tempDataList.size()) {
                        tempDataList.add(new NativeAdSampleData(nativeAdInfo));
                    } else {
                        tempDataList.add(index, new NativeAdSampleData(nativeAdInfo));
                    }
                    Log.d(ADJgDemoConstant.TAG, "onAdReceive hash code: " + adInfoList.get(i).hashCode());
                }
                nativeAdAdapter.addData(tempDataList);
            }

            @Override
            public void onAdExpose(ADJgNativeAdInfo adJgNativeAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdExpose: " + adJgNativeAdInfo.hashCode());
            }

            @Override
            public void onAdClick(ADJgNativeAdInfo adJgNativeAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClick: " + adJgNativeAdInfo.hashCode());
            }

            @Override
            public void onAdClose(ADJgNativeAdInfo adJgNativeAdInfo) {
                Log.d(ADJgDemoConstant.TAG, "onAdClose: " + adJgNativeAdInfo.hashCode());
                // 广告被关闭，释放和移除ADJgNativeAdInfo
                nativeAdAdapter.removeData(adJgNativeAdInfo);
            }

            @Override
            public void onAdFailed(ADJgError adJgError) {
                if (adJgError != null) {
                    Log.d(ADJgDemoConstant.TAG, "onAdFailed: " + adJgError.toString());
                }
                nativeAdAdapter.addData(tempDataList);
            }
        });
        loadData();
    }

    /**
     * 加载数据和广告
     */
    private void loadData() {
        tempDataList.clear();
        mockNormalDataRequest();
        // 信息流广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        adJgNativeAd.setSceneId(ADJgDemoConstant.NATIVE_AD_SCENE_ID);
        // 请求广告数据，参数一广告位ID，参数二请求数量[1,3]
        adJgNativeAd.loadAd(ADJgDemoConstant.NATIVE_AD_POS_ID1, ADJgDemoConstant.NATIVE_AD_COUNT);
    }

    /**
     * 模拟普通数据请求
     */
    private void mockNormalDataRequest() {
        for (int i = 0; i < 20; i++) {
            tempDataList.add(new NativeAdSampleData("模拟的普通数据 : " + (nativeAdAdapter == null ? 0 : nativeAdAdapter.getItemCount() + i)));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initAd();
    }
}
