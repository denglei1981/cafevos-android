package com.changanford.common.utilext

import android.util.Log
import com.changanford.common.util.MConstant.isShowLog
import com.changanford.common.util.longLog


/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.Log
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 17:16
 * @Description: 打印日志
 * *********************************************************************************
 */

fun String.logE() {
    if (isShowLog) {
        Log.e("EVOSLog", this)
    }
}

fun String.longE() {
    if (isShowLog) {
        longLog("EVOSLog", this)
    }
}

fun String.logD() {
    if (isShowLog) {
        Log.d("EVOSLog", this)
    }
}

fun String.logW() {
    if (isShowLog) {
        Log.w("EVOSLog", this)
    }
}

fun Int.logE() {
    if (isShowLog) {
        Log.e("EVOSLog", "$this")
    }
}

fun Int.logD() {
    if (isShowLog) {
        Log.d("EVOSLog", "$this")
    }
}

fun Int.logW() {
    if (isShowLog) {
        Log.w("EVOSLog", "$this")
    }
}