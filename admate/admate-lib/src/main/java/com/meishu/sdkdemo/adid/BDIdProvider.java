package com.meishu.sdkdemo.adid;

public class BDIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        //激励视频-竖版
        return "73646b0799030991";
    }

    @Override
    public String rewardLandscape() {
        //激励视频-横版
        return "73646b0799030991";
    }


    @Override
    public String feedMix() {
        return "73646b0299030991";
    }

    @Override
    public String video() {
        //未调用
        return "100424181";
    }

    @Override
    public String paster() {
        //贴片
        return "73646b0699030991";  //视频
    }

    @Override
    public String image() {
        //未调用 NativeAdSelectActivity -> ImageNotInRecyclerActivity
        return "100424179";
    }

    @Override
    public String insertScreen() {
        //插屏
        return "73646b0599030991";
    }

    @Override
    public String splash() {
        //开屏
        return "73646b0499030991";//点睛模式

    }

    @Override
    public String banner() {
        //BANNER
        return "73646b0399030991";
    }

    @Override
    public String videoFeed() {
        return "73646b0899030991";
    }

    @Override
    public String fullScreenVideo() {
        return "73646b0999030991";
    }

    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_BD;
    }
}
