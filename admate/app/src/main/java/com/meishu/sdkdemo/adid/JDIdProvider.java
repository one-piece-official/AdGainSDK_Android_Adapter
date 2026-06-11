package com.meishu.sdkdemo.adid;

public class JDIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        return null;
    }

    @Override
    public String rewardLandscape() {
        return null;
    }


    @Override
    public String feedMix() {
        return "73646b1099091991";
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
        return "73646b0599091991";
    }

    @Override
    public String splash() {
        return "73646b0000005002";
    }

    @Override
    public String banner() {
        return "73646b0399091991";
    }

    @Override
    public String videoFeed() {
        return null;
    }

    @Override
    public String fullScreenVideo() {
        return null;
    }

    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_JD;
    }
}
