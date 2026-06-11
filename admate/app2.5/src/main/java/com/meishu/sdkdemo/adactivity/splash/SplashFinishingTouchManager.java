package com.meishu.sdkdemo.adactivity.splash;

import android.animation.Animator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.meishu.sdk.core.ad.splash.ISplashAd;
import com.meishu.sdkdemo.SdkDemoApplication;

public class SplashFinishingTouchManager {
    private static final String TAG = "SplashClickEyeManager";
    private volatile static SplashFinishingTouchManager mInstance;
    private ISplashAd mSplashAd;
    private View mSplashShowView;
    private int[] mOriginSplashPos = new int[2];
    private int mClickEyeViewAnimationTime;//悬浮窗缩放动画的，单位ms

    private int mClickEyeViewMargin;//悬浮窗最小离屏幕边缘的距离
    private int mClickEyeViewMarginBottom;//悬浮窗默认距离屏幕底端的高度
    private boolean mIsSupportSplashClickEye = false;
    public interface AnimationCallBack {
        void animationStart(int animationTime);

        void animationEnd();
    }
    /**
     * 单例获取SplashClickEyeManager对象
     *
     * @return
     */
    public static SplashFinishingTouchManager getInstance() {
        if (mInstance == null) {
            synchronized (SplashFinishingTouchManager.class) {
                if (mInstance == null) {
                    mInstance = new SplashFinishingTouchManager();
                }
            }
        }
        return mInstance;
    }

    public void setSplashInfo(ISplashAd splashAd, View splashView) {
        Context context = SdkDemoApplication.getAppContext();
        this.mSplashAd = splashAd;
        this.mSplashShowView = splashView;
//        mSplashShowView.getLocationOnScreen(mOriginSplashPos);
        mClickEyeViewAnimationTime=800;
        mClickEyeViewMargin = UIUtils.dp2px(context, 16);
        mClickEyeViewMarginBottom = UIUtils.dp2px(context, 20);
    }

    public void clearSplashStaticData() {
        mSplashAd = null;
        mSplashShowView = null;
        mIsSupportSplashClickEye=false;
    }


    public ISplashAd getSplashAd() {
        return mSplashAd;
    }

    public View getSplashShowView(){
        return mSplashShowView;
    }

    public void startSplashClickEyeAnimationInTwoActivity(final ViewGroup decorView,
                                                               final ViewGroup splashViewContainer,
                                                               final AnimationCallBack callBack) {
        if (decorView==null || splashViewContainer == null) {
            return;
        }
        if (mSplashAd == null || mSplashShowView == null) {
            return;
        }
        startSplashClickEyeAnimation(mSplashShowView,decorView,splashViewContainer, callBack);
    }

    private void startSplashClickEyeAnimation(View splash,ViewGroup decorView, ViewGroup splashViewContainer,AnimationCallBack callBack) {
        Context context = SdkDemoApplication.getAppContext();


        int deviceWidth = Math.min(UIUtils.getScreenHeightInPx(context), UIUtils.getScreenWidthInPx(context));
        int deviceHeight = Math.max(UIUtils.getScreenHeightInPx(context), UIUtils.getScreenWidthInPx(context));
        //默认的点睛宽高
        int mClickEyeViewWidth = Math.round(deviceWidth * 0.3f);//屏幕宽度的30%，之前使用PxUtils.dpToPx(context, 90);
        int mClickEyeViewHeight = Math.round(mClickEyeViewWidth * 16 / 9.0f);//根据宽度计算高度，之前使用PxUtils.dpToPx(context, 160);
        int animationContainerWidth = decorView.getMeasuredWidth();
        int animationContainerHeight = decorView.getMeasuredHeight();

        int splashViewWidth = splash.getWidth();
        int splashViewHeight = splash.getHeight();
        float xScaleRatio = (float) mClickEyeViewWidth / splashViewWidth;
        float yScaleRation = (float) mClickEyeViewHeight / splashViewHeight;
        final float animationDistX = deviceWidth - mClickEyeViewWidth-mClickEyeViewMargin;
        final float animationDistY = deviceHeight - mClickEyeViewHeight-mClickEyeViewMarginBottom;  //最终位于container的y坐标
        Log.e(TAG, "startSplashClickEyeAnimation: distX="+animationDistX+",distY="+animationDistY );
        UIUtils.removeFromParent(splash);
        decorView.addView(splash);
        splash.setPivotX(0);
        splash.setPivotY(0);

        splash.animate()
                .scaleX(xScaleRatio)
                .scaleY(yScaleRation)
                .x(animationDistX)
                .y(animationDistY)
                .setInterpolator(new OvershootInterpolator(0))
                .setDuration(mClickEyeViewAnimationTime)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Log.e(TAG, "onAnimationStart: " );
                        if (callBack != null) {
                            callBack.animationStart(mClickEyeViewAnimationTime);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.e(TAG, "onAnimationEnd: " );
//                        UIUtils.removeFromParent(anim_view);

                        if (callBack != null) {
                            callBack.animationEnd();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }


    public boolean isSupportSplashFinishingTouch() {
        return mIsSupportSplashClickEye;
    }

    public void setSupportSplashFinishingTouch(boolean isSupportSplashClickEye) {
        this.mIsSupportSplashClickEye = isSupportSplashClickEye;
    }

}
