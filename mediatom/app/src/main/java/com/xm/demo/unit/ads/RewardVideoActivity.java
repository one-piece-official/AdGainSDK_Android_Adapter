package com.xm.demo.unit.ads;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xm.demo.R;
import com.yd.saas.base.interfaces.AdViewVideoListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdVideo;

public class RewardVideoActivity extends Activity {

    // 因为视频加载有一定延迟，建议加上loading，提高用户体验
    private ProgressDialog progressDialog;
    private YdVideo ydVideo;
    private String key = "10e06808bad26969";

    public static void launch(Context context, String trim) {
        Intent _Intent = new Intent(context, RewardVideoActivity.class);
        _Intent.putExtra("media_id", trim);
        context.startActivity(_Intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
        String mediaId = getIntent().getStringExtra("media_id");
        if (!TextUtils.isEmpty(mediaId)) {
            this.key = mediaId;
        }
    }

    public void showVideo(View view) {
        loadRewardVideoAd();
    }

    private void loadRewardVideoAd() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        ydVideo = new YdVideo.Builder(this)
                .setKey(key)
                .setVideoListener(new AdViewVideoListener() {

                    @Override
                    public void onAdShow() {
                        // 视频展示时调用
                        Log.i("RewardVideoActivity", "onAdShow");
                    }

                    @Override
                    public void onAdClose() {
                        // 视频关闭时调用
                        Log.i("RewardVideoActivity", "onAdClose");

                    }

                    @Override
                    public void onVideoPrepared() {
                        progressDialog.dismiss();
                        // 视频加载完成时调用
                        if (ydVideo != null && ydVideo.isReady()) {
                            ydVideo.show();
                        }
                    }

                    @Override
                    public void onVideoReward(double v) {
                        // 视频播放完毕奖励时调用
                        Log.i("RewardVideoActivity", "onVideoReward");

                    }

                    @Override
                    public void onVideoCompleted() {
                        Log.i("RewardVideoActivity", "onVideoCompleted");
                    }

                    @Override
                    public void onAdClick(String url) {
                        // 视频点击时调用
                        Log.i("RewardVideoActivity", "onAdClick");

                    }

                    @Override
                    public void onSkipVideo() {

                    }

                    @Override
                    public void onAdFailed(YdError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RewardVideoActivity.this, "onAdFailed: " + error.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                })
                .build();
        ydVideo.requestRewardVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ydVideo != null) {
            ydVideo.destroy();
        }
    }
}
