package com.meishu.sdkdemo;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import androidx.multidex.MultiDex;

import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.MSAdConfig;
import com.meishu.sdkdemo.utils.lifecycle.LifecycleUtil;
import com.meishu.sdkdemo.view.FloatView;
import com.tencent.bugly.crashreport.CrashReport;

public class SdkDemoApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        SdkDemoApplication.context=this;
        FloatView.init();
        LifecycleUtil.register(this);

        CrashReport.initCrashReport(getApplicationContext(),"9d18334ffa", true);
        MSAdConfig sdkConfig = new MSAdConfig.Builder()
                .appId("118897") // 118897   106083
//                .disableSensorType(Sensor.TYPE_ACCELEROMETER)//禁用摇一摇传感器
//                .disableSensorType(Sensor.TYPE_GRAVITY)//禁用重力传感器
//                .disableSensorType(Sensor.TYPE_GYROSCOPE)//禁用陀螺仪传感器
                .enableDebug(true)  //开启DEBUG模式，打印内部LOG
                .downloadConfirm(MSAdConfig.DOWNLOAD_CONFIRM_ALWAYS)  //下载提示模式
//                .userId("123456")                   //设置用户ID
//                .userType(1)                        //设置用户类型
//                .userKeywords("汽车,漫画")          //设置用户关键词
                .customController(new MSAdConfig.CustomController() {
                    @Override
                    public String getOaid() {
                        //媒体如果设置enableOaid(false)，可以在这传入自己的oaid
                        return super.getOaid();
                    }

                    @Override
                    public boolean isCanUseLocation() {
                        //是否可以获取位置信息
                        return super.isCanUseLocation();
                    }

                    @Override
                    public Location getLocation() {
                        //快手获取位置信息
                        return super.getLocation();
                    }

                    @Override
                    public boolean isCanUsePhoneState() {
                        //是否允许SDK主动使用手机硬件参数，如：imei
                        return super.isCanUsePhoneState();
                    }

                    @Override
                    public boolean isCsjUsePhoneState() {
                        //针对穿山甲单独开关
                        return super.isCsjUsePhoneState();
                    }

                    @Override
                    public String getDevImei() {
                        //当isCanUsePhoneState=false时，可传入imei信息，sdk使用您传入的imei信息
                        return super.getDevImei();
                    }

                    @Override
                    public boolean isCanUseWifiState() {
                        return super.isCanUseWifiState();
                    }

                    @Override
                    public String getMacAddress() {
                        //是否允许SDK主动使用ACCESS_WIFI_STATE权限
                        return super.getMacAddress();
                    }

                    @Override
                    public boolean isCanUseAndroidId() {
                        //是否允许SDK主动获取ANDROID_ID
                        return super.isCanUseAndroidId();
                    }

                    @Override
                    public String getAndroidId() {
                        //如果isCanUseAndroidId=false，那么sdk获取传入的android
                        return super.getAndroidId();
                    }

                    @Override
                    public boolean canUseMacAddress() {
                        //是否允许获取mac地址
                        return super.canUseMacAddress();
                    }

                    @Override
                    public boolean canUseNetworkState() {
                        //是否允许获取网络状态
                        return super.canUseNetworkState();
                    }

                    @Override
                    public boolean canUseStoragePermission() {
                        ////是否允许SDK主动使用WRITE_EXTERNAL_STORAGE权限
                        return super.canUseStoragePermission();
                    }

                    @Override
                    public boolean canReadInstalledPackages() {
                        //是否允许SDK主动获取设备上应用安装列表
                        return super.canReadInstalledPackages();
                    }

                    @Override
                    public boolean isCanUseImsi() {
                        //是否允许获取imsi
                        return super.isCanUseImsi();
                    }

                    @Override
                    public boolean isCanUsePermissionRecordAudio() {
                        //穿山甲使用，是否允许SDK在申明和授权了的情况下使用录音权限
                        return super.isCanUsePermissionRecordAudio();
                    }

                    @Override
                    public boolean isStorageCollectEnable() {
                        // 是否允许SDK获取存储容量信息
                        return super.isStorageCollectEnable();
                    }
                })
                .build();

        AdSdk.init(this, sdkConfig);

    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Context getAppContext() {
        return SdkDemoApplication.context;
    }


}
