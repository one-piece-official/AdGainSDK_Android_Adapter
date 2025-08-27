package com.jiguangssp.addemo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jiguangssp.addemo.adapter.NativeAdAdapter;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;
import com.jiguangssp.addemo.entity.NativeAdSampleData;

import java.util.ArrayList;
import java.util.List;

import cn.jiguang.jgssp.ad.ADJgNativeAd;
import cn.jiguang.jgssp.ad.data.ADJgNativeAdInfo;
import cn.jiguang.jgssp.ad.entity.ADJgAdSize;
import cn.jiguang.jgssp.ad.entity.ADJgExtraParams;
import cn.jiguang.jgssp.ad.error.ADJgError;
import cn.jiguang.jgssp.ad.listener.ADJgNativeAdListener;

import com.jiguangssp.addemo.R;

/**
 * @author ciba
 * @description 信息流广告Fragment示例
 * @date 2020/4/20
 */
public class NativeAdFragment extends BaseFragment {
    private NativeAdAdapter nativeAdAdapter;
    private ADJgNativeAd adJgNativeAd;
    private int refreshType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.activity_native_ad, null);

        initView(inflate);
        initListener();
        initData();

        return inflate;
    }

    @Override
    public String getTitle() {
        return "信息流广告";
    }

    private void initView(View inflate) {
        RecyclerView recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));

        nativeAdAdapter = new NativeAdAdapter(inflate.getContext());
        recyclerView.setAdapter(nativeAdAdapter);
    }

    private void initListener() {
    }

    private void initData() {
        // 创建信息流广告实例
        adJgNativeAd = new ADJgNativeAd(this);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        // 创建额外参数实例
        ADJgExtraParams extraParams = new ADJgExtraParams.Builder()
                // 设置整个广告视图预期宽高(目前仅头条平台需要，没有接入头条可不设置)，单位为px，高度如果小于等于0则高度自适应
                .adSize(new ADJgAdSize(widthPixels, 0))
                // 设置广告视图中的MediaView的预期宽高(目前仅Inmobi平台需要,Inmobi的MediaView高度为自适应，没有接入Inmobi平台可不设置)，单位为px
                .nativeAdMediaViewSize(new ADJgAdSize(widthPixels))
                .build();
        // 设置一些额外参数，有些平台的广告可能需要传入一些额外参数，如果有接入头条、Inmobi平台，该参数必须设置
        adJgNativeAd.setLocalExtraParams(extraParams);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        adJgNativeAd.setOnlySupportPlatform(ADJgDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM);
        // 设置广告监听
        adJgNativeAd.setListener(new ADJgNativeAdListener() {
            @Override
            public void onRenderFailed(ADJgNativeAdInfo adJgNativeAdInfo, ADJgError adJgError) {
                Log.d(ADJgDemoConstant.TAG, "onRenderFailed: " + adJgError.toString());
                nativeAdAdapter.removeData(adJgNativeAdInfo);
            }

            @Override
            public void onAdReceive(List<ADJgNativeAdInfo> adInfoList) {
                Log.d(ADJgDemoConstant.TAG, "onAdReceive: " + adInfoList.size());
                List<NativeAdSampleData> nativeAdSampleDataList = new ArrayList<>();
                for (int i = 0; i < adInfoList.size(); i++) {
                    ADJgNativeAdInfo nativeAdInfo = adInfoList.get(i);
                    nativeAdSampleDataList.add(new NativeAdSampleData(nativeAdInfo));
                }
                nativeAdAdapter.addData(nativeAdSampleDataList);
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
                nativeAdAdapter.removeData(adJgNativeAdInfo);
            }

            @Override
            public void onAdFailed(ADJgError adJgError) {
                if (adJgError != null) {
                    Log.d(ADJgDemoConstant.TAG, "onAdFailed: " + adJgError.toString());
                }
            }
        });

    }


    /**
     * 加载数据和广告
     */
    private void loadData() {
        List<NativeAdSampleData> normalDataList = mockNormalDataRequest();
        nativeAdAdapter.addData(normalDataList);

        // 请求广告数据，参数一广告位ID，参数二请求数量[1,3]
        adJgNativeAd.loadAd(ADJgDemoConstant.NATIVE_AD_POS_ID, ADJgDemoConstant.NATIVE_AD_COUNT);
    }

    /**
     * 模拟普通数据请求
     *
     * @return : 普通数据列表
     */
    private List<NativeAdSampleData> mockNormalDataRequest() {
        List<NativeAdSampleData> normalDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            normalDataList.add(new NativeAdSampleData("模拟的普通数据 : " + (nativeAdAdapter.getItemCount() + i)));
        }
        return normalDataList;
    }
}
