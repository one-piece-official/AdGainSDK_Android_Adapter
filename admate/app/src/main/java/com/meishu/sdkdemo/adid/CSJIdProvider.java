package com.meishu.sdkdemo.adid;

public class CSJIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        //激励视频-竖版
        return "73646b0799010991";
    }

    @Override
    public String rewardLandscape() {
        //激励视频-横版
        return "73646b0799010991";
    }


    @Override
    public String feedMix() {
        return "73646b0299010991";
    }


    @Override
    public String video() {
        //未调用
        return "100424151";
    }

    @Override
    public String paster() {
        //贴片
        return "73646b0699010991";  //视频
    }

    @Override
    public String image() {
        //未调用 NativeAdSelectActivity -> ImageNotInRecyclerActivity
        return "100424120";
    }

    @Override
    public String insertScreen() {
        //插屏
        return "73646b0599010991";
    }

    @Override
    public String splash() {
        //开屏
//        return "73646b0499010991";
        return "73646b0000008028";//开屏自渲染
    }

    @Override
    public String banner() {
        //BANNER
        return "73646b0399010991";
    }

    @Override
    public String videoFeed() {
        //DRAW信息流
        return "73646b0899010991";
    }

    @Override
    public String fullScreenVideo() {
        return "73646b0999010991";
    }

    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_CSJ;
    }
}
