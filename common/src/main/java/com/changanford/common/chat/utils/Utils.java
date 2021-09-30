package com.changanford.common.chat.utils;

import android.content.Context;
import android.text.TextUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件名：NetUtils
 * 创建者: zcy
 * 创建日期：2020/10/15 14:14
 * 描述: TODO
 * 修改描述：TODO
 */
public class Utils {

    public static boolean isHttpOrHttps(String pathStr) {
        if (pathStr.startsWith("http://") || pathStr.startsWith("https://")) {
            return true;
        }
        return false;
    }

    public static String InputTimeAll(Long timeMillis, String timeStyle) {
        if (timeMillis == null || timeMillis == 0) {
            return "";
        }
        if (!isNotNull(timeStyle)) {
            timeStyle = "yyyy-MM-dd HH:mm";
        }
        Date date = new Timestamp(timeMillis);
        SimpleDateFormat sf = new SimpleDateFormat(timeStyle);
        return sf.format(date);
    }


    public static String InputTimeAll(String time, String timeStyle) {
        if (!isNotNull(time)) {
            return "";
        }
        if (!isNotNull(timeStyle)) {
            timeStyle = "yyyy-MM-dd HH:mm";
        }
        if (time.length() > 9) {
            time = time.substring(0, 10);
        }

        SimpleDateFormat sdr = new SimpleDateFormat(timeStyle);
        long i = Long.parseLong(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }


    public static boolean isNotNull(String content) {
        if (TextUtils.isEmpty(content) || content.length() == 0) {
            return false;
        }
        return true;
    }


    public static int dip2Px(Context context, int dip) {
        float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }
}
