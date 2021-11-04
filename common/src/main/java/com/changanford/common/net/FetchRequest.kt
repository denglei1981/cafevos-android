package com.changanford.common.net

import com.changanford.common.basic.BaseApplication
import com.changanford.common.ui.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.net.FetchNetwork
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 16:01
 * @Description: 　处理网络请求的异常情况
 * *********************************************************************************
 */
suspend fun <T> fetchRequest(
    showLoading: Boolean = false,
    request: suspend ApiClient.() -> CommonResponse<T>
): CommonResponse<T> {
    var dialog: LoadingDialog? = null
    if (showLoading) {
        withContext(Dispatchers.Main) {
            dialog = LoadingDialog(BaseApplication.curActivity)
            dialog?.show()
        }
    }
    return try {
        request(ApiClient)
    } catch (e: Exception) {
        CommonResponse(data = null, msg = e.message ?: "报错", code = 1)
    } finally {//处理某些特殊情况
        if (showLoading) {
            withContext(Dispatchers.Main) {
                dialog?.dismiss()
            }
        }
    }
}

/**
 * 请求成功的处理
 */
fun <T> CommonResponse<T>.onSuccess(block: (T?) -> Unit): CommonResponse<T> {
    if (this.code == 0) {//TODO 后面统一处理
        block(this.data)
    }
    return this
}

/**
 * 请求失败或异常的处理
 */
fun <T> CommonResponse<T>.onFailure(block: (T?) -> Unit): CommonResponse<T> {
    if (this.code != 0) {
        //TODO 做一些统一的处理
        block(this.data)
    }
    return this
}

/**
 * 请求失败或异常的处理
 */
fun <T> CommonResponse<T>.onWithCodeFailure(block: (Int) -> Unit): CommonResponse<T> {
        //TODO 做一些统一的处理
    if (this.code != 0) {
        block(this.code)
    }
    return this
}

fun <T> CommonResponse<T>.onWithMsgFailure(block: (msg: String?) -> Unit): CommonResponse<T> {
    if (this.code != 0) {
        //TODO 做一些统一的处理
        block(this.msg)
    }
    return this
}
