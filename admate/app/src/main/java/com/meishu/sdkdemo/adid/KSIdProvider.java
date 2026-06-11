package com.meishu.sdkdemo.adid;

public class KSIdProvider implements IAdIdProvider {
    @Override
    public String rewardPortrait() {
        return "73646b0799050991";
    }

    @Override
    public String rewardLandscape() {
        return "73646b0799050991";
    }



    @Override
    public String feedMix() {
        return "73646b0299050991";
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
        return "73646b0599050991";
    }

    @Override
    public String splash() {
//        return "73646b0499050991";
        return "73646b0199050991";//自渲染
    }

    @Override
    public String banner() {
        return null;
    }

    @Override
    public String videoFeed() {
        return "";
    }

    @Override
    public String fullScreenVideo() {
        return "73646b0999050991";
    }

    @Override
    public String platformName() {
        return IdProviderFactory.PLATFORM_KS;
    }
}
