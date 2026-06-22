package com.meishu.sdkdemo.adactivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.utils.LogUtil;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adactivity.banner.BannerAdActivity;
import com.meishu.sdkdemo.adactivity.feed.NativeRecyclerListSelectActivity;
import com.meishu.sdkdemo.adactivity.fullscreenvideo.FullScreenVideoActivity;
import com.meishu.sdkdemo.adactivity.interstitial.InterstitialAdActivity;
import com.meishu.sdkdemo.adactivity.paster.PasterActivity;
import com.meishu.sdkdemo.adactivity.rewardvideo.RewardVideoActivity;
import com.meishu.sdkdemo.adactivity.splash.PrepareSplashActivity;

import java.util.ArrayList;
import java.util.List;

//import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admate_activity_main);
        Button bannerAD = findViewById(R.id.bannerAD);
        bannerAD.setOnClickListener(this);
        findViewById(R.id.slashAD).setOnClickListener(this);
        findViewById(R.id.interstitialAD).setOnClickListener(this);
        findViewById(R.id.pasterAD).setOnClickListener(this);
        findViewById(R.id.pasterAD).setVisibility(View.GONE);
        findViewById(R.id.nativeRecyclerAD).setOnClickListener(this);
        findViewById(R.id.rewardVideoAd).setOnClickListener(this);
        findViewById(R.id.videoFeedAd).setOnClickListener(this);
        findViewById(R.id.full_screen_video).setOnClickListener(this);
        // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
            checkNotifationPermission();
        }
        initSdk();
        initVersionName();
    }

    private void initSdk() {
        CheckBox cb1 = findViewById(R.id.cb1);
        CheckBox cb2 = findViewById(R.id.cb2);
        CheckBox cb3 = findViewById(R.id.cb3);
        CheckBox cbRecommend = findViewById(R.id.personRecomend);
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // CheckBox被选中时的处理
                    LogUtil.e(TAG, "cb1 checked ");
                    AdSdk.adConfig().disableSensorType(Sensor.TYPE_ACCELEROMETER);//禁用摇一摇传感器
                } else {
                    // CheckBox未被选中时的处理
                    LogUtil.e(TAG, "cb1 unChecked ");
                    AdSdk.adConfig().clearDisableSensor(Sensor.TYPE_ACCELEROMETER);
                }
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LogUtil.e(TAG, "cb2 checked ");
                    AdSdk.adConfig().disableSensorType(Sensor.TYPE_GRAVITY);//禁用重力传感器
                } else {
                    LogUtil.e(TAG, "cb2 unChecked ");
                    AdSdk.adConfig().clearDisableSensor(Sensor.TYPE_GRAVITY);
                }
            }
        });

        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LogUtil.e(TAG, "cb3 checked ");
                    AdSdk.adConfig().disableSensorType(Sensor.TYPE_GYROSCOPE);//禁用陀螺仪传感器
                } else {
                    LogUtil.e(TAG, "cb3 unChecked ");
                    AdSdk.adConfig().clearDisableSensor(Sensor.TYPE_GYROSCOPE);
                }
            }
        });

        cbRecommend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LogUtil.e(TAG, "cbRecommend checked ");
                    AdSdk.adConfig().enableSdkPersonalRecommend(true);
                } else {
                    LogUtil.e(TAG, "cbRecommend unChecked ");
                    AdSdk.adConfig().enableSdkPersonalRecommend(false);
                }
            }
        });

    }


    private void checkNotifationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {

//            if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, POST_NOTIFICATIONS)) {
//                    enableNotification();
//                } else {
//                    ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 100);
//                }
//            }
        } else {
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            boolean isEnabled = manager.areNotificationsEnabled();
            if (!isEnabled) {
                enableNotification();
            }

        }


    }

    private void enableNotification() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("通知功能未开启，无法在通知栏显示下载状态，去开启吧～")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                                intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
                                intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);
                            } else {
                                //5.0-7.1 21-25
                                intent.putExtra("app_package", getPackageName());
                                intent.putExtra("app_uid", getApplicationInfo().uid);
                            }

                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //直接跳转到当前应用的设置界面
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();

        lp.width = (int) (((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() * 0.7);
        dialog.getWindow().setAttributes(lp);
    }

    private void initVersionName() {

        try {
            PackageManager pm = getPackageManager();
            String packageName = getPackageName();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

            ((TextView) findViewById(R.id.main_info_demo)).setText(packageInfo.versionName);
            ((TextView) findViewById(R.id.main_info_meishu)).setText(AdSdk.getVersionName());
            ((TextView) findViewById(R.id.main_info_package)).setText(getPackageName());

            new Thread(() -> {
                try {
                    while (true) {
                        if (!TextUtils.isEmpty(AdSdk.getLocalOaid())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView) findViewById(R.id.main_info_oaid)).setText(AdSdk.getLocalOaid());
                                }
                            });

                            break;
                        }
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (AdSdk.adConfig()==null){
            Toast.makeText(this, "请先初始化SDK", Toast.LENGTH_SHORT).show();
            return;
        }
        int id = v.getId();
        if (id == R.id.bannerAD) {
            intent.setClass(this, BannerAdActivity.class);
            startActivity(intent);
        } else if (id == R.id.slashAD) {
            intent.setClass(this, PrepareSplashActivity.class);
            startActivity(intent);
//                finish();
        } else if (id == R.id.interstitialAD) {
            intent.setClass(this, InterstitialAdActivity.class);
            startActivity(intent);
        } else if (id == R.id.pasterAD) {
            intent.setClass(this, PasterActivity.class);
            startActivity(intent);
        } else if (id == R.id.nativeRecyclerAD) {
            intent.setClass(this, NativeRecyclerListSelectActivity.class);
            startActivity(intent);
        } else if (id == R.id.rewardVideoAd) {
            intent.setClass(this, RewardVideoActivity.class);
            startActivity(intent);
        } else if (id == R.id.videoFeedAd) {
//                intent.setClass(this, PrepareVideoFeedActivity.class);
            startActivity(intent);
        } else if (id == R.id.full_screen_video) {
            intent.setClass(this, FullScreenVideoActivity.class);
            startActivity(intent);
        }
    }

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用SDK，否则SDK不会工作。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);//申请经纬度坐标权限
        }

        if (lackedPermission.size() != 0) {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


}
