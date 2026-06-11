package com.meishu.sdkdemo.adid;


public class GDTIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        //激励视频-竖版
        return "73646b0799020991";
    }

    @Override
    public String rewardLandscape() {
        //激励视频-横版
        return "73646b0799020991";
    }



    @Override
    public String feedMix() {
        return "73646b0299020991";

    }



    @Override
    public String video() {
        //未调用
        return "100424166";
    }

    @Override
    public String paster() {
        //贴片
        return "73646b0699020991";  //视频
    }

    @Override
    public String image() {
        //未调用 NativeAdSelectActivity -> ImageNotInRecyclerActivity
        return "100424120";
    }

    @Override
    public String insertScreen() {
        //插屏
        return "73646b0599020991";
    }

    @Override
    public String splash() {
        //开屏
        return "73646b0499020991";
    }

    @Override
    public String banner() {
        //BANNER
        return "73646b0399020991";
    }

    @Override
    public String videoFeed() {
        return "";
    }

    @Override
    public String fullScreenVideo() {
        return "73646b0999020991";
    }

    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_GDT;
    }
}
