package com.changanford.common.util;

import android.content.ClipboardManager;
import android.content.Context;

import com.changanford.common.utilext.ToastUtilsKt;

public class MTextUtil {

    //复制内容到系统剪切板
    public static void copystr(Context context, String str) {
        ClipboardManager cm = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(str);
        ToastUtilsKt.toastShow("已复制到剪切板");
    }
}
