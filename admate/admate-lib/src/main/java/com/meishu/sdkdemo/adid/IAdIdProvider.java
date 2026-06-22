package com.meishu.sdkdemo.adid;

public interface IAdIdProvider {
    /** 竖版激励视频 */
    String rewardPortrait();
    /** 横版激励视频 */
    String rewardLandscape();

    /** 信息流自渲染、模板混合 */
    String feedMix();

    /** 纯视频 */
    String video();
    /** 视频暂停贴片 */
    String paster();
    /** 图片 */
    String image();
    /** 插屏 */
    String insertScreen();
    /** 开屏 */
    String splash();
    /** banner */
    String banner();
    /** videoFeed (穿山甲 draw 信息流) */
    String videoFeed();
    /** 全屏视频 */
    String fullScreenVideo();
    String platformName();
}
