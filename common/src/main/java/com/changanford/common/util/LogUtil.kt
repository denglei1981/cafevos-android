package com.changanford.common.util

import android.util.Log

/**
 * @Author: lcw
 * @Date: 2020/8/10
 * @Des:
 */
/**
 * 截断输出日志
 * @param
 */
fun longLog(tag: String?, mMsg: String?) {
    var msg = mMsg
    if (tag == null || tag.isEmpty() || msg == null || msg.isEmpty()
    ) return
    val segmentSize = 3 * 1024
    val length = msg.length.toLong()
    // 长度小于等于限制直接打印
    if (length <= segmentSize) {
        Log.d(tag, msg)
    } else {
        // 循环分段打印日志
        while (msg!!.length > segmentSize) {
            val logContent = msg.substring(0, segmentSize)
            msg = msg.replace(logContent, "")
            Log.d(tag, logContent)
        }
        // 打印剩余日志
        Log.d(tag, msg)
    }
}