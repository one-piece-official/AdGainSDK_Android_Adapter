package com.meishu.sdkdemo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adactivity.AdConfig;
import com.meishu.sdkdemo.adactivity.DemoBaseActivity;
import com.meishu.sdkdemo.utils.lifecycle.IAppStateNotifyService;
import com.meishu.sdkdemo.utils.lifecycle.LifecycleUtil;

import java.util.HashMap;
import java.util.Map;

public class FloatView {
    private static final int REQ_CODE = 10001;
    private WindowManager windowManager;
    private FloatingActionButton fab;
    private WindowManager.LayoutParams params;
    private boolean isShowing;

    private static volatile boolean allowShowFloatView = false;

    private static final Map<Activity, FloatView> floatMap = new HashMap<>();

    public static void init() {
        LifecycleUtil.addOnAppStateListener(new IAppStateNotifyService.OnAppStateListener() {
            @Override
            public void onAppStateUpdate(boolean foreground) {
            }

            @Override
            public void onActivityResume(Activity activity) {
                showFloatView(activity);
            }

            @Override
            public void onActivityPause(Activity activity) {
                hideFloatView(activity);
            }
        });
    }

    public static void setAllowShowFloatView(boolean allowShowFloatView) {
        FloatView.allowShowFloatView = allowShowFloatView;
        if (allowShowFloatView) {
            showFloatView(LifecycleUtil.getCurActivity());
        } else {
            hideFloatView(LifecycleUtil.getCurActivity());
        }
    }

    private static void showFloatView(Activity activity) {
        if (activity instanceof DemoBaseActivity && allowShowFloatView) {
            if (!floatMap.containsKey(activity)) {
                floatMap.put(activity, new FloatView());
            }
            floatMap.get(activity).showFloat(activity);
        }
    }

    private static void hideFloatView(Activity activity) {
        if (floatMap.containsKey(activity)) {
            floatMap.remove(activity).hideFloat();
        }
    }

    private FloatView() {
    }

    private void showFloat(Activity activity) {
        initFab(activity);
        checkOverlayPermission(activity);
    }

    public void hideFloat() {
        if (isShowing && fab != null) {
            windowManager.removeView(fab);
            isShowing = false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initFab(Activity activity) {
        fab = new FloatingActionButton(activity);
        fab.setImageResource(android.R.drawable.ic_menu_help);
        fab.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.common_google_signin_btn_text_light))); // 设置背景颜色
        fab.setOnClickListener(v -> showContextMenu(v));
        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.x = 100;
        params.y = 100;

        fab.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_UP:
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (initialTouchX - event.getRawX());
                        params.y = initialY + (int) (initialTouchY - event.getRawY());
                        windowManager.updateViewLayout(fab, params);
                        return false;
                }
                return false;
            }
        });
    }

    private void showContextMenu(View anchor) {
        PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.float_menu, popup.getMenu());

        Menu menu = popup.getMenu();
        menu.findItem(R.id.menu_auto_render).setChecked(AdConfig.isAutoRender());
        updateMenuItemsState(menu);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_auto_render:
                    AdConfig.setIsAutoRender(!item.isChecked());
                    item.setChecked(AdConfig.isAutoRender());
                    updateMenuItemsState(menu);
                    break;
                case R.id.menu_call_render:
                    callRender();
                    break;
            }
            return true;
        });
        popup.show();
    }

    private void updateMenuItemsState(Menu menu) {
//        menu.findItem(R.id.menu_call_render).setEnabled(AdConfig.isAutoRender());
    }

    private void callRender() {
        if (LifecycleUtil.getCurActivity() instanceof DemoBaseActivity) {
            ((DemoBaseActivity) LifecycleUtil.getCurActivity()).callRender();
        }
    }

    private void checkOverlayPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQ_CODE);
            } else {
                windowManager.addView(fab, params);
                isShowing = true;
            }
        } else {
            Toast.makeText(activity, "不支持显示浮窗", Toast.LENGTH_SHORT).show();
        }
    }
}
