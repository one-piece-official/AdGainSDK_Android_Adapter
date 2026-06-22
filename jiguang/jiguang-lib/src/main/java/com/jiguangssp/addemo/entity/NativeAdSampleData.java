package com.jiguangssp.addemo.entity;

import cn.jiguang.jgssp.ad.data.ADJgNativeAdInfo;

/**
 * @author ciba
 * @description 信息流广告示例数据
 * @date 2020/4/1
 */
public class NativeAdSampleData {
    /**
     * 普通数据
     */
    private String normalData;
    /**
     * 信息流广告对象
     */
    private ADJgNativeAdInfo nativeAdInfo;

    public NativeAdSampleData(String normalData) {
        this.normalData = normalData;
    }

    public NativeAdSampleData(ADJgNativeAdInfo nativeAdInfo) {
        this.nativeAdInfo = nativeAdInfo;
    }

    public String getNormalData() {
        return normalData;
    }

    public ADJgNativeAdInfo getNativeAdInfo() {
        return nativeAdInfo;
    }
}
