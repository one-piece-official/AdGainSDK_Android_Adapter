package com.xm.demo;

import com.xm.demo.base.CustomApplication;
import com.yd.saas.ydsdk.manager.YdConfig;

public class MainApplication extends CustomApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 广告初始化必须在主进程中
         */
        YdConfig.getInstance().init(this, "a3fdd30b422c028a","",true);

    }

}
