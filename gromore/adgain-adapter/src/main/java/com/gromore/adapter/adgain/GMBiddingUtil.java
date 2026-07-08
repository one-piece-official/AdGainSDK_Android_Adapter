package com.gromore.adapter.adgain;

import com.adgain.sdk.api.IBidding;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class GMBiddingUtil {

    private static final CopyOnWriteArrayList<WeakReference<NotifyBiddingListener>> listeners = new CopyOnWriteArrayList<>();

    protected interface NotifyBiddingListener {
        void notifyBiddingResult(Object object);
    }

    // 开发者调用的方法
    public static void gmNotifyLoss(Object object) {
        try {
            for (int i = 0; i < listeners.size(); i++) {
                WeakReference<NotifyBiddingListener> reference = listeners.get(i);
                NotifyBiddingListener listener = reference.get();
                if (listener != null) {
                    listener.notifyBiddingResult(object);
                } else {
                    listeners.remove(reference);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.d("GMBiddingUtil", "notifyBiddingResult: Exception " + e.getMessage());
        }
    }

    protected static void addNotifyBiddingListener(NotifyBiddingListener listener) {
        if (listener == null || containsNotifyBiddingListener(listener)) return;
        listeners.add(new WeakReference<>(listener));
    }

    protected static void removeNotifyBiddingListener(NotifyBiddingListener listener) {
        if (listener == null) return;
        for (WeakReference<NotifyBiddingListener> reference : listeners) {
            NotifyBiddingListener item = reference.get();
            if (item == null || item == listener) {
                listeners.remove(reference);
            }
        }
    }

    protected static void clearNotifyBiddingListener() {
        if (listeners != null) listeners.clear();
    }

    // 插屏激励Adapter 调用的方法
    protected static void adgainNotifyLoss(IBidding gtBaseAd, String winPrice, NotifyBiddingListener listener) {
        Map<String, Object> map = new HashMap<>();
//        Log.d("GMBiddingUtil", "adgainNotifyLoss: winPrice " + winPrice + " " + gtBaseAd);
        map.put(IBidding.WIN_PRICE, winPrice);
        map.put(IBidding.THIRD_MEDIATION, "gm");
        if (gtBaseAd != null) {
            gtBaseAd.sendLossNotification(map);
        }
        GMBiddingUtil.removeNotifyBiddingListener(listener);
    }

    private static boolean containsNotifyBiddingListener(NotifyBiddingListener listener) {
        boolean contains = false;
        for (WeakReference<NotifyBiddingListener> reference : listeners) {
            NotifyBiddingListener item = reference.get();
            if (item == null) {
                listeners.remove(reference);
            } else if (item == listener) {
                contains = true;
            }
        }
        return contains;
    }
}
