package com.meishu.sdkdemo.adid;

public class HYIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        //激励视频-竖版
        return "100425667";
    }

    @Override
    public String rewardLandscape() {
        //激励视频-横版
        return "100425667";
    }

    @Override
    public String feedMix() {

//        return "100425140";//hr 自渲染
        return "100425641";//hr 模板
    }


    @Override
    public String video() {
        //未调用
        return "";
    }

    @Override
    public String paster() {
        //贴片
        //return "73646b0699001991";  //素材随机
        return "";  //视频
    }

    @Override
    public String image() {
        //未调用 NativeAdSelectActivity -> ImageNotInRecyclerActivity
        return "";
    }

    @Override
    public String insertScreen() {
        //插屏
        return "100425631";
    }

    @Override
    public String splash() {
        //开屏
        return "100425628";

    }

    @Override
    public String banner() {
        //BANNER
        return "100425638";
    }

    @Override
    public String videoFeed() {
        //DRAW信息流
        return "100425668";
    }

    @Override
    public String fullScreenVideo() {
        return "100425669";
    }



    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_HY;
    }
}
