package com.changanford.common.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.changanford.common.util.toast.ToastUtils;

/**
 * @author: niubobo
 * @date: 2024/8/13
 * @description：
 */
public class HijackingPrevent {
    public final static String sDES = "福域已进入后台";

    /**
     * 退出APP的标识
     */
    private boolean isExit = false;
    /**
     * 延时事件
     */
    private Runnable runnable;
    /**
     * 延时事件发送和取消
     */
    private Handler handler;

    /**
     * 创建单例
     */
    private HijackingPrevent() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isExit()) {
                    isExit = false;
                    ToastUtils.INSTANCE.reToast(sDES);
                }
            }
        };
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static HijackingPrevent getInstance() {
        return Holder.S_HIJACKING_PROVENT;
    }

    /**
     * Holder初始化单例
     */
    private static class Holder {
        private static final HijackingPrevent S_HIJACKING_PROVENT = new HijackingPrevent();
    }

    /**
     * 退出activity时，延时通知
     */
    public synchronized void delayNotify(Activity activity) {
        // 不需要通知，则返回
        if (!isNeedNotify(activity)) {
            return;
        }
        setExit(true);
        // 先移除已有的
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 500);

    }

    /**
     * 进入当前app activity时，移除通知
     */
    public synchronized void removeNotify() {
        if (isExit()) {
            setExit(false);
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * 判断是否需要通知Toast
     */
    public synchronized boolean isNeedNotify(Activity activity) {
        if (activity == null) {
            return false;
        }
        String actName = activity.getClass().getName();
        if (TextUtils.isEmpty(actName)) {
            return false;
        }
        //除了申请权限的activity，其它都需要延时通知
        return !actName.contains("UtilsTransActivity");
    }

    /**
     * 是否退出app
     *
     * @return
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * 设置app退出与否标识
     *
     * @param isExit
     */
    public void setExit(boolean isExit) {
        this.isExit = isExit;
    }
}
