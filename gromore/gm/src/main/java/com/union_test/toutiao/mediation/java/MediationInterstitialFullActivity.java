package com.union_test.toutiao.mediation.java;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdNative.FullScreenVideoAdListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.mediation.ad.IMediationAdSlot;
import com.bytedance.sdk.openadsdk.mediation.ad.IMediationNativeToBannerListener;
import com.bytedance.sdk.openadsdk.mediation.ad.IMediationSplashRequestInfo;
import com.gromore.adapter.adgain.GMBiddingUtil;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.mediation.java.utils.Const;

import java.util.Collections;
import java.util.Map;

/**
 * 融合demo，插全屏广告使用示例。更多功能参考接入文档。
 * <p>
 * 注意：每次加载的广告，只能展示一次
 * <p>
 * 接入步骤：
 * 1、创建AdSlot对象
 * 2、创建TTAdNative对象
 * 3、创建加载、展示监听器
 * 4、加载广告
 * 5、加载成功后，展示广告
 * 6、在onDestroy中销毁广告
 */
public final class MediationInterstitialFullActivity extends Activity {

    public String mMediaId; // 融合广告位

    private TTFullScreenVideoAd mTTFullScreenVideoAd; // 插全屏广告对象

    private FullScreenVideoAdListener mFullScreenVideoListener; // 广告加载监听器

    private TTFullScreenVideoAd.FullScreenVideoAdInteractionListener mFullScreenVideoAdInteractionListener; // 广告展示监听器

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mediation_activity_interstitial_full);

        // 聚合广告位（在GroMore平台的广告位，注意不是adn的代码位）
        this.mMediaId = getResources().getString(R.string.full_media_id);
        TextView tvMediationId = (TextView) this.findViewById(R.id.tv_media_id);
        tvMediationId.setText(getString(R.string.ad_mediation_id, this.mMediaId));

        // 加载广告
        this.findViewById(R.id.bt_load).setOnClickListener((OnClickListener) (new OnClickListener() {
            public final void onClick(View it) {
                loadInterstitialFullAd();
            }
        }));

        // 展示广告
        this.findViewById(R.id.bt_show).setOnClickListener((OnClickListener) (new OnClickListener() {
            public final void onClick(View it) {
                showInterstitialFullAd();
            }
        }));
    }

    private void loadInterstitialFullAd() {
        /** 1、创建AdSlot对象 */

        AdSlot adslot = new AdSlot.Builder()
                .setCodeId(this.mMediaId)
                .setMediationAdSlot(new IMediationAdSlot() {
                    @Override
                    public boolean isSplashShakeButton() {
                        return false;
                    }

                    @Override
                    public boolean isSplashPreLoad() {
                        return false;
                    }

                    @Override
                    public boolean isMuted() {
                        return false;
                    }

                    @Override
                    public float getVolume() {
                        return 0;
                    }

                    @Override
                    public boolean isUseSurfaceView() {
                        return false;
                    }

                    
                    @Override
                    public Map<String, Object> getExtraObject() {
                        return Collections.emptyMap();
                    }

                    @Override
                    public boolean isBidNotify() {
                        return true;
                    }

                    
                    @Override
                    public String getScenarioId() {
                        return "";
                    }

                    @Override
                    public boolean isAllowShowCloseBtn() {
                        return false;
                    }

                    
                    @Override
                    public IMediationNativeToBannerListener getMediationNativeToBannerListener() {
                        return null;
                    }

                    @Override
                    public float getShakeViewWidth() {
                        return 0;
                    }

                    @Override
                    public float getShakeViewHeight() {
                        return 0;
                    }

                    
                    @Override
                    public String getWxAppId() {
                        return "";
                    }

                    
                    @Override
                    public IMediationSplashRequestInfo getMediationSplashRequestInfo() {
                        return null;
                    }

                    
                    @Override
                    public String getRewardName() {
                        return "";
                    }

                    @Override
                    public int getRewardAmount() {
                        return 0;
                    }
                })
                .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)
                .build();

        /** 2、创建TTAdNative对象 */

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(this);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */
        if (adNativeLoader != null) {

            adNativeLoader.loadFullScreenVideoAd(adslot, this.mFullScreenVideoListener);
        }
    }

    // 在加载成功后展示广告
    private void showInterstitialFullAd() {
        if (this.mTTFullScreenVideoAd == null) {
            Log.d(Const.TAG, "请先加载广告或等待广告加载完毕后再调用show方法");
            return;
        }
        /** 5、设置展示监听器，展示广告 */

        this.mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(this.mFullScreenVideoAdInteractionListener);

        this.mTTFullScreenVideoAd.showFullScreenVideoAd(MediationInterstitialFullActivity.this);
    }

    private void initListeners() {
        // 广告加载监听器

        this.mFullScreenVideoListener = new FullScreenVideoAdListener() {
            public void onError(int code, String message) {
                Log.d(Const.TAG, "InterstitialFull onError code = " + code + " msg = " + message);
            }


            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                Log.d(Const.TAG, "InterstitialFull onFullScreenVideoLoaded");
                mTTFullScreenVideoAd = ad;
            }


            public void onFullScreenVideoCached() {
                Log.d(Const.TAG, "InterstitialFull onFullScreenVideoCached");
            }


            public void onFullScreenVideoCached(TTFullScreenVideoAd ad) {
                Log.d(Const.TAG, "InterstitialFull onFullScreenVideoCached");
                mTTFullScreenVideoAd = ad;
            }
        };
        // 广告展示监听器

        this.mFullScreenVideoAdInteractionListener = new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

            public void onAdShow() {
                GMBiddingUtil.gmNotifyLoss(mTTFullScreenVideoAd);
                Log.d(Const.TAG, "InterstitialFull onAdShow");
            }


            public void onAdVideoBarClick() {
                Log.d(Const.TAG, "InterstitialFull onAdVideoBarClick");
            }


            public void onAdClose() {
                Log.d(Const.TAG, "InterstitialFull onAdClose");
            }


            public void onVideoComplete() {
                Log.d(Const.TAG, "InterstitialFull onVideoComplete");
            }


            public void onSkippedVideo() {
                Log.d(Const.TAG, "InterstitialFull onSkippedVideo");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** 6、在onDestroy中销毁广告 */
        if (mTTFullScreenVideoAd != null && mTTFullScreenVideoAd.getMediationManager() != null) {
            mTTFullScreenVideoAd.getMediationManager().destroy();
        }
    }
}
