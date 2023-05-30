package com.changanford.common.util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.webkit.CookieManager;

import com.changanford.common.basic.BaseApplication;

/**
 * Created by ${zy} on 2018/9/14.
 */

public class FastClickUtils {
    private static final int MIN_DELAY_TIME = 1000;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;

    //连续点击
    private final static int COUNTS = 5;//点击次数
    private final static long DURATION = 2 * 1000;//规定有效时间
    private static long[] mHits = null;

    public static boolean isFastClick() {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) <= MIN_DELAY_TIME) {
            flag = true;
//            T.showShort("亲，太快了");
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static boolean isFastClick(int num) {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) <= num) {
            flag = true;
//            T.showShort("亲，太快了");
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static boolean fastRepeatClick() {
        if (mHits == null) {
            mHits = new long[COUNTS];
        }
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            //   String tips = "您已在[" + DURATION + "]ms内连续点击【" + mHits.length + "】次了！！！";
            //  T.showShort(tips);
            mHits = null;
            return true;
        } else {
            return false;
        }
    }

    public static void relaunchApp() {

        CookieManager.getInstance().removeAllCookies(null);

        CookieManager.getInstance().flush();

        PackageManager packageManager = BaseApplication.INSTANT.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(BaseApplication.INSTANT.getPackageName());
        if (intent == null) return;
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        BaseApplication.INSTANT.startActivity(mainIntent);
        System.exit(0);
    }

}
