package com.changanford.common.chat.utils;

import android.util.Log;

import com.changanford.common.BuildConfig;

public   class LogUtil {

    public static void d(String msg) {
        Log.d("chatui", msg);
    }
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.d(tag, msg);
    }
}
