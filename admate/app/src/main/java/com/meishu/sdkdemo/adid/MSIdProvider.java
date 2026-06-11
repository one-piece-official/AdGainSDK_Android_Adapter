package com.meishu.sdkdemo.adid;

public class MSIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        //激励视频-竖版
        return "1092420";

    }

    @Override
    public String rewardLandscape() {
        //激励视频-横版
        return "1092420";
    }



    @Override
    public String feedMix() {
        return "1063888";
    }

    @Override
    public String video() {
        //未调用
        return "";
    }

    @Override
    public String paster() {
        //贴片
        return "1063888";
    }

    @Override
    public String image() {
        return "";
    }

    @Override
    public String insertScreen() {
        //插屏
        return "1092419"; // 1092419  1063886
    }

    @Override
    public String splash() {
        //开屏
        return "1092334 ";  // 1092334  demoId 1063885
    }

    @Override
    public String banner() {
        //BANNER
        return "1063889";
    }

    @Override
    public String videoFeed() {
        //DRAW信息流
        return "1063888";

    }

    @Override
    public String fullScreenVideo() {
        return "1063887";
    }

    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_MS;
    }
}
