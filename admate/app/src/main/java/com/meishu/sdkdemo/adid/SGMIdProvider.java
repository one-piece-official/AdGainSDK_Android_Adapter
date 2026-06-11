package com.meishu.sdkdemo.adid;

public class SGMIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        return "73646b0000007003";
    }

    @Override
    public String rewardLandscape() {
        return "73646b0000007003";
    }

    @Override
    public String feedMix() {
        return "73646b0000007005";
    }

    @Override
    public String video() {
        return null;
    }

    @Override
    public String paster() {
        return null;
    }

    @Override
    public String image() {
        return null;
    }

    @Override
    public String insertScreen() {
//        return "73646b0000007004";
        return "1048210";
    }

    @Override
    public String splash() {
//        return "73646b0499131991";
        return "73646b0000007002";
    }

    @Override
    public String banner() {
        return null;
    }

    @Override
    public String videoFeed() {
        return null;
    }

    @Override
    public String fullScreenVideo() {
        return "73646b0000007006";
    }

    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_SGM;
    }
}
